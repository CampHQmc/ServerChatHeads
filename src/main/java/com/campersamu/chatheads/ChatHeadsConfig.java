package com.campersamu.chatheads;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class ChatHeadsConfig extends ReflectiveConfig {
    @Comment({
            "The URL used for downloading player heads.",
            "\"<id>\" will be replaced with the skin texture ID.",
            "Example: https://crafatar.com/avatars/<id>?size=8&overlay"
    })
    public final TrackedValue<String> url = value("http://textures.minecraft.net/texture/<id>");
}
