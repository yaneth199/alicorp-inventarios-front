SIGPI - Sistema Integrado de Gestión de Pedidos e Inventarios
Proyecto web Java para Apache NetBeans

TECNOLOGÍAS
- Java 17
- Spring Boot 3.3.5
- Spring MVC + Thymeleaf
- Spring Data JPA
- H2 Database persistente (se crea automáticamente)
- HTML5 + CSS3 + JavaScript
- Maven

CREDENCIALES
Usuario: admin
Contraseña: admin123

CÓMO ABRIR EN APACHE NETBEANS
1. Descomprime el ZIP.
2. Abre Apache NetBeans.
3. Ve a File > Open Project.
4. Selecciona la carpeta SIGPI_Alicorp_NetBeans (la que contiene pom.xml).
5. NetBeans reconocerá el proyecto Maven.
6. Espera a que Maven descargue las dependencias la primera vez.
7. Verifica que el JDK del proyecto sea Java 17 o superior.
8. Clic derecho al proyecto > Run.
9. Si NetBeans pregunta la clase principal, selecciona:
   pe.edu.utp.sigpi.SigpiApplication
10. Cuando veas en Output: Started SigpiApplication, abre:
    http://localhost:8080

IMPORTANTE
- La primera ejecución necesita Internet para que Maven descargue Spring Boot y H2.
- La base de datos se crea sola en la carpeta data/.
- No necesitas instalar MySQL para esta versión entregable.
- Si luego el profesor exige MySQL, se puede migrar cambiando la dependencia y application.properties.

MÓDULOS FUNCIONALES
- Login y cierre de sesión.
- Dashboard con KPI, gráficos y alertas.
- CRUD de productos.
- CRUD/activación de categorías.
- Inventario con entradas y salidas reales.
- Validación para impedir salidas superiores al stock.
- Pedidos con múltiples productos y descuento automático de stock.
- Cambio de estado del pedido.
- Clientes.
- Proveedores.
- Kardex / movimientos de inventario.
- Reportes y exportación CSV compatible con Excel.
- Impresión / guardado en PDF desde el navegador.
- Gestión de usuarios.
- Perfil del usuario.
- Configuración de la empresa.

ESTRUCTURA
src/main/java/pe/edu/utp/sigpi/
  config/      configuración e inicialización
  controller/  controladores web
  model/       entidades JPA
  repository/  acceso a datos
  service/     lógica de negocio

src/main/resources/
  templates/   vistas HTML Thymeleaf
  static/css/  estilos
  static/js/   JavaScript
  application.properties

GITHUB
Sube al repositorio todo el proyecto excepto target/ y data/ (ya están en .gitignore).
Cada integrante debe trabajar en su propia rama y enviar Pull Request.

PROYECTO ACADÉMICO
Desarrollo e implementación de un sistema web para optimizar la gestión de pedidos e inventarios en la empresa Alicorp S.A.C., Arequipa - 2026.

========================================
CORRECCION DE ARRANQUE H2 / HIBERNATE
========================================
Esta versión usa la base de datos H2 directamente en la raíz del proyecto:
  jdbc:h2:file:./sigpi_db
Así no depende de que exista una carpeta data previamente.

Si alguna vez aparece un error de base de datos dañada o bloqueada:
1. Cierra NetBeans o detén el proyecto.
2. Ejecuta REINICIAR_BASE_DATOS.bat.
3. Vuelve a ejecutar el proyecto.
Los datos de demostración se cargarán otra vez automáticamente.


CORRECCION NETBEANS - 27/08/2026
---------------------------------
Se definio explicitamente la clase principal de Spring Boot:
pe.edu.utp.sigpi.SigpiApplication

Esto corrige el error:
Could not find or load main class ${start-class}
ClassNotFoundException: ${start-class}

Forma recomendada de ejecutar:
1. File > Open Project
2. Seleccionar esta carpeta
3. Clean and Build
4. Run
5. Abrir http://localhost:8080
6. Usuario: admin / Clave: admin123
