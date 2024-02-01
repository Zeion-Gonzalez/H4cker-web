package com.instabug.crash;

import android.content.Context;
import android.content.Intent;
import com.instabug.crash.cache.CrashesCacheManager;
import com.instabug.crash.models.Crash;
import com.instabug.crash.network.InstabugCrashesUploaderService;
import com.instabug.crash.p016b.C0561a;
import com.instabug.crash.p016b.C0563c;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEventSubscriber;
import com.instabug.library.core.plugin.Plugin;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.OnDiskCache;
import com.instabug.library.util.InstabugSDKLogger;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class CrashPlugin extends Plugin {
    private Subscription subscribe;

    @Override // com.instabug.library.core.plugin.Plugin
    public void init(Context context) {
        super.init(context);
        C0561a.m920a(context);
        subscribeOnSDKEvents();
        prepareCrashesCache();
        setExceptionHandler();
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public void release() {
        if (this.subscribe != null) {
            this.subscribe.unsubscribe();
        }
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public long getLastActivityTime() {
        return C0561a.m919a().m924c();
    }

    private void subscribeOnSDKEvents() {
        this.subscribe = SDKCoreEventSubscriber.subscribe(new Action1<SDKCoreEvent>() { // from class: com.instabug.crash.CrashPlugin.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(SDKCoreEvent sDKCoreEvent) {
                String type = sDKCoreEvent.getType();
                char c = 65535;
                switch (type.hashCode()) {
                    case 3599307:
                        if (type.equals(SDKCoreEvent.User.TYPE_USER)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1843485230:
                        if (type.equals(SDKCoreEvent.Network.TYPE_NETWORK)) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1984987798:
                        if (type.equals(SDKCoreEvent.Session.TYPE_SESSION)) {
                            c = 1;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        if (sDKCoreEvent.getValue().equals(SDKCoreEvent.User.VALUE_LOGGED_OUT)) {
                            CrashPlugin.this.clearUserActivities();
                            return;
                        }
                        return;
                    case 1:
                        if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_STARTED)) {
                            CrashPlugin.this.startCrashesUploaderService();
                            return;
                        }
                        return;
                    case 2:
                        if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Network.VALUE_ACTIVATED)) {
                            CrashPlugin.this.startCrashesUploaderService();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCrashesUploaderService() {
        Context context;
        if (this.contextWeakReference != null && (context = this.contextWeakReference.get()) != null) {
            context.startService(new Intent(this.contextWeakReference.get(), InstabugCrashesUploaderService.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearUserActivities() {
        C0563c.m929a().m931a(0L);
    }

    private void prepareCrashesCache() {
        InstabugSDKLogger.m1803v(this, "Creating Crashes disk cache");
        if (this.contextWeakReference != null && this.contextWeakReference.get() != null) {
            CacheManager.getInstance().addCache(new OnDiskCache(this.contextWeakReference.get(), CrashesCacheManager.CRASHES_DISK_CACHE_KEY, CrashesCacheManager.CRASHES_DISK_CACHE_FILE_NAME, Crash.class));
        }
    }

    private void setExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new C0558a());
    }
}
