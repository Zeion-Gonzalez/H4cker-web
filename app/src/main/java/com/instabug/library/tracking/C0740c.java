package com.instabug.library.tracking;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.instabug.library.C0629b;
import com.instabug.library.C0634c;
import com.instabug.library.Feature;
import com.instabug.library.visualusersteps.C0763d;

/* compiled from: InstabugTouchEventsTracker.java */
/* renamed from: com.instabug.library.tracking.c */
/* loaded from: classes.dex */
public class C0740c {

    /* renamed from: a */
    private static C0740c f1224a;

    /* renamed from: b */
    private int f1225b;

    /* renamed from: c */
    private int f1226c;

    private C0740c() {
    }

    /* renamed from: a */
    public static C0740c m1740a() {
        if (f1224a == null) {
            f1224a = new C0740c();
        }
        return f1224a;
    }

    /* renamed from: a */
    public boolean m1745a(Activity activity, MotionEvent motionEvent) {
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        C0634c.a[] aVarArr = new C0634c.a[motionEvent.getPointerCount()];
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            aVarArr[i] = new C0634c.a((int) motionEvent.getX(i), (int) motionEvent.getY(i));
        }
        C0634c.m1186a().m1187a(aVarArr);
        if (motionEvent.getAction() == 0) {
            this.f1225b = rawX;
            this.f1226c = rawY;
            return false;
        }
        if (motionEvent.getAction() != 1 || Math.abs(this.f1225b - rawX) > 25 || Math.abs(this.f1226c - rawY) > 25) {
            return false;
        }
        m1742a(activity, m1739a(activity.getWindow().getDecorView(), rawX, rawY));
        return true;
    }

    /* renamed from: a */
    private void m1742a(Activity activity, View view) {
        if (view != null) {
            String m1741a = m1741a(activity, view.getId());
            if (m1743b()) {
                C0741d.m1746a().m1753a(activity.getClass().getName(), m1741a, view.getClass().getName());
            }
            if (m1744c()) {
                C0763d.m1884a().m1896a("view_tapped", activity.getClass().getSimpleName(), m1741a);
            }
        }
    }

    /* renamed from: b */
    private boolean m1743b() {
        return C0629b.m1160a().m1170b(Feature.TRACK_USER_STEPS) == Feature.State.ENABLED;
    }

    /* renamed from: c */
    private boolean m1744c() {
        return C0629b.m1160a().m1170b(Feature.REPRO_STEPS) == Feature.State.ENABLED;
    }

    /* renamed from: a */
    private static String m1741a(Context context, int i) {
        if (i == -1 || context == null) {
            return null;
        }
        try {
            if (context.getResources() != null) {
                return context.getResources().getResourceEntryName(i);
            }
            return null;
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    /* renamed from: a */
    private View m1739a(View view, int i, int i2) {
        View view2;
        View view3 = null;
        int i3 = 0;
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        if (i2 < iArr[1] || i < iArr[0] || i2 > iArr[1] + view.getHeight() || i > iArr[0] + view.getWidth()) {
            return null;
        }
        if (!(view instanceof ViewGroup)) {
            return view;
        }
        while (true) {
            int i4 = i3;
            if (i4 >= ((ViewGroup) view).getChildCount()) {
                view2 = view3;
                break;
            }
            View childAt = ((ViewGroup) view).getChildAt(i4);
            if (childAt instanceof ViewGroup) {
                View m1739a = m1739a(childAt, i, i2);
                if (m1739a == null) {
                    m1739a = view3;
                }
                view3 = m1739a;
            } else {
                View m1739a2 = m1739a(childAt, i, i2);
                if (m1739a2 != null) {
                    view3 = m1739a2;
                }
            }
            if (view3 != null) {
                view2 = view3;
                break;
            }
            i3 = i4 + 1;
        }
        return view2 == null ? view : view2;
    }
}
