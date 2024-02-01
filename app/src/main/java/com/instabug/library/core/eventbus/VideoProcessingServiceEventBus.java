package com.instabug.library.core.eventbus;

import com.instabug.library.internal.video.VideoProcessingService;

/* loaded from: classes.dex */
public class VideoProcessingServiceEventBus extends EventBus<VideoProcessingService.Action> {
    private static VideoProcessingServiceEventBus instance;

    public static VideoProcessingServiceEventBus getInstance() {
        if (instance == null) {
            instance = new VideoProcessingServiceEventBus();
        }
        return instance;
    }
}
