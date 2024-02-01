package andhook.lib.xposed.callbacks;

import android.os.Bundle;
import java.io.Serializable;

/* loaded from: classes2.dex */
public abstract class XCallback implements Comparable<XCallback> {
    public static final int PRIORITY_DEFAULT = 50;
    public static final int PRIORITY_HIGHEST = 10000;
    public static final int PRIORITY_LOWEST = -10000;
    public final int priority;

    protected void call(Param param) throws Throwable {
    }

    public XCallback() {
        this.priority = 50;
    }

    public XCallback(int i) {
        this.priority = i;
    }

    /* loaded from: classes2.dex */
    public static abstract class Param {
        public final Object[] callbacks = null;
        private Bundle extra;

        public synchronized Bundle getExtra() {
            if (this.extra == null) {
                this.extra = new Bundle();
            }
            return this.extra;
        }

        public Object getObjectExtra(String str) {
            Serializable serializable = getExtra().getSerializable(str);
            if (serializable instanceof SerializeWrapper) {
                return ((SerializeWrapper) serializable).object;
            }
            return null;
        }

        public void setObjectExtra(String str, Object obj) {
            getExtra().putSerializable(str, new SerializeWrapper(obj));
        }

        /* loaded from: classes2.dex */
        private static class SerializeWrapper implements Serializable {
            private static final long serialVersionUID = 1;
            private final Object object;

            public SerializeWrapper(Object obj) {
                this.object = obj;
            }
        }
    }

    public static void callAll(Param param) {
        if (param.callbacks == null) {
            throw new IllegalStateException("This object was not created for use with callAll");
        }
        for (int i = 0; i < param.callbacks.length; i++) {
            try {
                ((XCallback) param.callbacks[i]).call(param);
            } catch (Throwable unused) {
            }
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(XCallback xCallback) {
        if (this == xCallback) {
            return 0;
        }
        int i = xCallback.priority;
        int i2 = this.priority;
        return i != i2 ? i - i2 : System.identityHashCode(this) < System.identityHashCode(xCallback) ? -1 : 1;
    }
}
