package com.instabug.library.core.eventbus.coreeventbus;

import p045rx.Subscription;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class SDKCoreEventSubscriber {
    public static Subscription subscribe(Action1<SDKCoreEvent> action1) {
        return SDKCoreEventBus.getInstance().subscribe(action1);
    }
}
