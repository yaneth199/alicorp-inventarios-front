@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
call scripts\crear_config.bat
if errorlevel 1 goto error
echo Iniciando SIGPI. La primera vez se descargan Java, Maven y PostgreSQL.
echo La construccion ejecuta las pruebas unitarias antes de generar la aplicacion.
docker compose -p sigpi-alicorp --env-file .env -f compose.yaml up -d --build --wait --wait-timeout 240
if errorlevel 1 goto error
echo SIGPI y PostgreSQL estan disponibles.
echo Ejecuta VER_ACCESO.bat para consultar la clave inicial del usuario admin.
powershell -NoProfile -Command "$line=Get-Content .env | Where-Object {$_ -match '^SIGPI_PORT='} | Select-Object -First 1; $port=if($line){($line -split '=',2)[1]}else{'8080'}; Start-Process ('http://localhost:'+ $port)"
pause
exit /b 0
:error
echo No se completo el arranque. Copia el error de esta ventana.
echo Puedes utilizar VER_LOGS.bat para consultar el registro.
pause
exit /b 1
