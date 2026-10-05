@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
call scripts\comprobar_docker.bat
if errorlevel 1 goto error
if not exist test-results mkdir test-results
echo Ejecutando pruebas Java y PostgreSQL en una base temporal independiente.
docker compose -p sigpi-alicorp-tests -f compose.test.yaml up --build --force-recreate --abort-on-container-exit --exit-code-from tests
set "TEST_RESULT=%ERRORLEVEL%"
docker compose -p sigpi-alicorp-tests -f compose.test.yaml cp tests:/build/target/surefire-reports test-results\unit
docker compose -p sigpi-alicorp-tests -f compose.test.yaml cp tests:/build/target/failsafe-reports test-results\integration
docker compose -p sigpi-alicorp-tests -f compose.test.yaml down
if not "%TEST_RESULT%"=="0" goto error
echo PRUEBAS CORRECTAS. Resultados en test-results.
pause
exit /b 0
:error
echo PRUEBAS NO COMPLETADAS O CON FALLOS. Revisa el error y test-results.
pause
exit /b 1
