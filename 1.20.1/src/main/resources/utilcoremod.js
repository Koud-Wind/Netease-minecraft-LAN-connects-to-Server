var CoreModManager = Java.type('com.netease.mc.coremod.CoreModManager');
var OPCODES = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var Opcode2Size = {};

String.prototype.replaceAll = function(s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}

function addFilterCoreMods()
{
    var gameType = CoreModManager.GetGameType();
    // netgame:2
    if (gameType == 2)
    {

         CoreModManager.AddCoreModMethodData(
             "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService",
             "hasJoinedServer",
             "hasJoinedServer",
             "(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/net/InetAddress;)Lcom/mojang/authlib/GameProfile;",
             "com.netease.mc.mod.authlib.authlibWrapper",
             null);

    }

    CoreModManager.AddCoreModMethodData(
         "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService",
         "joinServer",
         "joinServer",
         "(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/lang/String;)V",
         "com.netease.mc.mod.authlib.authlibWrapper",
         null);


   //friendPlay
    CoreModManager.AddCoreModMethodData(
        "com.mojang.authlib.HttpAuthenticationService",
        "performGetRequest",
        "performGetRequest",
        "(Ljava/net/URL;Ljava/lang/String;)Ljava/lang/String;",
        "com.netease.mc.mod.friendplay.FriendPlayMod",
        null);

     //skin
    CoreModManager.AddCoreModMethodData(
         "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService",
         "getTextures",
         "getTextures",
         "(Lcom/mojang/authlib/GameProfile;Z)Ljava/util/Map;",
         "com.netease.mc.mod.skin.SkinHandler",
         null);


     //skin
    CoreModManager.AddCoreModMethodData(
         "net.minecraft.client.renderer.texture.HttpTexture",
         "m_118032_",
         "processLegacySkin",
         "(Lcom/mojang/blaze3d/platform/NativeImage;)Lcom/mojang/blaze3d/platform/NativeImage;",
         "com.netease.mc.mod.skin.SkinHandler",
         null);

    CoreModManager.AddCoreModMethodData(
         "net.minecraft.client.resources.SkinManager",
         "m_118815_",
         "getInsecureSkinInformation",
         "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;",
         "com.netease.mc.mod.skin.SkinHandler",
         null);

    //authlib
    CoreModManager.AddCoreModMethodData(
        "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService",
        "fillGameProfile",
        "fillGameProfile",
        "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;",
        "com.netease.mc.mod.authlib.authlibWrapper",
        null);

    //1.20
    CoreModManager.AddCoreModMethodData(
            "net.minecraft.server.network.ServerLoginPacketListenerImpl",
            "m_203792_",
            "isValidUsername",
            "(Ljava/lang/String;)Z",
            "com.netease.mc.mod.friendplay.FriendPlayMod",
            null,
            true);

    CoreModManager.AddCoreModMethodData(
            "net.minecraft.client.multiplayer.ClientPacketListener",
            "m_213672_",
            "handleServerData",
            "(Lnet/minecraft/network/protocol/game/ClientboundServerDataPacket;)V",
            "com.netease.mc.mod.friendplay.FriendPlayMod",
            null);

    CoreModManager.AddCoreModMethodData(
            "net.minecraft.network.Connection",
            "m_129495_",
            "setEncryptionKey",
            "(Ljavax/crypto/Cipher;Ljavax/crypto/Cipher;)V",
            "com.netease.mc.mod.encryption.EncryptionEnableWrapper",
            "com.netease.mc.mod.encryption.IConnectionOld");

    CoreModManager.AddCoreModMethodData(
                    "net.minecraft.server.MinecraftServer",
                    "m_6328_",
                    "getCompressionThreshold",
                    "()I",
                    "com.netease.mc.mod.encryption.EncryptionEnableWrapper",
                    "com.netease.mc.mod.encryption.IMinecraftServerOld");
}

function initializeCoreMod(){
    addFilterCoreMods()
    var coreModClassDataList = CoreModManager.getCoreModClassDataList();
    var dic = {};
    for (var i in coreModClassDataList)
    {
        var coreModClassData = coreModClassDataList[i]
        dic[coreModClassData.getClassName()] = {
            'target':{
                'type': 'CLASS',
                'name': coreModClassData.getClassName().replaceAll("\\.", '/')
            },
             'transformer':function(node){
                var classData = CoreModManager.getClassData(node.name);
                var coreModMethodDataList = classData.getCoreModMethodDataList();
                for (var j in coreModMethodDataList)
                {
                    var coreModMethodData = coreModMethodDataList[j];
                    CoreModManager.transformCommon(node, coreModMethodData);
                }
                return node;
             }
        }
    }

    return dic;
}