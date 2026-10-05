# Ejecutar en varias PCs y conservar el código

## Qué resuelve Docker

La imagen se construye con Maven 3.9.9 y JDK 17; la aplicación se ejecuta con Java 17.
PostgreSQL 16 funciona en un contenedor separado. No necesitas instalar Java, Maven,
PostgreSQL ni NetBeans para usar la aplicación con los BAT.

Sí necesitas Docker Desktop funcionando con contenedores Linux. En Windows puede
requerir habilitar virtualización/WSL 2 según las indicaciones de su instalador.
La primera construcción descarga imágenes y dependencias. Si la red bloquea Maven
Central o Docker Hub, también habrá que resolver ese acceso; Docker no lo evita.

El arranque espera a que PostgreSQL esté listo y a que la aplicación pueda consultarlo.
Los datos se conservan en el volumen `sigpi-alicorp_postgres_data`.

## Primera ejecución

1. Instala y abre Docker Desktop. Espera a que el motor esté listo.
2. Descomprime en una carpeta normal. No ejecutes los BAT dentro del ZIP.
3. Ejecuta `INICIAR.bat`. Déjalo terminar sin cerrar la ventana.
4. El navegador se abre al superar las comprobaciones de disponibilidad.
5. Abre `VER_ACCESO.bat` para ver la clave inicial y entra con `admin`.
6. Ejecuta `PROBAR.bat` para comprobar también login, páginas y transacciones.

Se crea `.env` automáticamente con claves aleatorias. Guarda ese archivo: recrearlo
con otras claves no cambia la contraseña de un volumen PostgreSQL ya existente.
Si cambias la contraseña del admin desde Mi perfil, la clave inicial mostrada por el BAT
ya no será la vigente. Mantén tu nueva clave.

`INICIAR.bat` reconstruye cuando cambian los archivos. No elimina la base ni repite
el lote de demostración si ya está registrado en demo_batches. Solo uses una instalación de este proyecto por PC
con el nombre Compose `sigpi-alicorp`: dos copias iniciadas usarían el mismo conjunto
de servicios y volumen.

## Tres PCs: elegir el funcionamiento

| Modalidad | Cómo se usa | Datos |
|---|---|---|
| Cada PC trabaja por separado | Copiar/clonar proyecto y ejecutar INICIAR.bat en cada PC | Una base diferente en cada PC |
| Las tres trabajan juntas | Arrancar en una PC y entrar desde los otros navegadores | Una sola base compartida |
| Reutilizar una compilación | Exportar imágenes en PC 1 e importarlas en PC 2/3 | Cada PC sigue teniendo su propia base |

Para prácticas individuales o presentaciones puedes usar la primera opción.
GitHub sincroniza código; no sincroniza pedidos ni registros de PostgreSQL.

### Una PC central para las tres

1. Ejecuta una vez `INICIAR.bat` en la PC central y detenla con `DETENER.bat`.
2. Edita `.env` con Bloc de notas y cambia solo:

```properties
SIGPI_BIND_IP=0.0.0.0
```

3. Vuelve a ejecutar `INICIAR.bat`.
4. Ejecuta `ipconfig` en la PC central y localiza su IPv4 de la red local.
5. Si Windows solicita autorización de red, habilita el acceso en tu red privada.
   Si hace falta una regla de firewall, permite TCP 8080 solo para esa red privada.
6. En las otras PCs abre `http://IP_DE_LA_PC_CENTRAL:8080`.
   Ejemplo ilustrativo: `http://192.168.1.50:8080`.

Las PCs deben poder comunicarse por la misma red. La central debe permanecer encendida,
con Docker y el proyecto activos. Las otras no necesitan Docker para acceder por navegador.
Puedes abrir el código en NetBeans en cualquiera de ellas de forma independiente.
Esta configuración es para red local; no publica el sistema en Internet.

### Arrancar en otra PC sin recompilar

Una vez que la primera PC arranque correctamente y supere las pruebas:

1. Ejecuta `EXPORTAR_IMAGENES.bat` en esa PC.
2. Copia el proyecto **junto con** `images/sigpi-imagenes.tar` a la otra PC.
3. Para una instalación nueva con claves diferentes, no copies el `.env` de la primera.
4. Abre Docker Desktop en la segunda PC y ejecuta `INICIAR_SIN_COMPILAR.bat`.

El TAR puede ser grande. Incluye aplicación compilada y PostgreSQL, no tu base de datos
ni las claves generadas. Mantén la misma arquitectura de CPU entre PCs, normalmente
Windows x64; una imagen exportada no garantiza funcionar en otra arquitectura.
El código sigue estando en `src/`. Si lo editas, vuelve a construir y exportar las imágenes.
`INICIAR_SIN_COMPILAR.bat` ejecuta lo que contiene el TAR, no recompila los cambios de `src/`.

## Abrir y editar en NetBeans

File → Open Project → carpeta de `pom.xml`. Se mantienen paquetes, vistas y código Java.
Puedes ejecutar por Docker aunque NetBeans todavía esté descargando sus dependencias.
Para que el editor resuelva todas las clases, NetBeans también debe cargar el proyecto Maven.

Después de editar: guarda y ejecuta `INICIAR.bat`. El contenedor usa la versión reconstruida.
No hace recarga automática de archivos. La configuración `application-local.properties`
queda excluida de la imagen para no mezclar tu conexión local con la conexión Docker.

## Subir a GitHub

No se ha subido nada a tu cuenta. Este paquete incluye `.gitignore` y una comprobación
GitHub Actions para compilar y probar con PostgreSQL al hacer push o pull request.

La carpeta que contiene `pom.xml` debe ser la raíz del repositorio, para que funcione
el workflow incluido. Sube lo siguiente:

- `src/`, `pom.xml` y `nbactions.xml`.
- `Dockerfile`, `compose.yaml`, `compose.test.yaml`, `docker/` y `scripts/`.
- Los BAT, guías, SQL y archivos `.github/`, `.gitignore`, `.gitattributes`, `.dockerignore` y `.env.example`.

No subas `.env`, archivos de contraseñas locales, backups/, images/, test-results/ ni target/.
`.gitignore` ya los excluye cuando utilizas Git. La carga manual desde el navegador de
GitHub no aplica esas exclusiones automáticamente: selecciona los archivos con cuidado.

Desde GitHub Desktop: añade esta carpeta como repositorio local, revisa los archivos del
primer commit y usa Publish repository. Puedes mantenerlo privado. En las otras PCs,
clona ese repositorio y ejecuta `INICIAR.bat`.

Para actualizar después, detén la aplicación, guarda/commitea tus cambios si los tienes,
obtén la versión del repositorio y vuelve a ejecutar `INICIAR.bat`. El código cambia;
el volumen de datos permanece. Cambios futuros del esquema requerirán migraciones.

En la pestaña Actions, el workflow `Java and PostgreSQL tests` debe terminar en verde.
Eso valida los casos automatizados de esa versión; no sustituye probar todos los usos posibles.
Las contraseñas del workflow y de compose.test.yaml solo sirven para bases temporales de pruebas.

## Respaldos

Con el proyecto iniciado, ejecuta `RESPALDAR.bat`. El resultado se guarda en `backups/`.
Copiar el código o exportar imágenes no respalda los pedidos. Tampoco borres el volumen
Docker: ahí residen los datos. DETENER.bat solo detiene los servicios.

El respaldo contiene cuentas con sus contraseñas derivadas. Para recuperar en otra PC,
usa una base de destino vacía; no ejecutes una restauración sobre tu base de trabajo
sin preparar antes una copia. La restauración no está automatizada en un BAT destructivo.

## Solución de problemas

| Síntoma | Qué revisar |
|---|---|
| No se encontró Docker | Instalar Docker Desktop y volver a abrir el BAT |
| No se puede conectar al motor | Abrir Docker Desktop y esperar; utilizar Linux containers |
| Error descargando parent/dependencias | Revisar red/proxy de Maven dentro de Docker y mensaje completo |
| Error al descargar imágenes | Revisar acceso a Docker Hub y conexión |
| Puerto 8080 ocupado | Cambiar SIGPI_PORT=8081 en .env y volver a iniciar |
| PostgreSQL rechaza contraseña tras recrear .env | Recuperar la clave original de ese volumen; no se actualiza cambiando .env |
| Servicio unhealthy | Ejecutar VER_LOGS.bat y revisar el primer error de la aplicación |
| Las otras PCs no conectan | Revisar IP central, SIGPI_BIND_IP, red privada y firewall |
| Imagen importada no refleja un cambio | Reconstruir/exportar nuevamente en la PC de origen |
| PROBAR.bat falla | Copiar el error completo y revisar test-results; no considerar probada esa versión |

No ejecutes `docker compose down -v` para detener: elimina volúmenes y perderías datos.

Documentación consultada:
- [Orden de arranque y healthchecks](https://docs.docker.com/compose/how-tos/startup-order/).
- [Variables de entorno de Compose](https://docs.docker.com/compose/how-tos/environment-variables/envvars-precedence/).
