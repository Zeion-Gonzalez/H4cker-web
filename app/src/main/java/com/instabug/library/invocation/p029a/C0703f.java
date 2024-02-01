package com.instabug.library.invocation.p029a;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.instabug.library.invocation.InterfaceC0697a;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: TwoFingerSwipeLeftInvoker.java */
/* renamed from: com.instabug.library.invocation.a.f */
/* loaded from: classes.dex */
public class C0703f implements InterfaceC0698a<MotionEvent> {

    /* renamed from: a */
    private GestureDetectorCompat f1064a;

    /* renamed from: b */
    private a f1065b;

    /* renamed from: c */
    private Context f1066c;

    /* renamed from: d */
    private boolean f1067d = false;

    /* renamed from: e */
    private InterfaceC0697a f1068e;

    public C0703f(Context context, InterfaceC0697a interfaceC0697a) {
        this.f1066c = context;
        this.f1068e = interfaceC0697a;
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: a */
    public void mo1407a() {
        this.f1065b = new a();
        this.f1064a = new GestureDetectorCompat(this.f1066c, this.f1065b);
    }

    /* renamed from: a */
    public void m1505a(MotionEvent motionEvent) {
        if (this.f1064a != null) {
            switch (motionEvent.getAction() & 255) {
                case 2:
                    if (motionEvent.getPointerCount() >= 2) {
                        this.f1067d = true;
                        break;
                    } else {
                        return;
                    }
            }
            this.f1064a.onTouchEvent(motionEvent);
        }
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: b */
    public void mo1408b() {
        this.f1065b = null;
        this.f1064a = null;
    }

    /* compiled from: TwoFingerSwipeLeftInvoker.java */
    /* renamed from: com.instabug.library.invocation.a.f$a */
    /* loaded from: classes.dex */
    class a extends GestureDetector.SimpleOnGestureListener {
        a() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (C0703f.this.m1500a(motionEvent, motionEvent2) && C0703f.this.f1067d) {
                InstabugSDKLogger.m1799d(this, "Two fingers swiped left, invoking SDK");
                C0703f.this.f1068e.mo1405a();
            }
            C0703f.this.f1067d = false;
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public boolean m1500a(MotionEvent motionEvent, MotionEvent motionEvent2) {
        return motionEvent != null && motionEvent2 != null && motionEvent.getX() > motionEvent2.getX() && motionEvent.getX() - motionEvent2.getX() >= Math.abs(motionEvent.getY() - motionEvent2.getY());
    }
}
