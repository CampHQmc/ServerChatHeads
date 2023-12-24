package com.campersamu.chatheads;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.text.Text.literal;
import static net.minecraft.text.TextColor.fromRgb;

public class ChatHeadsInit implements DedicatedServerModInitializer {
    //region Constants
    public static final String MODID = "chatheads";
    public static final TextColor[][] DEFAULT_HEAD_TEXTURE = new TextColor[][]{   //hex 0xC01044 -> TextColor.fromRgb(0xC01044)
            {fromRgb(0x191919), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x191919)},
            {fromRgb(0x191919), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x191919)},
            {fromRgb(0x191919), fromRgb(0x191919), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x0c0c0c), fromRgb(0x191919), fromRgb(0x191919)},
            {fromRgb(0x191919), fromRgb(0x191919), fromRgb(0x191919), fromRgb(0x191919), fromRgb(0x191919), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0x191919)},

            {fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0)},
            {fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0)},

            {fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0)},
            {fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0), fromRgb(0xffd7b0)},
    };
    public static final Text DEFAULT_HEAD = paintHead(DEFAULT_HEAD_TEXTURE);
    public static final HashMap<UUID, TextColor[][]> HEAD_CACHE = new HashMap<>();
    //endregion

    @Override
    public void onInitializeServer() {
        //Add Mod Resources to Polymer Resource Pack
        PolymerRPUtils.addAssetSource(MODID);

        //Register Placeholder
        Placeholders.register(new Identifier(MODID, "player"), (ctx, arg) -> {
            if ((arg == null || arg.isEmpty()) && ctx.gameProfile() != null)
                return PlaceholderResult.value(paintHead(HEAD_CACHE.getOrDefault(ctx.gameProfile().getId(), DEFAULT_HEAD_TEXTURE)));
            final var playerProfile = ctx.server().getUserCache().findByName(arg);
            return playerProfile.map(gameProfile -> PlaceholderResult.value(paintHead(HEAD_CACHE.getOrDefault(gameProfile.getId(), DEFAULT_HEAD_TEXTURE))))
                    .orElseGet(() -> PlaceholderResult.value(DEFAULT_HEAD));
        });
    }

    //region Util
    public static Text paintHead(TextColor[][] head) {
        MutableText text = Text.empty();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                text = text
                        .append(literal("" + (char) (((int) '\uF810') + y)).setStyle(Style.EMPTY.withColor(head[y][x]).withFont(Identifier.of(MODID, "pixel"))))
                        .append(literal("\uE001").fillStyle(Style.EMPTY.withFont(Identifier.of(MODID, "pixel"))));
            }
            text = text.append(literal("\uE008").fillStyle(Style.EMPTY.withFont(Identifier.of(MODID, "pixel"))));
        }

        text.append(literal(" "));

        return text;
    }
    //endregion
}
