package com.instabug.library.network;

/* loaded from: classes.dex */
public abstract class InstabugNetworkBasedBackgroundService extends AbstractIntentServiceC0722a {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected boolean mustHaveNetworkConnection() {
        return true;
    }
}
