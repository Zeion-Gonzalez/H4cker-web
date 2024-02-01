package org.jcodec.common.logging;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.tools.MainUtils;

/* loaded from: classes.dex */
public class OutLogSink implements Logger.LogSink {
    public static SimpleFormat DEFAULT_FORMAT = new SimpleFormat(MainUtils.colorString("[#level]", "#color_code") + MainUtils.bold("\t#class.#method (#file:#line):") + "\t#message");
    private MessageFormat fmt;
    private PrintStream out;

    /* loaded from: classes.dex */
    public interface MessageFormat {
        String formatMessage(Logger.Message message);
    }

    public OutLogSink() {
        this(System.out, DEFAULT_FORMAT);
    }

    public OutLogSink(MessageFormat fmt) {
        this(System.out, fmt);
    }

    public OutLogSink(PrintStream out, MessageFormat fmt) {
        this.out = out;
        this.fmt = fmt;
    }

    @Override // org.jcodec.common.logging.Logger.LogSink
    public void postMessage(Logger.Message msg) {
        this.out.println(this.fmt.formatMessage(msg));
    }

    /* loaded from: classes.dex */
    public static class SimpleFormat implements MessageFormat {
        private static Map<Logger.Level, MainUtils.ANSIColor> colorMap = new HashMap<Logger.Level, MainUtils.ANSIColor>() { // from class: org.jcodec.common.logging.OutLogSink.SimpleFormat.1
            {
                put(Logger.Level.DEBUG, MainUtils.ANSIColor.BROWN);
                put(Logger.Level.INFO, MainUtils.ANSIColor.GREEN);
                put(Logger.Level.WARN, MainUtils.ANSIColor.MAGENTA);
                put(Logger.Level.ERROR, MainUtils.ANSIColor.RED);
            }
        };
        private String fmt;

        public SimpleFormat(String fmt) {
            this.fmt = fmt;
        }

        @Override // org.jcodec.common.logging.OutLogSink.MessageFormat
        public String formatMessage(Logger.Message msg) {
            return this.fmt.replace("#level", String.valueOf(msg.getLevel())).replace("#color_code", String.valueOf(colorMap.get(msg.getLevel()).ordinal() + 30)).replace("#class", msg.getClassName()).replace("#method", msg.getMethodName()).replace("#file", msg.getFileName()).replace("#line", String.valueOf(msg.getLineNumber())).replace("#message", msg.getMessage());
        }
    }
}
