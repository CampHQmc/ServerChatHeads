package com.campersamu.chatheads.mixin;

import com.campersamu.chatheads.ChatHeads;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @ModifyExpressionValue(method = "logChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String stripFromServerLogs(String original) {
        return original.replace(ChatHeads.STRING, "?");
    }

    @ModifyExpressionValue(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String stripFromServerLogsRedirected(String original) { // styled chat logs messages this way instead
        return original.replace(ChatHeads.STRING, "?");
    }
}
