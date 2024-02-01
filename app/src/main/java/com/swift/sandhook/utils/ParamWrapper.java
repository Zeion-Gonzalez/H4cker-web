package com.swift.sandhook.utils;

import com.swift.sandhook.SandHook;

/* loaded from: classes2.dex */
public class ParamWrapper {
    private static boolean is64Bit = SandHook.is64Bit();

    public static boolean support(Class cls) {
        return is64Bit ? (cls == Float.TYPE || cls == Double.TYPE) ? false : true : (cls == Float.TYPE || cls == Double.TYPE || cls == Long.TYPE) ? false : true;
    }

    public static Object addressToObject(Class cls, long j) {
        if (is64Bit) {
            return addressToObject64(cls, j);
        }
        return addressToObject32(cls, (int) j);
    }

    public static Object addressToObject64(Class cls, long j) {
        if (cls == null) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                return Integer.valueOf((int) j);
            }
            if (cls == Long.TYPE) {
                return Long.valueOf(j);
            }
            if (cls == Short.TYPE) {
                return Short.valueOf((short) j);
            }
            if (cls == Byte.TYPE) {
                return Byte.valueOf((byte) j);
            }
            if (cls == Character.TYPE) {
                return Character.valueOf((char) j);
            }
            if (cls == Boolean.TYPE) {
                return Boolean.valueOf(j != 0);
            }
            throw new RuntimeException("unknown type: " + cls.toString());
        }
        return SandHook.getObject(j);
    }

    public static Object addressToObject32(Class cls, int i) {
        if (cls == null) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                return Integer.valueOf(i);
            }
            if (cls == Short.TYPE) {
                return Short.valueOf((short) i);
            }
            if (cls == Byte.TYPE) {
                return Byte.valueOf((byte) i);
            }
            if (cls == Character.TYPE) {
                return Character.valueOf((char) i);
            }
            if (cls == Boolean.TYPE) {
                return Boolean.valueOf(i != 0);
            }
            throw new RuntimeException("unknown type: " + cls.toString());
        }
        return SandHook.getObject(i);
    }

    public static long objectToAddress(Class cls, Object obj) {
        if (is64Bit) {
            return objectToAddress64(cls, obj);
        }
        return objectToAddress32(cls, obj);
    }

    public static int objectToAddress32(Class cls, Object obj) {
        if (obj == null) {
            return 0;
        }
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                return ((Integer) obj).intValue();
            }
            if (cls == Short.TYPE) {
                return ((Short) obj).shortValue();
            }
            if (cls == Byte.TYPE) {
                return ((Byte) obj).byteValue();
            }
            if (cls == Character.TYPE) {
                return ((Character) obj).charValue();
            }
            if (cls == Boolean.TYPE) {
                return Boolean.TRUE.equals(obj) ? 1 : 0;
            }
            throw new RuntimeException("unknown type: " + cls.toString());
        }
        return (int) SandHook.getObjectAddress(obj);
    }

    public static long objectToAddress64(Class cls, Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                return ((Integer) obj).intValue();
            }
            if (cls == Long.TYPE) {
                return ((Long) obj).longValue();
            }
            if (cls == Short.TYPE) {
                return ((Short) obj).shortValue();
            }
            if (cls == Byte.TYPE) {
                return ((Byte) obj).byteValue();
            }
            if (cls == Character.TYPE) {
                return ((Character) obj).charValue();
            }
            if (cls == Boolean.TYPE) {
                return Boolean.TRUE.equals(obj) ? 1L : 0L;
            }
            throw new RuntimeException("unknown type: " + cls.toString());
        }
        return SandHook.getObjectAddress(obj);
    }
}
