package org.jcodec.containers.mxf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MXFUtil {
    public static <T> T resolveRef(List<MXFMetadata> metadata, C0893UL refs, Class<T> class1) {
        List<T> res = resolveRefs(metadata, new C0893UL[]{refs}, class1);
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public static <T> List<T> resolveRefs(List<MXFMetadata> metadata, C0893UL[] refs, Class<T> class1) {
        List<MXFMetadata> copy = new ArrayList<>(metadata);
        Iterator<MXFMetadata> iterator = copy.iterator();
        while (iterator.hasNext()) {
            MXFMetadata next = iterator.next();
            if (next.getUid() == null || !class1.isAssignableFrom(next.getClass())) {
                iterator.remove();
            }
        }
        List result = new ArrayList();
        for (C0893UL c0893ul : refs) {
            for (MXFMetadata meta : copy) {
                if (meta.getUid().equals(c0893ul)) {
                    result.add(meta);
                }
            }
        }
        return result;
    }

    public static <T> T findMeta(Collection<MXFMetadata> metadata, Class<T> class1) {
        Iterator i$ = metadata.iterator();
        while (i$.hasNext()) {
            T t = (T) i$.next();
            if (t.getClass().isAssignableFrom(class1)) {
                return t;
            }
        }
        return null;
    }

    public static <T> List<T> findAllMeta(Collection<MXFMetadata> metadata, Class<T> class1) {
        List result = new ArrayList();
        for (MXFMetadata mxfMetadata : metadata) {
            if (class1.isAssignableFrom(mxfMetadata.getClass())) {
                result.add(mxfMetadata);
            }
        }
        return result;
    }
}
