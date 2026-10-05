# Estado de verificación

## Ejecutado durante esta entrega

- Análisis sintáctico Java 17 de los archivos de producción y pruebas.
- Comparación del orden y rutas de los once apartados con el ZIP original.
- Revisión estática de formularios POST, campos CSRF, enlaces, esquema y configuración.
- Compilación y ejecución independiente del generador PDF/XLSX con datos de prueba.
- Lectura del XLSX generado con openpyxl: celdas numéricas y texto seguro, incluso textos que comienzan con signo igual.
- Lectura del PDF con pypdf y revisión visual de una página renderizada: tabla multipágina legible.

## Pendiente de ejecución con dependencias reales

No se pudo ejecutar Maven, Spring Boot, PostgreSQL, Docker ni automatización de navegador sobre la aplicación en este entorno. El análisis sintáctico no equivale a compilar con todas las dependencias. No se afirma que la suite, el arranque Docker o todos los flujos hayan pasado aquí.

Se incluyen 13 pruebas unitarias y 6 de integración para ejecutar con `PROBAR.bat` o `mvn verify -Pintegration` sobre una base temporal. Cubren contraseñas/permisos, pedidos, exportaciones, login y páginas, nueve reportes, cinco pestañas de configuración, borradores, transacciones, concurrencia de stock y consistencia/idempotencia de la semilla. Consulta las clases de `src/test/java` para los casos exactos.

`INICIAR.bat` construye ejecutando pruebas unitarias. `PROBAR.bat` agrega PostgreSQL temporal e integración; no utiliza la base de trabajo. Revisa `test-results/`. La suite debe terminar sin errores antes de considerar validada la instalación.

## Comprobación de aceptación en navegador

1. Iniciar sesión como admin; abrir los once apartados y verificar que tablas y gráficos carguen.
2. Crear y editar categoría, producto, cliente y proveedor; buscar y exportar. Desactivar producto y comprobar que no esté disponible para nuevos pedidos.
3. Registrar entrada y salida; comprobar responsable, proveedor, documento, stock y movimiento. Intentar salida superior al stock y verificar rechazo sin cambios.
4. Guardar borrador de pedido, salir y entrar; comprobar recuperación. Registrar un pedido y comprobar descuento por línea.
5. Avanzar hasta Enviado y Entregado y revisar historial. Cancelar otro y comprobar devolución única; rechazar transiciones inválidas.
6. Recorrer nueve reportes, aplicar filtros y abrir PDF/XLSX; comparar resultados con la pantalla.
7. Cambiar parámetros y empresa; verificar valores al recargar. Descargar respaldo desde Configuración y con RESPALDAR.bat.
8. Entrar como Ventas, Almacén y Supervisor; comprobar límites de acceso y perfil.
9. Reiniciar y comprobar persistencia y ausencia de repetición del lote DEM.
10. Ejecutar desde otra PC según DOCKER_Y_GITHUB.md. Verificar restauración de un respaldo en una base vacía separada antes de depender de él.

Los tiempos de respuesta del informe requieren medición en el equipo y volumen de datos objetivo; no se han medido aquí.
