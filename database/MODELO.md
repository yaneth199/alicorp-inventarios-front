# Modelo PostgreSQL

El esquema canónico es `src/main/resources/db/schema.sql`; `database/01_esquema.sql` contiene la misma definición. Se aplica al arranque de Spring Boot y luego Hibernate valida entidades/tablas. Se usa PostgreSQL también en las pruebas de integración.

| Tabla | Propósito y relaciones |
|---|---|
| app_users | Usuarios, roles, contraseña derivada, estado |
| categories | Categorías de productos |
| products | Producto, categoría, precios, stock, mínimo y estado lógico |
| clients | Clientes identificados por documento |
| providers | Proveedores para entradas |
| sales_orders | Cabecera, cliente, responsable, fecha, estado y total |
| order_items | Detalle de pedido, producto, cantidad y precio histórico |
| inventory_movements | Producto, entrada/salida, cantidad, saldo, actor, documento y proveedor opcional |
| company_settings | Empresa, mínimo sugerido, alertas, sesión y rol inicial |
| order_status_events | Pedido, estado, fecha/hora, responsable y nota |
| order_drafts | Borrador persistente por usuario; no afecta stock |
| demo_batches | Marca de lote de demostración aplicado una sola vez |

Las FK conservan relaciones y las restricciones impiden cantidades o precios inválidos. Pedidos y ajustes usan transacciones y bloqueo pesimista de productos; las líneas se bloquean en orden de ID. Los estados se validan en servicio; cancelar devuelve stock una sola vez. La secuencia `sales_order_code_seq` evita numeraciones simultáneas iguales.

El stock se descuenta al registrar pedido, no al enviarlo. Cambiar de estado no vuelve a descontarlo. Desactivar producto conserva pedidos y movimientos anteriores. Los reportes de inventario muestran existencias actuales; no reconstruyen cortes históricos por fecha.

La semilla Java crea coherentemente movimientos y estados usando el servicio de pedidos. Se habilita con SIGPI_DEMO=true. No ejecutar un segundo script de inserciones manuales. La marca APF1-2026-V3 evita repetir el lote. Los registros anteriores permanecen intactos.

Para respaldar, usa RESPALDAR.bat (pg_dump) o Configuración → Copias de seguridad (instantánea SQL transaccional). Restaura en una base vacía. Los respaldos contienen datos y hashes de contraseña: consérvalos privados.
