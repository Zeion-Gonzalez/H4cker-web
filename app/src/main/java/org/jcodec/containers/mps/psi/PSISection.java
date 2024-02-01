package org.jcodec.containers.mps.psi;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class PSISection {
    private int currentNextIndicator;
    private int lastSectionNumber;
    private int sectionNumber;
    private int specificId;
    private int tableId;
    private int versionNumber;

    public PSISection(PSISection other) {
        this(other.tableId, other.specificId, other.versionNumber, other.currentNextIndicator, other.sectionNumber, other.lastSectionNumber);
    }

    public PSISection(int tableId, int specificId, int versionNumber, int currentNextIndicator, int sectionNumber, int lastSectionNumber) {
        this.tableId = tableId;
        this.specificId = specificId;
        this.versionNumber = versionNumber;
        this.currentNextIndicator = currentNextIndicator;
        this.sectionNumber = sectionNumber;
        this.lastSectionNumber = lastSectionNumber;
    }

    public static PSISection parse(ByteBuffer data) {
        int tableId = data.get() & 255;
        int w0 = data.getShort() & 65535;
        if ((49152 & w0) != 32768) {
            throw new RuntimeException("Invalid section data");
        }
        int sectionLength = w0 & 4095;
        data.limit(data.position() + sectionLength);
        int specificId = data.getShort() & 65535;
        int b0 = data.get() & 255;
        int versionNumber = (b0 >> 1) & 31;
        int currentNextIndicator = b0 & 1;
        int sectionNumber = data.get() & 255;
        int lastSectionNumber = data.get() & 255;
        return new PSISection(tableId, specificId, versionNumber, currentNextIndicator, sectionNumber, lastSectionNumber);
    }

    public int getTableId() {
        return this.tableId;
    }

    public int getSpecificId() {
        return this.specificId;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public int getCurrentNextIndicator() {
        return this.currentNextIndicator;
    }

    public int getSectionNumber() {
        return this.sectionNumber;
    }

    public int getLastSectionNumber() {
        return this.lastSectionNumber;
    }
}
