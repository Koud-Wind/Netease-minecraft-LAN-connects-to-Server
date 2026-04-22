@echo off
setlocal enabledelayedexpansion

REM 下面是网易模组目录
set "targetDir=C:\MCLDownload\Game\.minecraft\mods"
set "targetDir2=C:\netease_minecraft_neoforge\mods"

for %%F in ("%targetDir%\*") do (
    set "fileName=%%~nF"
    REM 名称末尾为@0就会删除
    if "!fileName:~-2!"=="@0" (
        del "%%F"
    )
)

for %%F in ("%targetDir2%\*") do (
    set "fileName=%%~nF"
    REM 名称末尾为@0就会删除
    if "!fileName:~-2!"=="@0" (
        del "%%F"
    )
)

endlocal