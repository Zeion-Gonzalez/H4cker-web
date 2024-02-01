package com.instabug.library.migration;

import android.content.Context;
import android.support.annotation.NonNull;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import p045rx.Observable;
import p045rx.Subscriber;

/* compiled from: V2CacheFilesMigration.java */
/* renamed from: com.instabug.library.migration.f */
/* loaded from: classes.dex */
public class C0715f extends AbstractMigration {

    /* renamed from: a */
    private Context f1109a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0715f() {
        super("v2_cache_files_migration");
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public int getMigrationVersion() {
        return 2;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void initialize(@NonNull Context context) {
        this.f1109a = context;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public boolean shouldMigrate() {
        if (getMigrationVersion() <= SettingsManager.getInstance().getLastMigrationVersion()) {
            return false;
        }
        File file = new File(this.f1109a.getCacheDir() + "/issues.cache");
        File file2 = new File(this.f1109a.getCacheDir() + "/conversations.cache");
        InstabugSDKLogger.m1803v(this, getMigrationId() + " is " + (file.exists() || file2.exists()));
        return file.exists() || file2.exists();
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public Observable<AbstractMigration> migrate() {
        return Observable.create(new Observable.OnSubscribe<AbstractMigration>() { // from class: com.instabug.library.migration.f.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Subscriber<? super AbstractMigration> subscriber) {
                File file = new File(C0715f.this.f1109a.getCacheDir() + "/issues.cache");
                File file2 = new File(C0715f.this.f1109a.getCacheDir() + "/conversations.cache");
                if (file.exists() ? file.delete() : false) {
                    InstabugSDKLogger.m1803v(C0715f.class, "Issues cache file found and deleted");
                }
                if (file2.exists() ? file2.delete() : false) {
                    InstabugSDKLogger.m1803v(C0715f.class, "Conversations cache file found and deleted");
                }
                subscriber.onNext(C0715f.this);
                subscriber.onCompleted();
            }
        });
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void doPreMigration() {
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void doAfterMigration() {
    }
}
