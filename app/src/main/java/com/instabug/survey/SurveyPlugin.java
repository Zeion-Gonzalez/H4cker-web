package com.instabug.survey;

import android.content.Context;
import android.content.Intent;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEventSubscriber;
import com.instabug.library.core.plugin.Plugin;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.OnDiskCache;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.cache.SurveysCacheManager;
import com.instabug.survey.network.service.InstabugSurveysSubmitterService;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p033b.C0770a;
import com.instabug.survey.p033b.C0771b;
import com.instabug.survey.p034c.C0777e;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class SurveyPlugin extends Plugin {
    private Subscription subscribe;

    @Override // com.instabug.library.core.plugin.Plugin
    public void init(Context context) {
        super.init(context);
        C0771b.m1975a(context);
        C0770a.m1966a();
        subscribeOnSDKEvents();
        prepareSurveysCache();
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public void release() {
        if (this.subscribe != null) {
            this.subscribe.unsubscribe();
        }
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public long getLastActivityTime() {
        return C0771b.m1974a().m1977b();
    }

    private void subscribeOnSDKEvents() {
        this.subscribe = SDKCoreEventSubscriber.subscribe(new Action1<SDKCoreEvent>() { // from class: com.instabug.survey.SurveyPlugin.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(SDKCoreEvent sDKCoreEvent) {
                Context context;
                String type = sDKCoreEvent.getType();
                char c = 65535;
                switch (type.hashCode()) {
                    case -290659267:
                        if (type.equals(SDKCoreEvent.Feature.TYPE_FEATURES)) {
                            c = 2;
                            break;
                        }
                        break;
                    case 3599307:
                        if (type.equals(SDKCoreEvent.User.TYPE_USER)) {
                            c = 0;
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
                            SurveyPlugin.clearUserActivities();
                            return;
                        }
                        return;
                    case 1:
                        if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_STARTED)) {
                            SurveyPlugin.this.startFetchingSurveys();
                            return;
                        } else {
                            if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Session.VALUE_FINISHED) && SurveyPlugin.this.contextWeakReference != null && (context = (Context) SurveyPlugin.this.contextWeakReference.get()) != null) {
                                context.startService(new Intent(context, InstabugSurveysSubmitterService.class));
                                return;
                            }
                            return;
                        }
                    case 2:
                        if (sDKCoreEvent.getValue().equals(SDKCoreEvent.Feature.VALUE_FETCHED)) {
                            SurveyPlugin.this.startFetchingSurveys();
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
    public static void clearUserActivities() {
        C0771b.m1974a().m1976a(0L);
    }

    private void prepareSurveysCache() {
        InstabugSDKLogger.m1803v(this, "Creating Surveys disk cache");
        if (this.contextWeakReference != null && this.contextWeakReference.get() != null) {
            CacheManager.getInstance().addCache(new OnDiskCache(this.contextWeakReference.get(), SurveysCacheManager.SURVEYS_DISK_CACHE_KEY, SurveysCacheManager.SURVEYS_DISK_CACHE_FILE_NAME, C0769c.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFetchingSurveys() {
        if (C0777e.m1994a() && this.contextWeakReference != null && this.contextWeakReference.get() != null) {
            InstabugSDKLogger.m1803v(this, "initialize Instabug Surveys Manager");
            C0766a.m1901a(this.contextWeakReference.get()).m1911a();
        }
    }
}
