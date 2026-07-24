package com.netease.mc.core.filter;

import com.netease.mc.core.filter.BaseAsmTransformer;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BaseMethodTransfer implements BaseAsmTransformer.IMethodTransformer {
    private static HashMap<Integer, Integer> Opcode2Size = new HashMap<Integer, Integer>() {
        {
            put(25, 1);
            put(21, 1);
            put(23, 1);
            put(24, 2);
            put(22, 2);
        }
    };

    @Override
    public void transform(ClassNode cn, String classObfName, MethodNode mn, String srgName, boolean devEnv) {
        MethodNode wrapperMethod = new MethodNode(mn.access, mn.name, mn.desc, (String) null, (String[]) mn.exceptions.toArray(new String[mn.exceptions.size()]));
        BaseAsmTransformer.RegisterTransformer r = (BaseAsmTransformer.RegisterTransformer) getClass().getAnnotation(BaseAsmTransformer.RegisterTransformer.class);
        mn.name = r.mcpName() + "Old";
        mn.access = 1;
        int offset = 0;
        for (Integer opcode : getParamOpcodes(mn.desc)) {
            int size = Opcode2Size.getOrDefault(opcode, 0).intValue();
            if (size > 0) {
                wrapperMethod.instructions.add(new VarInsnNode(opcode.intValue(), offset));
                offset += size;
            }
        }
        if (r.oldInterface().getName().equals(Object.class.getName())) {
            wrapperMethod.instructions.add(new MethodInsnNode(184, r.newClass().getName().replace(".", "/"), r.mcpName() + "Wrapper", "(L" + cn.name.replace(".", "/") + ";" + r.desc().substring(1), false));
        } else {
            wrapperMethod.instructions.add(new MethodInsnNode(184, r.newClass().getName().replace(".", "/"), r.mcpName() + "Wrapper", "(L" + r.oldInterface().getName().replace(".", "/") + ";" + r.desc().substring(1), false));
        }
        wrapperMethod.instructions.add(new InsnNode(getReturnOpcode(mn.desc)));
        wrapperMethod.maxStack = 3;
        cn.methods.add(wrapperMethod);
        if (!r.oldInterface().getName().equals(Object.class.getName()) && !cn.interfaces.contains(r.oldInterface().getName().replace(".", "/"))) {
            cn.interfaces.add(r.oldInterface().getName().replace(".", "/"));
        }
    }

    private ArrayList<Integer> getParamOpcodes(String desc) {
        String params = desc.substring(1, desc.indexOf(41));
        ArrayList<Integer> paramOpcodelist = new ArrayList<>();
        paramOpcodelist.add(25);
        int i = 0;
        while (i < params.length()) {
            switch (params.charAt(i)) {
                case 'B':
                case 'C':
                case 'I':
                case 'S':
                case 'Z':
                    paramOpcodelist.add(21);
                    break;
                case 'D':
                    paramOpcodelist.add(24);
                    break;
                case 'E':
                case 'G':
                case 'H':
                case 'K':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                default:
                    LogManager.getLogger().error("unknown ASM : " + params.charAt(i));
                    break;
                case 'F':
                    paramOpcodelist.add(23);
                    break;
                case 'J':
                    paramOpcodelist.add(22);
                    break;
                case 'L':
                    paramOpcodelist.add(25);
                    i = params.indexOf(59, i);
                    break;
                case '[':
                    paramOpcodelist.add(25);
                    while (params.charAt(i) == '[') {
                        i++;
                    }
                    if (params.charAt(i) == 'L') {
                        i = params.indexOf(59, i);
                    }
                    break;
            }
            i++;
        }
        return paramOpcodelist;
    }

    private int getReturnOpcode(String desc) {
        char ret = desc.charAt(desc.indexOf(41) + 1);
        int returnOpcode = 0;
        switch (ret) {
            case 'B':
            case 'C':
            case 'I':
            case 'S':
            case 'Z':
                returnOpcode = 172;
                break;
            case 'D':
                returnOpcode = 175;
                break;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'W':
            case 'X':
            case 'Y':
            default:
                LogManager.getLogger().error("unknown return ASM : " + ret);
                break;
            case 'F':
                returnOpcode = 174;
                break;
            case 'J':
                returnOpcode = 173;
                break;
            case 'L':
            case '[':
                returnOpcode = 176;
                break;
            case 'V':
                returnOpcode = 177;
                break;
        }
        return returnOpcode;
    }
}