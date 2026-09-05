package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sigpi.model.AppUser;
import pe.edu.utp.sigpi.repository.AppUserRepository;

import java.time.LocalDateTime;

@Controller
public class AuthController {
    private final AppUserRepository userRepository;

    public AuthController(AppUserRepository userRepository) { this.userRepository = userRepository; }

    @GetMapping("/")
    public String root(HttpSession session) {
        return session.getAttribute("user") == null ? "redirect:/login" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        AppUser user = userRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null || !user.isActive() || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            model.addAttribute("username", username);
            return "login";
        }
        user.setLastAccess(LocalDateTime.now());
        userRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
