package moe.exusiai.utils;

import java.util.UUID;

public class UUIDUtil {
    public static String noDashStringToUUID(String noDashUUID) {
        String uuid = UUID.fromString(
                noDashUUID
                        .replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                        )
        ).toString();
        return uuid;
    }
}
