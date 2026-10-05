package pe.edu.utp.sigpi.controller;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.Provider;
import pe.edu.utp.sigpi.repository.ProviderRepository;
import java.io.IOException;

@Controller @RequestMapping("/providers")
public class ProviderController {
    private final ProviderRepository repo; public ProviderController(ProviderRepository repo){this.repo=repo;}
    @GetMapping public String list(@RequestParam(defaultValue="") String q,Model m){m.addAttribute("providers",q.isBlank()?repo.findAll():repo.findByBusinessNameContainingIgnoreCaseOrRucContainingIgnoreCase(q,q));m.addAttribute("q",q);return "providers";}
    @GetMapping("/new") public String form(Model m){m.addAttribute("provider",new Provider());return "provider-form";}
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){m.addAttribute("provider",repo.findById(id).orElseThrow());return "provider-form";}
    @PostMapping("/save") public String save(Provider p,RedirectAttributes ra){repo.save(p);ra.addFlashAttribute("success","Proveedor guardado");return "redirect:/providers";}
    @PostMapping("/{id}/toggle") public String toggle(@PathVariable Long id){Provider p=repo.findById(id).orElseThrow();p.setActive(!p.isActive());repo.save(p);return "redirect:/providers";}
    @GetMapping("/export") public void export(HttpServletResponse r)throws IOException{var w=pe.edu.utp.sigpi.service.Csv.start(r,"proveedores_sigpi.csv");pe.edu.utp.sigpi.service.Csv.row(w,"Código","RUC","Razón social","Contacto","Teléfono","Correo","Estado");for(Provider p:repo.findAll())pe.edu.utp.sigpi.service.Csv.row(w,p.getCode(),p.getRuc(),p.getBusinessName(),p.getContact(),p.getPhone(),p.getEmail(),p.isActive()?"Activo":"Inactivo");}
}
