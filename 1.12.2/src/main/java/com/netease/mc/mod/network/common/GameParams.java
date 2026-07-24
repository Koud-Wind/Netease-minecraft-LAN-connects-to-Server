package com.netease.mc.mod.network.common;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.lang.reflect.Field;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class GameParams {
    private static PropertyMap map;
    private static final String GAMEID = "gameid";
    private static final String UID = "uid";
    private static final String LAUNCHER_PORT = "launcherport";

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0043, code lost:
    
        com.netease.mc.mod.network.common.GameParams.map = r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void init() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            Field[] field = Minecraft.class.getDeclaredFields();
            int j = 0;
            while (true) {
                if (j >= field.length) {
                    break;
                }
                field[j].getName();
                field[j].setAccessible(true);
                Object tile = field[j].get(mc);
                if (tile instanceof PropertyMap) {
                    PropertyMap pm = (PropertyMap) tile;
                    if (pm.containsKey(GAMEID)) {
                        break;
                    }
                }
                j++;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (SecurityException e3) {
            e3.printStackTrace();
        }
    }

    public static String getValue(String key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Set<Property> pro = (Set) map.get(key);
        return pro.iterator().next().getValue();
    }

    public static boolean hasValue(String key) {
        return map != null && map.containsKey(key);
    }
}
