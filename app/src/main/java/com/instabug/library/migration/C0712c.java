package com.instabug.library.migration;

import android.content.Context;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.schedulers.Schedulers;

/* compiled from: MigrationManager.java */
/* renamed from: com.instabug.library.migration.c */
/* loaded from: classes.dex */
public final class C0712c {

    /* renamed from: a */
    private static final AbstractMigration[] f1106a = {new C0710a(), new C0715f(), new C0711b(), new C0713d(), new C0714e()};

    /* renamed from: a */
    public static void m1570a(Context context) {
        Observable<AbstractMigration>[] m1573b = m1573b(context);
        if (m1573b != null && m1573b.length != 0) {
            Observable.merge(m1573b).observeOn(Schedulers.m2140io()).subscribeOn(Schedulers.m2140io()).subscribe((Subscriber) new Subscriber<AbstractMigration>() { // from class: com.instabug.library.migration.c.1
                @Override // p045rx.Subscriber
                public void onStart() {
                    super.onStart();
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    InstabugSDKLogger.m1799d(C0712c.class, "All Migrations completed, setting lastMigrationVersion to 4");
                    SettingsManager.getInstance().setLastMigrationVersion(4);
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    InstabugSDKLogger.m1799d(C0712c.class, "Migration failed" + th.getMessage());
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(AbstractMigration abstractMigration) {
                    InstabugSDKLogger.m1799d(C0712c.class, "Migration " + abstractMigration.getMigrationId() + " done");
                    abstractMigration.doAfterMigration();
                }
            });
        } else {
            InstabugSDKLogger.m1799d(C0712c.class, "No migrations to run");
        }
    }

    /* renamed from: b */
    private static Observable<AbstractMigration>[] m1573b(Context context) {
        ArrayList arrayList = new ArrayList();
        for (AbstractMigration abstractMigration : f1106a) {
            abstractMigration.initialize(context);
            if (m1571a(abstractMigration)) {
                abstractMigration.doPreMigration();
                arrayList.add(abstractMigration.migrate());
            }
        }
        return m1572a(arrayList);
    }

    /* renamed from: a */
    private static boolean m1571a(AbstractMigration abstractMigration) {
        boolean z = abstractMigration.getMigrationVersion() <= 4 && abstractMigration.shouldMigrate();
        InstabugSDKLogger.m1799d(C0712c.class, "Checking if should apply this migration: " + abstractMigration.getMigrationId() + ", result is " + z + " last migration version is " + SettingsManager.getInstance().getLastMigrationVersion() + " target migration version 4");
        return z;
    }

    /* renamed from: a */
    private static Observable[] m1572a(ArrayList<Observable<AbstractMigration>> arrayList) {
        Observable[] observableArr = new Observable[arrayList.size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < arrayList.size()) {
                observableArr[i2] = arrayList.get(i2);
                i = i2 + 1;
            } else {
                return observableArr;
            }
        }
    }
}
