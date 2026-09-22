@echo off
chcp 65001 >nul
title Менеджер облікових записів SQL
color 03
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/* com.l2journey.tools.accountmanager.SQLAccountManager
if %errorlevel% == 0 (
echo.
echo Execution successful
echo.
) else (
echo.
echo Під час запуску Диспетчера облікових записів сталася помилка!
echo.
echo Можливі причини того, чому це відбувається:
echo.
echo - Відсутні файли .jar або каталог ../libs.
echo - Сервер MySQL не запущено або неправильні налаштування MySQL:
echo   Перевірте ./config/loginserver.ini
echo - Були надані неправильні типи даних або значення поза межами діапазону:
echo    вкажіть правильні значення для кожного обов'язкового поля
echo.
)
pause