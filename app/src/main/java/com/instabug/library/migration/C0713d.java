package com.instabug.library.migration;

import android.content.Context;
import android.support.annotation.NonNull;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.StringUtility;
import p045rx.Observable;
import p045rx.Subscriber;

/* compiled from: SDKBackwardMigration.java */
/* renamed from: com.instabug.library.migration.d */
/* loaded from: classes.dex */
public class C0713d extends AbstractMigration {
    /* JADX INFO: Access modifiers changed from: package-private */
    public C0713d() {
        super("sdk_backward_migration");
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public int getMigrationVersion() {
        return 4;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void initialize(@NonNull Context context) {
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public boolean shouldMigrate() {
        String lastSDKVersion = SettingsManager.getInstance().getLastSDKVersion();
        return ("4.11.2".contains("-") || lastSDKVersion.contains("-")) ? StringUtility.compareVersion(String.valueOf("4.11.2".charAt(0)), String.valueOf(lastSDKVersion.charAt(0))) == -1 || !SettingsManager.getInstance().isSDKVersionSet() : StringUtility.compareVersion("4.11.2", lastSDKVersion) == -1 || !SettingsManager.getInstance().isSDKVersionSet();
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public Observable<AbstractMigration> migrate() {
        return Observable.create(new Observable.OnSubscribe<AbstractMigration>() { // from class: com.instabug.library.migration.d.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Subscriber<? super AbstractMigration> subscriber) {
                CacheManager.getInstance().invalidateAllCaches();
                subscriber.onNext(C0713d.this);
                subscriber.onCompleted();
            }
        });
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void doPreMigration() {
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void doAfterMigration() {
        InstabugSDKLogger.m1802i(this, "doAfterMigration called");
        SettingsManager.getInstance().setCurrentSDKVersion("4.11.2");
    }
}
