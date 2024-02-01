package com.instabug.library.settings;

import java.io.Serializable;

/* loaded from: classes.dex */
public class AttachmentsTypesParams implements Serializable {
    private boolean allowAttachImageFromGallery;
    private boolean allowScreenRecording;
    private boolean allowTakeExtraScreenshot;
    private boolean shouldTakesInitialScreenshot;

    public AttachmentsTypesParams() {
        this.shouldTakesInitialScreenshot = true;
        this.allowTakeExtraScreenshot = true;
        this.allowAttachImageFromGallery = true;
        this.allowScreenRecording = true;
    }

    public AttachmentsTypesParams(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        this.shouldTakesInitialScreenshot = z;
        this.allowTakeExtraScreenshot = z2;
        this.allowAttachImageFromGallery = z3;
        this.allowScreenRecording = z5;
    }

    public boolean isShouldTakesInitialScreenshot() {
        return this.shouldTakesInitialScreenshot;
    }

    public AttachmentsTypesParams setShouldTakesInitialScreenshot(boolean z) {
        this.shouldTakesInitialScreenshot = z;
        return this;
    }

    public boolean isAllowTakeExtraScreenshot() {
        return this.allowTakeExtraScreenshot;
    }

    public AttachmentsTypesParams setAllowTakeExtraScreenshot(boolean z) {
        this.allowTakeExtraScreenshot = z;
        return this;
    }

    public boolean isAllowAttachImageFromGallery() {
        return this.allowAttachImageFromGallery;
    }

    public AttachmentsTypesParams setAllowAttachImageFromGallery(boolean z) {
        this.allowAttachImageFromGallery = z;
        return this;
    }

    public boolean isAllowScreenRecording() {
        return this.allowScreenRecording;
    }

    public AttachmentsTypesParams setAllowScreenRecording(boolean z) {
        this.allowScreenRecording = z;
        return this;
    }

    public String toString() {
        return this.shouldTakesInitialScreenshot + ", " + this.allowTakeExtraScreenshot + ", " + this.allowAttachImageFromGallery + ", " + this.allowScreenRecording;
    }
}
