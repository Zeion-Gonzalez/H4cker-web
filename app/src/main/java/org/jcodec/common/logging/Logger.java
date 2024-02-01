package org.jcodec.common.logging;

import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class Logger {
    private static List<LogSink> sinks;
    private static List<LogSink> stageSinks = new LinkedList();

    /* loaded from: classes.dex */
    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    /* loaded from: classes.dex */
    public interface LogSink {
        void postMessage(Message message);
    }

    /* loaded from: classes.dex */
    public static class Message {
        private String className;
        private String fileName;
        private Level level;
        private int lineNumber;
        private String message;
        private String methodName;

        public Message(Level level, String fileName, String className, String methodName, int lineNumber, String message) {
            this.level = level;
            this.fileName = fileName;
            this.className = className;
            this.methodName = methodName;
            this.message = methodName;
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public Level getLevel() {
            return this.level;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getClassName() {
            return this.className;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public int getLineNumber() {
            return this.lineNumber;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static void debug(String message) {
        message(Level.DEBUG, message);
    }

    public static void info(String message) {
        message(Level.INFO, message);
    }

    public static void warn(String message) {
        message(Level.WARN, message);
    }

    public static void error(String message) {
        message(Level.ERROR, message);
    }

    private static void message(Level level, String message) {
        if (sinks == null) {
            synchronized (Logger.class) {
                if (sinks == null) {
                    sinks = stageSinks;
                    stageSinks = null;
                    if (sinks.isEmpty()) {
                        sinks.add(new OutLogSink());
                    }
                }
            }
        }
        StackTraceElement tr = Thread.currentThread().getStackTrace()[3];
        Message msg = new Message(level, tr.getFileName(), tr.getClassName(), tr.getMethodName(), tr.getLineNumber(), message);
        for (LogSink logSink : sinks) {
            logSink.postMessage(msg);
        }
    }

    public static void addSink(LogSink sink) {
        if (stageSinks == null) {
            throw new IllegalStateException("Logger already started");
        }
        stageSinks.add(sink);
    }
}
