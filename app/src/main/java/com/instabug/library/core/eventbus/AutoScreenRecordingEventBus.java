package com.instabug.library.core.eventbus;

import com.instabug.library.internal.video.AutoScreenRecordingService;

/* loaded from: classes.dex */
public class AutoScreenRecordingEventBus extends EventBus<AutoScreenRecordingService.Action> {
    private static AutoScreenRecordingEventBus instance;

    public static AutoScreenRecordingEventBus getInstance() {
        if (instance == null) {
            instance = new AutoScreenRecordingEventBus();
        }
        return instance;
    }
}
