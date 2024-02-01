package com.instabug.library.tracking;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: InstabugTouchEventsHandler.java */
/* renamed from: com.instabug.library.tracking.b */
/* loaded from: classes.dex */
public class C0739b {
    /* renamed from: a */
    static /* synthetic */ boolean m1737a() {
        return m1738b();
    }

    /* renamed from: a */
    public static void m1735a(MotionEvent motionEvent, Activity activity) {
        if (m1738b()) {
            InstabugSDKLogger.m1799d(C0739b.class, motionEvent.toString());
            C0704b.m1513c().m1521a(motionEvent);
        }
        InstabugInternalTrackingDelegate.getInstance().trackTouchEvent(motionEvent, activity);
    }

    /* renamed from: a */
    public static void m1736a(View view, final Activity activity) {
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.instabug.library.tracking.b.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                if (C0739b.m1737a()) {
                    C0704b.m1513c().m1521a(motionEvent);
                }
                InstabugInternalTrackingDelegate.getInstance().trackTouchEvent(motionEvent, activity);
                return false;
            }
        });
    }

    /* renamed from: b */
    private static boolean m1738b() {
        return C0704b.m1513c().m1523d() == InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT;
    }
}
