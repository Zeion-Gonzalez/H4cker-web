package com.instabug.library.visualusersteps;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.instabug.library.internal.storage.AttachmentManager;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.DiskUtils;
import java.io.File;
import p045rx.Observable;
import p045rx.functions.Func0;

/* loaded from: classes.dex */
public class VisualUserStepsHelper {
    /* JADX INFO: Access modifiers changed from: private */
    public static Uri getVisualUserStepsFile(Context context, String str) {
        return DiskUtils.zipFiles(context, "usersteps_" + str, DiskUtils.listFilesInDirectory(getVisualUserStepsDirectory(InstabugInternalTrackingDelegate.getInstance().getTargetActivity())));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static File getVisualUserStepsDirectory(Context context) {
        return AttachmentManager.getNewDirectory(context, "usersteps");
    }

    @NonNull
    public static Observable<Uri> getVisualUserStepsFileObservable(final Context context, final String str) {
        return Observable.defer(new Func0<Observable<Uri>>() { // from class: com.instabug.library.visualusersteps.VisualUserStepsHelper.1
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<Uri> call() {
                return Observable.just(VisualUserStepsHelper.getVisualUserStepsFile(context, str));
            }
        });
    }
}
