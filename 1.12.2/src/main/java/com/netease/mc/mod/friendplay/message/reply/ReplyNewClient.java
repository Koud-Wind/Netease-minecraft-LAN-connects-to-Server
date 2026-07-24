package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;

public class ReplyNewClient extends Reply {
    public static final int SMID = 1793;

    public void handler(int port) {
        if (!GameState.acceptList.contains(Integer.valueOf(port))) {
            GameState.acceptList.add(Integer.valueOf(port));
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Short.valueOf(GameState.gameid), (byte) 0});
    }
}