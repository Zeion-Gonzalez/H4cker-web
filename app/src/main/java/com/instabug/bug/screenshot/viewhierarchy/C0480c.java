package com.instabug.bug.screenshot.viewhierarchy;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.instabug.library.C0577R;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Observable;
import p045rx.functions.Func0;

/* compiled from: ViewHierarchyInspector.java */
/* renamed from: com.instabug.bug.screenshot.viewhierarchy.c */
/* loaded from: classes.dex */
public class C0480c {

    /* compiled from: ViewHierarchyInspector.java */
    /* renamed from: com.instabug.bug.screenshot.viewhierarchy.c$a */
    /* loaded from: classes.dex */
    public enum a {
        STARTED,
        FAILED,
        COMPLETED
    }

    /* renamed from: a */
    public static Observable<C0479b> m217a(final C0479b c0479b) {
        return Observable.defer(new Func0<Observable<C0479b>>() { // from class: com.instabug.bug.screenshot.viewhierarchy.c.1
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<C0479b> call() {
                return Observable.just(C0480c.m226d(C0479b.this));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public static C0479b m226d(C0479b c0479b) {
        if (c0479b.m211o().getVisibility() == 0) {
            try {
                c0479b.m199c(m221b(c0479b.m211o()));
                c0479b.m195b(m215a(c0479b.m211o()));
                c0479b.m190a(m224c(c0479b.m211o()));
                c0479b.m193b(m225d(c0479b.m211o()));
                c0479b.m185a(m228f(c0479b));
                c0479b.m196b(m233k(c0479b));
                if (c0479b.m211o() instanceof ViewGroup) {
                    c0479b.m191a(true);
                    m227e(c0479b);
                } else {
                    c0479b.m191a(false);
                }
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(C0478a.class, "inspect view hierarchy got error: " + e.getMessage() + ",View hierarchy id:" + c0479b.m182a() + ", time in MS: " + System.currentTimeMillis(), e);
            }
        }
        return c0479b;
    }

    /* renamed from: e */
    private static void m227e(C0479b c0479b) throws JSONException {
        if (c0479b.m211o() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) c0479b.m211o();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i).getId() != C0577R.id.instabug_extra_screenshot_button && viewGroup.getChildAt(i).getId() != C0577R.id.instabug_floating_button) {
                    C0479b c0479b2 = new C0479b();
                    c0479b2.m197b(false);
                    c0479b2.m189a(c0479b.m182a() + "-" + i);
                    c0479b2.m187a(viewGroup.getChildAt(i));
                    c0479b2.m188a(c0479b);
                    c0479b2.m183a(c0479b.m212p());
                    c0479b.m194b(m226d(c0479b2));
                }
            }
        }
    }

    /* renamed from: a */
    private static String m215a(View view) {
        String simpleName = view.getClass().getSimpleName();
        char c = 65535;
        switch (simpleName.hashCode()) {
            case -1495589242:
                if (simpleName.equals("ProgressBar")) {
                    c = 16;
                    break;
                }
                break;
            case -1406842887:
                if (simpleName.equals("WebView")) {
                    c = 19;
                    break;
                }
                break;
            case -1346021293:
                if (simpleName.equals("MultiAutoCompleteTextView")) {
                    c = 11;
                    break;
                }
                break;
            case -1125439882:
                if (simpleName.equals("HorizontalScrollView")) {
                    c = '\r';
                    break;
                }
                break;
            case -957993568:
                if (simpleName.equals("VideoView")) {
                    c = 18;
                    break;
                }
                break;
            case -938935918:
                if (simpleName.equals("TextView")) {
                    c = '\t';
                    break;
                }
                break;
            case -937446323:
                if (simpleName.equals("ImageButton")) {
                    c = 6;
                    break;
                }
                break;
            case -830787764:
                if (simpleName.equals("TableRow")) {
                    c = 4;
                    break;
                }
                break;
            case -443652810:
                if (simpleName.equals("RelativeLayout")) {
                    c = 1;
                    break;
                }
                break;
            case 382765867:
                if (simpleName.equals("GridView")) {
                    c = 14;
                    break;
                }
                break;
            case 776382189:
                if (simpleName.equals("RadioButton")) {
                    c = 17;
                    break;
                }
                break;
            case 799298502:
                if (simpleName.equals("ToggleButton")) {
                    c = 21;
                    break;
                }
                break;
            case 1125864064:
                if (simpleName.equals("ImageView")) {
                    c = 7;
                    break;
                }
                break;
            case 1127291599:
                if (simpleName.equals("LinearLayout")) {
                    c = 0;
                    break;
                }
                break;
            case 1283054733:
                if (simpleName.equals("SearchView")) {
                    c = 20;
                    break;
                }
                break;
            case 1310765783:
                if (simpleName.equals("FrameLayout")) {
                    c = 2;
                    break;
                }
                break;
            case 1410352259:
                if (simpleName.equals("ListView")) {
                    c = 15;
                    break;
                }
                break;
            case 1413872058:
                if (simpleName.equals("AutoCompleteTextView")) {
                    c = '\n';
                    break;
                }
                break;
            case 1666676343:
                if (simpleName.equals("EditText")) {
                    c = '\b';
                    break;
                }
                break;
            case 1713715320:
                if (simpleName.equals("TableLayout")) {
                    c = 3;
                    break;
                }
                break;
            case 2001146706:
                if (simpleName.equals("Button")) {
                    c = 5;
                    break;
                }
                break;
            case 2059813682:
                if (simpleName.equals("ScrollView")) {
                    c = '\f';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (((LinearLayout) view).getOrientation() == 0) {
                    return "HorizontalLinearLayout";
                }
                return "VerticalLinearLayout";
            case 1:
                return "RelativeLayout";
            case 2:
                return "FrameLayout";
            case 3:
                return "TableLayout";
            case 4:
                return "TableRow";
            case 5:
                return "Button";
            case 6:
                return "ImageButton";
            case 7:
                return "ImageView";
            case '\b':
                return "EditText";
            case '\t':
                return "TextView";
            case '\n':
                return "AutoCompleteTextView";
            case 11:
                return "MultiAutoCompleteTextView";
            case '\f':
                return "ScrollView";
            case '\r':
                return "HorizontalScrollView";
            case 14:
                return "GridView";
            case 15:
                return "ListView";
            case 16:
                return "ProgressBar";
            case 17:
                return "RadioButton";
            case 18:
                return "VideoView";
            case 19:
                return "WebView";
            case 20:
                return "SearchView";
            case 21:
                return "ToggleButton";
            default:
                return "default";
        }
    }

    /* renamed from: b */
    private static String m221b(View view) {
        return view.getClass().getSimpleName();
    }

    /* renamed from: c */
    private static JSONObject m224c(View view) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("resource_id", m214a(view.getContext(), view.getId())).put("height", view.getHeight()).put("width", view.getWidth()).put("padding_top", view.getPaddingTop()).put("padding_bottom", view.getPaddingBottom()).put("padding_right", view.getPaddingRight()).put("padding_left", view.getPaddingLeft()).put("visibility", view.getVisibility());
        if (Build.VERSION.SDK_INT >= 17) {
            jSONObject.put("padding_end", view.getPaddingEnd()).put("padding_start", view.getPaddingStart());
        }
        if (Build.VERSION.SDK_INT >= 11) {
            jSONObject.put("x", view.getX()).put("y", view.getY());
        }
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            m220a((LinearLayout.LayoutParams) view.getLayoutParams(), jSONObject);
        } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            m219a((FrameLayout.LayoutParams) view.getLayoutParams(), jSONObject);
        } else if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            m218a(view.getContext(), (RelativeLayout.LayoutParams) view.getLayoutParams(), jSONObject);
        }
        return jSONObject;
    }

    /* renamed from: a */
    private static String m214a(Context context, int i) throws JSONException {
        String resourceEntryName;
        if (i == -1) {
            return String.valueOf(i);
        }
        if (context != null) {
            try {
                if (context.getResources() != null && context.getResources().getResourceEntryName(i) != null) {
                    resourceEntryName = context.getResources().getResourceEntryName(i);
                    return resourceEntryName;
                }
            } catch (Resources.NotFoundException e) {
                return String.valueOf(i);
            }
        }
        resourceEntryName = String.valueOf(i);
        return resourceEntryName;
    }

    /* renamed from: a */
    private static void m220a(LinearLayout.LayoutParams layoutParams, JSONObject jSONObject) throws JSONException {
        jSONObject.put("gravity", layoutParams.gravity).put("margin_top", layoutParams.topMargin).put("margin_bottom", layoutParams.bottomMargin).put("margin_left", layoutParams.leftMargin).put("margin_right", layoutParams.rightMargin);
    }

    /* renamed from: a */
    private static void m219a(FrameLayout.LayoutParams layoutParams, JSONObject jSONObject) throws JSONException {
        jSONObject.put("gravity", layoutParams.gravity).put("margin_top", layoutParams.topMargin).put("margin_bottom", layoutParams.bottomMargin).put("margin_left", layoutParams.leftMargin).put("margin_right", layoutParams.rightMargin);
    }

    /* renamed from: a */
    private static void m218a(Context context, RelativeLayout.LayoutParams layoutParams, JSONObject jSONObject) throws JSONException {
        String valueOf;
        jSONObject.put("margin_top", layoutParams.topMargin).put("margin_bottom", layoutParams.bottomMargin).put("margin_left", layoutParams.leftMargin).put("margin_right", layoutParams.rightMargin);
        int[] rules = layoutParams.getRules();
        for (int i = 0; i < rules.length; i++) {
            if (rules[i] > 0) {
                valueOf = m214a(context, rules[i]);
            } else {
                valueOf = String.valueOf(rules[i]);
            }
            jSONObject.put(m213a(i), valueOf);
        }
    }

    /* renamed from: a */
    private static String m213a(int i) {
        switch (i) {
            case 0:
                return "leftOf";
            case 1:
                return "rightOf";
            case 2:
                return "above";
            case 3:
                return "below";
            case 4:
                return "alignBaseline";
            case 5:
                return "alignLeft";
            case 6:
                return "alignTop";
            case 7:
                return "alignRight";
            case 8:
                return "alignBottom";
            case 9:
                return "alignParentLeft";
            case 10:
                return "alignParentTop";
            case 11:
                return "alignParentRight";
            case 12:
                return "alignParentBottom";
            case 13:
                return "centerInParent";
            case 14:
                return "centerHorizontal";
            case 15:
                return "centerVertical";
            case 16:
                return "startOf";
            case 17:
            default:
                return "notIdentified";
            case 18:
                return "alignStart";
            case 19:
                return "alignEnd";
            case 20:
                return "alignParentStart";
            case 21:
                return "alignParentEnd";
        }
    }

    /* renamed from: d */
    private static Rect m225d(View view) {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        return new Rect(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight());
    }

    /* renamed from: f */
    private static Rect m228f(C0479b c0479b) {
        if (c0479b.m205i()) {
            return c0479b.m210n();
        }
        Rect rect = new Rect(c0479b.m210n().left, c0479b.m210n().top, c0479b.m210n().right, c0479b.m210n().bottom);
        return !rect.intersect(new Rect(m229g(c0479b.m202f()), m230h(c0479b.m202f()), m231i(c0479b.m202f()), m232j(c0479b.m202f()))) ? new Rect(0, 0, 0, 0) : rect;
    }

    /* renamed from: g */
    private static int m229g(C0479b c0479b) {
        int i = c0479b.m209m().left;
        int paddingLeft = c0479b.m211o().getPaddingLeft();
        int i2 = c0479b.m210n().left;
        return (paddingLeft != 0 && i <= i2 + paddingLeft) ? i2 + paddingLeft : i;
    }

    /* renamed from: h */
    private static int m230h(C0479b c0479b) {
        int i = c0479b.m209m().top;
        int paddingTop = c0479b.m211o().getPaddingTop();
        int i2 = c0479b.m210n().top;
        return (paddingTop != 0 && i <= i2 + paddingTop) ? i2 + paddingTop : i;
    }

    /* renamed from: i */
    private static int m231i(C0479b c0479b) {
        int i = c0479b.m209m().right;
        int paddingRight = c0479b.m211o().getPaddingRight();
        int i2 = c0479b.m210n().right;
        return (paddingRight != 0 && i >= i2 - paddingRight) ? i2 - paddingRight : i;
    }

    /* renamed from: j */
    private static int m232j(C0479b c0479b) {
        int i = c0479b.m209m().bottom;
        int paddingBottom = c0479b.m211o().getPaddingBottom();
        int i2 = c0479b.m210n().bottom;
        return (paddingBottom != 0 && i >= i2 - paddingBottom) ? i2 - paddingBottom : i;
    }

    /* renamed from: k */
    private static JSONObject m233k(C0479b c0479b) throws JSONException {
        return new JSONObject().put("x", c0479b.m209m().left / c0479b.m212p()).put("y", c0479b.m209m().top / c0479b.m212p()).put("w", c0479b.m209m().width() / c0479b.m212p()).put("h", c0479b.m209m().height() / c0479b.m212p());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static JSONObject m216a(Activity activity, int i) throws JSONException {
        return new JSONObject().put("w", activity.getWindow().getDecorView().getWidth() / i).put("h", activity.getWindow().getDecorView().getHeight() / i);
    }

    /* renamed from: b */
    public static List<C0479b> m222b(C0479b c0479b) {
        ArrayList arrayList = new ArrayList();
        if (c0479b != null) {
            arrayList.add(c0479b);
            if (c0479b.m204h()) {
                Iterator<C0479b> it = c0479b.m203g().iterator();
                while (it.hasNext()) {
                    arrayList.addAll(m222b(it.next()));
                }
            }
        }
        return arrayList;
    }
}
