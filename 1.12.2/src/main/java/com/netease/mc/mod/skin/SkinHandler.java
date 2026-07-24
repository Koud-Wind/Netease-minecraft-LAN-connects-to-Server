package com.netease.mc.mod.skin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.netease.mc.mod.Config;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import com.netease.mc.mod.skin.message.reply.LoadSkinReply;
import com.netease.mc.mod.skin.message.reply.LoadSkinReplyV2;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

public class SkinHandler {
    private static final int TIMEOUT = 60000;
    public static final SkinHandler skinHandler = new SkinHandler();
    public static HashMap<String, Object> lockObjectMap = new HashMap<>();
    public static HashMap<String, String> nameSkinMap = new HashMap<>();
    public static HashMap<String, String> nameCapeMap = new HashMap<>();
    public static HashMap<String, Boolean> nameSkinMode = new HashMap<>();
    private static final File assetSkinsDir = new File("./assets/skins");
    private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final Cache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
    private static final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
        public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
            return Minecraft.getMinecraft().getSessionService().getTextures(p_load_1_, false);
        }
    });
    private static ScheduledExecutorService schduler = Executors.newScheduledThreadPool(10);
    private static final String[] WHITELISTED_DOMAINS = {".minecraft.net", ".mojang.com", ".163.com", ".netease.com"};
    private static final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> defaultReturn = Collections.emptyMap();
    private static final Set<GameProfile> onLoading = Collections.newSetFromMap(new ConcurrentHashMap());

    public static String CopySkinToAsset(File f) {
        if (f == null) {
            return null;
        }
        try {
            String sha = DigestUtils.sha256Hex(new FileInputStream(f)).toLowerCase();
            File subDir = new File(assetSkinsDir, sha.substring(0, 2));
            subDir.mkdirs();
            File skin = new File(subDir, sha);
            FileUtils.copyFile(f, skin);
            return "http://127.0.0.1/" + sha;
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTexturesWrapper(GameProfile profile, boolean requireSecure) {
        if (profile == null) {
            return new HashMap();
        }
        Thread current = Thread.currentThread();
        if (current.getName().contains("Client")) {
            Exception ex = new Exception();
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            ex.printStackTrace(printWriter);
            Common.Log(result.toString());
            return getTextures(profile, requireSecure);
        }
        if (profile.getName() == null) {
            return new HashMap();
        }
        String name = profile.getName();
        Common.Log(String.format("player %s start loading skin , ThreadID %s", name, current.getName()));
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> resultCache = getTextures(profile, requireSecure);
        if (resultCache != null && resultCache.size() != 0) {
            return resultCache;
        }
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> resultCache2 = new HashMap<>();
        String LocalSkinUrl = null;
        String LocalCapeUrl = null;
        String username = profile.getName();
        Minecraft.getMinecraft();
        Object object = new Object();
        lockObjectMap.put(username, object);
        MessageRequest mrq = new MessageRequest();
        Common.Log("skin:send msg to launcher!");
        mrq.send(LoadSkinReply.SMID, new Object[]{Short.valueOf(GameState.gameid), username});
        mrq.send(LoadSkinReplyV2.SMID, new Object[]{Short.valueOf(GameState.gameid), username, profile.getId().toString()});
        synchronized (lockObjectMap.get(username)) {
            try {
                lockObjectMap.get(username).wait(60000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lockObjectMap.remove(username);
        if (nameSkinMap.containsKey(profile.getName())) {
            LocalSkinUrl = nameSkinMap.get(username);
        }
        if (nameCapeMap.containsKey(profile.getName())) {
            LocalCapeUrl = nameCapeMap.get(username);
        }
        if (LocalSkinUrl != null && LocalSkinUrl != "") {
            Common.Log(String.format("player %s start loading skinurl : %s", name, LocalSkinUrl));
            String url = CopySkinToAsset(new File(LocalSkinUrl));
            if (url != null) {
                HashMap<String, String> map = null;
                if (isSlim(new File(LocalSkinUrl))) {
                    map = new HashMap<String, String>() {
                        {
                            put("model", "slim");
                        }
                    };
                }
                resultCache2.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(url, map));
            }
        }
        if (LocalCapeUrl != null && LocalCapeUrl != "") {
            Common.Log(String.format("player %s start loading capeurl : %s", name, LocalCapeUrl));
            String url2 = CopySkinToAsset(new File(LocalCapeUrl));
            if (url2 != null) {
                resultCache2.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(url2, (Map) null));
            }
        }
        cache.put(name, resultCache2);
        return resultCache2;
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        if (Config.disableOnlineSkin) return new HashMap();

        String mojangTexturesBase64 = null;
        Property serverTextureProperty = (Property) Iterables.getFirst(profile.getProperties().get("textures"), (Object) null);
        if (serverTextureProperty != null) {
            mojangTexturesBase64 = serverTextureProperty.getValue();
        }
        if (mojangTexturesBase64 == null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textureProperty = (Map) cache.getIfPresent(profile.getName());
            if (textureProperty != null) {
                return textureProperty;
            }
            mojangTexturesBase64 = fetchTexturesBase64(profile.getName());
            if (mojangTexturesBase64 == null) {
                return new HashMap();
            }
        }
        try {
            String json = new String(Base64.decodeBase64(mojangTexturesBase64), Charsets.UTF_8);
            MinecraftTexturesPayload result = (MinecraftTexturesPayload) gson.fromJson(json, MinecraftTexturesPayload.class);
            if (result.getTextures() == null) {
                return new HashMap();
            }
            for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : result.getTextures().entrySet()) {
                if (!isWhitelistedDomain(entry.getValue().getUrl())) {
                    return new HashMap();
                }
            }
            return result.getTextures();
        } catch (JsonParseException e) {
            return new HashMap();
        }
    }

    private static boolean isSlim(File skinPng) {
        if (skinPng == null) {
            return false;
        }
        try {
            BufferedImage img = ImageIO.read(skinPng);
            int s = img.getWidth() / 64;
            return isTransparent(img, 46 * s, 52 * s, 47 * s, 63 * s);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isTransparent(BufferedImage img, int x0, int y0, int x1, int y1) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (w <= 0 || h <= 0 || !img.getColorModel().hasAlpha()) {
            return false;
        }
        int sx = Math.max(0, Math.min(w, Math.min(x0, x1)));
        int ex = Math.max(0, Math.min(w, Math.max(x0, x1)));
        int sy = Math.max(0, Math.min(h, Math.min(y0, y1)));
        int ey = Math.max(0, Math.min(h, Math.max(y0, y1)));
        if (sx >= ex || sy >= ey) {
            return true;
        }

        int t = 0;
        for (int y = sy; y <= ey; y++) {
            for (int x = sx; x <= ex; x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                if (a == 0) t++;
            }
        }
        return t > (y1 - y0) / 2;
    }

    private static String fetchTexturesBase64(String playerName) {
        String body = get("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        if (body == null) {
            return null;
        }
        UUID uuid = ((GameProfile) gson.fromJson(body, GameProfile.class)).getId();
        String body2 = get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        if (body2 == null) {
            return null;
        }
        Iterator it = new JsonParser().parse(body2).getAsJsonObject().getAsJsonArray("properties").iterator();
        if (it.hasNext()) {
            JsonElement e = (JsonElement) it.next();
            JsonObject p = e.getAsJsonObject();
            if (p.has("value")) {
                return p.get("value").getAsString();
            }
            return null;
        }
        return null;
    }

    private static String get(String url) {
        try {
            HttpURLConnection c = openUnsafeHttps(url);
            c.setRequestMethod("GET");
            c.setConnectTimeout(5000);
            c.setReadTimeout(8000);
            c.setRequestProperty("Accept", "application/json");
            int code = c.getResponseCode();
            if (code != 200) {
                return null;
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));
                Throwable th = null;
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    String string = sb.toString();
                    if (br != null) {
                        if (0 != 0) {
                            try {
                                br.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            br.close();
                        }
                    }
                    return string;
                } catch (Throwable th3) {
                    if (br != null) {
                        if (0 != 0) {
                            try {
                                br.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            br.close();
                        }
                    }
                    throw th3;
                }
            } finally {
                c.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static HttpsURLConnection openUnsafeHttps(String url) throws Exception {
        TrustManager[] trustAll = {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] c, String a) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] c, String a) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new SecureRandom());
        HttpsURLConnection c = (HttpsURLConnection) new URL(url).openConnection();
        c.setSSLSocketFactory(sc.getSocketFactory());
        c.setHostnameVerifier((h, s) -> {
            return true;
        });
        return c;
    }

    private static boolean isWhitelistedDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            for (int i = 0; i < WHITELISTED_DOMAINS.length; i++) {
                if (domain.endsWith(WHITELISTED_DOMAINS[i])) {
                    return true;
                }
            }
            return false;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCacheWrapper(final GameProfile gp) {
        if (onLoading.contains(gp)) {
            return defaultReturn;
        }
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> ret = (Map) skinCacheLoader.getIfPresent(gp);
        if (ret == null) {
            onLoading.add(gp);
            String name = gp.getName() == null ? gp.toString() : gp.getName();
            new Thread(() -> {
                SkinHandler.skinCacheLoader.getUnchecked(gp);
                SkinHandler.onLoading.remove(gp);
            }, "Skin-Fetch-" + name).start();
            return defaultReturn;
        }
        return ret;
    }

    public static void loadSkullTexture(final GameProfile profile, final NBTTagCompound target) {
        target.removeTag("SkullOwner");
        schduler.schedule(() -> {
            final GameProfile prof = TileEntitySkull.updateGameprofile(profile);
            FMLClientHandler.instance().getClient().addScheduledTask(() -> target.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), prof)));
        }, 1L, TimeUnit.MILLISECONDS);
    }
}
