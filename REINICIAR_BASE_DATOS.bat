@echo off
echo Cerrando/limpiando base de datos local de SIGPI...
if exist sigpi_db.mv.db del /f /q sigpi_db.mv.db
if exist sigpi_db.trace.db del /f /q sigpi_db.trace.db
echo Base de datos reiniciada. Ahora abre NetBeans y ejecuta el proyecto otra vez.
pause
