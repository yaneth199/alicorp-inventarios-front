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
    @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.service.DraftsService drafts;
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
        if(from!=null&&to!=null&&from.isAfter(to))throw new IllegalArgumentException("Rango de fechas inválido");
        var orders = orderRepository.findAllByOrderByOrderDateDescIdDesc();
        if(!q.isBlank()) orders = orders.stream().filter(o -> o.getCode().toLowerCase().contains(q.toLowerCase()) || o.getClient().getName().toLowerCase().contains(q.toLowerCase())).toList();
        if(from!=null) orders = orders.stream().filter(o -> !o.getOrderDate().isBefore(from)).toList();
        if(to!=null) orders = orders.stream().filter(o -> !o.getOrderDate().isAfter(to)).toList();
        if(!status.isBlank()) orders = orders.stream().filter(o -> o.getStatus().equalsIgnoreCase(status)).toList();
        model.addAttribute("orders", orders); model.addAttribute("q",q); model.addAttribute("from",from); model.addAttribute("to",to); model.addAttribute("status",status);
        model.addAttribute("pending", orderRepository.countByStatus("Pendiente"));
        model.addAttribute("processing", orderRepository.countByStatus("En proceso"));
        model.addAttribute("prepared", orderRepository.countByStatus("Preparado"));
        model.addAttribute("sent",orderRepository.countByStatus("Enviado"));
        model.addAttribute("delivered", orderRepository.countByStatus("Entregado"));
        model.addAttribute("cancelled", orderRepository.countByStatus("Cancelado")); return "orders";
    }
    @GetMapping("/export") public void export(jakarta.servlet.http.HttpServletResponse response)throws java.io.IOException {
        var w=pe.edu.utp.sigpi.service.Csv.start(response,"pedidos_sigpi.csv");
        pe.edu.utp.sigpi.service.Csv.row(w,"Pedido","Fecha","Cliente","Estado","Total");
        for(var o:orderRepository.findAllByOrderByOrderDateDescIdDesc())pe.edu.utp.sigpi.service.Csv.row(w,o.getCode(),o.getOrderDate(),o.getClient().getName(),o.getStatus(),o.getTotal());
    }
    @GetMapping("/new")
    public String form(Model model,HttpSession session) { model.addAttribute("draft",drafts.get(((AppUser)session.getAttribute("user")).getId())); session.setAttribute("orderToken",java.util.UUID.randomUUID().toString()); model.addAttribute("clients", clientRepository.findAll().stream().filter(c->c.isActive()).toList()); model.addAttribute("products", productRepository.findAll().stream().filter(p->p.isActive()&&p.getCategory().isActive()).toList()); return "order-form"; }
    @PostMapping("/save")
    public String save(@RequestParam Long clientId, @RequestParam List<Long> productIds, @RequestParam List<Integer> quantities,
                       HttpSession session, @RequestParam String orderToken, RedirectAttributes ra) {
        synchronized(session) {
        if(!orderToken.equals(session.getAttribute("orderToken")))throw new IllegalArgumentException("Formulario ya enviado o caducado; vuelve a abrir Nuevo pedido");
        try {
            AppUser u=(AppUser)session.getAttribute("user");
            drafts.save(u.getId(),clientId,productIds,quantities);
            var order=orderService.create(clientId, productIds, quantities, u==null?"Administrador":u.getFullName());
            drafts.clear(u.getId());
            session.removeAttribute("orderToken");
            ra.addFlashAttribute("success", "Pedido "+order.getCode()+" registrado correctamente");
            return "redirect:/orders/"+order.getId();
        } catch (IllegalArgumentException e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/orders/new"; }
        }
    }
    @PostMapping("/draft") public String draft(@RequestParam(required=false)Long clientId,@RequestParam List<Long> productIds,@RequestParam List<Integer> quantities,HttpSession session,RedirectAttributes ra){drafts.save(((AppUser)session.getAttribute("user")).getId(),clientId,productIds,quantities);ra.addFlashAttribute("success","Borrador guardado. No se ha descontado stock.");return "redirect:/orders/new";}
    @PostMapping("/draft/clear") public String clearDraft(HttpSession session){drafts.clear(((AppUser)session.getAttribute("user")).getId());return "redirect:/orders/new";}
    @GetMapping("/{id}") public String detail(@PathVariable Long id, Model model,HttpSession session) {
      var order=orderRepository.findById(id).orElseThrow();model.addAttribute("order",order);
      var next=java.util.Map.of("Pendiente","En proceso","En proceso","Preparado","Preparado","Enviado","Enviado","Entregado");var options=new java.util.ArrayList<String>();if(next.containsKey(order.getStatus())){options.add(next.get(order.getStatus()));if(!((AppUser)session.getAttribute("user")).getRole().equals("Almacén"))options.add("Cancelado");}model.addAttribute("nextStates",options);return "order-detail";
    }
    @GetMapping("/{id}/export/{format}") public org.springframework.http.ResponseEntity<byte[]> exportOrder(@PathVariable Long id,@PathVariable String format)throws java.io.IOException{var order=orderRepository.findById(id).orElseThrow();var rows=new java.util.ArrayList<java.util.List<Object>>();for(var i:order.getItems())rows.add(java.util.Arrays.asList(i.getProduct().getCode(),i.getProduct().getName(),i.getQuantity(),i.getPrice(),i.getSubtotal()));var table=new pe.edu.utp.sigpi.service.ReportTable(order.getCode(),java.util.List.of("Código","Producto","Cantidad","Precio","Subtotal"),rows,order.getClient().getName()+" · "+order.getOrderDate()+" · "+order.getStatus()+" · Total S/ "+order.getTotal());return pe.edu.utp.sigpi.service.Downloads.table(table,format,"pedido_"+order.getId());}

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, @RequestParam String status, RedirectAttributes ra, HttpSession session) { if(((AppUser)session.getAttribute("user")).getRole().equals("Almacén") && !java.util.Set.of("En proceso","Preparado","Enviado","Entregado").contains(status))throw new IllegalArgumentException("Almacén solo puede avanzar la preparación y entrega"); orderService.updateStatus(id,status,((AppUser)session.getAttribute("user")).getFullName()); ra.addFlashAttribute("success","Estado actualizado"); return "redirect:/orders/"+id; }
}
