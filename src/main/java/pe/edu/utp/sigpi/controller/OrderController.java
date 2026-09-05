package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.AppUser;
import pe.edu.utp.sigpi.repository.ClientRepository;
import pe.edu.utp.sigpi.repository.ProductRepository;
import pe.edu.utp.sigpi.repository.SalesOrderRepository;
import pe.edu.utp.sigpi.service.OrderService;

import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final SalesOrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    public OrderController(SalesOrderRepository orderRepository, ClientRepository clientRepository, ProductRepository productRepository, OrderService orderService) {
        this.orderRepository=orderRepository; this.clientRepository=clientRepository; this.productRepository=productRepository; this.orderService=orderService;
    }
    @GetMapping
    public String list(@RequestParam(defaultValue="") String q,
                       @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
                       @RequestParam(defaultValue="") String status, Model model) {
        var orders = orderRepository.findAllByOrderByOrderDateDescIdDesc();
        if(!q.isBlank()) orders = orders.stream().filter(o -> o.getCode().toLowerCase().contains(q.toLowerCase()) || o.getClient().getName().toLowerCase().contains(q.toLowerCase())).toList();
        if(from!=null) orders = orders.stream().filter(o -> !o.getOrderDate().isBefore(from)).toList();
        if(to!=null) orders = orders.stream().filter(o -> !o.getOrderDate().isAfter(to)).toList();
        if(!status.isBlank()) orders = orders.stream().filter(o -> o.getStatus().equalsIgnoreCase(status)).toList();
        model.addAttribute("orders", orders); model.addAttribute("q",q); model.addAttribute("from",from); model.addAttribute("to",to); model.addAttribute("status",status);
        model.addAttribute("pending", orderRepository.countByStatus("Pendiente"));
        model.addAttribute("processing", orderRepository.countByStatus("En proceso"));
        model.addAttribute("prepared", orderRepository.countByStatus("Preparado"));
        model.addAttribute("delivered", orderRepository.countByStatus("Entregado"));
        model.addAttribute("cancelled", orderRepository.countByStatus("Cancelado")); return "orders";
    }
    @GetMapping("/new")
    public String form(Model model) { model.addAttribute("clients", clientRepository.findAll()); model.addAttribute("products", productRepository.findAll()); return "order-form"; }
    @PostMapping("/save")
    public String save(@RequestParam Long clientId, @RequestParam List<Long> productIds, @RequestParam List<Integer> quantities,
                       HttpSession session, RedirectAttributes ra) {
        try {
            AppUser u=(AppUser)session.getAttribute("user");
            var order=orderService.create(clientId, productIds, quantities, u==null?"Administrador":u.getFullName());
            ra.addFlashAttribute("success", "Pedido "+order.getCode()+" registrado correctamente");
            return "redirect:/orders/"+order.getId();
        } catch (IllegalArgumentException e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/orders/new"; }
    }
    @GetMapping("/{id}") public String detail(@PathVariable Long id, Model model) { model.addAttribute("order", orderRepository.findById(id).orElseThrow()); return "order-detail"; }
    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, @RequestParam String status, RedirectAttributes ra) { orderService.updateStatus(id,status); ra.addFlashAttribute("success","Estado actualizado"); return "redirect:/orders/"+id; }
}
