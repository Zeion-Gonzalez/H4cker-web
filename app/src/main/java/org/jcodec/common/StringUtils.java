package org.jcodec.common;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class StringUtils {
    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        int sizePlus1;
        int sizePlus12;
        int sizePlus13;
        int sizePlus14;
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            sizePlus1 = 1;
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        sizePlus14 = sizePlus1 + 1;
                        if (sizePlus1 == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    } else {
                        sizePlus14 = sizePlus1;
                    }
                    i++;
                    start = i;
                    sizePlus1 = sizePlus14;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        } else if (separatorChars.length() != 1) {
            sizePlus1 = 1;
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        sizePlus12 = sizePlus1 + 1;
                        if (sizePlus1 == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    } else {
                        sizePlus12 = sizePlus1;
                    }
                    i++;
                    start = i;
                    sizePlus1 = sizePlus12;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        } else {
            char sep = separatorChars.charAt(0);
            int sizePlus15 = 1;
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        sizePlus13 = sizePlus15 + 1;
                        if (sizePlus15 == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    } else {
                        sizePlus13 = sizePlus15;
                    }
                    i++;
                    start = i;
                    sizePlus15 = sizePlus13;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
            if (!match || (preserveAllTokens && lastMatch)) {
                list.add(str.substring(start, i));
            }
            return (String[]) list.toArray(new String[list.size()]);
        }
        if (!match) {
        }
        list.add(str.substring(start, i));
        return (String[]) list.toArray(new String[list.size()]);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                i++;
                start = i;
            } else {
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] split(String str) {
        return split(str, null, -1);
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char c : delimiters) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    public static String capitaliseAllWords(String str) {
        return capitalize(str);
    }

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (str != null && str.length() != 0 && delimLen != 0) {
            int strLen = str.length();
            StringBuffer buffer = new StringBuffer(strLen);
            boolean capitalizeNext = true;
            for (int i = 0; i < strLen; i++) {
                char ch = str.charAt(i);
                if (isDelimiter(ch, delimiters)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(ch);
                }
            }
            return buffer.toString();
        }
        return str;
    }

    public static String join(Object[] array) {
        return join(array, (String) null);
    }

    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return "";
        }
        StringBuffer buf = new StringBuffer(bufSize * ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1));
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return "";
        }
        StringBuffer buf = new StringBuffer(bufSize * ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length()));
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
}
