package com.campersamu.chatheads.mixin;

import com.campersamu.chatheads.ChatHeads;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;shouldSetPositionOnLoad()Z"))
    private void cacheSkinOnLoad(NbtCompound nbt, CallbackInfo ci) { // happens when fabric tailor loads a skin
        Entity self = (Entity) (Object) this;
        if (self instanceof ServerPlayerEntity spe) ChatHeads.tryGetPlayerHead(ChatHeads.getSkinId(spe));
    }
}
