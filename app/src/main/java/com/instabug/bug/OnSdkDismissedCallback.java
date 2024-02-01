package com.instabug.bug;

import android.support.annotation.NonNull;
import com.instabug.bug.model.EnumC0472b;

/* loaded from: classes.dex */
public interface OnSdkDismissedCallback {

    /* loaded from: classes.dex */
    public enum DismissType {
        SUBMIT,
        CANCEL,
        ADD_ATTACHMENT
    }

    void onSdkDismissed(@NonNull DismissType dismissType, @NonNull EnumC0472b enumC0472b);
}
