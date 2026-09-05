package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.AppUser;
import pe.edu.utp.sigpi.repository.AppUserRepository;

@Controller
public class ProfileController {
    private final AppUserRepository repo;
    public ProfileController(AppUserRepository repo){this.repo=repo;}
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){model.addAttribute("profile", session.getAttribute("user")); return "profile";}
    @PostMapping("/profile")
    public String save(@RequestParam String fullName,@RequestParam String email,@RequestParam(required=false) String password,HttpSession session,RedirectAttributes ra){
        AppUser current=(AppUser)session.getAttribute("user"); AppUser u=repo.findById(current.getId()).orElseThrow();
        u.setFullName(fullName);u.setEmail(email);if(password!=null&&!password.isBlank())u.setPassword(password);repo.save(u);session.setAttribute("user",u);ra.addFlashAttribute("success","Perfil actualizado");return "redirect:/profile";
    }
}
