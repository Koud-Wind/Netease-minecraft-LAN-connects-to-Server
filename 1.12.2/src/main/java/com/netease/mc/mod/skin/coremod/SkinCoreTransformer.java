package com.netease.mc.mod.skin.coremod;

import com.google.common.collect.ImmutableSet;
import com.netease.mc.mod.skin.SkinHandler;
import java.util.Set;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SkinCoreTransformer implements IClassTransformer {

        class msfix extends MethodVisitor {
        public msfix() {
            super(327680, (MethodVisitor) null);
        }
    }

        class SkinGetTexturesVisitor extends ClassVisitor {
        Set<String> names;
        String cl;
        private final String INVOKE_TARGET_CLASS;

        public SkinGetTexturesVisitor(ClassVisitor cv) {
            super(327680, cv);
            this.INVOKE_TARGET_CLASS = SkinHandler.class.getName().replace(".", "/");
            this.cl = FMLDeobfuscatingRemapper.INSTANCE.unmap("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
            this.names = ImmutableSet.of("getTextures");
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (this.names.contains(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.cl, name, desc)) && "(Lcom/mojang/authlib/GameProfile;Z)Ljava/util/Map;".equals(desc)) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                mv.visitVarInsn(25, 1);
                mv.visitVarInsn(21, 2);
                mv.visitMethodInsn(184, this.INVOKE_TARGET_CLASS, "getTexturesWrapper", "(Lcom/mojang/authlib/GameProfile;Z)Ljava/util/Map;", false);
                LogManager.getLogger().info("");
                mv.visitInsn(176);
                mv.visitMaxs(3, 2);
                mv.visitEnd();
                return SkinCoreTransformer.this.new msfix();
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

        class SkinLoadSkinFromCacheVisitor extends ClassVisitor {
        Set<String> names;
        String cl;
        private final String INVOKE_TARGET_CLASS;

        public SkinLoadSkinFromCacheVisitor(ClassVisitor cv) {
            super(327680, cv);
            this.INVOKE_TARGET_CLASS = SkinHandler.class.getName().replace(".", "/");
            this.cl = FMLDeobfuscatingRemapper.INSTANCE.unmap("net.minecraft.client.resources.SkinManager");
            this.names = ImmutableSet.of("loadSkinFromCache", "func_152788_a");
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ("(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;".equals(desc)) {
                LogManager.getLogger().info("replace " + desc);
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                mv.visitVarInsn(25, 1);
                mv.visitMethodInsn(184, this.INVOKE_TARGET_CLASS, "loadSkinFromCacheWrapper", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;", false);
                mv.visitInsn(176);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
                return SkinCoreTransformer.this.new msfix();
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService".equals(transformedName)) {
            return transformSkin(basicClass);
        }
        if ("net.minecraft.client.resources.SkinManager".equals(transformedName)) {
            return transformLoadSkinCache(basicClass);
        }
        return basicClass;
    }

    private byte[] transformSkin(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(1);
        classReader.accept(new SkinGetTexturesVisitor(classWriter), 8);
        return classWriter.toByteArray();
    }

    private byte[] transformLoadSkinCache(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(1);
        classReader.accept(new SkinLoadSkinFromCacheVisitor(classWriter), 8);
        return classWriter.toByteArray();
    }
}