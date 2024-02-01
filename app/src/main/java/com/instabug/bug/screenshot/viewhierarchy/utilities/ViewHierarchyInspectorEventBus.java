package com.instabug.bug.screenshot.viewhierarchy.utilities;

import com.instabug.bug.screenshot.viewhierarchy.C0480c;
import com.instabug.library.core.eventbus.EventBus;

/* loaded from: classes.dex */
public class ViewHierarchyInspectorEventBus extends EventBus<C0480c.a> {
    private static ViewHierarchyInspectorEventBus instance;

    public static ViewHierarchyInspectorEventBus getInstance() {
        if (instance == null) {
            instance = new ViewHierarchyInspectorEventBus();
        }
        return instance;
    }
}
