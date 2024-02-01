package com.instabug.library.bugreporting.model;

import com.instabug.library.model.BaseReport;

/* loaded from: classes.dex */
public class Bug extends BaseReport {

    /* loaded from: classes.dex */
    public enum Type {
        BUG("bug"),
        FEEDBACK("feedback"),
        NOT_AVAILABLE("not-available");

        private final String name;

        Type(String str) {
            this.name = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.name;
        }
    }
}
