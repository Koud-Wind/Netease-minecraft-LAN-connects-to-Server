package com.netease.mc.core.filter;

import com.netease.mc.mod.filter.FilterWrapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.FMLRelaunchLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class BaseAsmTransformer implements IClassTransformer {
    private Map<String, Map<String, IMethodTransformer>> map = new HashMap();

        public interface IMethodTransformer {
        void transform(ClassNode classNode, String str, MethodNode methodNode, String str2, boolean z);
    }

        @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RegisterTransformer {
        String className();

        String srgName() default "";

        String mcpName();

        String desc();

        Class oldInterface() default Object.class;

        Class newClass() default FilterWrapper.class;
    }

    protected BaseAsmTransformer() {
        RegisterTransformer annotation;
        for (Class<?> c : getClass().getDeclaredClasses()) {
            if (IMethodTransformer.class.isAssignableFrom(c) && (annotation = (RegisterTransformer) c.getAnnotation(RegisterTransformer.class)) != null) {
                try {
                    IMethodTransformer transformer = (IMethodTransformer) c.asSubclass(IMethodTransformer.class).newInstance();
                    String srgName = annotation.srgName().equals("") ? annotation.mcpName() : annotation.srgName();
                    hookMethod(annotation.className(), srgName, annotation.mcpName(), annotation.desc(), transformer);
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }

    protected void hookMethod(String className, String srgName, String mcpName, String desc, IMethodTransformer targetTransformer) {
        if (!this.map.containsKey(className)) {
            this.map.put(className, new HashMap());
        }
        this.map.get(className).put(srgName + desc, targetTransformer);
        this.map.get(className).put(mcpName + desc, targetTransformer);
    }

    public byte[] transform(String obfClassName, String className, byte[] bytes) {
        if (!this.map.containsKey(className)) {
            return bytes;
        }
        Map<String, IMethodTransformer> transMap = this.map.get(className);
        boolean deobfuscatedEnvironment = obfClassName.equals(className);
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        List<MethodNode> ml = new ArrayList<>();
        ml.addAll(cn.methods);
        for (MethodNode mn : ml) {
            String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(obfClassName, mn.name, mn.desc);
            String methodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(mn.desc);
            if (transMap.containsKey(methodName + methodDesc)) {
                try {
                    transMap.get(methodName + methodDesc).transform(cn, obfClassName, mn, methodName, deobfuscatedEnvironment);
                } catch (Exception e) {
                    FMLRelaunchLog.warning("An error happened when transforming method %s in class %s(%s). The whole class was not modified.", new Object[]{methodName + methodDesc, obfClassName, className});
                    e.printStackTrace();
                    return bytes;
                }
            }
        }
        ClassWriter cw = new ClassWriter(1);
        cn.accept(cw);
        return cw.toByteArray();
    }
}