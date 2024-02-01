package org.jcodec.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class Tuple {

    /* loaded from: classes.dex */
    public interface Mapper<T, U> {
        U map(T t);
    }

    /* renamed from: org.jcodec.common.Tuple$_1 */
    /* loaded from: classes.dex */
    public static class C0870_1<T0> {

        /* renamed from: v0 */
        public final T0 f1499v0;

        public C0870_1(T0 v0) {
            this.f1499v0 = v0;
        }
    }

    /* renamed from: org.jcodec.common.Tuple$_2 */
    /* loaded from: classes.dex */
    public static class C0871_2<T0, T1> {

        /* renamed from: v0 */
        public final T0 f1500v0;

        /* renamed from: v1 */
        public final T1 f1501v1;

        public C0871_2(T0 v0, T1 v1) {
            this.f1500v0 = v0;
            this.f1501v1 = v1;
        }
    }

    /* renamed from: org.jcodec.common.Tuple$_3 */
    /* loaded from: classes.dex */
    public static class C0872_3<T0, T1, T2> {

        /* renamed from: v0 */
        public final T0 f1502v0;

        /* renamed from: v1 */
        public final T1 f1503v1;

        /* renamed from: v2 */
        public final T2 f1504v2;

        public C0872_3(T0 v0, T1 v1, T2 v2) {
            this.f1502v0 = v0;
            this.f1503v1 = v1;
            this.f1504v2 = v2;
        }
    }

    /* renamed from: org.jcodec.common.Tuple$_4 */
    /* loaded from: classes.dex */
    public static class C0873_4<T0, T1, T2, T3> {

        /* renamed from: v0 */
        public final T0 f1505v0;

        /* renamed from: v1 */
        public final T1 f1506v1;

        /* renamed from: v2 */
        public final T2 f1507v2;

        /* renamed from: v3 */
        public final T3 f1508v3;

        public C0873_4(T0 v0, T1 v1, T2 v2, T3 v3) {
            this.f1505v0 = v0;
            this.f1506v1 = v1;
            this.f1507v2 = v2;
            this.f1508v3 = v3;
        }
    }

    /* renamed from: _1 */
    public static <T0> C0870_1<T0> m2127_1(T0 v0) {
        return new C0870_1<>(v0);
    }

    /* renamed from: _2 */
    public static <T0, T1> C0871_2<T0, T1> m2128_2(T0 v0, T1 v1) {
        return new C0871_2<>(v0, v1);
    }

    /* renamed from: _3 */
    public static <T0, T1, T2> C0872_3<T0, T1, T2> m2129_3(T0 v0, T1 v1, T2 v2) {
        return new C0872_3<>(v0, v1, v2);
    }

    /* renamed from: _4 */
    public static <T0, T1, T2, T3> C0873_4<T0, T1, T2, T3> m2130_4(T0 v0, T1 v1, T2 v2, T3 v3) {
        return new C0873_4<>(v0, v1, v2, v3);
    }

    public static <T0, T1> Map<T0, T1> asMap(Iterable<C0871_2<T0, T1>> it) {
        HashMap<T0, T1> result = new HashMap<>();
        for (C0871_2<T0, T1> el : it) {
            result.put(el.f1500v0, el.f1501v1);
        }
        return result;
    }

    public static <T0, T1> Map<T0, T1> asMap(C0871_2<T0, T1>[] arr) {
        HashMap<T0, T1> result = new HashMap<>();
        for (C0871_2<T0, T1> el : arr) {
            result.put(el.f1500v0, el.f1501v1);
        }
        return result;
    }

    public static <T0, T1> List<C0871_2<T0, T1>> asList(Map<T0, T1> m) {
        LinkedList<C0871_2<T0, T1>> result = new LinkedList<>();
        Set<Map.Entry<T0, T1>> entrySet = m.entrySet();
        for (Map.Entry<T0, T1> entry : entrySet) {
            result.add(m2128_2(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public static <T0, T1> C0871_2<T0, T1>[] asArray(Map<T0, T1> m) {
        C0871_2<T0, T1>[] result = (C0871_2[]) new Object[m.size()];
        Set<Map.Entry<T0, T1>> entrySet = m.entrySet();
        int i = 0;
        for (Map.Entry<T0, T1> entry : entrySet) {
            result[i] = m2128_2(entry.getKey(), entry.getValue());
            i++;
        }
        return result;
    }

    public static <T0> List<T0> _1_project0(List<C0870_1<T0>> temp) {
        List<T0> result = new LinkedList<>();
        for (C0870_1<T0> _1 : temp) {
            result.add(_1.f1499v0);
        }
        return result;
    }

    public static <T0, T1> List<T0> _2_project0(List<C0871_2<T0, T1>> temp) {
        List<T0> result = new LinkedList<>();
        for (C0871_2<T0, T1> _2 : temp) {
            result.add(_2.f1500v0);
        }
        return result;
    }

    public static <T0, T1> List<T1> _2_project1(List<C0871_2<T0, T1>> temp) {
        List<T1> result = new LinkedList<>();
        for (C0871_2<T0, T1> _2 : temp) {
            result.add(_2.f1501v1);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T0> _3_project0(List<C0872_3<T0, T1, T2>> temp) {
        List<T0> result = new LinkedList<>();
        for (C0872_3<T0, T1, T2> _3 : temp) {
            result.add(_3.f1502v0);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T1> _3_project1(List<C0872_3<T0, T1, T2>> temp) {
        List<T1> result = new LinkedList<>();
        for (C0872_3<T0, T1, T2> _3 : temp) {
            result.add(_3.f1503v1);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T2> _3_project2(List<C0872_3<T0, T1, T2>> temp) {
        List<T2> result = new LinkedList<>();
        for (C0872_3<T0, T1, T2> _3 : temp) {
            result.add(_3.f1504v2);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T0> _4_project0(List<C0873_4<T0, T1, T2, T3>> temp) {
        List<T0> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : temp) {
            result.add(_4.f1505v0);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T1> _4_project1(List<C0873_4<T0, T1, T2, T3>> temp) {
        List<T1> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : temp) {
            result.add(_4.f1506v1);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T2> _4_project2(List<C0873_4<T0, T1, T2, T3>> temp) {
        List<T2> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : temp) {
            result.add(_4.f1507v2);
        }
        return result;
    }

    public static <T0, T1, T2, T3> List<T3> _4_project3(List<C0873_4<T0, T1, T2, T3>> temp) {
        List<T3> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : temp) {
            result.add(_4.f1508v3);
        }
        return result;
    }

    public static <T0, U> List<C0870_1<U>> _1map0(List<C0870_1<T0>> l, Mapper<T0, U> mapper) {
        LinkedList<C0870_1<U>> result = new LinkedList<>();
        for (C0870_1<T0> _1 : l) {
            result.add(m2127_1(mapper.map(_1.f1499v0)));
        }
        return result;
    }

    public static <T0, T1, U> List<C0871_2<U, T1>> _2map0(List<C0871_2<T0, T1>> l, Mapper<T0, U> mapper) {
        LinkedList<C0871_2<U, T1>> result = new LinkedList<>();
        for (C0871_2<T0, T1> _2 : l) {
            result.add(m2128_2(mapper.map(_2.f1500v0), _2.f1501v1));
        }
        return result;
    }

    public static <T0, T1, U> List<C0871_2<T0, U>> _2map1(List<C0871_2<T0, T1>> l, Mapper<T1, U> mapper) {
        LinkedList<C0871_2<T0, U>> result = new LinkedList<>();
        for (C0871_2<T0, T1> _2 : l) {
            result.add(m2128_2(_2.f1500v0, mapper.map(_2.f1501v1)));
        }
        return result;
    }

    public static <T0, T1, T2, U> List<C0872_3<U, T1, T2>> _3map0(List<C0872_3<T0, T1, T2>> l, Mapper<T0, U> mapper) {
        LinkedList<C0872_3<U, T1, T2>> result = new LinkedList<>();
        for (C0872_3<T0, T1, T2> _3 : l) {
            result.add(m2129_3(mapper.map(_3.f1502v0), _3.f1503v1, _3.f1504v2));
        }
        return result;
    }

    public static <T0, T1, T2, U> List<C0872_3<T0, U, T2>> _3map1(List<C0872_3<T0, T1, T2>> l, Mapper<T1, U> mapper) {
        LinkedList<C0872_3<T0, U, T2>> result = new LinkedList<>();
        for (C0872_3<T0, T1, T2> _3 : l) {
            result.add(m2129_3(_3.f1502v0, mapper.map(_3.f1503v1), _3.f1504v2));
        }
        return result;
    }

    public static <T0, T1, T2, U> List<C0872_3<T0, T1, U>> _3map3(List<C0872_3<T0, T1, T2>> l, Mapper<T2, U> mapper) {
        LinkedList<C0872_3<T0, T1, U>> result = new LinkedList<>();
        for (C0872_3<T0, T1, T2> _3 : l) {
            result.add(m2129_3(_3.f1502v0, _3.f1503v1, mapper.map(_3.f1504v2)));
        }
        return result;
    }

    public static <T0, T1, T2, T3, U> List<C0873_4<U, T1, T2, T3>> _4map0(List<C0873_4<T0, T1, T2, T3>> l, Mapper<T0, U> mapper) {
        LinkedList<C0873_4<U, T1, T2, T3>> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : l) {
            result.add(m2130_4(mapper.map(_4.f1505v0), _4.f1506v1, _4.f1507v2, _4.f1508v3));
        }
        return result;
    }

    public static <T0, T1, T2, T3, U> List<C0873_4<T0, U, T2, T3>> _4map1(List<C0873_4<T0, T1, T2, T3>> l, Mapper<T1, U> mapper) {
        LinkedList<C0873_4<T0, U, T2, T3>> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : l) {
            result.add(m2130_4(_4.f1505v0, mapper.map(_4.f1506v1), _4.f1507v2, _4.f1508v3));
        }
        return result;
    }

    public static <T0, T1, T2, T3, U> List<C0873_4<T0, T1, U, T3>> _4map3(List<C0873_4<T0, T1, T2, T3>> l, Mapper<T2, U> mapper) {
        LinkedList<C0873_4<T0, T1, U, T3>> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : l) {
            result.add(m2130_4(_4.f1505v0, _4.f1506v1, mapper.map(_4.f1507v2), _4.f1508v3));
        }
        return result;
    }

    public static <T0, T1, T2, T3, U> List<C0873_4<T0, T1, T2, U>> _4map4(List<C0873_4<T0, T1, T2, T3>> l, Mapper<T3, U> mapper) {
        LinkedList<C0873_4<T0, T1, T2, U>> result = new LinkedList<>();
        for (C0873_4<T0, T1, T2, T3> _4 : l) {
            result.add(m2130_4(_4.f1505v0, _4.f1506v1, _4.f1507v2, mapper.map(_4.f1508v3)));
        }
        return result;
    }
}
