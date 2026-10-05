@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
if not exist .env goto error
if not exist backups mkdir backups
for /f %%T in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd_HHmmss_fff"') do set "STAMP=%%T"
if not defined STAMP goto error
set "BACKUP_FILE=backups\sigpi_%STAMP%.backup"
docker compose -p sigpi-alicorp --env-file .env -f compose.yaml exec -T db pg_dump -U sigpi -d sigpi_alicorp -Fc > "%BACKUP_FILE%"
if errorlevel 1 goto error
echo Respaldo creado: %BACKUP_FILE%
echo Conserva tambien .env en una ubicacion privada.
pause
exit /b 0
:error
echo No se completo el respaldo. No uses un archivo generado con errores.
pause
exit /b 1
