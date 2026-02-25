package com.campersamu.chatheads.mixin;

import com.campersamu.chatheads.ChatHeads;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "sendPlayerStatus", at = @At("TAIL"))
    private void cacheSkinOnSendStatus(ServerPlayerEntity player, CallbackInfo ci) { // happens when fabric tailor reloads a skin
        ChatHeads.tryGetPlayerHead(ChatHeads.getSkinId(player));
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"))
    private void cacheOnSpawn(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) { // for new players
        ChatHeads.tryGetPlayerHead(ChatHeads.getSkinId(player));
    }
}
