package pe.edu.utp.sigpi.controller;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.sigpi.model.Client;
import pe.edu.utp.sigpi.repository.ClientRepository;
import java.io.IOException;

@Controller @RequestMapping("/clients")
public class ClientController {
    private final ClientRepository repo; public ClientController(ClientRepository repo){this.repo=repo;}
    @GetMapping public String list(@RequestParam(defaultValue="") String q, Model m){m.addAttribute("clients", q.isBlank()?repo.findAll():repo.findByNameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCase(q,q));m.addAttribute("q",q);return "clients";}
    @GetMapping("/new") public String form(Model m){m.addAttribute("client",new Client());return "client-form";}
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){m.addAttribute("client",repo.findById(id).orElseThrow());return "client-form";}
    @PostMapping("/save") public String save(Client client, RedirectAttributes ra){repo.save(client);ra.addFlashAttribute("success","Cliente guardado correctamente");return "redirect:/clients";}
    @GetMapping("/export") public void export(HttpServletResponse r)throws IOException{r.setContentType("text/csv; charset=UTF-8");r.setHeader("Content-Disposition","attachment; filename=clientes_sigpi.csv");r.getWriter().write("Código,Documento,Nombre,Dirección,Teléfono,Correo,Estado\n");for(Client c:repo.findAll())r.getWriter().printf("%s,%s,\"%s\",\"%s\",%s,%s,%s%n",c.getCode(),c.getDocumentNumber(),c.getName(),c.getAddress(),c.getPhone(),c.getEmail(),c.isActive()?"Activo":"Inactivo");}
}
