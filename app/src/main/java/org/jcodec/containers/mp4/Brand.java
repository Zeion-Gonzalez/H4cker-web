package org.jcodec.containers.mp4;

import java.util.Arrays;
import org.jcodec.containers.mp4.boxes.FileTypeBox;

/* loaded from: classes.dex */
public enum Brand {
    MOV("qt  ", 512, new String[]{"qt  "}),
    MP4("isom", 512, new String[]{"isom", "iso2", "avc1", "mp41"});

    private FileTypeBox ftyp;

    Brand(String majorBrand, int version, String[] compatible) {
        this.ftyp = new FileTypeBox(majorBrand, version, Arrays.asList(compatible));
    }

    public FileTypeBox getFileTypeBox() {
        return this.ftyp;
    }
}
