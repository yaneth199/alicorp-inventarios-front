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
    @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.repository.CompanySettingRepository settings;
    private final AppUserRepository userRepository;

    public AuthController(AppUserRepository userRepository) { this.userRepository = userRepository; }

    @GetMapping("/")
    public String root(HttpSession session) {
        return session.getAttribute("user") == null ? "redirect:/login" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session, jakarta.servlet.http.HttpServletRequest request) {
        AppUser user = userRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null || !user.isActive() || !pe.edu.utp.sigpi.service.Passwords.matches(password,user.getPassword())) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            model.addAttribute("username", username);
            return "login";
        }
        user.setLastAccess(LocalDateTime.now());
        userRepository.save(user);
        request.changeSessionId();
        session.setMaxInactiveInterval(60*settings.findById(1L).map(pe.edu.utp.sigpi.model.CompanySetting::getSessionMinutes).orElse(30));
        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
