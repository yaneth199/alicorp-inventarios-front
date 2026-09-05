package pe.edu.utp.sigpi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.Category;
import pe.edu.utp.sigpi.repository.CategoryRepository;
import pe.edu.utp.sigpi.repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    public CategoryController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository; this.productRepository = productRepository;
    }
    @GetMapping
    public String list(Model model) {
        var categories = categoryRepository.findAll();
        Map<Long, Long> counts = new HashMap<>();
        for (Category c : categories) counts.put(c.getId(), productRepository.findAll().stream().filter(p -> p.getCategory().getId().equals(c.getId())).count());
        model.addAttribute("categories", categories); model.addAttribute("counts", counts); return "categories";
    }
    @GetMapping("/new") public String newForm(Model m) { m.addAttribute("category", new Category()); return "category-form"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id, Model m) { m.addAttribute("category", categoryRepository.findById(id).orElseThrow()); return "category-form"; }
    @PostMapping("/save")
    public String save(Category category, RedirectAttributes ra) { categoryRepository.save(category); ra.addFlashAttribute("success", "Categoría guardada"); return "redirect:/categories"; }
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) { Category c=categoryRepository.findById(id).orElseThrow(); c.setActive(!c.isActive()); categoryRepository.save(c); return "redirect:/categories"; }
}
