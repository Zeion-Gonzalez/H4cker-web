package org.jcodec.common.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class ToJSON {
    static Set<Class> primitive = new HashSet();
    static Set<String> omitMethods = new HashSet();

    static {
        primitive.add(Boolean.class);
        primitive.add(Byte.class);
        primitive.add(Short.class);
        primitive.add(Integer.class);
        primitive.add(Long.class);
        primitive.add(Float.class);
        primitive.add(Double.class);
        primitive.add(Character.class);
        omitMethods.add("getClass");
        omitMethods.add("get");
    }

    public static List<String> allFields(Class claz) {
        return allFieldsExcept(claz, new String[0]);
    }

    public static List<String> allFieldsExcept(Class claz, String... except) {
        List<String> result = new ArrayList<>();
        Method[] arr$ = claz.getDeclaredMethods();
        for (Method method : arr$) {
            if (isGetter(method)) {
                try {
                    String name = toName(method);
                    result.add(name);
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public static String toJSON(Object obj) {
        StringBuilder builder = new StringBuilder();
        IntArrayList stack = new IntArrayList();
        toJSONSub(obj, stack, builder);
        return builder.toString();
    }

    public static void fieldsToJSON(Object obj, StringBuilder builder, String... fields) {
        Method[] methods = obj.getClass().getMethods();
        for (String field : fields) {
            Method m = findGetter(methods, field);
            if (m != null) {
                invoke(obj, new IntArrayList(), builder, m, field);
            }
        }
    }

    private static Method findGetter(Method[] methods, String field) {
        String isGetter = getterName("is", field);
        String getGetter = getterName("get", field);
        for (Method method : methods) {
            if ((isGetter.equals(method.getName()) || getGetter.equals(method.getName())) && isGetter(method)) {
                return method;
            }
        }
        return null;
    }

    private static String getterName(String pref, String field) {
        if (field == null) {
            throw new IllegalArgumentException("Passed null string as field name");
        }
        char[] ch = field.toCharArray();
        if (ch.length != 0) {
            if (ch.length > 1 && Character.isUpperCase(ch[1])) {
                ch[0] = Character.toLowerCase(ch[0]);
            } else {
                ch[0] = Character.toUpperCase(ch[0]);
            }
            return pref + new String(ch);
        }
        return pref;
    }

    private static void toJSONSub(Object obj, IntArrayList stack, StringBuilder builder) {
        int id = System.identityHashCode(obj);
        if (stack.contains(id)) {
            builder.append("\"!-!-!-!-LOOP-!-!-!-!\"");
            return;
        }
        stack.push(id);
        if (obj instanceof ByteBuffer) {
            obj = NIOUtils.toArray((ByteBuffer) obj);
        }
        if (obj == null) {
            builder.append("null");
        } else if (obj instanceof String) {
            builder.append("\"");
            escape((String) obj, builder);
            builder.append("\"");
        } else if (obj instanceof Map) {
            Iterator it = ((Map) obj).entrySet().iterator();
            builder.append("{");
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                builder.append("\"");
                builder.append(e.getKey());
                builder.append("\":");
                toJSONSub(e.getValue(), stack, builder);
                if (it.hasNext()) {
                    builder.append(",");
                }
            }
            builder.append("}");
        } else if (obj instanceof Iterable) {
            Iterator it2 = ((Iterable) obj).iterator();
            builder.append("[");
            while (it2.hasNext()) {
                toJSONSub(it2.next(), stack, builder);
                if (it2.hasNext()) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof Object[]) {
            builder.append("[");
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                toJSONSub(Array.get(obj, i), stack, builder);
                if (i < len - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof long[]) {
            long[] a = (long[]) obj;
            builder.append("[");
            for (int i2 = 0; i2 < a.length; i2++) {
                builder.append(String.format("0x%016x", Long.valueOf(a[i2])));
                if (i2 < a.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof int[]) {
            int[] a2 = (int[]) obj;
            builder.append("[");
            for (int i3 = 0; i3 < a2.length; i3++) {
                builder.append(String.format("0x%08x", Integer.valueOf(a2[i3])));
                if (i3 < a2.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof float[]) {
            float[] a3 = (float[]) obj;
            builder.append("[");
            for (int i4 = 0; i4 < a3.length; i4++) {
                builder.append(String.format(".3f", Float.valueOf(a3[i4])));
                if (i4 < a3.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof double[]) {
            double[] a4 = (double[]) obj;
            builder.append("[");
            for (int i5 = 0; i5 < a4.length; i5++) {
                builder.append(String.format(".6f", Double.valueOf(a4[i5])));
                if (i5 < a4.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof short[]) {
            short[] a5 = (short[]) obj;
            builder.append("[");
            for (int i6 = 0; i6 < a5.length; i6++) {
                builder.append(String.format("0x%04x", Short.valueOf(a5[i6])));
                if (i6 < a5.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof byte[]) {
            byte[] a6 = (byte[]) obj;
            builder.append("[");
            for (int i7 = 0; i7 < a6.length; i7++) {
                builder.append(String.format("0x%02x", Byte.valueOf(a6[i7])));
                if (i7 < a6.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else if (obj instanceof boolean[]) {
            boolean[] a7 = (boolean[]) obj;
            builder.append("[");
            for (int i8 = 0; i8 < a7.length; i8++) {
                builder.append(a7[i8]);
                if (i8 < a7.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        } else {
            builder.append("{");
            Method[] arr$ = obj.getClass().getMethods();
            for (Method method : arr$) {
                if (!omitMethods.contains(method.getName()) && isGetter(method)) {
                    String name = toName(method);
                    invoke(obj, stack, builder, method, name);
                }
            }
            builder.append("}");
        }
        stack.pop();
    }

    private static void invoke(Object obj, IntArrayList stack, StringBuilder builder, Method method, String name) {
        try {
            Object invoke = method.invoke(obj, new Object[0]);
            builder.append('\"');
            builder.append(name);
            builder.append("\":");
            if (invoke != null && primitive.contains(invoke.getClass())) {
                builder.append(invoke);
            } else {
                toJSONSub(invoke, stack, builder);
            }
            builder.append(",");
        } catch (Exception e) {
        }
    }

    private static void escape(String invoke, StringBuilder sb) {
        char[] ch = invoke.toCharArray();
        for (char c : ch) {
            if (c < ' ') {
                sb.append(String.format("\\%02x", Integer.valueOf(c)));
            } else {
                sb.append(c);
            }
        }
    }

    private static String toName(Method method) {
        if (!isGetter(method)) {
            throw new IllegalArgumentException("Not a getter");
        }
        char[] name = method.getName().toCharArray();
        int ind = name[0] == 'g' ? 3 : 2;
        name[ind] = Character.toLowerCase(name[ind]);
        return new String(name, ind, name.length - ind);
    }

    public static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers())) {
            return (method.getName().startsWith("get") || (method.getName().startsWith("is") && method.getReturnType() == Boolean.TYPE)) && method.getParameterTypes().length == 0 && !Void.TYPE.equals(method.getReturnType());
        }
        return false;
    }
}
