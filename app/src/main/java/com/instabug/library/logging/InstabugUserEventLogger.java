package com.instabug.library.logging;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.internal.storage.cache.p028a.C0672a;
import com.instabug.library.internal.storage.cache.p028a.C0674c;
import com.instabug.library.user.UserEvent;
import com.instabug.library.user.UserEventParam;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class InstabugUserEventLogger {
    private static final long DELAY = 1;
    private static final int USER_EVENT_COUNT_LIMIT = 10000;
    private static volatile InstabugUserEventLogger instabugUserEventLogger;
    private ScheduledExecutorService insertionExecutor;
    private List<UserEvent> userEvents = new ArrayList();
    private ConcurrentHashMap<String, Integer> userEventsCount = new ConcurrentHashMap<>();

    private InstabugUserEventLogger() {
    }

    public static InstabugUserEventLogger getInstance() {
        if (instabugUserEventLogger == null) {
            instabugUserEventLogger = new InstabugUserEventLogger();
        }
        return instabugUserEventLogger;
    }

    public synchronized void logUserEvent(@NonNull String str, UserEventParam... userEventParamArr) throws IllegalStateException {
        synchronized (this) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("eventIdentifier").setType(String.class), new Api.Parameter().setName("userEventParams").setType(UserEventParam.class));
            if (C0629b.m1160a().m1170b(Feature.USER_EVENTS) == Feature.State.ENABLED) {
                UserEvent date = new UserEvent().setEventIdentifier(str).setDate(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds());
                for (UserEventParam userEventParam : userEventParamArr) {
                    date.addParam(userEventParam);
                }
                if (this.userEvents.size() >= 10000) {
                    this.userEvents.remove(0);
                }
                this.userEvents.add(date);
                Integer num = this.userEventsCount.get(str);
                if (num != null) {
                    this.userEventsCount.put(str, Integer.valueOf(num.intValue() + 1));
                } else {
                    this.userEventsCount.put(str, 1);
                }
            }
            runInsertionHandler();
        }
    }

    private void runInsertionHandler() {
        if (this.insertionExecutor == null) {
            Executors.newScheduledThreadPool(1).schedule(new Runnable() { // from class: com.instabug.library.logging.InstabugUserEventLogger.1
                @Override // java.lang.Runnable
                public void run() {
                    for (Map.Entry entry : InstabugUserEventLogger.this.userEventsCount.entrySet()) {
                        InstabugUserEventLogger.this.incrementEventLoggingCount((String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                    }
                    InstabugUserEventLogger.this.userEventsCount.clear();
                    InstabugUserEventLogger.this.insertionExecutor = null;
                }
            }, 1L, TimeUnit.SECONDS);
        }
    }

    @VisibleForTesting
    int getLoggingEventCount(@NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("userEventIdentifier").setType(String.class));
        return getEventLoggingCount(str);
    }

    public List<UserEvent> getUserEvents() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return this.userEvents;
    }

    public void clearAll() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        this.userEvents.clear();
    }

    public void clearLoggingData() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0672a.m1309a().m1311b().m1320a("user_events_logs", (String) null, (String[]) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void incrementEventLoggingCount(@NonNull String str, int i) {
        int eventLoggingCount = getEventLoggingCount(str);
        boolean z = eventLoggingCount > 0;
        C0674c m1311b = C0672a.m1309a().m1311b();
        ContentValues contentValues = new ContentValues();
        contentValues.put("event_identifier", str);
        contentValues.put("event_logging_count", Integer.valueOf(eventLoggingCount + i));
        if (z) {
            InstabugSDKLogger.m1799d(this, "update: " + m1311b.m1313a("user_events_logs", contentValues, "event_identifier=?", new String[]{str}));
        } else {
            InstabugSDKLogger.m1799d(this, "insert: " + m1311b.m1314a("user_events_logs", (String) null, contentValues));
        }
        m1311b.m1321b();
    }

    private int getEventLoggingCount(@NonNull String str) {
        int i;
        C0674c m1311b = C0672a.m1309a().m1311b();
        Cursor m1316a = m1311b.m1316a("user_events_logs", new String[]{"event_logging_count"}, "event_identifier =?", new String[]{str}, null, null, null);
        if (m1316a.getCount() > 0) {
            m1316a.moveToFirst();
            i = m1316a.getInt(m1316a.getColumnIndex("event_logging_count"));
        } else {
            i = 0;
        }
        m1316a.close();
        m1311b.m1321b();
        return i;
    }
}
