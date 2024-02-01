package com.instabug.library.core.p024ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.core.p024ui.BaseContract;
import com.instabug.library.core.ui.BaseContract.Presenter;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.SystemServiceUtils;

/* loaded from: classes.dex */
public abstract class ToolbarFragment<P extends BaseContract.Presenter> extends BaseFragment<P> {
    protected ImageButton toolbarImageButtonClose;
    protected ImageButton toolbarImageButtonDone;

    @LayoutRes
    protected abstract int getContentLayout();

    protected abstract String getTitle();

    protected abstract void initContentViews(View view, Bundle bundle);

    protected abstract void onCloseButtonClicked();

    protected abstract void onDoneButtonClicked();

    @Override // com.instabug.library.core.p024ui.BaseFragment
    @CallSuper
    protected void initViews(View view, Bundle bundle) {
        initToolbarViews();
        ViewStub viewStub = (ViewStub) findViewById(C0577R.id.instabug_content);
        viewStub.setLayoutResource(getContentLayout());
        viewStub.inflate();
        initContentViews(view, bundle);
        setTitle(getTitle());
    }

    private void initToolbarViews() {
        this.toolbarImageButtonDone = (ImageButton) findViewById(C0577R.id.instabug_btn_toolbar_right);
        this.toolbarImageButtonDone.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.library.core.ui.ToolbarFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemServiceUtils.hideInputMethod(ToolbarFragment.this.getActivity());
                ToolbarFragment.this.onDoneButtonClicked();
            }
        });
        this.toolbarImageButtonClose = (ImageButton) findViewById(C0577R.id.instabug_btn_toolbar_left);
        this.toolbarImageButtonClose.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.library.core.ui.ToolbarFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemServiceUtils.hideInputMethod(ToolbarFragment.this.getActivity());
                ToolbarFragment.this.onCloseButtonClicked();
                ToolbarFragment.this.getActivity().onBackPressed();
            }
        });
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0577R.layout.instabug_fragment_toolbar;
    }

    protected void setTitle(String str) {
        if (this.rootView == null) {
            InstabugSDKLogger.m1803v(this, "Calling setTitle before inflating the view! Ignoring call");
            return;
        }
        TextView textView = (TextView) findViewById(C0577R.id.instabug_fragment_title);
        InstabugSDKLogger.m1803v(this, "Setting fragment title to \"" + str + "\"");
        textView.setText(str);
    }
}
