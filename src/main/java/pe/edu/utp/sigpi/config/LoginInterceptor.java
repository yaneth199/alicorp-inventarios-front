package pe.edu.utp.sigpi.config;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.edu.utp.sigpi.repository.AppUserRepository;
import pe.edu.utp.sigpi.model.AppUser;
import java.util.*;
@Component
public class LoginInterceptor implements HandlerInterceptor {
 private final AppUserRepository users;
 public LoginInterceptor(AppUserRepository users){this.users=users;}
 public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler) throws Exception {
  String uri=req.getRequestURI().substring(req.getContextPath().length());
  res.setHeader("X-Content-Type-Options","nosniff");res.setHeader("X-Frame-Options","DENY");res.setHeader("Cache-Control","no-store");
  if(uri.equals("/health/ready")||uri.startsWith("/css/")||uri.startsWith("/js/")||uri.equals("/error"))return true;
  HttpSession session=req.getSession();
  if(session.getAttribute("csrf")==null)session.setAttribute("csrf",UUID.randomUUID().toString());
  if(!Set.of("GET","HEAD","OPTIONS").contains(req.getMethod()) && !session.getAttribute("csrf").equals(req.getParameter("_csrf"))){res.sendError(403);return false;}
  if(uri.equals("/")||uri.equals("/login"))return true;
  AppUser previous=(AppUser)session.getAttribute("user");
  AppUser u=previous==null?null:users.findById(previous.getId()).orElse(null);
  if(u==null||!u.isActive()){session.removeAttribute("user");res.sendRedirect(req.getContextPath()+"/login");return false;}
  session.setAttribute("user",u);
  String role=u.getRole(); String area=uri.split("/").length>1?uri.split("/")[1]:"";
  boolean write=!Set.of("GET","HEAD").contains(req.getMethod())||uri.endsWith("/new")||uri.endsWith("/edit")||uri.equals("/inventory/movement");
  boolean allowed=role.equals("Administrador") || Set.of("profile","logout","dashboard").contains(area);
  if(!allowed && role.equals("Supervisor")) allowed=!write && !Set.of("users","settings").contains(area);
  if(!allowed && role.equals("Ventas")) allowed=Set.of("orders","clients").contains(area)||(!write && Set.of("products","inventory","categories").contains(area));
  if(!allowed && role.equals("Almacén")) allowed=Set.of("inventory","movements","providers","products","categories").contains(area)|| (area.equals("orders")&&(!write||uri.matches("/orders/\\d+/status")));
  if(!allowed){res.sendError(403);return false;}return true;
 }
}
