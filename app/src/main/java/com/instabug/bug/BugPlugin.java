package com.instabug.bug;

import android.content.Context;
import com.instabug.bug.settings.C0482a;
import com.instabug.library.core.plugin.Plugin;
import com.instabug.library.core.plugin.PluginPromptOption;
import java.util.ArrayList;
import p045rx.Subscription;

/* loaded from: classes.dex */
public class BugPlugin extends Plugin {
    private Subscription coreEventsSubscription;

    @Override // com.instabug.library.core.plugin.Plugin
    public void init(Context context) {
        super.init(context);
        C0459a.m26a(context);
        subscribeOnCoreEvents();
    }

    private void subscribeOnCoreEvents() {
        this.coreEventsSubscription = C0459a.m22a(C0459a.m30b(this.contextWeakReference.get()));
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public ArrayList<PluginPromptOption> getPromptOptions() {
        return C0459a.m35c(this.contextWeakReference.get());
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public void release() {
        C0459a.m23a();
        unSubscribeFromCoreEvents();
    }

    private void unSubscribeFromCoreEvents() {
        C0459a.m29a(this.coreEventsSubscription);
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public long getLastActivityTime() {
        return C0482a.m236a().m264l();
    }
}
