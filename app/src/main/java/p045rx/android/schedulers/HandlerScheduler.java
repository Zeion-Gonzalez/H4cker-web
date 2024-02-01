package p045rx.android.schedulers;

import android.os.Handler;
import p045rx.Scheduler;

@Deprecated
/* loaded from: classes.dex */
public final class HandlerScheduler extends LooperScheduler {
    @Override // p045rx.android.schedulers.LooperScheduler, p045rx.Scheduler
    public /* bridge */ /* synthetic */ Scheduler.Worker createWorker() {
        return super.createWorker();
    }

    @Deprecated
    public static HandlerScheduler from(Handler handler) {
        if (handler == null) {
            throw new NullPointerException("handler == null");
        }
        return new HandlerScheduler(handler);
    }

    private HandlerScheduler(Handler handler) {
        super(handler);
    }
}
