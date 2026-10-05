@echo off
setlocal
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
if not exist .env goto error
docker compose -p sigpi-alicorp --env-file .env -f compose.yaml stop
if errorlevel 1 goto error
echo Aplicacion detenida. La base y los datos se conservan.
pause
exit /b 0
:error
echo No se pudo detener. Comprueba Docker Desktop y la configuracion.
pause
exit /b 1
