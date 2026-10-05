package pe.edu.utp.sigpi.controller;
import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.mvc.support.RedirectAttributes;import org.springframework.http.*;
import pe.edu.utp.sigpi.model.CompanySetting;import pe.edu.utp.sigpi.repository.*;import pe.edu.utp.sigpi.service.BackupService;
@Controller @RequestMapping("/settings")
public class SettingsController {
 private final CompanySettingRepository settings;private final AppUserRepository users;private final ProductRepository products;private final SalesOrderRepository orders;private final BackupService backup;
 public SettingsController(CompanySettingRepository s,AppUserRepository u,ProductRepository p,SalesOrderRepository o,BackupService b){settings=s;users=u;products=p;orders=o;backup=b;}
 @GetMapping public String page(@RequestParam(defaultValue="company")String tab,Model m){if(!java.util.Set.of("company","inventory","users","backup","system").contains(tab))throw new IllegalArgumentException("Sección no válida");m.addAttribute("tab",tab);m.addAttribute("setting",settings.findById(1L).orElseGet(CompanySetting::new));m.addAttribute("userCount",users.count());m.addAttribute("productCount",products.count());m.addAttribute("orderCount",orders.count());m.addAttribute("javaVersion",System.getProperty("java.version"));return "settings";}
 @PostMapping public String save(@RequestParam(defaultValue="company")String tab,CompanySetting form,RedirectAttributes ra){var s=settings.findById(1L).orElseGet(CompanySetting::new);
  switch(tab){case "company":if(form.getBusinessName()==null||form.getBusinessName().isBlank())throw new IllegalArgumentException("Razón social obligatoria");s.setBusinessName(form.getBusinessName());s.setRuc(form.getRuc());s.setAddress(form.getAddress());s.setBranch(form.getBranch());s.setPhone(form.getPhone());s.setEmail(form.getEmail());break;
   case "inventory":if(form.getDefaultMinStock()<0)throw new IllegalArgumentException("Stock mínimo inválido");s.setDefaultMinStock(form.getDefaultMinStock());s.setLowStockAlerts(form.isLowStockAlerts());break;
   case "users":if(form.getSessionMinutes()<5||form.getSessionMinutes()>480||!java.util.Set.of("Ventas","Almacén","Supervisor").contains(form.getDefaultRole()))throw new IllegalArgumentException("Sesión entre 5 y 480 minutos y rol válido");s.setSessionMinutes(form.getSessionMinutes());s.setDefaultRole(form.getDefaultRole());break;
   case "system":if(form.getSystemName()==null||form.getSystemName().isBlank())throw new IllegalArgumentException("Nombre del sistema obligatorio");s.setSystemName(form.getSystemName());break;
   default:throw new IllegalArgumentException("Sección no válida");}
  settings.save(s);ra.addFlashAttribute("success","Configuración guardada");return "redirect:/settings?tab="+tab;
 }
 @PostMapping("/backup") public ResponseEntity<byte[]> backup()throws java.io.IOException{return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=sigpi_"+java.time.LocalDate.now()+".sql").contentType(MediaType.parseMediaType("application/sql;charset=UTF-8")).body(backup.snapshot());}
}
