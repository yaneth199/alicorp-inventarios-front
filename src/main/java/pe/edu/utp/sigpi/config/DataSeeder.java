package pe.edu.utp.sigpi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.utp.sigpi.model.*;
import pe.edu.utp.sigpi.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final ProviderRepository providerRepository;
    private final AppUserRepository userRepository;
    private final MovementRepository movementRepository;
    private final SalesOrderRepository orderRepository;
    private final CompanySettingRepository settingRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository,
                      ClientRepository clientRepository, ProviderRepository providerRepository,
                      AppUserRepository userRepository, MovementRepository movementRepository,
                      SalesOrderRepository orderRepository, CompanySettingRepository settingRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.movementRepository = movementRepository;
        this.orderRepository = orderRepository;
        this.settingRepository = settingRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        Category aceites = categoryRepository.save(new Category("CAT-001", "Aceites", "Aceites comestibles y para cocinar", true));
        Category pastas = categoryRepository.save(new Category("CAT-002", "Pastas", "Fideos, tallarines y similares", true));
        Category limpieza = categoryRepository.save(new Category("CAT-003", "Limpieza", "Detergentes, jabones y limpiadores", true));
        Category grasas = categoryRepository.save(new Category("CAT-004", "Grasas", "Manteca, margarina y similares", true));
        Category granos = categoryRepository.save(new Category("CAT-005", "Granos", "Arroz, menestras y cereales", true));
        Category cereales = categoryRepository.save(new Category("CAT-006", "Cereales", "Avenas, granolas y cereales", true));
        Category lacteos = categoryRepository.save(new Category("CAT-007", "Lácteos", "Leche, yogur y derivados", true));
        categoryRepository.save(new Category("CAT-008", "Snacks", "Galletas, papas fritas y similares", false));

        Product p1 = productRepository.save(new Product("PRD-001", "Aceite Primor 1L", aceites, "Unidad", bd("5.20"), bd("7.50"), 342, 50));
        Product p2 = productRepository.save(new Product("PRD-002", "Fideos Don Vittorio 500g", pastas, "Paquete", bd("2.10"), bd("3.20"), 18, 30));
        Product p3 = productRepository.save(new Product("PRD-003", "Detergente Ace 360g", limpieza, "Bolsa", bd("3.80"), bd("5.90"), 0, 20));
        Product p4 = productRepository.save(new Product("PRD-004", "Manteca Famosa 500g", grasas, "Bloque", bd("4.50"), bd("6.80"), 127, 40));
        Product p5 = productRepository.save(new Product("PRD-005", "Arroz Costeño 5kg", granos, "Bolsa", bd("12.00"), bd("17.50"), 89, 60));
        Product p6 = productRepository.save(new Product("PRD-006", "Avena 3 Ositos 180g", cereales, "Sobre", bd("1.80"), bd("2.90"), 12, 25));
        Product p7 = productRepository.save(new Product("PRD-007", "Leche Gloria 400g", lacteos, "Tarro", bd("3.60"), bd("5.20"), 210, 80));
        Product p8 = productRepository.save(new Product("PRD-008", "Jabón Bolívar 230g", limpieza, "Barra", bd("1.50"), bd("2.30"), 0, 30));

        Client c1 = clientRepository.save(new Client("CLI-001", "20452678901", "Supermercados La Colina SAC", "Av. Ejército 801, Cayma", "054-271234", "compras@lacolinal.pe", true));
        Client c2 = clientRepository.save(new Client("CLI-002", "10234567890", "Bodega El Buen Precio", "Jr. Mercaderes 412, Cercado", "054-222345", "buenprecio@gmail.com", true));
        Client c3 = clientRepository.save(new Client("CLI-003", "20601234567", "Minimarket Los Andes", "Calle Palacio Viejo 203, Cercado", "054-233456", "losandes@hotmail.com", true));
        Client c4 = clientRepository.save(new Client("CLI-004", "20504987654", "Distribuidora Del Sur EIRL", "Av. Aviación 1520, Paucarpata", "054-244567", "delsur@empresa.pe", true));
        Client c5 = clientRepository.save(new Client("CLI-005", "10345678901", "Tienda San Martín", "Calle San Martín 780, Miraflores", "054-255678", "", false));

        providerRepository.save(new Provider("PRV-001", "20100190346", "Proveedora Andina SAC", "Ing. Roberto Salas", "054-288990", "ventas@andina.pe"));
        providerRepository.save(new Provider("PRV-002", "20501234567", "Distribuidora Lima Norte SRL", "Sra. Carmen Vega", "01-4561234", "carmen@limanorte.com"));
        providerRepository.save(new Provider("PRV-003", "20604321987", "Suministros Arequipa SRL", "Sr. Hugo Medina", "054-299001", "hmedina@sumarequipa.pe"));

        AppUser u1 = new AppUser("Juan Carlos Quispe Mamani", "admin", "admin123", "j.quispe@sigpi.pe", "Administrador", true);
        u1.setLastAccess(LocalDateTime.of(2026, 8, 27, 8, 42)); userRepository.save(u1);
        AppUser u2 = new AppUser("María Elena Condori Vargas", "mcondori", "ventas123", "m.condori@sigpi.pe", "Ventas", true);
        u2.setLastAccess(LocalDateTime.of(2026, 8, 27, 9, 15)); userRepository.save(u2);
        AppUser u3 = new AppUser("Carlos Alberto Flores Ramos", "cflores", "almacen123", "c.flores@sigpi.pe", "Almacén", true);
        u3.setLastAccess(LocalDateTime.of(2026, 8, 26, 17, 30)); userRepository.save(u3);
        AppUser u4 = new AppUser("Ana Lucía Paredes Torres", "aparedes", "super123", "a.paredes@sigpi.pe", "Supervisor", true);
        u4.setLastAccess(LocalDateTime.of(2026, 8, 25, 11, 0)); userRepository.save(u4);
        AppUser u5 = new AppUser("Luis Miguel Huanca Castro", "lhuanca", "almacen123", "l.huanca@sigpi.pe", "Almacén", false);
        u5.setLastAccess(LocalDateTime.of(2026, 8, 10, 9, 0)); userRepository.save(u5);

        seedOrder("PED-2026-001", c1, LocalDate.of(2026,8,27), "Entregado", "Juan Quispe", p1, 8);
        seedOrder("PED-2026-002", c2, LocalDate.of(2026,8,27), "En proceso", "María Condori", p2, 4);
        seedOrder("PED-2026-003", c3, LocalDate.of(2026,8,26), "Pendiente", "Carlos Flores", p5, 12);
        seedOrder("PED-2026-004", c4, LocalDate.of(2026,8,26), "Preparado", "Juan Quispe", p4, 6);
        seedOrder("PED-2026-005", c2, LocalDate.of(2026,8,25), "Entregado", "María Condori", p7, 3);
        seedOrder("PED-2026-006", c5, LocalDate.of(2026,8,25), "Cancelado", "Carlos Flores", p6, 7);
        seedOrder("PED-2026-007", c1, LocalDate.of(2026,8,24), "Entregado", "Juan Quispe", p1, 15);
        seedOrder("PED-2026-008", c3, LocalDate.of(2026,8,24), "En proceso", "María Condori", p2, 5);

        movementRepository.save(new InventoryMovement(LocalDate.of(2026,8,27), p1, "ENTRADA", 200, 342, "GR-0892", "Juan Quispe"));
        movementRepository.save(new InventoryMovement(LocalDate.of(2026,8,27), p2, "SALIDA", 30, 18, "PED-2026-002", "María Condori"));
        movementRepository.save(new InventoryMovement(LocalDate.of(2026,8,26), p4, "ENTRADA", 100, 127, "GR-0891", "Juan Quispe"));
        movementRepository.save(new InventoryMovement(LocalDate.of(2026,8,26), p3, "SALIDA", 20, 0, "PED-2026-003", "Carlos Flores"));
        movementRepository.save(new InventoryMovement(LocalDate.of(2026,8,25), p5, "AJUSTE", 5, 89, "AJU-0014", "Juan Quispe"));
        movementRepository.save(new InventoryMovement(LocalDate.of(2026,8,25), p7, "ENTRADA", 150, 210, "GR-0890", "María Condori"));

        CompanySetting s = new CompanySetting();
        s.setId(1L); s.setBusinessName("Alicorp S.A.C."); s.setRuc("20100116392");
        s.setAddress("Av. Argentina 4793, Callao"); s.setBranch("Arequipa — Parque Industrial");
        s.setPhone("054-200000"); s.setEmail("arequipa@alicorp.pe"); s.setSystemName("SIGPI"); s.setVersion("1.0.0 — 2026");
        settingRepository.save(s);
    }

    private void seedOrder(String code, Client client, LocalDate date, String status, String responsible, Product product, int qty) {
        SalesOrder o = new SalesOrder();
        o.setCode(code); o.setClient(client); o.setOrderDate(date); o.setStatus(status); o.setResponsible(responsible);
        OrderItem item = new OrderItem(); item.setOrder(o); item.setProduct(product); item.setQuantity(qty);
        item.setPrice(product.getSalePrice()); item.setSubtotal(product.getSalePrice().multiply(BigDecimal.valueOf(qty)));
        o.getItems().add(item);
        // Valor visual similar al prototipo, manteniendo cálculo consistente.
        o.setTotal(item.getSubtotal());
        orderRepository.save(o);
    }

    private BigDecimal bd(String value) { return new BigDecimal(value); }
}
