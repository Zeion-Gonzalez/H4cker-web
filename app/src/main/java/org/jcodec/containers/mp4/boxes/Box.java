package org.jcodec.containers.mp4.boxes;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.tools.ToJSON;

/* loaded from: classes.dex */
public abstract class Box {
    private static final String GET_MODEL_FIELDS = "getModelFields";
    protected Header header;

    protected abstract void doWrite(ByteBuffer byteBuffer);

    public abstract void parse(ByteBuffer byteBuffer);

    public Box(Header header) {
        this.header = header;
    }

    public Box(Box other) {
        this.header = other.header;
    }

    public Header getHeader() {
        return this.header;
    }

    public static Box findFirst(NodeBox box, String... path) {
        return (Box) findFirst(box, Box.class, path);
    }

    public static <T> T findFirst(NodeBox box, Class<T> clazz, String... path) {
        Object[] findAll = findAll(box, clazz, path);
        if (findAll.length > 0) {
            return (T) findAll[0];
        }
        return null;
    }

    public static Box[] findAll(Box box, String... path) {
        return (Box[]) findAll(box, Box.class, path);
    }

    private static void findSub(Box box, List<String> path, Collection<Box> result) {
        if (path.size() > 0) {
            String head = path.remove(0);
            if (box instanceof NodeBox) {
                NodeBox nb = (NodeBox) box;
                for (Box candidate : nb.getBoxes()) {
                    if (head == null || head.equals(candidate.header.getFourcc())) {
                        findSub(candidate, path, result);
                    }
                }
            }
            path.add(0, head);
            return;
        }
        result.add(box);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T[] findAll(Box box, Class<T> class1, String... path) {
        List<Box> result = new LinkedList<>();
        List<String> tlist = new LinkedList<>();
        for (String type : path) {
            tlist.add(type);
        }
        findSub(box, tlist, result);
        return (T[]) result.toArray((Object[]) Array.newInstance((Class<?>) class1, 0));
    }

    public void write(ByteBuffer buf) {
        ByteBuffer dup = buf.duplicate();
        NIOUtils.skip(buf, 8);
        doWrite(buf);
        this.header.setBodySize((buf.position() - dup.position()) - 8);
        Assert.assertEquals(this.header.headerSize(), 8);
        this.header.write(dup);
    }

    public String getFourcc() {
        return this.header.getFourcc();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        dump(sb);
        return sb.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return toString().equals(obj.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dump(StringBuilder sb) {
        sb.append("{\"tag\":\"" + this.header.getFourcc() + "\",");
        List<String> fields = new ArrayList<>(0);
        collectModel(getClass(), fields);
        ToJSON.fieldsToJSON(this, sb, (String[]) fields.toArray(new String[0]));
        sb.append("}");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void collectModel(Class claz, List<String> model) {
        if (Box.class != claz && Box.class.isAssignableFrom(claz)) {
            collectModel(claz.getSuperclass(), model);
            try {
                Method method = claz.getDeclaredMethod(GET_MODEL_FIELDS, List.class);
                method.invoke(this, model);
            } catch (NoSuchMethodException e) {
                checkWrongSignature(claz);
                model.addAll(ToJSON.allFields(claz));
            } catch (Exception e2) {
            }
        }
    }

    private void checkWrongSignature(Class claz) {
        Method[] arr$ = claz.getDeclaredMethods();
        for (Method method : arr$) {
            if (method.getName().equals(GET_MODEL_FIELDS)) {
                Logger.warn("Class " + claz.getCanonicalName() + " contains 'getModelFields' of wrong signature.\nDid you mean to define 'protected void " + GET_MODEL_FIELDS + "(List<String> model) ?");
                return;
            }
        }
    }

    /* renamed from: as */
    public static <T extends Box> T m2135as(Class<T> class1, LeafBox box) {
        try {
            T res = class1.getConstructor(Header.class).newInstance(box.getHeader());
            res.parse(box.getData());
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
