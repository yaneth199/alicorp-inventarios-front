# Matriz funcional — Informe APF1

Correspondencia de la implementación con los requisitos del informe. No constituye un acta de pruebas aprobadas; el estado de ejecución está en VERIFICACION.md.

| Requisito | Función | Implementación |
|---|---|---|
| RF01 | Autenticación | Login, sesión, perfil y cierre POST |
| RF02 | Usuarios | Altas, edición, búsqueda y estado |
| RF03 | Roles | Administrador, Almacén, Ventas y Supervisor; validación servidor |
| RF04 | Productos | Registro con código, categoría, unidad y precios |
| RF05 | Búsqueda de productos | Código, nombre, categoría y estado |
| RF06 | Actualizar productos | Formulario con validaciones |
| RF07 | Baja lógica | Desactivación, conservación de historial |
| RF08 | Entradas y salidas | Movimiento con cantidad, documento y proveedor opcional |
| RF09 | Stock | Existencias y estados calculados |
| RF10 | Registrar pedido | Cliente, múltiples líneas, total, borrador y transacción |
| RF11 | Buscar pedidos | Número, cliente, fechas y estado |
| RF12 | Estados | Pendiente, En proceso, Preparado, Enviado, Entregado, Cancelado |
| RF13 | Historial | Estado, fecha/hora, actor y nota en detalle |
| RF14 | Stock automático | Descuento atómico, bloqueo y devolución al cancelar |
| RF15 | Inventario | Inventario general, bajo stock y agotados |
| RF16 | Reportes de pedidos | Fecha, cliente, estado y productos solicitados |
| RF17 | Dashboard | Seis KPI, tres gráficos, alertas y últimos pedidos |
| RF18 | Exportación | PDF y XLSX con filtros |
| RF19 | Movimientos | Fecha, responsable, cantidad y saldo |
| RF20 | Alertas | Umbral por producto y alertas configurables |

## Subpaneles originales

**Reportes:** Inventario, Stock bajo, Agotados, Entradas, Salidas, Pedidos por fecha, Pedidos por cliente, Pedidos por estado, Productos más solicitados. Cada opción tiene tabla y exportación; los reportes de pedidos incluyen gráficos de evolución.

**Configuración:** Información de empresa, Parámetros de inventario, Configuración de usuarios, Copias de seguridad y Datos del sistema. El mínimo es el valor sugerido para nuevos productos; no altera mínimos ya personalizados. La duración de sesión se aplica a nuevos inicios de sesión.

**Menú principal:** Dashboard, Productos, Categorías, Inventario, Pedidos, Clientes, Proveedores, Movimientos, Reportes, Usuarios, Configuración. Mantiene el orden del ZIP original. Las opciones visibles dependen del rol.

Se conserva En proceso y Preparado del avance y se agrega Enviado del informe. Para órdenes anteriores a esta versión se registra únicamente el estado conocido durante la migración. Los datos demostrativos sí incluyen todas sus transiciones.
