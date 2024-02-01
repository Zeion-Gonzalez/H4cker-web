package com.instabug.library.model;

import java.io.File;
import java.io.Serializable;

/* loaded from: classes.dex */
public class AssetEntity implements Serializable {
    private File file;
    private String key;
    private AssetType type;
    private String url;

    /* loaded from: classes.dex */
    public enum AssetType {
        IMAGE,
        AUDIO,
        VIDEO
    }

    public AssetEntity(String str, AssetType assetType, String str2, File file) {
        this.key = str;
        this.type = assetType;
        this.url = str2;
        this.file = file;
    }

    public String getKey() {
        return this.key;
    }

    public AssetType getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public File getFile() {
        return this.file;
    }
}
