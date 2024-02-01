package com.instabug.library.analytics.model;

import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Api {
    private static final String KEY_COUNT = "count";
    private static final String KEY_IS_DEPRECATED = "is_deprecated";
    private static final String KEY_METHOD = "method";
    private static final String KEY_PARAMETERS = "parameters";
    private static final String KEY_TIME_STAMP = "time_stamp";
    private String apiName;
    private int count = 1;
    private boolean isDeprecated;
    private ArrayList<Parameter> parameters;
    private long timeStamp;

    public Api() {
        setTimeStamp(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds());
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public Api setTimeStamp(long j) {
        this.timeStamp = j;
        return this;
    }

    public String getApiName() {
        return this.apiName;
    }

    public Api setApiName(String str) {
        this.apiName = str;
        return this;
    }

    public boolean isDeprecated() {
        return this.isDeprecated;
    }

    public Api setDeprecated(boolean z) {
        this.isDeprecated = z;
        return this;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void incrementCount() {
        this.count++;
    }

    public ArrayList<Parameter> getParameters() {
        return this.parameters;
    }

    public Api setParameters(ArrayList<Parameter> arrayList) {
        this.parameters = arrayList;
        return this;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_TIME_STAMP, getTimeStamp()).put(KEY_METHOD, getApiName()).put(KEY_IS_DEPRECATED, isDeprecated()).put(KEY_COUNT, getCount()).put(KEY_PARAMETERS, Parameter.toJson(getParameters()));
        } catch (JSONException e) {
            InstabugSDKLogger.m1801e(Api.class, e.getMessage(), e);
        }
        return jSONObject;
    }

    public static JSONArray toJson(ArrayList<Api> arrayList) {
        JSONArray jSONArray = new JSONArray();
        Iterator<Api> it = arrayList.iterator();
        while (it.hasNext()) {
            jSONArray.put(it.next().toJson());
        }
        return jSONArray;
    }

    /* loaded from: classes.dex */
    public static class Parameter {
        private static final String KEY_NAME = "name";
        private static final String KEY_TYPE = "type";
        private static final String KEY_VALUE = "value";
        private String name;
        private String type;
        private String value;

        public String getName() {
            return this.name;
        }

        public Parameter setName(String str) {
            this.name = str;
            return this;
        }

        public String getType() {
            return this.type;
        }

        public Parameter setType(Class<?> cls) {
            this.type = cls.getSimpleName();
            return this;
        }

        public String getValue() {
            return this.value;
        }

        public Parameter setValue(Object obj) {
            if (obj == null) {
                this.value = "null";
            } else {
                this.value = obj.toString();
            }
            return this;
        }

        public JSONObject toJson() {
            JSONObject jSONObject = new JSONObject();
            try {
                if (getName() != null) {
                    jSONObject.put(KEY_NAME, getName());
                }
                if (getType() != null) {
                    jSONObject.put(KEY_TYPE, getType());
                }
                if (getValue() != null) {
                    jSONObject.put(KEY_VALUE, getValue());
                }
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(Parameter.class, e.getMessage(), e);
            }
            return jSONObject;
        }

        public static Parameter fromJson(JSONObject jSONObject) {
            Parameter parameter = new Parameter();
            try {
                if (jSONObject.has(KEY_NAME)) {
                    parameter.name = jSONObject.getString(KEY_NAME);
                }
                if (jSONObject.has(KEY_TYPE)) {
                    parameter.type = jSONObject.getString(KEY_TYPE);
                }
                if (jSONObject.has(KEY_VALUE)) {
                    parameter.value = jSONObject.getString(KEY_VALUE);
                }
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(Parameter.class, e.getMessage(), e);
            }
            return parameter;
        }

        public static JSONArray toJson(ArrayList<Parameter> arrayList) {
            JSONArray jSONArray = new JSONArray();
            Iterator<Parameter> it = arrayList.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().toJson());
            }
            return jSONArray;
        }

        public static ArrayList<Parameter> fromJson(JSONArray jSONArray) {
            ArrayList<Parameter> arrayList = new ArrayList<>();
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    arrayList.add(fromJson(jSONArray.getJSONObject(i)));
                } catch (JSONException e) {
                    InstabugSDKLogger.m1801e(Parameter.class, e.getMessage(), e);
                }
            }
            return arrayList;
        }
    }
}
