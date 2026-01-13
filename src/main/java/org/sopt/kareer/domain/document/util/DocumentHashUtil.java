package org.sopt.kareer.domain.document.util;

import org.sopt.kareer.domain.exception.DocumentErrorCode;
import org.sopt.kareer.domain.exception.DocumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public class DocumentHashUtil {

    public static String sha256(File file) {
        try (InputStream is = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new DocumentException(DocumentErrorCode.DOCUMENT_HASH_FAILED, e.getMessage());
        }
    }
}
