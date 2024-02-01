package p045rx.internal.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import p045rx.functions.Func0;
import p045rx.internal.util.RxThreadFactory;
import p045rx.plugins.RxJavaHooks;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public enum GenericScheduledExecutorServiceFactory {
    ;

    static final String THREAD_NAME_PREFIX = "RxScheduledExecutorPool-";
    static final RxThreadFactory THREAD_FACTORY = new RxThreadFactory(THREAD_NAME_PREFIX);

    static ThreadFactory threadFactory() {
        return THREAD_FACTORY;
    }

    public static ScheduledExecutorService create() {
        Func0<? extends ScheduledExecutorService> f = RxJavaHooks.getOnGenericScheduledExecutorService();
        return f == null ? createDefault() : f.call();
    }

    static ScheduledExecutorService createDefault() {
        return Executors.newScheduledThreadPool(1, threadFactory());
    }
}
