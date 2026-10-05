@echo off
setlocal
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
if not exist .env goto error
docker compose -p sigpi-alicorp --env-file .env -f compose.yaml logs --tail 150
pause
exit /b 0
:error
echo No se encontraron servicios disponibles. Ejecuta INICIAR.bat primero.
pause
exit /b 1
