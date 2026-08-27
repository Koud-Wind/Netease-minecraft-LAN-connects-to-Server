@echo off
setlocal enabledelayedexpansion

REM 从注册表查询游戏目录
for /f "usebackq tokens=2*" %%A in (`reg query "HKCU\Software\Netease\MCLauncher" /v "DownloadPath" 2^>nul`) do (
    set "panfu=%%B"
    REM 提取父目录
    for %%i in ("%%B") do set "parent=%%~dpi"
    REM 拼接为NeoForge游戏目录
    set "neo_panfu=!parent!netease_minecraft_neoforge"
)

REM 未找到
if defined panfu (
    echo.>nul
) else (
    echo 找不到，网易我的世界启动器游戏安装位置。
    pause>nul
    exit /b
)

:CL
echo.
echo.运行中...（点启动游戏）
echo.

REM 定义两个目录（注意路径结构不同）
set "dir1=%panfu%\Game\.minecraft\mods"
set "dir2=%neo_panfu%\mods"

if not exist "%dir1%" mkdir "%dir1%" 2>nul
if not exist "%dir2%" mkdir "%dir2%" 2>nul

REM 在两个目录创建监测文件
echo 3401765#JuwLBFt>"%dir1%\JuwLBFt.log"
echo 3401765#JuwLBFt>"%dir2%\JuwLBFt.log"

REM 循环检测，最多900次
for /l %%i in (1,1,900) do (
    set "processed="
    
    REM 检测目录1的 log 是否被删
    if not exist "!dir1!\JuwLBFt.log" (
        echo 检测到\mods目录的log被删，删除该目录下@0文件...
        for %%F in ("!dir1!\*") do (
            set "fname=%%~nF"
            if "!fname:~-2!"=="@0" del "%%F"
        )
        set processed=1
    )
    
    REM 检测目录2的 log 是否被删
    if not exist "!dir2!\JuwLBFt.log" (
        echo 检测到mods目录的log被删，删除该目录下@0文件...
        for %%F in ("!dir2!\*") do (
            set "fname=%%~nF"
            if "!fname:~-2!"=="@0" del "%%F"
        )
        set processed=1
    )
    
    REM 如果处理过任意一个，跳出循环
    if defined processed goto afterLoop
    
    REM 等待1秒后继续检测
    timeout /t 1 /nobreak >nul
)

:afterLoop
echo.
echo 运行结束
pause > nul
exit /b
