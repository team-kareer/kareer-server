package org.sopt.kareer.global.document.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDateUtils {
    private static final List<DateTimeFormatter> DEFAULT_DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("d MMM yyyy")
                    .toFormatter(Locale.ENGLISH),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd MMM yyyy")
                    .toFormatter(Locale.ENGLISH)
    );

    public static LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String value = raw.trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        for (DateTimeFormatter formatter : DEFAULT_DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static LocalDate parseLocalDate(String value) {
        try {
            int yy = Integer.parseInt(value.substring(0, 2));
            int month = Integer.parseInt(value.substring(2, 4));
            int day = Integer.parseInt(value.substring(4, 6));

            int year = 2000 + yy;
            LocalDate date = LocalDate.of(year, month, day);

            if (date.isAfter(LocalDate.now())) {
                date = date.minusYears(100);
            }

            return date;
        } catch (Exception e) {
            return null;
        }
    }
}
