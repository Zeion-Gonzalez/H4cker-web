package org.jcodec.containers.mps.psi;

import java.nio.ByteBuffer;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.IntIntMap;

/* loaded from: classes.dex */
public class PATSection extends PSISection {
    private int[] networkPids;
    private IntIntMap programs;

    public PATSection(PSISection psi, int[] networkPids, IntIntMap programs) {
        super(psi);
        this.networkPids = networkPids;
        this.programs = programs;
    }

    public int[] getNetworkPids() {
        return this.networkPids;
    }

    public IntIntMap getPrograms() {
        return this.programs;
    }

    public static PATSection parse(ByteBuffer data) {
        PSISection psi = PSISection.parse(data);
        IntArrayList networkPids = new IntArrayList();
        IntIntMap programs = new IntIntMap();
        while (data.remaining() > 4) {
            int programNum = data.getShort() & 65535;
            int w = data.getShort();
            int pid = w & 8191;
            if (programNum == 0) {
                networkPids.add(pid);
            } else {
                programs.put(programNum, pid);
            }
        }
        return new PATSection(psi, networkPids.toArray(), programs);
    }
}
