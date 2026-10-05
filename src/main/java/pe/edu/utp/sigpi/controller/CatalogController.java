package pe.edu.utp.sigpi.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.sigpi.model.*;
import pe.edu.utp.sigpi.repository.*;
import pe.edu.utp.sigpi.service.OrderService;
import java.util.*;
import java.math.BigDecimal;
@Controller
public class CatalogController {
 private final ProductRepository products;private final ClientRepository clients;private final OrderService orders;
 public CatalogController(ProductRepository p,ClientRepository c,OrderService o){products=p;clients=c;orders=o;}
 @SuppressWarnings("unchecked") private Map<Long,Integer> cart(HttpSession s){if(s.getAttribute("cart")==null)s.setAttribute("cart",new TreeMap<Long,Integer>());return (Map<Long,Integer>)s.getAttribute("cart");}
 public record Line(Product product,int quantity,BigDecimal subtotal){}
 @GetMapping("/catalog") public String catalog(@RequestParam(defaultValue="")String q,Model m){m.addAttribute("q",q);m.addAttribute("products",products.findAll().stream().filter(p->p.isActive()&&p.getCategory().isActive()&&(p.getName()+p.getCode()).toLowerCase().contains(q.toLowerCase())).toList());return "catalog";}
 @GetMapping("/cart") public String view(HttpSession s,Model m){synchronized(s){List<Line> lines=new ArrayList<>();for(var e:cart(s).entrySet()){Product p=products.findById(e.getKey()).orElseThrow();lines.add(new Line(p,e.getValue(),p.getSalePrice().multiply(BigDecimal.valueOf(e.getValue()))));}m.addAttribute("lines",lines);m.addAttribute("total",lines.stream().map(Line::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add));m.addAttribute("clients",clients.findAll().stream().filter(Client::isActive).toList());return "cart";}}
 @PostMapping("/cart/add") public String add(@RequestParam Long productId,@RequestParam int quantity,HttpSession s,RedirectAttributes ra){synchronized(s){Product p=products.findById(productId).orElseThrow();int qty=Math.addExact(cart(s).getOrDefault(productId,0),quantity);if(quantity<=0||qty>p.getStock()||!p.isActive()||!p.getCategory().isActive())throw new IllegalArgumentException("Cantidad inválida o stock insuficiente");cart(s).put(productId,qty);}ra.addFlashAttribute("success","Producto agregado al carrito");return "redirect:/catalog";}
 @PostMapping("/cart/update") public String update(@RequestParam Long productId,@RequestParam int quantity,HttpSession s){synchronized(s){if(quantity<0)throw new IllegalArgumentException("Cantidad inválida");if(quantity==0)cart(s).remove(productId);else{Product p=products.findById(productId).orElseThrow();if(quantity>p.getStock()||!cart(s).containsKey(productId))throw new IllegalArgumentException("Stock insuficiente");cart(s).put(productId,quantity);}}return "redirect:/cart";}
 @PostMapping("/cart/checkout") public String checkout(@RequestParam Long clientId,HttpSession s,RedirectAttributes ra){synchronized(s){try{var c=cart(s);var o=orders.create(clientId,new ArrayList<>(c.keySet()),new ArrayList<>(c.values()),((AppUser)s.getAttribute("user")).getFullName());c.clear();ra.addFlashAttribute("success","Pedido registrado correctamente");return "redirect:/orders/"+o.getId();}catch(IllegalArgumentException e){ra.addFlashAttribute("error",e.getMessage());return "redirect:/cart";}}}
}
