package org.jcodec.codecs.h264.io.model;

/* loaded from: classes.dex */
public class RefPicMarking {
    private Instruction[] instructions;

    /* loaded from: classes.dex */
    public enum InstrType {
        REMOVE_SHORT,
        REMOVE_LONG,
        CONVERT_INTO_LONG,
        TRUNK_LONG,
        CLEAR,
        MARK_LONG
    }

    /* loaded from: classes.dex */
    public static class Instruction {
        private int arg1;
        private int arg2;
        private InstrType type;

        public Instruction(InstrType type, int arg1, int arg2) {
            this.type = type;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        public InstrType getType() {
            return this.type;
        }

        public int getArg1() {
            return this.arg1;
        }

        public int getArg2() {
            return this.arg2;
        }
    }

    public RefPicMarking(Instruction[] instructions) {
        this.instructions = instructions;
    }

    public Instruction[] getInstructions() {
        return this.instructions;
    }
}
