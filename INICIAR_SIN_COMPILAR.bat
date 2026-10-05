@echo off
setlocal
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
call scripts\crear_config.bat
if errorlevel 1 goto error
if not exist images\sigpi-imagenes.tar goto missing
docker image load -i images\sigpi-imagenes.tar
if errorlevel 1 goto error
docker compose -p sigpi-alicorp --env-file .env -f compose.yaml up -d --no-build --pull never --wait --wait-timeout 240
if errorlevel 1 goto error
echo SIGPI disponible. Consulta VER_ACCESO.bat para la clave inicial.
powershell -NoProfile -Command "$line=Get-Content .env | Where-Object {$_ -match '^SIGPI_PORT='} | Select-Object -First 1; $port=if($line){($line -split '=',2)[1]}else{'8080'}; Start-Process ('http://localhost:'+ $port)"
pause
exit /b 0
:missing
echo Falta images\sigpi-imagenes.tar. Generalo en la primera PC con EXPORTAR_IMAGENES.bat.
:error
echo No se completo el arranque. Revisa el error de esta ventana.
pause
exit /b 1
