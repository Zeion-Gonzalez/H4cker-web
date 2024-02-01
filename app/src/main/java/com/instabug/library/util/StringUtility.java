package com.instabug.library.util;

import android.support.annotation.NonNull;
import java.util.List;

/* loaded from: classes.dex */
public class StringUtility {
    public static String trimString(String str) {
        return trimString(str, 4096);
    }

    public static String trimString(String str, int i) {
        if (str.length() > i) {
            return str.substring(0, i);
        }
        return str;
    }

    public static String[] trimStrings(String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = trimString(strArr[i]);
        }
        return strArr;
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+(?:\\.\\d+)?");
    }

    public static int compareVersion(String str, String str2) throws NumberFormatException {
        String[] split = str.split("\\.");
        String[] split2 = str2.split("\\.");
        int i = 0;
        while (true) {
            if (i >= split.length && i >= split2.length) {
                return 0;
            }
            if (i < split.length && i < split2.length) {
                if (Integer.parseInt(split[i]) < Integer.parseInt(split2[i])) {
                    return -1;
                }
                if (Integer.parseInt(split[i]) > Integer.parseInt(split2[i])) {
                    return 1;
                }
            } else if (i < split.length) {
                if (Integer.parseInt(split[i]) != 0) {
                    return 1;
                }
            } else if (i < split2.length && Integer.parseInt(split2[i]) != 0) {
                return -1;
            }
            i++;
        }
    }

    public static String removeExtension(@NonNull String str) {
        return str.replaceFirst("[.][^.]+$", "");
    }

    public static String toCommaSeparated(List<String> list) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size() - 1) {
                sb.append(list.get(i2));
                sb.append(",");
                i = i2 + 1;
            } else {
                sb.append(list.get(list.size() - 1));
                return sb.toString();
            }
        }
    }
}
