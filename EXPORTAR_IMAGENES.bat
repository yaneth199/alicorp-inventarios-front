@echo off
setlocal
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
if not exist images mkdir images
echo Exportando imagenes construidas. Ejecuta INICIAR.bat y PROBAR.bat antes.
docker image save -o images\sigpi-imagenes.tar sigpi-alicorp:local postgres:16-alpine
if errorlevel 1 goto error
echo Copia el proyecto y images\sigpi-imagenes.tar a la otra PC.
echo No copies .env si quieres claves diferentes en esa PC.
echo En la otra PC ejecuta INICIAR_SIN_COMPILAR.bat.
echo Este archivo no contiene los registros de la base de datos.
pause
exit /b 0
:error
echo No se pudo exportar. Comprueba que las dos imagenes existen y hay espacio.
pause
exit /b 1
