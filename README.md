# Netease-minecraft-LAN-connects-to-Server
利用网易我的世界本地联机(局域网), 将玩家人流引导至其他服务器

前言: 使用时并不会对网易虚拟局域网服务器造成多余负担压力, 但对于网易运营难度不要分享此教程及库内文件!



--1.12.2

1. 编写一个bat, 里面输入 del C:\MCLDownload\Game\.minecraft\mods\4641449394569051656@3@0.jar , 路径因人而异, 这是在删除网易的friendplay(联机)模组, 当然你也可以删除其他网易自带的模组(前提你知道哪些模组对应什么功能), 记得保存, 最好放在桌面方便启动

2. 利用网易的自动加载文件夹, 将 100@3@16.jar 文件放到 C:\MCLDownload\cache\game\V_1_12_2\mods 内, 如果你的地图没有Operator(1.13+), 需要将 101@3@16.jar 也加进去. 来到网易的联机页面, 启动游戏, 当网易读条到83%时运行刚才编写的bat, 进人地图后退出游戏, 打开网易游戏文件夹中的config文件夹, 有 serverGlobalConfig.properties 文件, 用记事本打开并配置好端口, 记得保存, 将这个文件放到 C:\MCLDownload\cache\game\V_1_12_2\config . (懒人操作: 再新建一个局域网存档) 前往你刚才启动游戏的存档, 去删掉 server.properties .

3. 启动游戏, 当网易读条到83%时运行刚才编写的bat, 进入地图后按ESC退出地图, 这时你为服务端准备的端口已解除了占用, 启动服务端, 一定要启动完毕后在从多人游戏中进入服务端, 不然显示重连页面 (不要让服务端把你踢出, 不然显示重连按钮你会发现点不了, 如果显示了且不方便关服那就用其他启动器进入)

4. 维护方面, 你可以自由进退服务端 (别让服务端把你踢出去) , 尽量使用热加载插件, 以及包含forge的服务端, 不要把网易的模组 (名称后为@0的模组) 放到服务端, 若你的服务端需要使用模组, 需要满足两个条件之一: " 模组必须在网易组件商城中 (包含搜索不到的模组) , 不需要其他人加载的模组". 一些小地图, 血量显示, 帧数/客户端优化不需要加入到服务端中, 他们不对于服务端起作用, 仅玩家安装即可. 



--1.16.5




