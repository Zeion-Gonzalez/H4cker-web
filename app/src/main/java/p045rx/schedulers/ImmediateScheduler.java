package p045rx.schedulers;

import p045rx.Scheduler;

@Deprecated
/* loaded from: classes.dex */
public final class ImmediateScheduler extends Scheduler {
    private ImmediateScheduler() {
        throw new IllegalStateException("No instances!");
    }

    @Override // p045rx.Scheduler
    public Scheduler.Worker createWorker() {
        return null;
    }
}
