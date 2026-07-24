package com.netease.mc.mod.skin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import com.netease.mc.mod.Config;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;

public class SkinHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final SkinHandler skinHandler = new SkinHandler();
    public static HashMap<String, Object> lockObjectMap = new HashMap();
    public static HashMap<String, String> nameSkinMap = new HashMap();
    public static HashMap<String, String> nameCapeMap = new HashMap();
    public static HashMap<String, Boolean> nameSkinMode = new HashMap();
    private static final File assetSkinsDir = new File("./assets/skins");
    private static final int TIMEOUT = 60000;
    private static Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final Cache<String, MinecraftProfileTextures> cache;
    private static final LoadingCache<GameProfile, MinecraftProfileTextures> skinCacheLoader;
    private static ScheduledExecutorService schduler;
    private static final String[] WHITELISTED_DOMAINS;
    private static final Set<GameProfile> onLoading;

    public static String CopySkinToAsset(File f) {
        try {
            if (f == null) {
                return null;
            } else {
                String sha = DigestUtils.sha256Hex(new FileInputStream(f)).toLowerCase();
                String filename = Hashing.sha1().hashUnencodedChars(sha).toString();
                File subDir = new File(assetSkinsDir, filename.substring(0, 2));
                subDir.mkdirs();
                File skin = new File(subDir, filename);
                FileUtils.copyFile(f, skin);
                return "http://127.0.0.1/" + sha;
            }
        } catch (Exception var5) {
            return null;
        }
    }

    public static MinecraftProfileTextures getTexturesWrapper(MinecraftSessionService service, GameProfile profile) {
        if (profile == null) {
            return MinecraftProfileTextures.EMPTY;
        } else {
            Thread current = Thread.currentThread();
            if (current.getName().contains("Client")) {
                Exception ex = new Exception();
                Writer result = new StringWriter();
                PrintWriter printWriter = new PrintWriter(result);
                ex.printStackTrace(printWriter);
                LOGGER.info(result.toString());
                Property packed = service.getPackedTextures(profile);
                return packed != null ? service.unpackTextures(packed) : MinecraftProfileTextures.EMPTY;
            } else if (profile.getName() == null) {
                return MinecraftProfileTextures.EMPTY;
            } else {
                String name = profile.getName();
                LOGGER.info(String.format("player %s start loading skin , ThreadID %s", name, current.getName()));
                MinecraftProfileTextures resultCache = unpackTextures(profile, service.getPackedTextures(profile));
                if (resultCache != null) {
                    return resultCache;
                } else {
                    String LocalSkinUrl = null;
                    String LocalCapeUrl = null;
                    String username = profile.getName();
                    Object object = new Object();
                    lockObjectMap.put(username, object);
                    MessageRequest mrq = new MessageRequest();
                    LOGGER.info("skin:send msg to launcher!");
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

                    MinecraftProfileTexture skin = null;
                    if (LocalSkinUrl != null && LocalSkinUrl != "") {
                        LOGGER.info(String.format("player %s start loading skinurl : %s", name, LocalSkinUrl));
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

                            skin = new MinecraftProfileTexture(url, modelmap);
                        }
                    }

                    MinecraftProfileTexture cape = null;
                    if (LocalCapeUrl != null && LocalCapeUrl != "") {
                        LOGGER.info(String.format("player %s start loading capeurl : %s", name, LocalCapeUrl));
                        String url = CopySkinToAsset(new File(LocalCapeUrl));
                        if (url != null) {
                            cape = new MinecraftProfileTexture(url, (Map)null);
                        }
                    }

                    resultCache = new MinecraftProfileTextures(skin, cape, (MinecraftProfileTexture)null, SignatureState.SIGNED);
                    cache.put(name, resultCache);
                    return resultCache;
                }
            }
        }
    }

    public static MinecraftProfileTextures unpackTextures(final GameProfile profile, final Property serverTextureProperty) {
        if (Config.disableOnlineSkin) return null;

        String mojangTexturesBase64 = null;
        if (serverTextureProperty != null)
            mojangTexturesBase64 = serverTextureProperty.value();

        if (mojangTexturesBase64 == null) {
            MinecraftProfileTextures textureProperty = cache.getIfPresent(profile.getName());
            if (textureProperty != null) {
                return textureProperty;
            } else {
                mojangTexturesBase64 = fetchTexturesBase64(profile.getName());
                if (mojangTexturesBase64 == null) return null;
            }
        }

        final MinecraftTexturesPayload result;
        try {
            final String json = new String(java.util.Base64.getDecoder().decode(mojangTexturesBase64), StandardCharsets.UTF_8);
            result = gson.fromJson(json, MinecraftTexturesPayload.class);
        } catch (final JsonParseException | IllegalArgumentException e) {
            return null;
        }

        if (result == null || result.textures() == null || result.textures().isEmpty()) {
            return null;
        }

        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = result.textures();
        for (final Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : textures.entrySet()) {
            final String url = entry.getValue().getUrl();
            if (!TextureUrlChecker.isAllowedTextureDomain(url)) {
                return null;
            }
        }

        return new MinecraftProfileTextures(
                textures.get(MinecraftProfileTexture.Type.SKIN),
                textures.get(MinecraftProfileTexture.Type.CAPE),
                textures.get(MinecraftProfileTexture.Type.ELYTRA),
                SignatureState.SIGNED
        );
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
        if (body == null) return null;
        UUID uuid = gson.fromJson(body, GameProfile.class).getId();

        body = get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        if (body == null) return null;

        for (JsonElement e : JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("properties")) {
            JsonObject p = e.getAsJsonObject();
            return p.has("value") ? p.get("value").getAsString() : null;
        }
        return null;
    }

    private static String get(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(5000);
            c.setReadTimeout(8000);
            c.setRequestProperty("Accept", "application/json");
            int code = c.getResponseCode();
            if (code != 200) return null;

            try (InputStream in = c.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
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

    public static CompletableFuture<PlayerSkin> getOrLoadWrapper(SkinManager manager, GameProfile gp) {
        if (onLoading.contains(gp)) {
            PlayerSkin playerskin = DefaultPlayerSkin.get(gp);
            return CompletableFuture.completedFuture(playerskin);
        } else {
            MinecraftProfileTextures ret = (MinecraftProfileTextures)skinCacheLoader.getIfPresent(gp);
            if (ret == null) {
                //return manager.registerTextures(gp.getId(), skinCacheLoader.getUnchecked(gp));

                onLoading.add(gp);

                return CompletableFuture.supplyAsync(() -> {
                    MinecraftProfileTextures skins = (MinecraftProfileTextures)skinCacheLoader.getUnchecked(gp);
                    onLoading.remove(gp);
                    return skins;
                }).thenCompose(skins -> {
                    CompletableFuture<CompletableFuture<PlayerSkin>> f = new CompletableFuture<>();
                    RenderSystem.recordRenderCall(() -> f.complete(manager.registerTextures(gp.getId(), skins)));
                    return f.thenCompose(Function.identity());
                });
                //CompletableFuture<PlayerSkin> f = new CompletableFuture<>();
                //RenderSystem.recordRenderCall(() -> manager.registerTextures(gp.getId(), skins).whenComplete((s, e) -> f.complete(s)));
                //return f;

            } else {
                return manager.registerTextures(gp.getId(), ret);
            }
        }
    }

    public static NativeImage processLegacySkinWrapper(HttpTexture texture, NativeImage p_118033_) {
        int i = p_118033_.getHeight();
        int j = p_118033_.getWidth();
        if (j % 64 == 0 && i % 32 == 0) {
            boolean flag = j / i == 2;
            if (flag) {
                NativeImage nativeimage = new NativeImage(64, 64, true);
                nativeimage.copyFrom(p_118033_);
                p_118033_.close();
                p_118033_ = nativeimage;
                nativeimage.fillRect(0, 32, 64, 32, 0);
                nativeimage.copyRect(4, 16, 16, 32, 4, 4, true, false);
                nativeimage.copyRect(8, 16, 16, 32, 4, 4, true, false);
                nativeimage.copyRect(0, 20, 24, 32, 4, 12, true, false);
                nativeimage.copyRect(4, 20, 16, 32, 4, 12, true, false);
                nativeimage.copyRect(8, 20, 8, 32, 4, 12, true, false);
                nativeimage.copyRect(12, 20, 16, 32, 4, 12, true, false);
                nativeimage.copyRect(44, 16, -8, 32, 4, 4, true, false);
                nativeimage.copyRect(48, 16, -8, 32, 4, 4, true, false);
                nativeimage.copyRect(40, 20, 0, 32, 4, 12, true, false);
                nativeimage.copyRect(44, 20, -8, 32, 4, 12, true, false);
                nativeimage.copyRect(48, 20, -16, 32, 4, 12, true, false);
                nativeimage.copyRect(52, 20, -8, 32, 4, 12, true, false);
            }

            HttpTexture.setNoAlpha(p_118033_, 0, 0, 32, 16);
            if (flag) {
                HttpTexture.doNotchTransparencyHack(p_118033_, 32, 0, 64, 32);
            }

            HttpTexture.setNoAlpha(p_118033_, 0, 16, 64, 32);
            HttpTexture.setNoAlpha(p_118033_, 16, 48, 48, 64);
            return p_118033_;
        } else {
            p_118033_.close();
            LOGGER.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", j, i, texture.toString());
            return null;
        }
    }

    static {
        cache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build();
        skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, MinecraftProfileTextures>() {
            public MinecraftProfileTextures load(GameProfile p_load_1_) throws Exception {
                return Minecraft.getInstance().getMinecraftSessionService().getTextures(p_load_1_);
            }
        });
        schduler = Executors.newScheduledThreadPool(10);
        WHITELISTED_DOMAINS = new String[]{".minecraft.net", ".mojang.com", ".163.com", ".netease.com"};
        onLoading = Collections.newSetFromMap(new ConcurrentHashMap());
    }
}
