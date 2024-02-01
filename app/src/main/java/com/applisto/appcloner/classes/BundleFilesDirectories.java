package com.applisto.appcloner.classes;

import android.content.Context;
import com.applisto.appcloner.classes.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class BundleFilesDirectories {
    private static final String TAG = BundleFilesDirectories.class.getSimpleName();
    private final List<String> mPaths;

    public BundleFilesDirectories(CloneSettings cloneSettings) {
        this.mPaths = cloneSettings.getStringList("bundleFilesDirectories");
        Log.m15i(TAG, "BundleFilesDirectories; mBundleFilesDirectories: " + this.mPaths);
    }

    public void install(Context context) {
        String string;
        long j;
        File file;
        InputStream open;
        FileOutputStream fileOutputStream;
        Log.m15i(TAG, "install; ");
        try {
            if (this.mPaths == null || this.mPaths.isEmpty()) {
                return;
            }
            String utils = Utils.toString(context.getAssets().open("bundle_file_list.json"), "UTF-8");
            Log.m15i(TAG, "init; fileListJson: " + utils);
            JSONArray jSONArray = new JSONArray(utils);
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    String string2 = jSONObject.getString("path");
                    string = jSONObject.getString("assetName");
                    j = jSONObject.getLong("lastModified");
                    Log.m15i(TAG, "init; path: " + string2 + ", assetName: " + string + ", lastModified: " + j);
                    file = new File(string2);
                } catch (Exception e) {
                    Log.m21w(TAG, e);
                }
                try {
                    try {
                        if (file.exists() && file.lastModified() / 1000 >= j / 1000) {
                            Log.m15i(TAG, "init; skipping file: " + file);
                        }
                        Utils.copy(open, fileOutputStream);
                        fileOutputStream.close();
                    } catch (Throwable th) {
                        fileOutputStream.close();
                        throw th;
                        break;
                    }
                    Utils.forceMkdir(file.getParentFile());
                    fileOutputStream = new FileOutputStream(file);
                } finally {
                    open.close();
                }
                Log.m15i(TAG, "init; saving new or modified file: " + file);
                open = context.getAssets().open(string);
            }
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
        }
    }
}
