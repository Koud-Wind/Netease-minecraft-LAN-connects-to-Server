# Netease-minecraft-LAN-connects-to-Server 2.0
利用网易我的世界本地联机 (局域网), 将玩家人流引导至指定服务端中

<br>

* **前言:** 我公开这个的个人意愿是希望能够改善网易局域网联机的游戏体验, 以及帮助毫无人流量的私服. 使用时并不会对网易虚拟局域网服务器造成多余负担压力, 但对于网易运营难度不要分享此教程及库内文件! 尽量所对接的服务器不要有或有较少的付费要求 (例如充值货币,VIP等), 以免网易重视

  *[1.0版本](https://github.com/Koud-Wind/Netease-minecraft-LAN-connects-to-Server/tree/main-1.0)*
<br>

## 基本步骤

**1.** 编写一个便于删除网易模组的bat脚本, 其中 `C:\MCLDownload\` 路径会因人而异, **需要修改**

```
:1.12.2附加: 使用此脚本需要装载1221@3@16与1222@3@16
@echo off
setlocal enabledelayedexpansion

REM 下面是网易模组目录
set "targetDir=C:\MCLDownload\Game\.minecraft\mods"

for %%F in ("%targetDir%\*") do (
    set "fileName=%%~nF"
    REM 名称末尾为@0就会删除
    if "!fileName:~-2!"=="@0" (
        del "%%F"
    )
)

endlocal

```

**2.** 进入 `\MCLDownload\cache\game` 路径, 其中像 `V_1_??` 这样命名的文件夹能够在进入对应版本游戏时将这个文件夹内的文件覆盖到 `\MCLDownload\Game\.minecraft\` , 所以需要在`\MCLDownload\cache\game\V_1_??\`中新建一个`mods`文件夹, 并放入**在这里下载好的模组`(比如201@3@16.jar)`**

**3.** 确保对接端口号没有占用的情况下, 启动局域网游戏, 当网易进度条加载到 `90%` 时运行你修改好的bat文件, 并等待进入地图

**4.** 直接退出你进入的地图, 将会退到主菜单, 网易玩家管理页面将会保留, **这时你才可以启动你需要对接的服务端**

**5.** 对接端口号默认为 `51457`, 确保你的服务端也使用此端口号

<br>

## 注意事项
+ 网易可能会更新强装模组, 名称会发生改变, 所以修改好bat文件后需要查看路径 `\MCLDownload\Game\.minecraft\mods\` 中是否留存不需要的网易模组 (名称尾部带有`@0`均为网易强装模组)
+ `V_1_12_2 对应 1.12.2`
  `V_1_16 对应 1.16.4`
  `V_1_18 对应 1.18.1`
  `V_1_20 对应 1.20.1`
+ 请勿过早运行bat文件, 否则将会停止启动游戏
+ 如果退出地图时客户端未响应, 需要重启游戏
+ 如果你打算对接forge服务端 (除`1.12.2` `1.20.X`), 需要向forge服务端加载对接专用模组, 非网易端的玩家也需要加载对接专用模组
+ 1.12.2版本若不进行任何对接 (普通的网易局域网游戏), 在局域网端口没有固定为对接端口号的情况下网易玩家将无法进入
+ 除1.12.2版本外均无法使用局域网端口模组来修改局域网端口号, 各版本也均无法使用模组来修改对接端口号
  
<br>

## 额外内容
+ 模组支持自由进退自己的服务器
+ 模组均支持进入其他玩家的房间 (均不可重连, 1.12.2不可中断连接)
+ 模组均删除了大量多余网易功能
+ 可以修改默认端口号, 在 `多人游戏 -> 直接连接` 页面输入 `51457->你想使用的端口号` 后点击连接, 重启游戏
  
  或者前往 `\MCLDownload\Game\.minecraft\options.txt` 中修改 `lastServer`

<br>
<br>
<br>

***作者:** Koud_Wind*
