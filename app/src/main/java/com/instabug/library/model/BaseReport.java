package com.instabug.library.model;

import android.support.annotation.Nullable;
import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class BaseReport implements Serializable {

    /* renamed from: id */
    protected String f1111id;
    @Nullable
    protected State state;
    private boolean hasVideo = false;
    private boolean isVideoEncoded = false;

    public String getId() {
        return this.f1111id;
    }

    public BaseReport setId(String str) {
        this.f1111id = str;
        return this;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    public BaseReport setState(@Nullable State state) {
        this.state = state;
        return this;
    }

    public boolean hasVideo() {
        return this.hasVideo;
    }

    public BaseReport setHasVideo(boolean z) {
        this.hasVideo = z;
        return this;
    }

    public boolean isVideoEncoded() {
        return this.isVideoEncoded;
    }

    public BaseReport setVideoEncoded(boolean z) {
        this.isVideoEncoded = z;
        return this;
    }
}
