# SIGPI Alicorp — Pedidos e Inventarios

Continuación del proyecto Java original, conforme al informe APF1 entregado. Conserva la estructura Maven/NetBeans, Spring Boot, Thymeleaf y el diseño del panel original. PostgreSQL es la base de datos.

## Arranque en Windows con Docker

1. Abre Docker Desktop con contenedores Linux.
2. Extrae todo el ZIP y ejecuta `INICIAR.bat`.
3. Cuando termine, abre `http://localhost:8080` (o el puerto de tu `.env`).
4. Consulta la clave inicial con `VER_ACCESO.bat`. Usuario: `admin`.
5. Ejecuta `PROBAR.bat`: compila y prueba contra una base PostgreSQL temporal separada.

La primera ejecución necesita descargar imágenes y dependencias. El código fuente completo permanece en `src/` y se abre con NetBeans seleccionando la carpeta de `pom.xml`.

## Actualizar una instalación anterior

Ejecuta `RESPALDAR.bat` y luego `DETENER.bat` desde tu versión anterior. Extrae esta versión en otra carpeta y copia allí **tu mismo `.env`**. Ejecuta `INICIAR.bat`. El nombre Compose se conserva, por lo que usa el volumen existente en esa PC. No regeneres claves ni elimines el volumen. No arranques simultáneamente dos copias en la misma PC.

La actualización agrega campos, historial, borradores y registro de semilla. Para pedidos anteriores conserva el estado actual y crea un evento que indica que no se conoce su historial previo. No inventa sus transiciones anteriores.

## Paneles y datos

Los once apartados originales están disponibles según el rol: Dashboard, Productos, Categorías, Inventario, Pedidos, Clientes, Proveedores, Movimientos, Reportes, Usuarios y Configuración. Se mantiene el registro interno de pedidos para clientes, con detalle, borrador, estados e historial.

Reportes conserva sus nueve opciones y Configuración sus cinco secciones. Los indicadores y gráficos consultan la base; las tablas tienen filtros y paginación. Las descargas Excel son XLSX y las descargas PDF son documentos reales.

Con `SIGPI_DEMO=true`, se agrega una sola vez un lote ficticio con 24 productos, 8 categorías, 20 clientes, 8 proveedores y 64 pedidos distribuidos en ocho meses. Incluye entradas, salidas, productos agotados, stock bajo y distintos estados. Los códigos DEM distinguen estos registros; las cantidades son adicionales a cualquier dato anterior. La semilla no reemplaza registros del usuario.

Usuarios de demostración: `ventas.demo`, `almacen.demo`, `supervisor.demo`. Al crearlos usan la clave inicial configurada para admin; cambiar esa variable después no restablece cuentas existentes. Los datos son ficticios para presentación académica.

## Guías

- `GUIA_NETBEANS.md`: Java, PostgreSQL y ejecución local.
- `DOCKER_Y_GITHUB.md`: varias PCs, GitHub, imágenes y respaldos.
- `MATRIZ_FUNCIONAL_APF1.md`: correspondencia con los requisitos del informe.
- `VERIFICACION.md`: comprobaciones realizadas y pendientes.
- `database/MODELO.md`: tablas y reglas de integridad.

Esta entrega contiene implementación y pruebas automatizadas; no debe considerarse validada en Docker hasta que `PROBAR.bat` termine correctamente en tu equipo. En el entorno de preparación no estaban disponibles Maven, Docker ni PostgreSQL para ejecutar esa validación completa.
