package com.instabug.bug.extendedbugreport;

import android.content.Context;
import com.instabug.bug.C0458R;
import com.instabug.bug.model.C0471a;
import com.instabug.library.util.LocaleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class ExtendedBugReport {

    /* loaded from: classes.dex */
    public enum State {
        DISABLED,
        ENABLED_WITH_REQUIRED_FIELDS,
        ENABLED_WITH_OPTIONAL_FIELDS
    }

    /* renamed from: a */
    public static List<C0471a> m108a(Context context, State state) {
        return state == State.ENABLED_WITH_REQUIRED_FIELDS ? m109a(context, true) : m109a(context, false);
    }

    /* renamed from: a */
    private static List<C0471a> m109a(Context context, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new C0471a(context.getString(C0458R.string.instabug_str_steps_to_reproduce), LocaleUtils.getLocaleStringResource(Locale.ENGLISH, C0458R.string.instabug_str_steps_to_reproduce, context), z, "repro_steps"));
        arrayList.add(new C0471a(context.getString(C0458R.string.instabug_str_actual_results), LocaleUtils.getLocaleStringResource(Locale.ENGLISH, C0458R.string.instabug_str_actual_results, context), z, "actual_result"));
        arrayList.add(new C0471a(context.getString(C0458R.string.instabug_str_expected_results), LocaleUtils.getLocaleStringResource(Locale.ENGLISH, C0458R.string.instabug_str_expected_results, context), z, "expected_result"));
        return arrayList;
    }
}
