package com.applisto.appcloner.classes;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class PersistentAppAccessibilityService extends AccessibilityService {
    private static final String TAG = PersistentAppAccessibilityService.class.getSimpleName();

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected void onServiceConnected() {
        Log.m15i(TAG, "onServiceConnected; ");
    }
}
