package org.sopt.kareer.global.external.discord.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

@Builder
public record DiscordEmbedMessage(
        String content,
        @Singular
        List<Embed> embeds
) {

    @Builder
    public record Embed(
            String title,
            String description,
            @Singular List<Field> fields
    ) {

    }

    @Builder
    public record Field(
            @NonNull String name,
            @NonNull String value,
            Boolean inline
    ) {
        public Field {
            inline = inline != null && inline;
        }
    }
}
