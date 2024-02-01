package com.instabug.library.user;

/* loaded from: classes.dex */
public class UserEventParam {
    private String key;
    private String value;

    public String getKey() {
        return this.key;
    }

    public UserEventParam setKey(String str) {
        this.key = str;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public UserEventParam setValue(String str) {
        this.value = str;
        return this;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UserEventParam)) {
            return false;
        }
        UserEventParam userEventParam = (UserEventParam) obj;
        return String.valueOf(userEventParam.getKey()).equals(String.valueOf(getKey())) && String.valueOf(userEventParam.getValue()).equals(String.valueOf(getValue()));
    }

    public int hashCode() {
        if (getKey() == null || getValue() == null) {
            return -1;
        }
        return String.valueOf(getKey() + ": " + getValue()).hashCode();
    }
}
