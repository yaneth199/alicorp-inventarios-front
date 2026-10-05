package pe.edu.utp.sigpi;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.jdbc.core.JdbcTemplate;import pe.edu.utp.sigpi.config.DataSeeder;
@SpringBootTest(properties={"sigpi.demo=true","sigpi.admin-password=PruebaTemporal123"})
class DemoIT {
 @Autowired JdbcTemplate jdbc;@Autowired DataSeeder seeder;
 @Test void demoIsCompleteIdempotentAndStockReconciles()throws Exception{
  assertEquals(24,jdbc.queryForObject("SELECT count(*) FROM products WHERE code LIKE 'DEM-PRD-%'",Integer.class));
  assertEquals(20,jdbc.queryForObject("SELECT count(*) FROM clients WHERE code LIKE 'DEM-CLI-%'",Integer.class));
  assertEquals(64,jdbc.queryForObject("SELECT count(*) FROM sales_orders WHERE responsible='Ventas Demo'",Integer.class));
  assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM products p WHERE p.code LIKE 'DEM-PRD-%' AND p.stock <> (SELECT COALESCE(sum(CASE WHEN m.type='ENTRADA' THEN m.quantity ELSE -m.quantity END),0) FROM inventory_movements m WHERE m.product_id=p.id)",Integer.class));
  assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM sales_orders o WHERE o.responsible='Ventas Demo' AND o.status <> (SELECT h.status FROM order_status_events h WHERE h.order_id=o.id ORDER BY h.changed_at DESC,h.id DESC LIMIT 1)",Integer.class));
  seeder.run();assertEquals(64,jdbc.queryForObject("SELECT count(*) FROM sales_orders WHERE responsible='Ventas Demo'",Integer.class));
 }
}
