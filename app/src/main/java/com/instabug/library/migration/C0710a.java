package com.instabug.library.migration;

import android.content.Context;
import android.support.annotation.NonNull;
import com.instabug.library.settings.SettingsManager;
import p045rx.Observable;
import p045rx.Subscriber;

/* compiled from: LastContactedAtMigration.java */
/* renamed from: com.instabug.library.migration.a */
/* loaded from: classes.dex */
public class C0710a extends AbstractMigration {

    /* renamed from: b */
    private static String f1102b = "last_contacted_at_migration";

    /* renamed from: a */
    private Context f1103a;

    public C0710a() {
        super(f1102b);
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public String getMigrationId() {
        return f1102b;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public int getMigrationVersion() {
        return 1;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void initialize(@NonNull Context context) {
        this.f1103a = context;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public boolean shouldMigrate() {
        return getMigrationVersion() > SettingsManager.getInstance().getLastMigrationVersion() && SettingsManager.getInstance().isDeviceRegistered() && SettingsManager.getInstance().getLastContactedAt() == 0;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public Observable<AbstractMigration> migrate() {
        return Observable.create(new Observable.OnSubscribe<AbstractMigration>() { // from class: com.instabug.library.migration.a.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Subscriber<? super AbstractMigration> subscriber) {
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
