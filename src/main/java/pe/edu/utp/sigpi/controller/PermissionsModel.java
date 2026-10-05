package pe.edu.utp.sigpi.controller;
import org.springframework.web.bind.annotation.*;import jakarta.servlet.http.HttpSession;import pe.edu.utp.sigpi.model.AppUser;import java.util.Set;
@ControllerAdvice
public class PermissionsModel {
 @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.repository.CompanySettingRepository settings;
 @org.springframework.beans.factory.annotation.Autowired private pe.edu.utp.sigpi.repository.ProductRepository products;
 @ModelAttribute("companyLabel") public String company(){var c=settings.findById(1L).orElseGet(pe.edu.utp.sigpi.model.CompanySetting::new);return (c.getBusinessName()==null?"Alicorp S.A.C.":c.getBusinessName())+" — "+(c.getBranch()==null?"Arequipa":c.getBranch());}
 @ModelAttribute("stockAlertCount") public long alerts(HttpSession s){if(s.getAttribute("user")==null)return 0;var c=settings.findById(1L).orElseGet(pe.edu.utp.sigpi.model.CompanySetting::new);return c.isLowStockAlerts()?products.findAll().stream().filter(p->p.isActive()&&p.getStock()<=p.getMinStock()).count():0;}

 private boolean role(HttpSession s,String... roles){var u=(AppUser)s.getAttribute("user");return u!=null&&Set.of(roles).contains(u.getRole());}
 @ModelAttribute("canManageCatalog")public boolean catalog(HttpSession s){return role(s,"Administrador","Almacén");}
 @ModelAttribute("canSell")public boolean sales(HttpSession s){return role(s,"Administrador","Ventas");}
 @ModelAttribute("canAdvanceOrders")public boolean orders(HttpSession s){return role(s,"Administrador","Ventas","Almacén");}
}
