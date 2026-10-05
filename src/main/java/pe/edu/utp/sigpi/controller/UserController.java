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
    @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.repository.CompanySettingRepository settings;
    private final AppUserRepository repo; public UserController(AppUserRepository repo){this.repo=repo;}
    @GetMapping public String list(@RequestParam(defaultValue="") String q,Model m){m.addAttribute("q",q);var users=repo.findAll().stream().filter(u->(u.getUsername()+u.getFullName()).toLowerCase().contains(q.toLowerCase())).toList();m.addAttribute("users",users);Map<String,Long> counts=users.stream().collect(Collectors.groupingBy(AppUser::getRole,Collectors.counting()));m.addAttribute("counts",counts);return "users";}
    @GetMapping("/new") public String form(Model m){var u=new AppUser();u.setRole(settings.findById(1L).map(pe.edu.utp.sigpi.model.CompanySetting::getDefaultRole).orElse("Ventas"));m.addAttribute("appUser",u);return "user-form";}
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){m.addAttribute("appUser",repo.findById(id).orElseThrow());return "user-form";}
    @PostMapping("/save") public String save(AppUser u,RedirectAttributes ra, jakarta.servlet.http.HttpSession session){if(!java.util.Set.of("Administrador","Ventas","Almacén","Supervisor").contains(u.getRole()))throw new IllegalArgumentException("Rol inválido");
    var current=(AppUser)session.getAttribute("user");
    if(current.getId().equals(u.getId()) && (!u.isActive() || !u.getRole().equals("Administrador")))throw new IllegalArgumentException("No puedes desactivar ni quitar tu propio acceso administrativo");
    if(u.getPassword()!=null&&!u.getPassword().isBlank())u.setPassword(pe.edu.utp.sigpi.service.Passwords.hash(u.getPassword()));
    if(u.getId()==null && (u.getPassword()==null||u.getPassword().isBlank()))throw new IllegalArgumentException("Contraseña obligatoria");
    if(u.getId()!=null){var old=repo.findById(u.getId()).orElseThrow();if(u.getPassword()==null||u.getPassword().isBlank())u.setPassword(old.getPassword());u.setLastAccess(old.getLastAccess());}repo.save(u);ra.addFlashAttribute("success","Usuario guardado");return "redirect:/users";}
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, jakarta.servlet.http.HttpSession session){if(((AppUser)session.getAttribute("user")).getId().equals(id))throw new IllegalArgumentException("No puedes eliminar tu propio usuario");repo.deleteById(id);return "redirect:/users";}
}
