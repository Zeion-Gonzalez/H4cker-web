package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class LoadSettingsBox extends Box {
    private int defaultHints;
    private int preloadDuration;
    private int preloadFlags;
    private int preloadStartTime;

    public static String fourcc() {
        return "load";
    }

    public LoadSettingsBox(Header header) {
        super(header);
    }

    public LoadSettingsBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.preloadStartTime = input.getInt();
        this.preloadDuration = input.getInt();
        this.preloadFlags = input.getInt();
        this.defaultHints = input.getInt();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.putInt(this.preloadStartTime);
        out.putInt(this.preloadDuration);
        out.putInt(this.preloadFlags);
        out.putInt(this.defaultHints);
    }

    public int getPreloadStartTime() {
        return this.preloadStartTime;
    }

    public int getPreloadDuration() {
        return this.preloadDuration;
    }

    public int getPreloadFlags() {
        return this.preloadFlags;
    }

    public int getDefaultHints() {
        return this.defaultHints;
    }
}
