package org.sopt.kareer.global.external.discord.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.kareer.global.exception.customexception.CustomException;
import org.sopt.kareer.global.external.discord.constant.DiscordConstants;
import org.sopt.kareer.global.external.discord.dto.DiscordEmbedMessage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordEmbedMessageBuilder {
    public static DiscordEmbedMessage buildDiscordEmbedMessage(CustomException e, HttpServletRequest request, String profile) {
        String method = request.getMethod();
        String url = getRequestUrl(request);
        String timestamp = LocalDateTime.now().toString();
        String title = "🚨 5xx Error (" + profile + ")";
        String stackTrace = truncate(getStackTrace(e), DiscordConstants.MAX_TRACE_LENGTH);

        return DiscordEmbedMessage.builder()
                .content("# 🔥레전드 상황 발생!!!!")
                .embed(embed(title, e, method, url, timestamp, stackTrace))
                .build();
    }

    private static DiscordEmbedMessage.Embed embed(
            String title,
            CustomException e,
            String method,
            String url,
            String timestamp,
            String stackTrace
    ) {
        return DiscordEmbedMessage.Embed.builder()
                .title(title)
                .description(" **500 에러 발생** ")
                .field(field("Status", String.valueOf(e.getErrorCode().getHttpStatus()), true))
                .field(field("ErrorCode", String.valueOf(e.getErrorCode()), true))
                .field(field("Message", truncate(e.getMessageDetail(), DiscordConstants.MAX_MESSAGE_LENGTH), false))
                .field(field("Request", "`" + method + " " + truncate(url, DiscordConstants.MAX_URL_LENGTH) + "`", false))
                .field(field("Time", "`" + timestamp + "`", true))
                .field(field("Stacktrace", "```" + stackTrace + "```", false))
                .build();
    }

    private static DiscordEmbedMessage.Field field(String name, String value, boolean inline) {
        return DiscordEmbedMessage.Field.builder()
                .name(name)
                .value(value == null || value.isBlank() ? "-" : value)
                .inline(inline)
                .build();
    }

    private static String getRequestUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        return query == null ? url.toString() : url.append("?").append(query).toString();
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static String truncate(String s, int max) {
        if (s == null) return "-";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "\n...(생략)";
    }
}
