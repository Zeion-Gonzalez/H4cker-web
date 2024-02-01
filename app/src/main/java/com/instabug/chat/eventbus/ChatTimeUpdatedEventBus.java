package com.instabug.chat.eventbus;

import com.instabug.library.core.eventbus.EventBus;

/* loaded from: classes.dex */
public class ChatTimeUpdatedEventBus extends EventBus<Long> {
    private static ChatTimeUpdatedEventBus instance;

    public static ChatTimeUpdatedEventBus getInstance() {
        if (instance == null) {
            instance = new ChatTimeUpdatedEventBus();
        }
        return instance;
    }
}
