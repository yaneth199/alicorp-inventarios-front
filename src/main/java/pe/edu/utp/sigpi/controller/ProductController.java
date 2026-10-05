package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.Product;
import pe.edu.utp.sigpi.repository.CategoryRepository;
import pe.edu.utp.sigpi.repository.ProductRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.repository.MovementRepository movements;
    @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.repository.CompanySettingRepository settings;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, @RequestParam(required=false) Long categoryId,
                       @RequestParam(defaultValue="") String status, Model model) {
        List<Product> products = q.isBlank() ? productRepository.findAll() : productRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q);
        if (categoryId != null) products = products.stream().filter(p -> p.getCategory().getId().equals(categoryId)).toList();
        if (!status.isBlank()) products = products.stream().filter(p -> p.getStockStatus().equalsIgnoreCase(status)).toList();
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("q", q); model.addAttribute("categoryId", categoryId); model.addAttribute("status", status);
        model.addAttribute("total", products.size());
        model.addAttribute("activeCount", products.stream().filter(Product::isActive).count());
        model.addAttribute("lowCount", products.stream().filter(p -> p.getStock() > 0 && p.getStock() <= p.getMinStock()).count());
        model.addAttribute("outCount", products.stream().filter(p -> p.getStock() <= 0).count());
        return "products";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        Product p=new Product();p.setMinStock(settings.findById(1L).map(pe.edu.utp.sigpi.model.CompanySetting::getDefaultMinStock).orElse(10));model.addAttribute("product",p);
        model.addAttribute("categories", categoryRepository.findAll());
        return "product-form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", productRepository.findById(id).orElseThrow());
        model.addAttribute("categories", categoryRepository.findAll());
        return "product-form";
    }

    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/save")
    public String save(@RequestParam(required=false) Long id, @RequestParam String code, @RequestParam String name,
                       @RequestParam(required=false, defaultValue="") String description, @RequestParam Long categoryId,
                       @RequestParam String unit, @RequestParam BigDecimal purchasePrice, @RequestParam BigDecimal salePrice,
                       @RequestParam int stock, @RequestParam int minStock,
                       @RequestParam(required=false, defaultValue="true") boolean active, RedirectAttributes ra) {
        Product p = id == null ? new Product() : productRepository.lockById(id).orElseThrow();
        if(id!=null && stock!=p.getStock())throw new IllegalArgumentException("Modifica existencias desde Entradas/Salidas de inventario; vuelve a cargar el producto");
        p.setCode(code); p.setName(name); p.setDescription(description); p.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        p.setUnit(unit); p.setPurchasePrice(purchasePrice); p.setSalePrice(salePrice); p.setStock(stock); p.setMinStock(minStock); p.setActive(active);
        productRepository.save(p);
        if(id==null && stock>0) movements.save(new pe.edu.utp.sigpi.model.InventoryMovement(java.time.LocalDate.now(),p,"ENTRADA",stock,stock,"APERTURA", "Alta de producto"));
        ra.addFlashAttribute("success", "Producto guardado correctamente");
        return "redirect:/products";
    }

    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Product p=productRepository.lockById(id).orElseThrow();p.setActive(false);productRepository.save(p);
        ra.addFlashAttribute("success","Producto desactivado. Se conserva su historial.");
        return "redirect:/products";
    }

    @GetMapping("/export") public void export(HttpServletResponse r)throws IOException{var w=pe.edu.utp.sigpi.service.Csv.start(r,"productos_sigpi.csv");pe.edu.utp.sigpi.service.Csv.row(w,"Código","Producto","Categoría","Unidad","Compra","Venta","Stock","Mínimo","Estado");for(Product p:productRepository.findAll())pe.edu.utp.sigpi.service.Csv.row(w,p.getCode(),p.getName(),p.getCategory().getName(),p.getUnit(),p.getPurchasePrice(),p.getSalePrice(),p.getStock(),p.getMinStock(),p.getStockStatus());}
}
