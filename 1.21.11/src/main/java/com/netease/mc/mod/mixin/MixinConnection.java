package com.netease.mc.mod.mixin;

import com.netease.mc.mod.encryption.EncryptionEnableWrapper;
import javax.crypto.Cipher;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Connection.class})
public abstract class MixinConnection {
    @Inject(method = {"setEncryptionKey"}, at = {@At("HEAD")}, cancellable = true)
    private void netease$setEncryptionKey(Cipher decryptCipher, Cipher encryptCipher, CallbackInfo ci) {
        LogManager.getLogger().info("setEncryptionKeyWrapper: " + EncryptionEnableWrapper.NetworkEncrypt);
        if (!EncryptionEnableWrapper.NetworkEncrypt.booleanValue()) {
            ci.cancel();
        }
    }
}