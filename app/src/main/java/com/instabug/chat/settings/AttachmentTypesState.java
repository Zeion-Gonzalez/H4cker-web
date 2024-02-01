package com.instabug.chat.settings;

import java.io.Serializable;

/* loaded from: classes.dex */
public class AttachmentTypesState implements Serializable {
    private boolean imageFromGalleryEnabled;
    private boolean screenRecordingEnabled;
    private boolean screenshotEnabled;

    public AttachmentTypesState() {
        this.screenshotEnabled = true;
        this.imageFromGalleryEnabled = true;
        this.screenRecordingEnabled = true;
    }

    public AttachmentTypesState(boolean z, boolean z2, boolean z3) {
        this.screenshotEnabled = z;
        this.imageFromGalleryEnabled = z2;
        this.screenRecordingEnabled = z3;
    }

    public boolean isScreenshotEnabled() {
        return this.screenshotEnabled;
    }

    public AttachmentTypesState setScreenshotEnabled(boolean z) {
        this.screenshotEnabled = z;
        return this;
    }

    public boolean isImageFromGalleryEnabled() {
        return this.imageFromGalleryEnabled;
    }

    public AttachmentTypesState setImageFromGalleryEnabled(boolean z) {
        this.imageFromGalleryEnabled = z;
        return this;
    }

    public boolean isScreenRecordingEnabled() {
        return this.screenRecordingEnabled;
    }

    public AttachmentTypesState setScreenRecordingEnabled(boolean z) {
        this.screenRecordingEnabled = z;
        return this;
    }

    public String toString() {
        return this.screenshotEnabled + ", " + this.imageFromGalleryEnabled + ", " + this.screenRecordingEnabled;
    }
}
