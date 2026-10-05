# Abrir SIGPI en Apache NetBeans

## Proyecto

Abre File → Open Project y selecciona la carpeta que contiene `pom.xml`. Usa JDK 17. El POM incorpora `spring-boot-starter-parent` 3.3.5, que administra las versiones de los starters y del controlador PostgreSQL. Esto corrige las versiones ausentes que mostraba la captura. La descarga inicial de dependencias sigue requiriendo acceso a Maven Central.

Es una aplicación Spring Boot con servidor embebido. No necesita registrarse en un Tomcat externo de NetBeans. Run Project usa `spring-boot:run` y el perfil `local`.

## Opción Docker

Puedes editar el código en NetBeans y ejecutar `INICIAR.bat` tras guardar los cambios. Docker reconstruye la aplicación y levanta PostgreSQL. No debes crear manualmente la base en esa modalidad.

## Opción PostgreSQL instalado en la PC

1. En pgAdmin, conectado a la base `postgres` con tu administrador, ejecuta por separado las instrucciones de `database/00_crear_base.sql`; cambia la contraseña de ejemplo. CREATE DATABASE debe ejecutarse fuera de una transacción.
2. Crea `src/main/resources/application-local.properties` con tus valores:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sigpi_alicorp
spring.datasource.username=sigpi
spring.datasource.password=TU_CLAVE_POSTGRES
sigpi.admin-password=TU_CLAVE_ADMIN_DE_AL_MENOS_8_CARACTERES
sigpi.demo=true
```

3. Ejecuta Clean and Build y luego Run Project. El esquema se aplica al arrancar y Hibernate valida su correspondencia. No necesitas ejecutar otro script de datos.
4. Abre `http://localhost:8080` e ingresa con `admin` y la clave que configuraste.

Este archivo local está excluido de Git y de Docker. No lo subas manualmente a GitHub. La variable de entorno equivalente para la clave de PostgreSQL es `DB_PASSWORD`; la del usuario inicial es `SIGPI_ADMIN_PASSWORD`.

## Uso y roles

| Rol original | Acceso |
|---|---|
| Administrador | Todos los módulos y configuración |
| Almacén | Productos, categorías, inventario, movimientos, proveedores y avance de pedidos |
| Ventas | Clientes y pedidos; consulta de productos, categorías e inventario |
| Supervisor | Consulta del sistema y reportes; sin modificaciones ni usuarios/configuración |

Todos pueden editar su perfil. Los controles se ocultan por permisos y el servidor también valida el acceso. Los nombres Almacén y Ventas corresponden al almacenero y vendedor del informe.

En Pedidos → Nuevo pedido selecciona cliente y productos. Guardar borrador conserva las líneas para ese usuario sin reservar stock. Registrar pedido descuenta todas las líneas en una transacción. La secuencia de atención es Pendiente → En proceso → Preparado → Enviado → Entregado. Cancelar antes de entregar devuelve el stock una sola vez. El detalle muestra responsable y fecha de cada transición.

Los productos se desactivan conservando sus referencias históricas. Los reportes exportan los datos filtrados completos, aunque la tabla muestre una página. Configuración permite editar empresa, mínimo sugerido/alertas, sesión/rol inicial, descargar respaldo y consultar datos del sistema.

## Si NetBeans sigue mostrando errores

Comprueba JDK 17 y abre el POM de esta versión; recarga el proyecto y descarga dependencias. Si falla, revisa el primer error completo en Output. Un fallo de proxy, certificado o repositorio no se corrige añadiendo versiones al azar. Para puerto ocupado usa otro `server.port` local. Para autenticación de PostgreSQL comprueba la contraseña del rol y el host/puerto configurados.
