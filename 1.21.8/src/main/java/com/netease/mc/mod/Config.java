package com.netease.mc.mod;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private static final Path PATH = Paths.get(System.getProperty("user.dir")).resolve("#-#netease.toml");

    public static boolean disableAutoCloseGame = true;
    public static boolean disableOnlineSkin = false;
    public static int bridgePort = -1;
    public static boolean disableGameClient = false;

    static {
        load();
    }

    public static void load() {
        try {
            Files.createDirectories(PATH.getParent());

            try (CommentedFileConfig config = CommentedFileConfig.builder(PATH).sync().build()) {
                config.load();

                if (!config.contains("disableAutoCloseGame")) {
                    config.set("disableAutoCloseGame", true);
                    config.setComment("disableAutoCloseGame", "\n - 禁用网易启动器自动请求关闭游戏客户端, 默认: true");
                }

                if (!config.contains("disableOnlineSkin")) {
                    config.set("disableOnlineSkin", false);
                    config.setComment("disableOnlineSkin", "\n - 禁用正版的皮肤与披风显示, 默认: false");
                }

                if (!config.contains("bridgePort")) {
                    config.set("bridgePort", -1);
                    config.setComment("bridgePort", "\n - 对接端口号, 一般为你的目标服务器端口, 默认: -1, 范围: 1-65535");
                }

                //if (!config.contains("disableGameClient")) {
                //    config.set("disableGameClient", false);
                //    config.setComment("disableGameClient", "\n - 阻止游戏客户端启动, 一般没有用处, 默认: false");
                //}

                disableAutoCloseGame = config.getOrElse("disableAutoCloseGame", true);
                disableOnlineSkin = config.getOrElse("disableOnlineSkin", false);
                bridgePort = config.getIntOrElse("bridgePort", -1);
                //disableGameClient = config.getOrElse("disableGameClient", false);

                config.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Config() {}
}
