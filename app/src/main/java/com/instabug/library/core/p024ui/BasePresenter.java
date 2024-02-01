package com.instabug.library.core.p024ui;

import com.instabug.library.core.p024ui.BaseContract;
import com.instabug.library.core.ui.BaseContract.View;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter {
    protected WeakReference<V> view;

    public BasePresenter(V v) {
        this.view = new WeakReference<>(v);
    }
}
