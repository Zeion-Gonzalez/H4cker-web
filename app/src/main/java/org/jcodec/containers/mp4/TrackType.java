package org.jcodec.containers.mp4;

import java.util.EnumSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public enum TrackType {
    VIDEO("vide"),
    SOUND("soun"),
    TIMECODE("tmcd"),
    HINT("hint"),
    TEXT("text"),
    HYPER_TEXT("wtxt"),
    CC("clcp"),
    SUB("sbtl"),
    MUSIC("musi"),
    MPEG1("MPEG"),
    SPRITE("sprt"),
    TWEEN("twen"),
    CHAPTERS("chap"),
    THREE_D("qd3d"),
    STREAMING("strm"),
    OBJECTS("obje");

    private String handler;

    TrackType(String handler) {
        this.handler = handler;
    }

    public String getHandler() {
        return this.handler;
    }

    public static TrackType fromHandler(String handler) {
        Iterator i$ = EnumSet.allOf(TrackType.class).iterator();
        while (i$.hasNext()) {
            TrackType val = (TrackType) i$.next();
            if (val.getHandler().equals(handler)) {
                return val;
            }
        }
        return null;
    }
}
