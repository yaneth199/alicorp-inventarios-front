-- Los datos demostrativos se cargan mediante DataSeeder.java.
-- Habilitar SIGPI_DEMO=true (Docker ya lo usa por defecto).
-- Lote APF1-2026-V3: 24 productos, 8 categorías, 20 clientes,
-- 8 proveedores y 64 pedidos, con movimientos e historial.
-- No se requieren INSERT manuales. El lote se aplica una vez.
SELECT code, created_at FROM demo_batches;
