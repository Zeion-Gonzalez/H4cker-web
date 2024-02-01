package com.instabug.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.instabug.library.util.InstabugSDKLogger;

/* loaded from: classes.dex */
public abstract class InstabugBaseFragment extends Fragment {
    private Activity activity;
    private boolean isStateRestored;
    private View view;

    protected abstract void consumeNewInstanceSavedArguments();

    @LayoutRes
    protected abstract int getLayout();

    protected abstract String getTitle();

    protected abstract void restoreState(Bundle bundle);

    protected abstract void saveState(Bundle bundle);

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        InstabugSDKLogger.m1803v(this, "onCreateView called");
        if (getArguments() != null) {
            InstabugSDKLogger.m1803v(this, "Arguments found, calling consumeNewInstanceSavedArguments with " + getArguments());
            consumeNewInstanceSavedArguments();
        }
        this.isStateRestored = false;
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        InstabugSDKLogger.m1803v(this, "onCreateView called");
        this.view = layoutInflater.inflate(getLayout(), viewGroup, false);
        setTitle(getTitle());
        return this.view;
    }

    @Override // android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        InstabugSDKLogger.m1803v(this, "onViewCreated called");
        super.onViewCreated(view, bundle);
        if (bundle != null) {
            InstabugSDKLogger.m1803v(this, "savedInstanceState found, calling restoreState");
            restoreState(bundle);
            this.isStateRestored = true;
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        InstabugSDKLogger.m1803v(this, "onSaveInstanceState called, calling saveState");
        saveState(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        InstabugSDKLogger.m1803v(this, "onPause called, calling saveState");
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        InstabugSDKLogger.m1803v(this, "onResume called, calling saveState");
    }

    public boolean isStateRestored() {
        return this.isStateRestored;
    }

    public Activity getPreservedActivity() {
        InstabugSDKLogger.m1803v(this, "Returning preserved activity " + this.activity);
        return this.activity;
    }

    public void setTitle(String str) {
        if (this.view == null) {
            InstabugSDKLogger.m1803v(this, "Calling setTitle before inflating the view! Ignoring call");
            return;
        }
        TextView textView = (TextView) this.view.findViewById(C0577R.id.instabug_fragment_title);
        if (textView != null) {
            InstabugSDKLogger.m1803v(this, "Setting fragment title to \"" + str + "\"");
            textView.setText(str);
        } else {
            InstabugSDKLogger.m1803v(this, "instabug_fragment_title wasn't found, make sure your layout.xml contains it");
        }
    }
}
