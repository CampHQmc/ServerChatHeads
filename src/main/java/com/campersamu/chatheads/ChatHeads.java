package com.campersamu.chatheads;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URLConnection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.text.Text.literal;
import static net.minecraft.text.TextColor.fromRgb;

public class ChatHeads implements ModInitializer {
    public static final String MODID = "chatheads";
    public static final Identifier PLACEHOLDER = Identifier.of(MODID, "player");
    public static final Identifier PIXEL_FONT = Identifier.of(MODID, "pixel");
    public static final Identifier NOXEL_FONT = Identifier.of(MODID, "noxel");

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ChatHeadsConfig CONFIG = ChatHeadsConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", ChatHeads.MODID, ChatHeadsConfig.class);

    public static final Text DEFAULT = paintHead(new TextColor[][]{ // white on grey [?] placeholder
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0xffffff), fromRgb(0xffffff), fromRgb(0xffffff), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0xffffff), fromRgb(0x2e2e2e), fromRgb(0xffffff), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0xffffff), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0xffffff), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0xffffff), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
            {fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e), fromRgb(0x2e2e2e)},
    });
    public static final String STRING = DEFAULT.getString();
    public static final Map<String, Text> CACHE = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MODID); // pixel and noxel font
        Placeholders.register(PLACEHOLDER, (ctx, arg) -> {
            String skinId = arg == null || arg.isEmpty() ? getSkinId(ctx.player()) : arg;
            if (skinId == null || skinId.isEmpty()) return PlaceholderResult.invalid("Missing skin ID!");
            return PlaceholderResult.value(tryGetPlayerHead(skinId));
        });
    }

    public static String getSkinId(ServerPlayerEntity player) {
        if (player == null || player.getServer() == null) return null;
        MinecraftProfileTexture skin = player.getServer().getSessionService().getTextures(player.getGameProfile()).skin();
        if (skin == null) return null;
        return skin.getHash();
    }

    public static Text tryGetPlayerHead(String skinId) {
        if (!CACHE.containsKey(skinId)) { // not already available, so return a placeholder and put the oven on.
            CACHE.put(skinId, DEFAULT.copy().styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Loading...")))));
            new Thread(() -> CACHE.put(skinId, getPlayerHeadImmediate(skinId))).start();
        }
        return CACHE.get(skinId);
    }

    private static Text getPlayerHeadImmediate(String hash) {
        String playerSkinUrl = CONFIG.url.value().replace("<id>", hash);
        BufferedImage image;
        try {
            LOGGER.info("[ChatHeads] Grabbing skin from %s".formatted(playerSkinUrl), hash);
            URLConnection conn = URI.create(playerSkinUrl).toURL().openConnection();
            conn.setRequestProperty("User-Agent", "ServerChatHeads/1.0 (+https://github.com/sisby-folk/ServerChatHeads; <sleepingdragoninn@gmail.com>)");
            image = ImageIO.read(conn.getInputStream());
        } catch (Exception e) {
            LOGGER.warn("[ChatHeads] Failed to get image for {}", hash, e);
            return DEFAULT.copy().styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Couldn't fetch skin!").formatted(Formatting.RED))));
        }

        int[][] headArgb = new int[8][8];

        boolean fullSkin = image.getWidth() == 64;

        if (fullSkin) {
            int faceX = 8;
            int faceY = 8;
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    int rgb = image.getRGB(faceX + x, faceY + y);
                    headArgb[y][x] = rgb;
                }
            }
            int hatX = 40;
            int hatY = 8;
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    int rgb = image.getRGB(hatX + x, hatY + y);
                    if (rgb != 0) headArgb[y][x] = overlay(headArgb[y][x], rgb);
                }
            }
        } else {
            // calculate the non-transparent square bounds
            int startXY = Integer.MAX_VALUE;
            int endXY = Integer.MIN_VALUE;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) != 0) {
                        startXY = Math.min(startXY, Math.min(x, y));
                        endXY = Math.max(endXY, Math.max(x, y));
                    }
                }
            }
            // grab roughly 8x8 pixels from it
            if (startXY < endXY) {
                float step = (endXY + 1 - startXY) / 8.0F;
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        int sourceX = MathHelper.clamp(startXY + Math.round((x + 0.49F) * step), startXY, endXY);
                        int sourceY = MathHelper.clamp(startXY + Math.round((y + 0.49F) * step), startXY, endXY);
                        headArgb[y][x] = image.getRGB(sourceX, sourceY);
                    }
                }
            }
        }

        TextColor[][] playerHead = new TextColor[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (headArgb[x][y] != 0) playerHead[x][y] = fromRgb(headArgb[x][y] & 0xffffff);
            }
        }

        return paintHead(playerHead);
    }

    private static int overlay(int baseARGB, int overlayARGB) {
        float a1 = ColorHelper.Argb.getAlpha(overlayARGB) / 255.0F;
        float r1 = ColorHelper.Argb.getRed(overlayARGB) / 255.0F;
        float g1 = ColorHelper.Argb.getGreen(overlayARGB) / 255.0F;
        float b1 = ColorHelper.Argb.getBlue(overlayARGB) / 255.0F;
        float a2 = ColorHelper.Argb.getAlpha(baseARGB) / 255.0F;
        float r2 = ColorHelper.Argb.getRed(baseARGB) / 255.0F;
        float g2 = ColorHelper.Argb.getGreen(baseARGB) / 255.0F;
        float b2 = ColorHelper.Argb.getBlue(baseARGB) / 255.0F;
        float aF = a1 + (a2*(1-a1));
        float rF = (r1*a1) + (r2*a2*(1-a1));
        float gF = (g1*a1) + (g2*a2*(1-a1));
        float bF = (b1*a1) + (b2*a2*(1-a1));
        return ColorHelper.Argb.getArgb((int)(255 * aF), (int)(255 * rF), (int)(255 * gF), (int)(255 * bF));
    }

    private static @NotNull Text paintHead(TextColor[][] head) {
        if (head == null) return Objects.requireNonNull(DEFAULT);
        MutableText text = Text.empty();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Identifier font = head[y][x] == null ? NOXEL_FONT : PIXEL_FONT;
                TextColor color = head[y][x];

                text = text
                        .append(literal("" + (char) (((int) '\uF810') + y)).setStyle(Style.EMPTY.withColor(color).withFont(font)))
                        .append(literal("\uE001").fillStyle(Style.EMPTY.withFont(font)));
            }
            text = text.append(literal("\uE008").fillStyle(Style.EMPTY.withFont(PIXEL_FONT)));
        }

        text.append(literal("  "));

        return text;
    }
}
