package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.AppUser;
import pe.edu.utp.sigpi.model.Product;
import pe.edu.utp.sigpi.repository.ProductRepository;
import pe.edu.utp.sigpi.repository.CategoryRepository;
import pe.edu.utp.sigpi.service.InventoryService;

@Controller
@RequestMapping("/inventory")
public class InventoryController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;
    public InventoryController(ProductRepository productRepository, CategoryRepository categoryRepository, InventoryService inventoryService) { this.productRepository=productRepository; this.categoryRepository=categoryRepository; this.inventoryService=inventoryService; }

    @GetMapping
    public String list(@RequestParam(defaultValue="") String q, @RequestParam(required=false) Long categoryId, @RequestParam(defaultValue="") String status, Model model) {
        var products=q.isBlank()?productRepository.findAll():productRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(q,q);
        if(categoryId!=null) products=products.stream().filter(p->p.getCategory().getId().equals(categoryId)).toList();
        if(!status.isBlank()) products=products.stream().filter(p->p.getStockStatus().equalsIgnoreCase(status)).toList();
        model.addAttribute("products", products); model.addAttribute("categories",categoryRepository.findAll()); model.addAttribute("q",q);model.addAttribute("categoryId",categoryId);model.addAttribute("status",status);
        model.addAttribute("available", products.stream().filter(p->p.getStock()>p.getMinStock()).count());
        model.addAttribute("low", products.stream().filter(p->p.getStock()>0 && p.getStock()<=p.getMinStock()).count());
        model.addAttribute("out", products.stream().filter(p->p.getStock()<=0).count());
        return "inventory";
    }
    @GetMapping("/movement")
    public String movementForm(@RequestParam String type, Model model) { model.addAttribute("type", type.toUpperCase()); model.addAttribute("products", productRepository.findAll()); return "movement-form"; }
    @PostMapping("/movement")
    public String movement(@RequestParam Long productId, @RequestParam String type, @RequestParam int quantity,
                           @RequestParam(required=false) String document, @RequestParam(required=false) String note,
                           HttpSession session, RedirectAttributes ra) {
        try {
            AppUser u=(AppUser)session.getAttribute("user");
            inventoryService.register(productId,type,quantity,document,u==null?"Administrador":u.getFullName(),note);
            ra.addFlashAttribute("success", ("ENTRADA".equalsIgnoreCase(type)?"Entrada":"Salida")+" registrada correctamente. El inventario ha sido actualizado.");
        } catch (IllegalArgumentException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/inventory";
    }
}
