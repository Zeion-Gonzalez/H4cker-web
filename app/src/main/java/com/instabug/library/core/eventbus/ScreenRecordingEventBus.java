package com.instabug.library.core.eventbus;

import com.instabug.library.internal.video.ScreenRecordEvent;

/* loaded from: classes.dex */
public class ScreenRecordingEventBus extends EventBus<ScreenRecordEvent> {
    private static ScreenRecordingEventBus instance;

    public static ScreenRecordingEventBus getInstance() {
        if (instance == null) {
            instance = new ScreenRecordingEventBus();
        }
        return instance;
    }
}
