package com.campersamu.chatheads.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

import static com.campersamu.chatheads.ChatHeads.*;
import static net.minecraft.text.TextColor.fromRgb;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    //region Mixin Variables
    @Shadow
    @Final
    private MinecraftServer server;
    //endregion

    //Mixin into the player connect/join event and download the skin for the player (needs a server restart to update)
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void chatheads$invokeDownloadOnJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        final var profile = player.getGameProfile();
        //Use a new Thread since downloading a skin is slow and would slow down the player joining process
        new Thread(() -> {
            synchronized (HEAD_CACHE) {
                final TextColor[][] head = HEAD_CACHE.computeIfAbsent(player.getUuid(), uuid -> chatheads$getPlayerHead(profile, player));
                HEAD_CACHE.put(profile.getId(), head);
            }
        }).start();
    }

    //region Util
    @Unique
    private TextColor[][] chatheads$getPlayerHead(final GameProfile profile, final ServerPlayerEntity player){
        //get skin url
        final boolean mojang = "mojang".equals(CONFIG.url.value());
        final String playerSkinUrl = mojang ? server.getSessionService().getTextures(profile).skin().getUrl() : CONFIG.url.value().replace("<uuid>", profile.getId().toString());

        //return default head if null
        if (playerSkinUrl == null) return DEFAULT_HEAD_TEXTURE;

        //pull the picture
        final BufferedImage image;
        try {
            image = ImageIO.read(new URL(playerSkinUrl));
        } catch (Exception e) {
            LOGGER.warn("Failed to get image for " + player.getName().getString());
            e.printStackTrace();
            return DEFAULT_HEAD_TEXTURE;
        }

        //generate the head
        final TextColor[][] playerHead = new TextColor[8][8];
        final int start = mojang ? 8 : 0;

        for (int x = start; x < start + 8; x++) {
            for (int y = start; y < start + 8; y++) {
                int rgb = image.getRGB(x, y);
                playerHead[y - start][x - start] = fromRgb(rgb & 0xffffff);
            }
        }

        return playerHead;
    }
    //endregion
}
