@echo off
setlocal
cd /d "%~dp0"
if not exist .env (
 echo Ejecuta INICIAR.bat primero.
 pause
 exit /b 1
)
powershell -NoProfile -Command "$lines=Get-Content .env; $port=($lines | Where-Object {$_ -match '^SIGPI_PORT='} | Select-Object -First 1) -split '=',2; $key=($lines | Where-Object {$_ -match '^SIGPI_ADMIN_PASSWORD='} | Select-Object -First 1) -split '=',2; Write-Host ('Direccion: http://localhost:'+ $port[1]); Write-Host 'Usuario: admin'; Write-Host ('Clave inicial: '+ $key[1]); Write-Host 'Si cambiaste la clave desde Mi perfil, utiliza tu nueva clave.'"
pause
