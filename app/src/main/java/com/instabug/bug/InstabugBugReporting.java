package com.instabug.bug;

import com.instabug.bug.OnSdkDismissedCallback;
import com.instabug.bug.cache.BugsCacheManager;
import com.instabug.bug.extendedbugreport.ExtendedBugReport;
import com.instabug.bug.model.Bug;
import com.instabug.bug.model.EnumC0472b;
import com.instabug.bug.model.ReportCategory;
import com.instabug.bug.settings.AttachmentsTypesParams;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.settings.C0483b;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.bugreporting.model.Bug;
import com.instabug.library.extendedbugreport.ExtendedBugReport;
import com.instabug.library.model.BugCategory;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class InstabugBugReporting {
    public static void setReportCategories(List<ReportCategory> list) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("reportCategories").setType(ReportCategory.class));
        C0482a.m236a().m245a(list);
    }

    private static void setLegacyReportCategories(List<com.instabug.library.bugreporting.model.ReportCategory> list) {
        ArrayList arrayList = new ArrayList();
        for (com.instabug.library.bugreporting.model.ReportCategory reportCategory : list) {
            arrayList.add(ReportCategory.getInstance().withIcon(reportCategory.getIcon()).withLabel(reportCategory.getLabel()));
        }
        setReportCategories(arrayList);
    }

    private static void setLegacyBugCategories(List<BugCategory> list) {
        ArrayList arrayList = new ArrayList();
        for (BugCategory bugCategory : list) {
            arrayList.add(ReportCategory.getInstance().withIcon(bugCategory.getIcon()).withLabel(bugCategory.getLabel()));
        }
        setReportCategories(arrayList);
    }

    private void updateBugCacheManager() {
        for (Bug bug : BugsCacheManager.getBugs()) {
            if (bug.m132f() == Bug.BugState.WAITING_VIDEO) {
                InstabugSDKLogger.m1803v(this, "found the video bug");
                bug.m113a(Bug.BugState.READY_TO_BE_SENT);
                BugsCacheManager.addBug(bug);
                return;
            }
        }
    }

    private static void setLegacyOnSdkDismissedCallback(final com.instabug.library.OnSdkDismissedCallback onSdkDismissedCallback) throws IllegalStateException {
        setOnSdkDismissedCallback(new OnSdkDismissedCallback() { // from class: com.instabug.bug.InstabugBugReporting.1
            @Override // com.instabug.bug.OnSdkDismissedCallback
            public void onSdkDismissed(OnSdkDismissedCallback.DismissType dismissType, EnumC0472b enumC0472b) {
                Bug.Type type;
                OnSdkDismissedCallback.DismissType dismissType2;
                switch (C04572.f50a[enumC0472b.ordinal()]) {
                    case 1:
                        type = Bug.Type.BUG;
                        break;
                    case 2:
                        type = Bug.Type.FEEDBACK;
                        break;
                    case 3:
                        type = Bug.Type.NOT_AVAILABLE;
                        break;
                    default:
                        type = Bug.Type.NOT_AVAILABLE;
                        break;
                }
                switch (C04572.f51b[dismissType.ordinal()]) {
                    case 1:
                        dismissType2 = OnSdkDismissedCallback.DismissType.SUBMIT;
                        break;
                    case 2:
                        dismissType2 = OnSdkDismissedCallback.DismissType.CANCEL;
                        break;
                    case 3:
                        dismissType2 = OnSdkDismissedCallback.DismissType.ADD_ATTACHMENT;
                        break;
                    default:
                        dismissType2 = OnSdkDismissedCallback.DismissType.CANCEL;
                        break;
                }
                com.instabug.library.OnSdkDismissedCallback.this.onSdkDismissed(dismissType2, type);
            }
        });
    }

    public static void setOnSdkDismissedCallback(OnSdkDismissedCallback onSdkDismissedCallback) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("onSdkDismissedCallback").setType(OnSdkDismissedCallback.class));
        C0482a.m236a().m239a(onSdkDismissedCallback);
    }

    public static void setAttachmentTypesEnabled(boolean z, boolean z2, boolean z3, boolean z4) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("initialScreenshot").setType(Boolean.class).setValue(Boolean.toString(z)), new Api.Parameter().setName("extraScreenshot").setType(Boolean.class).setValue(Boolean.toString(z2)), new Api.Parameter().setName("galleryImage").setType(Boolean.class).setValue(Boolean.toString(z3)), new Api.Parameter().setName("screenRecording").setType(Boolean.class).setValue(Boolean.toString(z4)));
        C0482a.m236a().m241a(new AttachmentsTypesParams(z, z2, z3, z4));
    }

    public static void setPreSendingRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("runnable").setType(Runnable.class));
        C0482a.m236a().m243a(runnable);
    }

    public static Runnable getPreSendingRunnable() {
        return C0483b.m272b().m287h();
    }

    public static void setShouldSkipInitialScreenshotAnnotation(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("willSkipInitialScreenshotAnnotating").setType(Boolean.class).setValue(Boolean.toString(z)));
        C0482a.m236a().m250b(z);
    }

    public static void setEmailFieldRequired(boolean z) {
        C0482a.m236a().m246a(z);
    }

    public static void setEmailFieldVisibility(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("emailFieldVisibility").setType(Boolean.class).setValue(Boolean.toString(z)));
        C0482a.m236a().m253c(z);
    }

    public static void setCommentFieldRequired(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("commentFieldRequired").setType(Boolean.class).setValue(Boolean.toString(z)));
        C0482a.m236a().m255d(z);
    }

    public static void setSuccessDialogEnabled(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enabled").setType(Boolean.class).setValue(Boolean.toString(z)));
        C0482a.m236a().m256e(z);
    }

    public static void openNewFeedback() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0459a.m36c();
    }

    public static void openNewBugReport() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0459a.m39d();
    }

    public static void addExtraReportField(CharSequence charSequence, boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fieldHint").setType(CharSequence.class).setValue(charSequence), new Api.Parameter().setName("required").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0482a.m236a().m242a(charSequence, z);
    }

    public static void clearExtraReportFields() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0482a.m236a().m267o();
    }

    public static void setExtendedBugReportState(ExtendedBugReport.State state) {
        ExtendedBugReport.State state2;
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        switch (state) {
            case ENABLED_WITH_REQUIRED_FIELDS:
                state2 = ExtendedBugReport.State.ENABLED_WITH_REQUIRED_FIELDS;
                break;
            case ENABLED_WITH_OPTIONAL_FIELDS:
                state2 = ExtendedBugReport.State.ENABLED_WITH_OPTIONAL_FIELDS;
                break;
            default:
                state2 = ExtendedBugReport.State.DISABLED;
                break;
        }
        C0482a.m236a().m240a(state2);
    }

    /* renamed from: com.instabug.bug.InstabugBugReporting$2 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C04572 {

        /* renamed from: a */
        static final /* synthetic */ int[] f50a;

        /* renamed from: b */
        static final /* synthetic */ int[] f51b;

        static {
            try {
                f52c[ExtendedBugReport.State.ENABLED_WITH_REQUIRED_FIELDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f52c[ExtendedBugReport.State.ENABLED_WITH_OPTIONAL_FIELDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            f51b = new int[OnSdkDismissedCallback.DismissType.values().length];
            try {
                f51b[OnSdkDismissedCallback.DismissType.SUBMIT.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f51b[OnSdkDismissedCallback.DismissType.CANCEL.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f51b[OnSdkDismissedCallback.DismissType.ADD_ATTACHMENT.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            f50a = new int[EnumC0472b.values().length];
            try {
                f50a[EnumC0472b.BUG.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f50a[EnumC0472b.FEEDBACK.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f50a[EnumC0472b.NOT_AVAILABLE.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
        }
    }
}
