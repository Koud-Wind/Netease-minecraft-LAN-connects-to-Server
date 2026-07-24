package com.netease.mc.mod.network.socket;

import com.netease.mc.mod.network.message.reply.MessageReply;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientNetworkHandler {
    public static final int SMIDLEN = 2;

    @SubscribeEvent
    public void onServerTick(TickEvent.ClientTickEvent event) {
        while (NetworkSocket.mRecvMsgQueue.count() > 0) {
            String msg = (String) NetworkSocket.mRecvMsgQueue.pop();
            if (!msg.isEmpty()) {
                int smid = getSidMid(msg);
                if (!NetworkHandler.replyHashMap.containsKey(Integer.valueOf(smid))) {
                    System.out.println("the msg is wrong " + smid + " " + msg);
                } else {
                    ((MessageReply) NetworkHandler.replyHashMap.get(Integer.valueOf(smid))).handMessage(msg);
                }
            }
        }
    }

    private int getSidMid(String msg) {
        if (msg.length() < 2) {
            return -1;
        }
        return (msg.charAt(0) * 256) + msg.charAt(1);
    }
}