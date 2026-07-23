package com.netease.mc.mod.network.networkMod;

import com.netease.mc.mod.network.message.reply.MessageReply;
import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.network.socket.NetworkSocket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientNetworkHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int SMIDLEN = 2;

    @SubscribeEvent
    public void onServerTick(ClientTickEvent.Post event) {
        while (NetworkSocket.mRecvMsgQueue.count() > 0) {
            byte[] msg = (byte[]) NetworkSocket.mRecvMsgQueue.pop();
            if (msg.length != 0) {
                int smid = getSidMid(msg);
                if (!NetworkHandler.replyHashMap.containsKey(Integer.valueOf(smid))) {
                    LOGGER.error("the msg is wrong " + smid + " " + String.valueOf(msg));
                } else {
                    LOGGER.info("ClientNetworkHandler receive message: " + smid);
                    ((MessageReply) NetworkHandler.replyHashMap.get(Integer.valueOf(smid))).handMessage(msg);
                }
            }
        }
    }

    private int getSidMid(byte[] msg) {
        if (msg.length < 2) {
            return -1;
        }
        return (msg[0] << 8) | msg[1];
    }
}