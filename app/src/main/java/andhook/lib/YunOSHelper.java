package andhook.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;

/* loaded from: classes2.dex */
public final class YunOSHelper {
    private static final HashMap<Class<?>, String> PRIMITIVE_TO_SIGNATURE = new HashMap<>(9);

    static {
        PRIMITIVE_TO_SIGNATURE.put(Byte.TYPE, "B");
        PRIMITIVE_TO_SIGNATURE.put(Character.TYPE, "C");
        PRIMITIVE_TO_SIGNATURE.put(Short.TYPE, "S");
        PRIMITIVE_TO_SIGNATURE.put(Integer.TYPE, "I");
        PRIMITIVE_TO_SIGNATURE.put(Long.TYPE, "J");
        PRIMITIVE_TO_SIGNATURE.put(Float.TYPE, "F");
        PRIMITIVE_TO_SIGNATURE.put(Double.TYPE, "D");
        PRIMITIVE_TO_SIGNATURE.put(Void.TYPE, "V");
        PRIMITIVE_TO_SIGNATURE.put(Boolean.TYPE, "Z");
    }

    public static String getSignature(Class<?> cls) {
        String str = PRIMITIVE_TO_SIGNATURE.get(cls);
        if (str != null) {
            return str;
        }
        if (cls.isArray()) {
            return "[" + getSignature(cls.getComponentType());
        }
        return "L" + cls.getName().replace('.', IOUtils.DIR_SEPARATOR_UNIX) + ";";
    }

    public static String getNativeTypeCode(Class<?> cls) {
        String str = PRIMITIVE_TO_SIGNATURE.get(cls);
        return str != null ? str : "L";
    }

    private static String getSignature(Class<?> cls, Class<?>[] clsArr) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class<?> cls2 : clsArr) {
            sb.append(getSignature(cls2));
        }
        sb.append(")");
        sb.append(getSignature(cls));
        return sb.toString();
    }

    public static String getSignature(Member member) {
        if (member instanceof Method) {
            Method method = (Method) member;
            return getSignature(method.getReturnType(), method.getParameterTypes());
        }
        if (member instanceof Constructor) {
            return getSignature(Void.TYPE, ((Constructor) member).getParameterTypes());
        }
        return null;
    }

    private static String getShorty(Class<?> cls, Class<?>[] clsArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(getNativeTypeCode(cls));
        for (Class<?> cls2 : clsArr) {
            sb.append(getNativeTypeCode(cls2));
        }
        return sb.toString();
    }

    public static String getShorty(Member member) {
        if (member instanceof Method) {
            Method method = (Method) member;
            return getShorty(method.getReturnType(), method.getParameterTypes());
        }
        if (member instanceof Constructor) {
            return getShorty(Void.TYPE, ((Constructor) member).getParameterTypes());
        }
        return null;
    }
}
