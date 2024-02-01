package com.instabug.library.core.eventbus.coreeventbus;

import com.instabug.library.core.eventbus.EventBus;

/* loaded from: classes.dex */
public class SDKCoreEventBus extends EventBus<SDKCoreEvent> {
    private static SDKCoreEventBus instance;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SDKCoreEventBus getInstance() {
        if (instance == null) {
            instance = new SDKCoreEventBus();
        }
        return instance;
    }
}
