# Pruebas Docker

1. Abre Docker Desktop con contenedores Linux.
2. Ejecuta INICIAR.bat: construye el código, ejecuta pruebas unitarias y comprueba disponibilidad de aplicación/base.
3. Ejecuta PROBAR.bat: añade las pruebas de integración contra PostgreSQL temporal, separado del volumen de trabajo.
4. Revisa resultado y reportes en test-results/. Un fallo no debe darse por aprobado.
5. Completa la lista de aceptación de VERIFICACION.md.

No se ejecutaron estos comandos en el entorno de preparación, donde no había Docker ni Maven. El proyecto incluye su configuración y casos; la validación completa está pendiente en un equipo con Docker. La primera construcción necesita red para descargar dependencias e imágenes.
