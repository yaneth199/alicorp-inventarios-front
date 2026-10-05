package pe.edu.utp.sigpi;
import org.junit.jupiter.api.*;import static org.junit.jupiter.api.Assertions.*;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.test.web.servlet.MockMvc;import org.springframework.mock.web.MockHttpSession;import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.sigpi.model.*;import pe.edu.utp.sigpi.repository.*;import pe.edu.utp.sigpi.service.*;import java.util.*;import java.math.BigDecimal;
@SpringBootTest(properties={"sigpi.demo=false","sigpi.admin-password=PruebaTemporal123"}) @AutoConfigureMockMvc
class PostgresIT {
 @Autowired pe.edu.utp.sigpi.config.DataSeeder seeder;
 @Autowired MockMvc mvc;@Autowired AppUserRepository users;@Autowired CategoryRepository categories;@Autowired ProductRepository products;@Autowired ClientRepository clients;@Autowired SalesOrderRepository orders;@Autowired OrderService service;@Autowired JdbcTemplate jdbc;
 @Test void renderPagesAndLoginWithRealDatabase()throws Exception {
  var login=mvc.perform(get("/login")).andExpect(status().isOk()).andReturn();var session=(MockHttpSession)login.getRequest().getSession();
  mvc.perform(post("/login").session(session).param("_csrf",session.getAttribute("csrf").toString()).param("username","admin").param("password","PruebaTemporal123")).andExpect(status().is3xxRedirection());
  for(String path:List.of("/dashboard","/products","/products/new","/categories","/categories/new","/inventory","/inventory/movement?type=ENTRADA","/orders","/orders/new","/clients","/clients/new","/providers","/providers/new","/movements","/reports","/reports?type=inventory","/reports?type=entries","/users","/users/new","/settings","/profile"))mvc.perform(get(path).session(session)).andExpect(status().isOk());
 }
 @Test void rollbackWholeOrderAndPersistCancellation(){
  String code=UUID.randomUUID().toString();var cat=categories.save(new Category(code,"Prueba","",true));var p=products.save(new Product(code,"Prueba",cat,"Unidad",BigDecimal.ONE,new BigDecimal("7.50"),10,1));var p2=products.save(new Product(code+"2","Sin stock",cat,"Unidad",BigDecimal.ONE,BigDecimal.TEN,0,1));var c=clients.save(new Client(code,"20000000002","Cliente IT","","","",true));
  assertThrows(IllegalArgumentException.class,()->service.create(c.getId(),List.of(p.getId(),p2.getId()),List.of(2,1),"Prueba"));assertEquals(10,products.findById(p.getId()).orElseThrow().getStock());
  var o=service.create(c.getId(),List.of(p.getId()),List.of(3),"Prueba");assertEquals(7,products.findById(p.getId()).orElseThrow().getStock());service.updateStatus(o.getId(),"Cancelado");service.updateStatus(o.getId(),"Cancelado");assertEquals(10,products.findById(p.getId()).orElseThrow().getStock());
 }

 @Test void allReportAndSettingsTabsAndNativeExports()throws Exception{
  var session=new MockHttpSession();session.setAttribute("csrf","test-token");session.setAttribute("user",users.findByUsernameIgnoreCase("admin").orElseThrow());
  for(String type:ReportsService.TYPES.keySet()){
   mvc.perform(get("/reports").param("type",type).session(session)).andExpect(status().isOk());
   var excel=mvc.perform(get("/reports/export/xlsx").param("type",type).session(session)).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();assertEquals('P',excel[0]);assertEquals('K',excel[1]);
   var pdf=mvc.perform(get("/reports/export/pdf").param("type",type).session(session)).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();assertTrue(new String(pdf,java.nio.charset.StandardCharsets.ISO_8859_1).startsWith("%PDF-"));
  }
  for(String tab:List.of("company","inventory","users","backup","system"))mvc.perform(get("/settings").param("tab",tab).session(session)).andExpect(status().isOk());
  mvc.perform(post("/settings").param("tab","inventory").param("defaultMinStock","25").param("lowStockAlerts","true").param("_csrf","test-token").session(session)).andExpect(status().is3xxRedirection());
  assertEquals(25,jdbc.queryForObject("SELECT default_min_stock FROM company_settings WHERE id=1",Integer.class));
 }
 @Test void draftsSurviveSessionAndKeepStock()throws Exception{
  var admin=users.findByUsernameIgnoreCase("admin").orElseThrow();String code=UUID.randomUUID().toString();var cat=categories.save(new Category(code,"Borrador","",true));var p=products.save(new Product(code,"Borrador",cat,"Unidad",BigDecimal.ONE,BigDecimal.TEN,8,1));
  var session=new MockHttpSession();session.setAttribute("csrf","draft-token");session.setAttribute("user",admin);
  mvc.perform(post("/orders/draft").session(session).param("_csrf","draft-token").param("productIds",p.getId().toString()).param("quantities","2")).andExpect(status().is3xxRedirection());
  var nextSession=new MockHttpSession();nextSession.setAttribute("user",admin);mvc.perform(get("/orders/new").session(nextSession)).andExpect(status().isOk());assertEquals(8,products.findById(p.getId()).orElseThrow().getStock());assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM order_drafts WHERE user_id=?",Integer.class,admin.getId()));
 }
 @Test void concurrentOrdersCannotOversell()throws Exception{
  String code=UUID.randomUUID().toString();var cat=categories.save(new Category(code,"Concurrencia","",true));var p=products.save(new Product(code,"Concurrente",cat,"Unidad",BigDecimal.ONE,BigDecimal.TEN,4,1));var c=clients.save(new Client(code,"80000000001","Cliente concurrencia","","","",true));
  var latch=new java.util.concurrent.CountDownLatch(1);var executor=java.util.concurrent.Executors.newFixedThreadPool(2);
  try{java.util.concurrent.Callable<Boolean> attempt=()->{latch.await();try{service.create(c.getId(),List.of(p.getId()),List.of(3),"Prueba");return true;}catch(IllegalArgumentException e){return false;}};var one=executor.submit(attempt);var two=executor.submit(attempt);latch.countDown();int success=(one.get(20,java.util.concurrent.TimeUnit.SECONDS)?1:0)+(two.get(20,java.util.concurrent.TimeUnit.SECONDS)?1:0);assertEquals(1,success);assertEquals(1,products.findById(p.getId()).orElseThrow().getStock());}finally{executor.shutdownNow();}
 }
}
