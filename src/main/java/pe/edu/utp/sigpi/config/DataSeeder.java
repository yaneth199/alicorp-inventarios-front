package pe.edu.utp.sigpi.config;
import org.springframework.boot.CommandLineRunner;import org.springframework.stereotype.Component;import org.springframework.transaction.annotation.Transactional;import org.springframework.beans.factory.annotation.Value;import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.sigpi.model.*;import pe.edu.utp.sigpi.repository.*;import pe.edu.utp.sigpi.service.*;
import java.util.*;import java.time.*;import java.math.BigDecimal;
@Component
public class DataSeeder implements CommandLineRunner {
 private final CategoryRepository categories;private final ProductRepository products;private final ClientRepository clients;private final ProviderRepository providers;private final AppUserRepository users;private final MovementRepository movements;private final CompanySettingRepository settings;private final OrderService orders;private final JdbcTemplate jdbc;
 @Value("${sigpi.demo:false}")private boolean demo;
 @Value("${sigpi.admin-password:}")private String adminPassword;
 public DataSeeder(CategoryRepository c,ProductRepository p,ClientRepository l,ProviderRepository v,AppUserRepository u,MovementRepository m,CompanySettingRepository s,OrderService o,JdbcTemplate jdbc){categories=c;products=p;clients=l;providers=v;users=u;movements=m;settings=s;orders=o;this.jdbc=jdbc;}
 @Override @Transactional public void run(String...args){
  if(users.count()==0)users.save(new AppUser("Administrador SIGPI","admin",Passwords.hash(adminPassword),"admin@example.test","Administrador",true));
  if(settings.count()==0){var s=new CompanySetting();s.setBusinessName("Alicorp S.A.C.");s.setRuc("20000000000");s.setBranch("Arequipa — Parque Industrial");s.setAddress("Dirección de demostración");s.setEmail("contacto@example.test");s.setPhone("054-200000");s.setSystemName("SIGPI");s.setVersion("3.0.0");settings.save(s);}
  if(!demo)return;
  jdbc.execute("SELECT pg_advisory_xact_lock(742319)");
  if(jdbc.queryForObject("SELECT count(*) FROM demo_batches WHERE code='APF1-2026-V3'",Long.class)>0)return;
  String[] catNames={"Aceites","Pastas","Limpieza","Grasas","Granos","Cereales","Salsas","Repostería"};List<Category> cats=new ArrayList<>();
  for(int i=0;i<catNames.length;i++){String code="DEM-CAT-"+(i+1);var c=categories.findAll().stream().filter(x->code.equals(x.getCode())).findFirst().orElseGet(()->new Category(code,"","Datos de demostración",true));c.setName(catNames[i]);cats.add(categories.save(c));}
  String[] names={"Aceite Primor 1L","Aceite Cocinero 1L","Aceite Cil 1L","Fideos Don Vittorio 500g","Fideos Nicolini 500g","Fideos Lavaggi 500g","Detergente Opal 800g","Detergente Bolívar 800g","Jabón Bolívar 230g","Manteca Famosa 500g","Margarina Manty 200g","Margarina Sello de Oro 500g","Arroz Costeño 5kg","Arroz Paisana 5kg","Lentejas de demostración 500g","Avena 3 Ositos 180g","Avena Ángel 150g","Cereal Ángel 250g","Mayonesa Alacena 475g","Salsa de ají Alacena 85g","Salsa de tomate 200g","Harina Blanca Flor 1kg","Premezcla Blanca Flor 500g","Azúcar de demostración 1kg"};
  List<Product> ps=new ArrayList<>();LocalDate start=LocalDate.now().withDayOfMonth(1).minusMonths(7);
  for(int i=0;i<names.length;i++){var price=BigDecimal.valueOf(250+(i%9)*175,2);int stock=i==8?0:1000;var p=new Product(String.format("DEM-PRD-%03d",i+1),names[i],cats.get(i/3),"Unidad",price.multiply(new BigDecimal("0.7")).setScale(2,java.math.RoundingMode.HALF_UP),price,stock,50);p.setDescription("Producto de demostración académica; precios referenciales");p=products.save(p);ps.add(p);if(stock>0)movements.save(new InventoryMovement(start.minusDays(1),p,"ENTRADA",stock,stock,"APERTURA-DEMO", "Administrador SIGPI"));}
  List<Client> cs=new ArrayList<>();String[] businesses={"Bodega El Buen Precio","Comercial La Colina","Minimarket Los Andes","Distribuidora del Sur","Tienda San Martín","Comercial Santa Rosa","Mercado La Unión","Bodega El Sol","Minimarket Cayma","Comercial Misti","Abarrotes San José","Tienda La Esperanza","Distribuidora Arequipeña","Mercadito Yanahuara","Comercial Miraflores","Tienda Las Flores","Minimarket El Puente","Comercial La Familia","Bodega San Pedro","Distribuciones El Portal"};
  for(int i=0;i<businesses.length;i++)cs.add(clients.save(new Client(String.format("DEM-CLI-%03d",i+1),String.format("9000000%04d",i+1),businesses[i],"Dirección de prueba "+(i+1)+", Arequipa","900000"+String.format("%03d",i+1),"cliente"+(i+1)+"@example.test",true)));
  List<Provider> vs=new ArrayList<>();for(int i=0;i<8;i++)vs.add(providers.save(new Provider(String.format("DEM-PRV-%03d",i+1),String.format("9100000%04d",i+1),"Proveedor de demostración "+(i+1),"Contacto "+(i+1),"910000"+String.format("%03d",i+1),"proveedor"+(i+1)+"@example.test")));
  // Existing accounts are never changed. New demo role users initially share the configured admin bootstrap key.
  if(adminPassword!=null&&adminPassword.length()>=8){String[] roles={"Ventas","Almacén","Supervisor"};String[] logins={"ventas.demo","almacen.demo","supervisor.demo"};for(int i=0;i<roles.length;i++)if(users.findByUsernameIgnoreCase(logins[i]).isEmpty())users.save(new AppUser("Usuario demo "+roles[i],logins[i],Passwords.hash(adminPassword),logins[i]+"@example.test",roles[i],true));}
  for(int i=0;i<64;i++){
   int month=i/8,index=i%8;LocalDate date=start.plusMonths(month).withDayOfMonth(1+index*3);if(date.isAfter(LocalDate.now()))date=LocalDate.now();
   int first=i%24;if(first==8)first=9;int second=(i+7)%24;if(second==8)second=10;
   var order=orders.createAt(cs.get(i%cs.size()).getId(),List.of(ps.get(first).getId(),ps.get(second).getId()),List.of(3+i%8,2+i%5),"Ventas Demo",date);
   String desired=List.of("Entregado","Entregado","Enviado","Preparado","En proceso","Pendiente","Cancelado","Entregado").get(index);
   int hour=10;
   if(desired.equals("Cancelado"))orders.updateStatusAt(order.getId(),"Cancelado","Administrador SIGPI",date.atTime(hour,0));
   else for(String status:List.of("En proceso","Preparado","Enviado","Entregado")){if(desired.equals("Pendiente"))break;orders.updateStatusAt(order.getId(),status,"Operador Demo",date.atTime(hour++,0));if(status.equals(desired))break;}
  }
  // Receipts and genuine low-stock examples, each reflected in the movement ledger.
  for(int i=0;i<8;i++){var p=ps.get(i);p.setStock(p.getStock()+100);var m=new InventoryMovement(LocalDate.now(),p,"ENTRADA",100,p.getStock(),"GR-DEMO-"+(i+1),"Almacén Demo");m.setProvider(vs.get(i));movements.save(m);}
  for(int id:List.of(1,5,11,17)){var p=ps.get(id);int target=12+id;if(p.getStock()>target){int qty=p.getStock()-target;p.setStock(target);movements.save(new InventoryMovement(LocalDate.now(),p,"SALIDA",qty,target,"SAL-DEMO-"+id,"Almacén Demo"));}}
  jdbc.update("INSERT INTO demo_batches(code) VALUES ('APF1-2026-V3')");
 }
}
