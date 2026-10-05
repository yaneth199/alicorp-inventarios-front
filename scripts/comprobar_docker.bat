@echo off
where docker >nul 2>&1
if errorlevel 1 (
 echo No se encontro Docker. Instala Docker Desktop y vuelve a abrir este archivo.
 exit /b 1
)
docker info >nul 2>&1
if errorlevel 1 (
 echo Abre Docker Desktop y espera hasta que el motor este listo.
 echo Utiliza contenedores Linux.
 exit /b 1
)
docker compose version >nul 2>&1
if errorlevel 1 (
 echo Se requiere Docker Compose V2 incluido en Docker Desktop.
 exit /b 1
)
exit /b 0
