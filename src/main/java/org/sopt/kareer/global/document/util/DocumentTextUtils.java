package org.sopt.kareer.global.document.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentTextUtils {

    public static String normalize(String text){
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }
}
