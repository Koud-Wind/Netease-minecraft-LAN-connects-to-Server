package com.netease.mc.mod.mixin;

import com.netease.mc.mod.Config;
import com.netease.mc.mod.friendplay.PlayerEnterWorldEventHandler;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewClient;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.network.socket.NetworkSocket;
import net.minecraft.client.main.Main;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// - modify
@Mixin(Main.class)
public class MainMixin {

    @Unique
    private static boolean Minecraft$checked = false;

    @Inject(method = "main", at = @At("HEAD"))
    private static void onMain(String[] args, CallbackInfo ci) throws InterruptedException {
        boolean shouldFreeze = false;
        if (!Minecraft$checked) {
            Minecraft$checked = true;
            /*
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(System.getProperty("user.dir")))) {
                for (Path p : ds) {
                    if (!Files.isRegularFile(p)) continue;

                    String name = p.getFileName().toString();
                    if (!name.startsWith("#-#")) continue;
                    String portText = name.substring(3);
                    if (portText.endsWith("#")) {
                        shouldFreeze = true;
                        portText = portText.substring(0, portText.length() - 1);
                        new MessageRequest().send(1281, new Object[]{GameState.gameid});
                        new MessageRequest().send(1537, new Object[]{GameState.gameid, 0, Integer.parseInt(portText)});
                    }
                    int tmpPort = Integer.parseInt(portText);
                    if (tmpPort < 1 || tmpPort > 65535) continue;

                    PlayerEnterWorldEventHandler.localPort = tmpPort;
                    break;
                }
            } catch (IOException | NumberFormatException ignored) {}
             */
            shouldFreeze = Config.disableGameClient;
        }

        if (shouldFreeze) {
            NetworkHandler.networkHandler.register(1793, new ReplyNewClient());
            //NetworkHandler.networkHandler.register(1799, new ReplyJoinGame());
            //NetworkHandler.networkHandler.register(1538, new ReplyStartExistSingle());
            //NetworkHandler.networkHandler.register(1793, new ReplyNewClient());
            //NetworkHandler.networkHandler.register(1540, new ReplyNewSingleV2());
            //NetworkHandler.networkHandler.register(1796, new ReplyReconnect());
            //NetworkHandler.networkHandler.registerAsync(2305, new ReplyOpScreenShot());
            //NetworkHandler.networkHandler.register(5121, new UpdateTokenReply());
            //NetworkHandler.networkHandler.register(1, new ReplyCloseMinecraft());

            //try (DataOutputStream out = new DataOutputStream(NetworkSocket.mSocket.getOutputStream())) {
                while (true) {
                    byte[] msg = NetworkSocket.mRecvMsgQueue.pop();
                    if (msg.length != 0) {
                        //LogManager.getLogger().info("cnm " + Arrays.toString(msg));
                        if (msg.length == 2 && msg[0] == 0 && msg[1] == 1) System.exit(0);

                        int smid = msg.length < 2 ? -1 : msg[0] << 8 | msg[1];
                        if (NetworkHandler.replyHashMap.containsKey(smid)) {
                            LogManager.getLogger().info("ClientNetworkHandler receive message: " + smid);
                            NetworkHandler.replyHashMap.get(smid).handMessage(msg);
                        }
                    }

                    //byte[] msg2 = NetworkSocket.mSendMsgQueue.pop();
                    //if (msg2.length != 0) {
                    //    LogManager.getLogger().info("cnm2 " + Arrays.toString(msg));
                    //    out.writeShort(LittleEndian.littleShort(msg2.length));
                    //    out.write(msg2);
                    //    out.flush();
                    //}

                }
            //} catch (IOException e) {
            //    Common.CatchException(e);
            //}

        }
    }
}
