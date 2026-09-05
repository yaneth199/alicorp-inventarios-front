package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sigpi.repository.ProductRepository;
import pe.edu.utp.sigpi.repository.SalesOrderRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final SalesOrderRepository orderRepository; private final ProductRepository productRepository;
    public ReportController(SalesOrderRepository orderRepository, ProductRepository productRepository){this.orderRepository=orderRepository;this.productRepository=productRepository;}
    @GetMapping
    public String reports(@RequestParam(defaultValue="Pedidos por Fecha") String type,Model m){
        m.addAttribute("type",type);m.addAttribute("months",List.of("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago"));
        m.addAttribute("ordersByMonth",List.of(60,78,90,84,104,119,96,85));m.addAttribute("salesByMonth",List.of(18400,22100,27800,25200,31500,36000,29200,25800));
        m.addAttribute("products",productRepository.findAll());m.addAttribute("orders",orderRepository.findAllByOrderByOrderDateDescIdDesc());
        BigDecimal total=orderRepository.findAll().stream().map(o->o.getTotal()).reduce(BigDecimal.ZERO,BigDecimal::add);m.addAttribute("salesTotal",total);return "reports";
    }
    @GetMapping("/orders.csv")
    public void csv(HttpServletResponse r)throws IOException{r.setContentType("text/csv; charset=UTF-8");r.setHeader("Content-Disposition","attachment; filename=reporte_pedidos_sigpi.csv");r.getWriter().write("Pedido,Fecha,Cliente,Estado,Responsable,Total\n");for(var o:orderRepository.findAllByOrderByOrderDateDescIdDesc())r.getWriter().printf("%s,%s,\"%s\",%s,\"%s\",%s%n",o.getCode(),o.getOrderDate(),o.getClient().getName(),o.getStatus(),o.getResponsible(),o.getTotal());}
}
