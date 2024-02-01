package org.jcodec.common.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.jcodec.common.StringUtils;

/* loaded from: classes.dex */
public class MainUtils {
    private static final String JCODEC_LOG_SINK_COLOR = "jcodec.colorPrint";
    private static Pattern flagPattern;
    public static boolean isColorSupported;

    /* loaded from: classes.dex */
    public enum ANSIColor {
        BLACK,
        RED,
        GREEN,
        BROWN,
        BLUE,
        MAGENTA,
        CYAN,
        GREY
    }

    static {
        isColorSupported = System.console() != null || Boolean.parseBoolean(System.getProperty(JCODEC_LOG_SINK_COLOR));
        flagPattern = Pattern.compile("^--([^=]+)=(.*)$");
    }

    /* loaded from: classes.dex */
    public static class Cmd {
        public String[] args;
        public Map<String, String> flags;

        public Cmd(Map<String, String> flags, String[] args) {
            this.flags = flags;
            this.args = args;
        }

        public Long getLongFlag(String flagName, Long defaultValue) {
            return this.flags.containsKey(flagName) ? new Long(this.flags.get(flagName)) : defaultValue;
        }

        public Integer getIntegerFlag(String flagName, Integer defaultValue) {
            return this.flags.containsKey(flagName) ? new Integer(this.flags.get(flagName)) : defaultValue;
        }

        public Boolean getBooleanFlag(String flagName, Boolean defaultValue) {
            return this.flags.containsKey(flagName) ? new Boolean(this.flags.get(flagName)) : defaultValue;
        }

        public Double getDoubleFlag(String flagName, Long defaultValue) {
            return Double.valueOf(this.flags.containsKey(flagName) ? new Double(this.flags.get(flagName)).doubleValue() : defaultValue.longValue());
        }

        public String getStringFlag(String flagName, String defaultValue) {
            return this.flags.containsKey(flagName) ? this.flags.get(flagName) : defaultValue;
        }

        public int[] getMultiIntegerFlag(String flagName, int[] defaultValue) {
            if (this.flags.containsKey(flagName)) {
                String[] split = StringUtils.split(this.flags.get(flagName), ",");
                int[] result = new int[split.length];
                for (int i = 0; i < split.length; i++) {
                    result[i] = Integer.parseInt(split[i]);
                }
                return result;
            }
            return defaultValue;
        }

        public Long getLongFlag(String flagName) {
            return getLongFlag(flagName, null);
        }

        public Integer getIntegerFlag(String flagName) {
            return getIntegerFlag(flagName, null);
        }

        public Boolean getBooleanFlag(String flagName) {
            return getBooleanFlag(flagName, null);
        }

        public Double getDoubleFlag(String flagName) {
            return getDoubleFlag(flagName, null);
        }

        public String getStringFlag(String flagName) {
            return getStringFlag(flagName, null);
        }

        public int[] getMultiIntegerFlag(String flagName) {
            return getMultiIntegerFlag(flagName, new int[0]);
        }

        public String getArg(int i) {
            if (i < this.args.length) {
                return this.args[i];
            }
            return null;
        }

        public int argsLength() {
            return this.args.length;
        }
    }

    public static Cmd parseArguments(String[] args) {
        Map<String, String> flags = new HashMap<>();
        int firstArg = 0;
        while (firstArg < args.length) {
            if (args[firstArg].startsWith("--")) {
                Matcher matcher = flagPattern.matcher(args[firstArg]);
                if (matcher.matches()) {
                    flags.put(matcher.group(1), matcher.group(2));
                } else {
                    flags.put(args[firstArg].substring(2), "true");
                }
            } else {
                if (!args[firstArg].startsWith("-")) {
                    break;
                }
                String substring = args[firstArg].substring(1);
                firstArg++;
                flags.put(substring, args[firstArg]);
            }
            firstArg++;
        }
        return new Cmd(flags, (String[]) Arrays.copyOfRange(args, firstArg, args.length));
    }

    public static void printHelp(Map<String, String> flags, String... params) {
        System.out.print(bold("Syntax:"));
        StringBuilder sample = new StringBuilder();
        StringBuilder detail = new StringBuilder();
        for (Map.Entry<String, String> entry : flags.entrySet()) {
            sample.append(" [" + bold(color("--" + entry.getKey() + "=<value>", ANSIColor.MAGENTA)) + "]");
            detail.append("\t" + bold(color("--" + entry.getKey(), ANSIColor.MAGENTA)) + "\t\t" + entry.getValue() + IOUtils.LINE_SEPARATOR_UNIX);
        }
        for (String string : params) {
            sample.append(bold(" <" + string + ">"));
        }
        System.out.println(sample);
        System.out.println(bold("Where:"));
        System.out.println(detail);
    }

    public static String bold(String str) {
        return isColorSupported ? "\u001b[1m" + str + "\u001b[0m" : str;
    }

    public static String colorString(String str, String placeholder) {
        return isColorSupported ? "\u001b[" + placeholder + "m" + str + "\u001b[0m" : str;
    }

    public static String color(String str, ANSIColor fg) {
        return isColorSupported ? "\u001b[" + ((fg.ordinal() & 7) + 30) + "m" + str + "\u001b[0m" : str;
    }

    public static String color(String str, ANSIColor fg, boolean bright) {
        if (isColorSupported) {
            return "\u001b[" + ((fg.ordinal() & 7) + 30) + ";" + (bright ? 1 : 2) + "m" + str + "\u001b[0m";
        }
        return str;
    }

    public static String color(String str, ANSIColor fg, ANSIColor bg) {
        return isColorSupported ? "\u001b[" + ((fg.ordinal() & 7) + 30) + ";" + ((bg.ordinal() & 7) + 40) + ";1m" + str + "\u001b[0m" : str;
    }

    public static String color(String str, ANSIColor fg, ANSIColor bg, boolean bright) {
        if (isColorSupported) {
            return "\u001b[" + ((fg.ordinal() & 7) + 30) + ";" + ((bg.ordinal() & 7) + 40) + ";" + (bright ? 1 : 2) + "m" + str + "\u001b[0m";
        }
        return str;
    }
}
