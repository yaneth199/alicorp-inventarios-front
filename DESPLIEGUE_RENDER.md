# SIGPI Alicorp: despliegue inicial con PostgreSQL

Este proyecto usa Java 17, Spring Boot y PostgreSQL. El ZIP no constituye un despliegue: la URL pública se obtiene cuando Render termina de publicar el servicio.

## 1. Actualizar GitHub

Crear una rama `feature/postgres-cloud` desde `develop` en el repositorio existente. Extraer este ZIP y subir el contenido de su carpeta, no el ZIP ni la carpeta contenedora. Mantener `pom.xml`, `Dockerfile` y `src` en la raíz del repositorio.

GitHub limita la carga web a 100 archivos por vez. Subir primero `src` y luego las otras carpetas y archivos. Revisar ambas cargas antes de confirmar cada commit. No subir `.env`, `application-local.properties`, respaldos, `target` ni contraseñas. Abrir un pull request hacia `develop`, comprobar Actions y resolver cualquier error antes de integrar.

## 2. Crear una base nueva en Render

En https://dashboard.render.com seleccionar New > Postgres. Nombre: `alicorp-db`; database: `sigpi_alicorp`; elegir una región y usar la misma para la aplicación. Elegir explícitamente el plan deseado antes de crear.

El plan gratuito de Render Postgres caduca a los 30 días y no ofrece respaldos administrados. Si la revisión docente será posterior, planificar una base duradera o el cambio de plan. Documentación: https://render.com/docs/free

Usar una base nueva para esta demostración. La aplicación crea el esquema al arrancar. La base local y su información no se trasladan con el código; este despliegue puede usar datos académicos de demostración.

## 3. Publicar la aplicación

Seleccionar New > Web Service, conectar el repositorio y elegir `develop` después de integrar el pull request (o `feature/postgres-cloud` para publicar antes de integrarlo).

- Name: `sigpi-alicorp` (Render puede pedir otro nombre si ya está ocupado).
- Language: Docker.
- Root Directory: vacío.
- Dockerfile Path: `./Dockerfile`.
- Region: la misma que PostgreSQL.
- Plan: seleccionar explícitamente el deseado; Free sirve para una demostración y se suspende tras 15 minutos sin tráfico.
- Health Check Path: `/health/ready`.

Agregar estas variables privadas en el servicio, no en GitHub:

| Variable | Valor |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `cloud` |
| `DB_URL` | `jdbc:postgresql://HOST_INTERNO:5432/sigpi_alicorp` usando el hostname interno real de la base |
| `DB_USER` | Usuario indicado por Render para esa base |
| `DB_PASSWORD` | Contraseña indicada por Render para esa base |
| `SIGPI_ADMIN_PASSWORD` | Una clave propia de 8 a 128 caracteres para el administrador inicial |
| `SIGPI_DEMO` | `true` para cargar datos de demostración académica una sola vez |
| `JAVA_TOOL_OPTIONS` | `-XX:MaxRAMPercentage=60.0` |

Render proporciona `PORT`; la aplicación y la comprobación de Docker lo leen. No pegar una URL `postgresql://usuario:clave@host/base` directamente en `DB_URL`: el driver Java requiere el prefijo `jdbc:postgresql://` y las credenciales van en variables separadas.

Desplegar y esperar a que el estado sea Live. Abrir la URL que muestra Render, iniciar sesión con `admin` y la clave configurada. La clave inicial no cambia automáticamente un administrador que ya existe. Con datos demo se crean también `ventas.demo`, `almacen.demo` y `supervisor.demo` con la misma clave inicial; cambiar sus claves desde el sistema si se van a compartir accesos.

## 4. Verificar y documentar

1. Abrir `/health/ready`: debe responder `UP`.
2. Iniciar sesión, registrar un cliente y un pedido, y comprobar el cambio de estado.
3. Cerrar sesión y volver a entrar para comprobar persistencia.
4. Guardar la URL real del servicio para el informe y una captura con su barra de dirección.
5. Usar el enlace de GitHub de la rama que contiene este código; no usar `main` mientras solo contenga el README.

La configuración `cloud` usa cookies seguras para HTTPS. Para NetBeans/local usar el perfil `local` con una configuración propia, siguiendo `GUIA_NETBEANS.md`.

## Cambios de esta preparación

- La comprobación de salud de Docker usa `PORT` y conserva 8080 como valor local.
- Se añadió el perfil `cloud` para HTTPS y un pool de hasta cinco conexiones.
- Se añadió esta guía sin incluir credenciales locales.

La compilación Maven y la integración PostgreSQL se deben comprobar en GitHub Actions o con `mvn -B -ntp clean verify -Pintegration` y una base de pruebas aislada. La compilación Maven se intentó aquí, pero no pudo descargar el POM padre de Spring Boot por un error de resolución de red; no se llegó a compilar ni ejecutar las pruebas Maven. La integración con una base PostgreSQL real aún debe verificarse. Se comprobó con Java 17 que la comprobación de Docker usa un puerto variable y distingue las respuestas UP y DOWN. No se ha publicado todavía ningún servicio.
