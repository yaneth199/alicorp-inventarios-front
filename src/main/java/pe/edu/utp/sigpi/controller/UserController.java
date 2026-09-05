package pe.edu.utp.sigpi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.AppUser;
import pe.edu.utp.sigpi.repository.AppUserRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {
    private final AppUserRepository repo; public UserController(AppUserRepository repo){this.repo=repo;}
    @GetMapping public String list(Model m){var users=repo.findAll();m.addAttribute("users",users);Map<String,Long> counts=users.stream().collect(Collectors.groupingBy(AppUser::getRole,Collectors.counting()));m.addAttribute("counts",counts);return "users";}
    @GetMapping("/new") public String form(Model m){m.addAttribute("appUser",new AppUser());return "user-form";}
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){m.addAttribute("appUser",repo.findById(id).orElseThrow());return "user-form";}
    @PostMapping("/save") public String save(AppUser u,RedirectAttributes ra){if(u.getId()!=null){var old=repo.findById(u.getId()).orElseThrow();if(u.getPassword()==null||u.getPassword().isBlank())u.setPassword(old.getPassword());u.setLastAccess(old.getLastAccess());}repo.save(u);ra.addFlashAttribute("success","Usuario guardado");return "redirect:/users";}
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id){repo.deleteById(id);return "redirect:/users";}
}
