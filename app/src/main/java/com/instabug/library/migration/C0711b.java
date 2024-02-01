package com.instabug.library.migration;

import android.content.Context;
import android.support.annotation.NonNull;
import com.instabug.library.settings.SettingsManager;
import p045rx.Observable;
import p045rx.Subscriber;

/* compiled from: LastContactedAtToLastBugAndLastChatTimeMigration.java */
/* renamed from: com.instabug.library.migration.b */
/* loaded from: classes.dex */
public class C0711b extends AbstractMigration {
    public C0711b() {
        super("last_contacted_at_to_last_bug_and_last_chat_time_migration");
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public String getMigrationId() {
        return "last_contacted_at_to_last_bug_and_last_chat_time_migration";
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public int getMigrationVersion() {
        return 3;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public void initialize(@NonNull Context context) {
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public boolean shouldMigrate() {
        return getMigrationVersion() > SettingsManager.getInstance().getLastMigrationVersion() && SettingsManager.getInstance().getLastContactedAt() != 0;
    }

    @Override // com.instabug.library.migration.AbstractMigration
    public Observable<AbstractMigration> migrate() {
        return Observable.create(new Observable.OnSubscribe<AbstractMigration>() { // from class: com.instabug.library.migration.b.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Subscriber<? super AbstractMigration> subscriber) {
                subscriber.onNext(C0711b.this);
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
