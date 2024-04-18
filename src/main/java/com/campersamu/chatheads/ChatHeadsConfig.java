package com.campersamu.chatheads;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class ChatHeadsConfig extends ReflectiveConfig {
    @Comment({
            "The URL used for downloading player heads.",
            "Please ensure the downloaded head is 8x8 pixels!",
            "Set to \"mojang\" to use Mojang's Authlib to get the URL automatically.",
            "\"<uuid>\" will be replaced with the player's UUID.",
            "Example: https://crafatar.com/avatars/<uuid>?size=8&overlay"
    })
    public final TrackedValue<String> url = value("mojang");
}
