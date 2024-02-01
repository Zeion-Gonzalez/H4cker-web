package p045rx.android.schedulers;

import android.os.Looper;
import p045rx.Scheduler;
import p045rx.android.plugins.RxAndroidPlugins;
import p045rx.android.plugins.RxAndroidSchedulersHook;

/* loaded from: classes.dex */
public final class AndroidSchedulers {
    private static final AndroidSchedulers INSTANCE = new AndroidSchedulers();
    private final Scheduler mainThreadScheduler;

    private AndroidSchedulers() {
        RxAndroidSchedulersHook hook = RxAndroidPlugins.getInstance().getSchedulersHook();
        Scheduler main = hook.getMainThreadScheduler();
        if (main != null) {
            this.mainThreadScheduler = main;
        } else {
            this.mainThreadScheduler = new LooperScheduler(Looper.getMainLooper());
        }
    }

    public static Scheduler mainThread() {
        return INSTANCE.mainThreadScheduler;
    }

    public static Scheduler from(Looper looper) {
        if (looper == null) {
            throw new NullPointerException("looper == null");
        }
        return new LooperScheduler(looper);
    }
}
