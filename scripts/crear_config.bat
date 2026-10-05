@echo off
if exist .env exit /b 0
powershell -NoProfile -Command "$ErrorActionPreference='Stop'; function NewSecret { $b=New-Object byte[] 18; $rng=[System.Security.Cryptography.RandomNumberGenerator]::Create(); try {$rng.GetBytes($b)} finally {$rng.Dispose()}; return ([System.BitConverter]::ToString($b)).Replace('-','') }; $lines=@(('POSTGRES_PASSWORD='+ (NewSecret)),('SIGPI_ADMIN_PASSWORD='+ (NewSecret)),'SIGPI_DEMO=true','SIGPI_PORT=8080','SIGPI_BIND_IP=127.0.0.1'); [System.IO.File]::WriteAllLines((Join-Path (Get-Location) '.env'),$lines,[System.Text.Encoding]::ASCII)"
if errorlevel 1 exit /b 1
echo Configuracion local creada. Conserva el archivo .env con las claves.
exit /b 0
