package com.instabug.library.core.p024ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.instabug.library.core.p024ui.BaseContract;
import com.instabug.library.core.ui.BaseContract.Presenter;
import com.instabug.library.util.InstabugSDKLogger;

/* loaded from: classes.dex */
public abstract class BaseFragment<P extends BaseContract.Presenter> extends Fragment implements BaseContract.View<Fragment> {
    protected P presenter;
    protected View rootView;

    @LayoutRes
    protected abstract int getLayout();

    protected abstract void initViews(View view, Bundle bundle);

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        InstabugSDKLogger.m1799d(this, "onCreate called");
        super.onCreate(bundle);
    }

    @Override // android.support.v4.app.Fragment
    @CallSuper
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        InstabugSDKLogger.m1799d(this, "onCreateView called");
        this.rootView = layoutInflater.inflate(getLayout(), viewGroup, false);
        initViews(this.rootView, bundle);
        return this.rootView;
    }

    @Override // android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        InstabugSDKLogger.m1799d(this, "onViewCreated called");
        super.onViewCreated(view, bundle);
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        InstabugSDKLogger.m1799d(this, "onStart called");
        super.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        InstabugSDKLogger.m1799d(this, "onStop called");
        super.onStop();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        InstabugSDKLogger.m1799d(this, "onDestroyView called");
        super.onDestroyView();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.instabug.library.core.ui.BaseContract.View
    public Fragment getViewContext() {
        return this;
    }

    @Override // com.instabug.library.core.ui.BaseContract.View
    public void finishActivity() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View findViewById(@IdRes int i) {
        return this.rootView.findViewById(i);
    }
}
