package com.instabug.library.core.eventbus;

import com.instabug.library.model.Session;

/* loaded from: classes.dex */
public class SessionStateEventBus extends EventBus<Session.SessionState> {
    private static SessionStateEventBus instance;

    public static SessionStateEventBus getInstance() {
        if (instance == null) {
            instance = new SessionStateEventBus();
        }
        return instance;
    }
}
