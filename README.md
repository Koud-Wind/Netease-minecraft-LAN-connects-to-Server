# Netease-minecraft-LAN-connects-to-Server
利用网易我的世界本地联机 (局域网), 将玩家人流引导至指定服务端中

<br>

* **前言:** 我公开这个的个人意愿是希望能够改善网易局域网联机的游戏体验, 以及帮助毫无人流量的私服. 使用时并不会对网易虚拟局域网服务器造成多余负担压力, 但对于网易运营难度不要分享此教程及库内文件! 尽量所对接的服务器不要有或有较少的付费要求 (例如充值货币,VIP等), 以免网易重视

  *[1.0版本](https://github.com/Koud-Wind/Netease-minecraft-LAN-connects-to-Server/tree/main-1.0)*
<br>

## 使用步骤

**0.** 启动服务端, 从 `server.properties` 或 `命令行后台` 得知服务端开放的端口号

**1.** 编写一个便于删除网易模组的bat脚本, 其中 `C:\MCLDownload\` 路径会因人而异, **需要修改**

```
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

```

**2.** 进入 `\MCLDownload\cache\game` 路径, 其中像 `V_1_??` 这样命名的文件夹能够在进入对应版本游戏时将这个文件夹内的文件覆盖到 `\MCLDownload\Game\.minecraft\` , 所以需要在`\MCLDownload\cache\game\V_1_??\`中新建一个`mods`文件夹, 并放入**在这里下载好的模组`(比如1201@3@16.jar)`**

**3.** 进入 `\MCLDownload\Game\.minecraft` 路径, 新建一个txt文本文档, 并重命名为 `#-#25565` , 其中 `25565` 是你**服务端开放的端口号**, 请自行修改 **(必须开启文件拓展名, 名称若是 `#-#25565.txt` 则不会生效)**

**4.** 启动局域网游戏, 当网易进度条加载到 `90%` 时运行你修改好的bat文件, 并等待网易玩家管理页面出现

**5.** 当你进入地图后, 可以直接退出地图, 从多人游戏中进入服务器

<br>

## 模组内容
+ 模组支持将服务端对接至网易局域网游戏房间
+ 模组均删除了大量多余网易功能, 优化游戏性能
+ 模组支持自由进退菜单
+ 模组优先显示正版账号的皮肤与披风, 服务端也可热改皮肤
+ 模组能够自动根据网易皮肤来套用合适的人物模型 (Steve与Alex模型)
+ 模组修改了玩家TAB栏显示, 会在玩家名称旁显示皮肤头像与数字延迟
+ 模组支持正常进入其他人开放的局域网游戏房间, 不允许重连
+ 模组仅支持网易版本 `1.12.2` `1.20` `1.21` `1.21.8`, [旧版](https://github.com/Koud-Wind/Netease-minecraft-LAN-connects-to-Server/tree/2.1.3)支持大部分网易版本
+ ~~模组支持免游戏窗口开放局域网游戏房间 (尚未完成) (以`#-#`开头以`#`结尾)~~

<br>


## 注意事项
+ `1.20.1 与 1.21` 可以免执行bat脚本, 但网易可能会更新强装模组, 以导致游戏报错或崩溃
+ `V_1_12_2 对应 1.12.2`
  `V_1_20 对应 1.20.1`
  `V_1_21 对应 1.21.0`
  `V_1_21_8 对应 1.21.8`
+ 请勿过早运行bat文件, 否则将会停止启动游戏
+ 若想普通开放网易局域网房间, 需要在 `\MCLDownload\Game\.minecraft` 中重命名或删除开头为 `#-#` 的文件
+ 对接端口号在 `51097 ~ 51996` 之间会在房间管理中显示`拦截窗口`, 请修改为**范围以外**的端口号
+ 对接服务端时, 若需要重启网易客户端, 无需关闭服务端
+ 一些服务端可能不支持使用中文名进入游戏, 请使用 [CnUsername](https://github.com/XPPlugins/CnUsername)

<br>

## 推荐项目
- [Paper](https://papermc.io/downloads/all): 基于 Spigot 的 Minecraft 游戏服务器, 旨在大幅提高性能并提供更高级的功能和 API
- [Folia](https://github.com/PaperMC/Folia): 突破 Minecraft 服务端的单线程限制, 每个区块组有独立的Tick, [不适合新手](https://docs.papermc.io/folia/faq)
- [Mohist](https://mohistmc.cn/software/mohist): 实现 Bukkit/Spigot/Paper API 的 Forge 服务器
- [Arclight](https://arclight.izzel.io): 实现 Bukkit API 的 Forge 服务器
- [Velocity](https://papermc.io/downloads/velocity): Minecraft 反向代理服务端, 利用它将多个子服连接起来


- [CnUsername](https://github.com/XPPlugins/CnUsername): 允许使用特定字符的玩家名称进入服务器
- [MultiLogin](https://github.com/CaaMoe/MultiLogin): 正版与多种外置登录共存


- [ScreenInMC](https://github.com/GZY-mingbai/ScreenInMC-Plugin): 允许在 Minecraft 上播放和使用各种媒体, 例如视频/直播/图片/VNC


- [Mc-Map](https://mc-map.djfun.de/faq.html): 将图片转换为 Minecraft 地图以展示给其他玩家


<br>
<br>
<br>

***作者:** Koud_Wind*
