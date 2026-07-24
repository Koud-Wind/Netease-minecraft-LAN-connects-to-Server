package com.netease.mc.mod;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class Config {
    private static final String CATEGORY = Configuration.CATEGORY_GENERAL;
    private static final File CONFIG_FILE = new File(System.getProperty("user.dir"), "#-#netease.cfg");

    public static boolean disableAutoCloseGame = true;
    public static boolean disableOnlineSkin = false;
    public static int bridgePort = -1;
    public static boolean disableGameClient = false;

    static {
        load();
    }

    public static void load() {
        Configuration config = new Configuration(CONFIG_FILE);
        try {
            config.load();

            disableAutoCloseGame = config.getBoolean(
                    "disableAutoCloseGame", CATEGORY, true, "禁用网易启动器自动请求关闭游戏客户端，默认：true");

            disableOnlineSkin = config.getBoolean(
                    "disableOnlineSkin", CATEGORY, false, "禁用正版的皮肤与披风显示，默认：false");

            bridgePort = config.get(
                    CATEGORY, "bridgePort", -1, "对接端口号，一般为目标服务器端口。默认：-1"
            ).getInt(-1);

            //disableGameClient = config.getBoolean(
            //        "disableGameClient", CATEGORY, false, "阻止游戏客户端启动，一般没有用处，默认：false");

            if (config.hasChanged()) {
                config.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Config() {}
}
