package com.swift.sandhook;

import android.util.Log;

/* loaded from: classes2.dex */
public class ClassNeverCall {
    private void neverCall() {
    }

    private native void neverCallNative();

    private native void neverCallNative2();

    private static void neverCallStatic() {
    }

    private void neverCall2() {
        Log.e("ClassNeverCall", "ClassNeverCall2");
    }
}
