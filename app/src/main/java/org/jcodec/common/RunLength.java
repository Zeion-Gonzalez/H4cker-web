package org.jcodec.common;

import android.support.v4.view.InputDeviceCompat;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public abstract class RunLength {
    protected IntArrayList counts = new IntArrayList();

    protected abstract void finish();

    protected abstract int recSize();

    public int estimateSize() {
        int[] counts = getCounts();
        int recCount = 0;
        int i = 0;
        while (i < counts.length) {
            for (int count = counts[i]; count >= 256; count += InputDeviceCompat.SOURCE_ANY) {
                recCount++;
            }
            i++;
            recCount++;
        }
        return (recSize() * recCount) + 4;
    }

    public int[] getCounts() {
        finish();
        return this.counts.toArray();
    }

    /* loaded from: classes.dex */
    public static class Integer extends RunLength {
        private static final int MIN_VALUE = Integer.MIN_VALUE;
        private int lastValue = Integer.MIN_VALUE;
        private int count = 0;
        private IntArrayList values = new IntArrayList();

        public void add(int value) {
            if (this.lastValue == Integer.MIN_VALUE || this.lastValue != value) {
                if (this.lastValue != Integer.MIN_VALUE) {
                    this.values.add(this.lastValue);
                    this.counts.add(this.count);
                    this.count = 0;
                }
                this.lastValue = value;
            }
            this.count++;
        }

        public int[] getValues() {
            finish();
            return this.values.toArray();
        }

        @Override // org.jcodec.common.RunLength
        protected void finish() {
            if (this.lastValue != Integer.MIN_VALUE) {
                this.values.add(this.lastValue);
                this.counts.add(this.count);
                this.lastValue = Integer.MIN_VALUE;
                this.count = 0;
            }
        }

        public void serialize(ByteBuffer bb) {
            ByteBuffer dup = bb.duplicate();
            int[] counts = getCounts();
            int[] values = getValues();
            NIOUtils.skip(bb, 4);
            int recCount = 0;
            int i = 0;
            while (i < counts.length) {
                int count = counts[i];
                while (count >= 256) {
                    bb.put((byte) -1);
                    bb.putInt(values[i]);
                    recCount++;
                    count += InputDeviceCompat.SOURCE_ANY;
                }
                bb.put((byte) (count - 1));
                bb.putInt(values[i]);
                i++;
                recCount++;
            }
            dup.putInt(recCount);
        }

        public static Integer parse(ByteBuffer bb) {
            Integer rl = new Integer();
            int recCount = bb.getInt();
            for (int i = 0; i < recCount; i++) {
                int count = (bb.get() & 255) + 1;
                int value = bb.getInt();
                rl.counts.add(count);
                rl.values.add(value);
            }
            return rl;
        }

        @Override // org.jcodec.common.RunLength
        protected int recSize() {
            return 5;
        }

        public int[] flattern() {
            int[] counts = getCounts();
            int total = 0;
            for (int i : counts) {
                total += i;
            }
            int[] values = getValues();
            int[] result = new int[total];
            int ind = 0;
            for (int i2 = 0; i2 < counts.length; i2++) {
                int j = 0;
                while (j < counts[i2]) {
                    result[ind] = values[i2];
                    j++;
                    ind++;
                }
            }
            return result;
        }
    }

    /* loaded from: classes.dex */
    public static class Long extends RunLength {
        private static final long MIN_VALUE = Long.MIN_VALUE;
        private long lastValue = MIN_VALUE;
        private int count = 0;
        private LongArrayList values = new LongArrayList();

        public void add(long value) {
            if (this.lastValue == MIN_VALUE || this.lastValue != value) {
                if (this.lastValue != MIN_VALUE) {
                    this.values.add(this.lastValue);
                    this.counts.add(this.count);
                    this.count = 0;
                }
                this.lastValue = value;
            }
            this.count++;
        }

        @Override // org.jcodec.common.RunLength
        public int[] getCounts() {
            finish();
            return this.counts.toArray();
        }

        public long[] getValues() {
            finish();
            return this.values.toArray();
        }

        @Override // org.jcodec.common.RunLength
        protected void finish() {
            if (this.lastValue != MIN_VALUE) {
                this.values.add(this.lastValue);
                this.counts.add(this.count);
                this.lastValue = MIN_VALUE;
                this.count = 0;
            }
        }

        public void serialize(ByteBuffer bb) {
            ByteBuffer dup = bb.duplicate();
            int[] counts = getCounts();
            long[] values = getValues();
            NIOUtils.skip(bb, 4);
            int recCount = 0;
            int i = 0;
            while (i < counts.length) {
                int count = counts[i];
                while (count >= 256) {
                    bb.put((byte) -1);
                    bb.putLong(values[i]);
                    recCount++;
                    count += InputDeviceCompat.SOURCE_ANY;
                }
                bb.put((byte) (count - 1));
                bb.putLong(values[i]);
                i++;
                recCount++;
            }
            dup.putInt(recCount);
        }

        public static Long parse(ByteBuffer bb) {
            Long rl = new Long();
            int recCount = bb.getInt();
            for (int i = 0; i < recCount; i++) {
                int count = (bb.get() & 255) + 1;
                long value = bb.getLong();
                rl.counts.add(count);
                rl.values.add(value);
            }
            return rl;
        }

        @Override // org.jcodec.common.RunLength
        protected int recSize() {
            return 9;
        }

        public long[] flattern() {
            int[] counts = getCounts();
            int total = 0;
            for (int i : counts) {
                total += i;
            }
            long[] values = getValues();
            long[] result = new long[total];
            int ind = 0;
            for (int i2 = 0; i2 < counts.length; i2++) {
                int j = 0;
                while (j < counts[i2]) {
                    result[ind] = values[i2];
                    j++;
                    ind++;
                }
            }
            return result;
        }
    }
}
