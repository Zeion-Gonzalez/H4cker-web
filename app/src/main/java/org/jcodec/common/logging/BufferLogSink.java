package org.jcodec.common.logging;

import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class BufferLogSink implements Logger.LogSink {
    private List<Logger.Message> messages = new LinkedList();

    @Override // org.jcodec.common.logging.Logger.LogSink
    public void postMessage(Logger.Message msg) {
        this.messages.add(msg);
    }

    public List<Logger.Message> getMessages() {
        return this.messages;
    }
}
