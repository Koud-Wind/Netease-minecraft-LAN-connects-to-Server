package com.netease.mc.mod.mixin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySelectorParser.class)
public abstract class MixinEntitySelectorParser {
    @Redirect(method = "parseNameOrUUID", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;readString()Ljava/lang/String;", remap = false))
    private String readName(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) return "";

        char first = reader.peek();
        if (first == '"' || first == '\'') {
            return reader.readQuotedString();
        }

        int start = reader.getCursor();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }
}