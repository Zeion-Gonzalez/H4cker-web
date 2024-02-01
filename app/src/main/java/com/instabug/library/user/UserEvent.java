package com.instabug.library.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UserEvent {
    private static final String EVENT = "event";
    private static final String PARAMS = "params";
    private static final String TIMESTAMP = "timestamp";
    private long date;
    private String eventIdentifier;
    private List<UserEventParam> params = new ArrayList();

    public String getEventIdentifier() {
        return this.eventIdentifier;
    }

    public UserEvent setEventIdentifier(String str) {
        this.eventIdentifier = str;
        return this;
    }

    public long getDate() {
        return this.date;
    }

    public UserEvent setDate(long j) {
        this.date = j;
        return this;
    }

    public List<UserEventParam> getParams() {
        return this.params;
    }

    public UserEvent addParam(UserEventParam userEventParam) {
        this.params.add(userEventParam);
        return this;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("event", getEventIdentifier());
        jSONObject.put(TIMESTAMP, getDate());
        JSONObject jSONObject2 = new JSONObject();
        for (UserEventParam userEventParam : getParams()) {
            jSONObject2.put(userEventParam.getKey(), userEventParam.getValue());
        }
        jSONObject.put(PARAMS, jSONObject2);
        return jSONObject;
    }

    public static JSONArray toJson(List<UserEvent> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        Iterator<UserEvent> it = list.iterator();
        while (it.hasNext()) {
            jSONArray.put(it.next().toJson());
        }
        return jSONArray;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UserEvent)) {
            return false;
        }
        UserEvent userEvent = (UserEvent) obj;
        if (!String.valueOf(userEvent.getEventIdentifier()).equals(String.valueOf(getEventIdentifier())) || !String.valueOf(userEvent.getDate()).equals(String.valueOf(getDate())) || userEvent.getParams().size() != getParams().size()) {
            return false;
        }
        for (int i = 0; i < this.params.size(); i++) {
            if (!userEvent.getParams().get(i).equals(getParams().get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (getEventIdentifier() != null) {
            return String.valueOf(getDate() + ": " + getEventIdentifier()).hashCode();
        }
        return -1;
    }
}
