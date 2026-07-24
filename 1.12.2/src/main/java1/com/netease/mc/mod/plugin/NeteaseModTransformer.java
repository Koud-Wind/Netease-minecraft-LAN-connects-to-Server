package com.netease.mc.mod.plugin;

import com.netease.mc.mod.friendplay.PlayerEnterWorldEventHandler;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewClient;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.network.socket.NetworkSocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.main.Main;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.network.NetworkManager;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

/* loaded from: YggdrasilServerTransformer.class */
public class NeteaseModTransformer implements IClassTransformer {
    //net.minecraft.client.main.Main
    //net.minecraft.client.gui.GuiPlayerTabOverlay
    //net.minecraft.network.NetworkManager
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.main.Main")) {
            return mainTransform(basicClass);
        } if (transformedName.equals("net.minecraft.client.gui.GuiPlayerTabOverlay") || transformedName.equals("net.minecraft.network.NetworkManager")) {
            return playerTabOverlayTransform(basicClass);
        }
        return basicClass;
    }

    private byte[] playerTabOverlayTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        final ClassWriter classWriter = new ClassWriter(classReader, 2);
        ClassVisitor classVisitor = new ClassVisitor(ASM4, classWriter) {

            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (name.equals("f") && descriptor.equals("()Z")) {
                    MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, name, descriptor, null, null);
                    {
                        AnnotationVisitor annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/minecraftforge/fml/relauncher/SideOnly;", true);
                        annotationVisitor0.visitEnum("value", "Lnet/minecraftforge/fml/relauncher/Side;", "CLIENT");
                        annotationVisitor0.visitEnd();
                    }
                    methodVisitor.visitCode();
                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    methodVisitor.visitLineNumber(417, label0);
                    methodVisitor.visitInsn(ICONST_1);
                    methodVisitor.visitInsn(IRETURN);
                    Label label1 = new Label();
                    methodVisitor.visitLabel(label1);
                    methodVisitor.visitLocalVariable("this", "Lnet/minecraft/network/NetworkManager;", null, label0, label1, 0);
                    methodVisitor.visitMaxs(1, 1);
                    methodVisitor.visitEnd();
                    return null;

                } else if (name.equals("a") && descriptor.equals("(IIILbsc;)V")) {
                    MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PROTECTED, name, descriptor, null, null);
                    methodVisitor.visitCode();
                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    methodVisitor.visitLineNumber(297, label0);
                    methodVisitor.visitVarInsn(ILOAD, 1);
                    methodVisitor.visitVarInsn(ILOAD, 2);
                    methodVisitor.visitVarInsn(ILOAD, 3);
                    methodVisitor.visitVarInsn(ALOAD, 4);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "com/netease/mc/mod/plugin/NeteaseModTransformer", "drawPing", "(IIILnet/minecraft/client/network/NetworkPlayerInfo;)V", false);
                    Label label1 = new Label();
                    methodVisitor.visitLabel(label1);
                    methodVisitor.visitLineNumber(299, label1);
                    methodVisitor.visitInsn(RETURN);
                    Label label2 = new Label();
                    methodVisitor.visitLabel(label2);
                    methodVisitor.visitLocalVariable("this", "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;", null, label0, label2, 0);
                    methodVisitor.visitLocalVariable("width", "I", null, label0, label2, 1);
                    methodVisitor.visitLocalVariable("x", "I", null, label0, label2, 2);
                    methodVisitor.visitLocalVariable("y", "I", null, label0, label2, 3);
                    methodVisitor.visitLocalVariable("info", "Lnet/minecraft/client/network/NetworkPlayerInfo;", null, label0, label2, 4);
                    methodVisitor.visitMaxs(4, 5);
                    methodVisitor.visitEnd();
                    return null;
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

        };
        classReader.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }

    private byte[] mainTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        final ClassWriter classWriter = new ClassWriter(classReader, 2);
        ClassVisitor classVisitor = new ClassVisitor(ASM4, classWriter) {

            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (name.equals("main")) {
                    MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                    methodVisitor.visitCode();
                    return new MethodVisitor(ASM4, methodVisitor) {
                        public void visitCode() {
                            super.visitCode();
                            Label label0 = new Label();
                            methodVisitor.visitLabel(label0);
                            methodVisitor.visitLineNumber(63, label0);
                            methodVisitor.visitVarInsn(ALOAD, 0);
                            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/netease/mc/mod/plugin/NeteaseModTransformer", "onMain", "([Ljava/lang/String;)V", false);
                        }
                    };
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

        };
        classReader.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }

    public static void drawPing(int width, int x, int y, NetworkPlayerInfo info) {
        if (info == null) return;

        int ping = info.getResponseTime();
        String text = ping < 0 ? "?" : Integer.toString(ping);

        int color;
        if (ping < 0) {
            color = 11141120;      // 0xAA0000
        } else if (ping <= 10) {
            color = 16777215;      // 0xFFFFFF
        } else if (ping <= 150) {
            color = 5635925;       // 0x55FF55
        } else if (ping <= 300) {
            color = 16777045;      // 0xFFFF55
        } else {
            color = 16733525;      // 0xFF5555
        }

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;

        int textW = font.getStringWidth(text);
        int drawX = x + width - 1 - textW;
        int drawY = y;

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        font.drawStringWithShadow(text, (float) drawX, (float) drawY, color);

        GlStateManager.disableBlend();
    }


    private static boolean Minecraft$checked = false;
    public static void onMain(String[] args) {
        boolean shouldFreeze = false;
        if (!Minecraft$checked) {
            Minecraft$checked = true;
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
        }

        if (shouldFreeze) {
            NetworkHandler.networkHandler.register(1793, new ReplyNewClient());

            while (true) {

                byte[] msg = NetworkSocket.mRecvMsgQueue.pop().getBytes(StandardCharsets.UTF_8);
                if (msg.length != 0) {
                    if (msg.length == 2 && msg[0] == 0 && msg[1] == 1) System.exit(0);

                    int smid = msg.length < 2 ? -1 : msg[0] << 8 | msg[1];
                    if (NetworkHandler.replyHashMap.containsKey(smid)) {
                        LogManager.getLogger().info("ClientNetworkHandler receive message: " + smid);
                        NetworkHandler.replyHashMap.get(smid).handMessage(new String(msg, StandardCharsets.UTF_8));
                    }
                }


            }

        }
    }
}