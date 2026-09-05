package pe.edu.utp.sigpi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.sigpi.model.Product;
import pe.edu.utp.sigpi.repository.ProductRepository;
import pe.edu.utp.sigpi.repository.SalesOrderRepository;
import pe.edu.utp.sigpi.service.DashboardService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {
    private final DashboardService dashboardService;
    private final ProductRepository productRepository;
    private final SalesOrderRepository orderRepository;

    public DashboardController(DashboardService dashboardService, ProductRepository productRepository, SalesOrderRepository orderRepository) {
        this.dashboardService = dashboardService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("totalProducts", dashboardService.totalProducts());
        model.addAttribute("lowStock", dashboardService.lowStock());
        model.addAttribute("pendingOrders", dashboardService.pendingOrders());
        model.addAttribute("completedOrders", dashboardService.completedOrders());
        model.addAttribute("inventoryValue", dashboardService.inventoryValue());
        model.addAttribute("todayOrders", orderRepository.findAll().stream().filter(o -> LocalDate.now().equals(o.getOrderDate())).count());
        model.addAttribute("alerts", products.stream().filter(p -> p.getStock() <= p.getMinStock()).limit(4).toList());
        model.addAttribute("orders", orderRepository.findAllByOrderByOrderDateDescIdDesc().stream().limit(5).toList());
        model.addAttribute("topProducts", products.stream().limit(6).toList());
        model.addAttribute("months", List.of("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago"));
        model.addAttribute("monthlyOrders", List.of(60,78,90,84,104,119,96,85));
        return "dashboard";
    }
}
