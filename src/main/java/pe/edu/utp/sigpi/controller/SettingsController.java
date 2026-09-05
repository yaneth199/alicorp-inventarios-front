package pe.edu.utp.sigpi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.CompanySetting;
import pe.edu.utp.sigpi.repository.CompanySettingRepository;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    private final CompanySettingRepository repo; public SettingsController(CompanySettingRepository repo){this.repo=repo;}
    @GetMapping public String page(Model m){m.addAttribute("setting",repo.findById(1L).orElseGet(CompanySetting::new));return "settings";}
    @PostMapping public String save(CompanySetting setting,RedirectAttributes ra){setting.setId(1L);repo.save(setting);ra.addFlashAttribute("success","Configuración guardada correctamente");return "redirect:/settings";}
}
