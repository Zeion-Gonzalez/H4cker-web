package com.instabug.library.core.p024ui;

/* loaded from: classes.dex */
public interface BaseContract {

    /* loaded from: classes.dex */
    public interface Presenter {
    }

    /* loaded from: classes.dex */
    public interface View<C> {
        void finishActivity();

        C getViewContext();
    }
}
