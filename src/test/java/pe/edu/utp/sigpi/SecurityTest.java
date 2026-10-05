package pe.edu.utp.sigpi;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
import pe.edu.utp.sigpi.config.LoginInterceptor;import pe.edu.utp.sigpi.repository.AppUserRepository;import pe.edu.utp.sigpi.model.AppUser;import pe.edu.utp.sigpi.service.*;
import org.springframework.mock.web.*;import java.util.Optional;
class SecurityTest {
 @Test void passwordHashUsesSaltAndRejectsWrongPassword(){String a=Passwords.hash("ClavePrueba123"),b=Passwords.hash("ClavePrueba123");assertNotEquals(a,b);assertTrue(Passwords.matches("ClavePrueba123",a));assertFalse(Passwords.matches("incorrecta",a));assertFalse(Passwords.matches("x","texto"));}
 @Test void warehouseCannotTypeAdminUrl()throws Exception {var repo=mock(AppUserRepository.class);var u=new AppUser("Almacén","almacen","","","Almacén",true);u.setId(1L);when(repo.findById(1L)).thenReturn(Optional.of(u));var req=new MockHttpServletRequest("GET","/users");req.getSession().setAttribute("user",u);var res=new MockHttpServletResponse();assertFalse(new LoginInterceptor(repo).preHandle(req,res,new Object()));assertEquals(403,res.getStatus());}
 @Test void postWithoutCsrfIsRejectedEvenAtLogin()throws Exception {var req=new MockHttpServletRequest("POST","/login");var res=new MockHttpServletResponse();assertFalse(new LoginInterceptor(mock(AppUserRepository.class)).preHandle(req,res,new Object()));assertEquals(403,res.getStatus());}
 @Test void csvEscapesQuotesAndFormulaPrefix(){assertEquals("\"a\"\"b\"",Csv.cell("a\"b"));assertEquals("\"'=1+1\"",Csv.cell("=1+1"));}
}
