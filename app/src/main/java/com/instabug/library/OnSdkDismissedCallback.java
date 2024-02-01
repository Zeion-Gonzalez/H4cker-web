package com.instabug.library;

import com.instabug.library.bugreporting.model.Bug;

/* loaded from: classes.dex */
public interface OnSdkDismissedCallback {

    /* loaded from: classes.dex */
    public enum DismissType {
        SUBMIT,
        CANCEL,
        ADD_ATTACHMENT
    }

    void onSdkDismissed(DismissType dismissType, Bug.Type type);
}
