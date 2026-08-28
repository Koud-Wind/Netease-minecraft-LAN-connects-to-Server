@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul

REM 目录设置
for /f "usebackq tokens=2*" %%A in (`reg query "HKCU\Software\Netease\MCLauncher" /v "DownloadPath" 2^> nul`) do (
    set "forge_path=%%B"
    for %%i in ("%%B") do set "neoforge_path=%%~dpinetease_minecraft_neoforge"
)

if not defined forge_path (
    echo 无法找到网易我的世界启动器游戏安装位置
    goto end
)

set "forge_mods=!forge_path!\Game\.minecraft\mods"
set "neoforge_mods=!neoforge_path!\mods"
if not exist "%forge_mods%" mkdir "%forge_mods%"
if not exist "%neoforge_mods%" mkdir "%neoforge_mods%"

echo forge: !forge_mods!
echo neoforge: !neoforge_mods!
echo [!time!] 已开始监听目录...

REM 创建监听文件

:loop
type nul > "%forge_mods%\replace_event"
type nul > "%neoforge_mods%\replace_event"
set "is_replace="

:loop2
if not exist "!forge_mods!\replace_event" (
    for %%F in ("!forge_mods!\*@0.*") do if exist "%%F" del "%%F"
    set "is_replace=1"
    echo [!time!] 已删除网易 forge 路径下的@0模组
)

if not exist "!neoforge_mods!\replace_event" (
    for %%F in ("!neoforge_mods!\*@0.*") do if exist "%%F" del "%%F"
    set "is_replace=1"
    echo [!time!] 已删除网易 neoforge 路径下的@0模组
)

REM 可将 loop 改为 end 避免持续监听
if defined is_replace goto loop

REM 延时
timeout /t 2 /nobreak > nul
goto loop2


:end
del /q "!forge_mods!\replace_event" "!neoforge_mods!\replace_event" 2> nul
pause
exit /b
