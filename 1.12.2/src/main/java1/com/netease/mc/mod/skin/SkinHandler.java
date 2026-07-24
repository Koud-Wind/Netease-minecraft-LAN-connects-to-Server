//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.netease.mc.mod.skin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SkinHandler {
    public static final SkinHandler skinHandler = new SkinHandler();
    public static HashMap<String, Object> lockObjectMap = new HashMap();
    public static HashMap<String, String> nameSkinMap = new HashMap();
    public static HashMap<String, String> nameCapeMap = new HashMap();
    public static HashMap<String, Boolean> nameSkinMode = new HashMap();
    private static final File assetSkinsDir = new File("./assets/skins");
    private static final int TIMEOUT = 60000;
    private static Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final Cache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> cache;
    private static final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCacheLoader;
    private static ScheduledExecutorService schduler;
    private static final String[] WHITELISTED_DOMAINS;
    private static final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> defaultReturn;
    private static final Set<GameProfile> onLoading;

    public static String CopySkinToAsset(File f) {
        try {
            if (f == null) {
                return null;
            } else {
                String sha = DigestUtils.sha256Hex(new FileInputStream(f)).toLowerCase();
                File subDir = new File(assetSkinsDir, sha.substring(0, 2));
                subDir.mkdirs();
                File skin = new File(subDir, sha);
                FileUtils.copyFile(f, skin);
                return "http://127.0.0.1/" + sha;
            }
        } catch (Exception var4) {
            return null;
        }
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTexturesWrapper(GameProfile profile, boolean requireSecure) {
        if (profile == null) {
            return new HashMap();
        } else {
            Thread current = Thread.currentThread();
            if (current.getName().contains("Client")) {
                Exception ex = new Exception();
                Writer result = new StringWriter();
                PrintWriter printWriter = new PrintWriter(result);
                ex.printStackTrace(printWriter);
                Common.Log(result.toString());
                return getTextures(profile, requireSecure);
            } else if (profile.getName() == null) {
                return new HashMap();
            } else {
                String name = profile.getName();
                Common.Log(String.format("player %s start loading skin , ThreadID %s", name, current.getName()));
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> resultCache = getTextures(profile, requireSecure);
                if (resultCache != null && resultCache.size() != 0) {
                    return resultCache;
                } else {
                    resultCache = new HashMap();
                    String LocalSkinUrl = null;
                    String LocalCapeUrl = null;
                    String username = profile.getName();
                    Minecraft mc = Minecraft.getMinecraft();
                    Object object = new Object();
                    lockObjectMap.put(username, object);
                    MessageRequest mrq = new MessageRequest();
                    Common.Log("skin:send msg to launcher!");
                    mrq.send(2049, new Object[]{GameState.gameid, username});
                    mrq.send(2050, new Object[]{GameState.gameid, username, profile.getId().toString()});
                    synchronized(lockObjectMap.get(username)) {
                        try {
                            lockObjectMap.get(username).wait(60000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    lockObjectMap.remove(username);
                    if (nameSkinMap.containsKey(profile.getName())) {
                        LocalSkinUrl = (String)nameSkinMap.get(username);
                    }

                    if (nameCapeMap.containsKey(profile.getName())) {
                        LocalCapeUrl = (String)nameCapeMap.get(username);
                    }

                    if (LocalSkinUrl != null && LocalSkinUrl != "") {
                        Common.Log(String.format("player %s start loading skinurl : %s", name, LocalSkinUrl));
                        String url = CopySkinToAsset(new File(LocalSkinUrl));
                        if (url != null) {
                            HashMap modelmap = null;
                            if (isSlim(new File(LocalSkinUrl))) {
                                modelmap = new HashMap<String, String>() {
                                    {
                                        this.put("model", "slim");
                                    }
                                };
                            }

                            resultCache.put(Type.SKIN, new MinecraftProfileTexture(url, modelmap));
                        }
                    }

                    if (LocalCapeUrl != null && LocalCapeUrl != "") {
                        Common.Log(String.format("player %s start loading capeurl : %s", name, LocalCapeUrl));
                        String url = CopySkinToAsset(new File(LocalCapeUrl));
                        if (url != null) {
                            resultCache.put(Type.CAPE, new MinecraftProfileTexture(url, (Map)null));
                        }
                    }

                    cache.put(name, resultCache);
                    return resultCache;
                }
            }
        }
    }

    private static boolean isSlim(File skinPng) {
        if (skinPng == null) return false;

        BufferedImage img;
        try {
            img = ImageIO.read(skinPng);
        } catch (Exception e) {
            return false;
        }

        int s = img.getWidth() / 64;
        return isTransparent(img,
                46 * s, 52 * s,
                47 * s, 63 * s
        );
    }

    private static boolean isTransparent(BufferedImage img, int x0, int y0, int x1, int y1) {
        int w = img.getWidth();
        int h = img.getHeight();

        if (w <= 0 || h <= 0 || !img.getColorModel().hasAlpha()) return false;

        int sx = Math.max(0, Math.min(w, Math.min(x0, x1)));
        int ex = Math.max(0, Math.min(w, Math.max(x0, x1)));
        int sy = Math.max(0, Math.min(h, Math.min(y0, y1)));
        int ey = Math.max(0, Math.min(h, Math.max(y0, y1)));

        if (sx >= ex || sy >= ey) return true;

        for (int y = sy; y <= ey; y++) {
            for (int x = sx; x <= ex; x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                if (a == 0) return true;
            }
        }
        return false;
    }

    private static String fetchTexturesBase64(String playerName) {
        String body = get("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        if (body == null) return null;
        UUID uuid = gson.fromJson(body, GameProfile.class).getId();

        body = get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        if (body == null) return null;

        for (JsonElement e : new JsonParser().parse(body).getAsJsonObject().getAsJsonArray("properties")) {
            JsonObject p = e.getAsJsonObject();
            return p.has("value") ? p.get("value").getAsString() : null;
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
            if (code != 200) return null;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            } finally {
                c.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static HttpsURLConnection openUnsafeHttps(String url) throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new SecureRandom());

        HttpsURLConnection c = (HttpsURLConnection) new URL(url).openConnection();
        c.setSSLSocketFactory(sc.getSocketFactory());
        c.setHostnameVerifier((h, s) -> true);
        return c;
    }

    private static boolean isWhitelistedDomain(String url) {
        URI uri = null;

        try {
            uri = new URI(url);
        } catch (URISyntaxException var4) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }

        String domain = uri.getHost();

        for(int i = 0; i < WHITELISTED_DOMAINS.length; ++i) {
            if (domain.endsWith(WHITELISTED_DOMAINS[i])) {
                return true;
            }
        }

        return false;
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        String mojangTexturesBase64 = null;

        final Property serverTextureProperty = Iterables.getFirst(profile.getProperties().get("textures"), null);
        if (serverTextureProperty != null)
            mojangTexturesBase64 = serverTextureProperty.getValue();

        if (mojangTexturesBase64 == null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textureProperty = cache.getIfPresent(profile.getName());
            if (textureProperty != null) {
                return textureProperty;
            } else {
                mojangTexturesBase64 = fetchTexturesBase64(profile.getName());
                if (mojangTexturesBase64 == null) return new HashMap();
            }
        }

        MinecraftTexturesPayload result;
        try {
            String json = new String(Base64.decodeBase64(mojangTexturesBase64), Charsets.UTF_8);
            result = (MinecraftTexturesPayload) gson.fromJson(json, MinecraftTexturesPayload.class);
        } catch (JsonParseException var7) {
            return new HashMap();
        }

        if (result.getTextures() == null) {
            return new HashMap();
        } else {
            for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : result.getTextures().entrySet()) {
                if (!isWhitelistedDomain(((MinecraftProfileTexture) entry.getValue()).getUrl())) {
                    return new HashMap();
                }
            }

            return result.getTextures();
        }
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCacheWrapper(final GameProfile gp) {
        if (onLoading.contains(gp)) {
            return defaultReturn;
        } else {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> ret = (Map)skinCacheLoader.getIfPresent(gp);
            if (ret == null) {
                onLoading.add(gp);
                String name = gp.getName() == null ? gp.toString() : gp.getName();
                (new Thread(new Runnable() {
                    public void run() {
                        SkinHandler.skinCacheLoader.getUnchecked(gp);
                        SkinHandler.onLoading.remove(gp);
                    }
                }, "Skin-Fetch-" + name)).start();
                return defaultReturn;
            } else {
                return ret;
            }
        }
    }

    public static void loadSkullTexture(final GameProfile profile, final NBTTagCompound target) {
        target.	removeTag("SkullOwner");
        schduler.schedule(new Runnable() {
            public void run() {
                final GameProfile prof = TileEntitySkull.updateGameprofile(profile);
                FMLClientHandler.instance().getClient().addScheduledTask(new Runnable() {
                    public void run() {
                        target.	setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), prof));
                    }
                });
            }
        }, 1L, TimeUnit.MILLISECONDS);
    }

    static {
        cache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build();
        skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
                return Minecraft.getMinecraft().getSessionService().getTextures(p_load_1_, false);
            }
        });
        schduler = Executors.newScheduledThreadPool(10);
        WHITELISTED_DOMAINS = new String[]{".minecraft.net", ".mojang.com", ".163.com", ".netease.com"};
        defaultReturn = Collections.emptyMap();
        onLoading = Collections.newSetFromMap(new ConcurrentHashMap());
    }
}
