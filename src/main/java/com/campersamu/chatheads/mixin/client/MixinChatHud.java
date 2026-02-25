package com.campersamu.chatheads.mixin.client;

import com.campersamu.chatheads.ChatHeads;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @ModifyExpressionValue(method = "logChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String stripFromServerLogs(String original) {
        return original.replace(ChatHeads.STRING, "?");
    }
}
