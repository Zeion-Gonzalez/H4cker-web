package org.jcodec.codecs.h264.decode.aso;

/* loaded from: classes.dex */
public class SliceGroupMapBuilder {
    public static int[] buildInterleavedMap(int picWidthInMbs, int picHeightInMbs, int[] runLength) {
        int numSliceGroups = runLength.length;
        int picSizeInMbs = picWidthInMbs * picHeightInMbs;
        int[] groups = new int[picSizeInMbs];
        int i = 0;
        do {
            for (int iGroup = 0; iGroup < numSliceGroups && i < picSizeInMbs; iGroup++) {
                for (int j = 0; j < runLength[iGroup] && i + j < picSizeInMbs; j++) {
                    groups[i + j] = iGroup;
                }
                i += runLength[iGroup];
            }
        } while (i < picSizeInMbs);
        return groups;
    }

    public static int[] buildDispersedMap(int picWidthInMbs, int picHeightInMbs, int numSliceGroups) {
        int picSizeInMbs = picWidthInMbs * picHeightInMbs;
        int[] groups = new int[picSizeInMbs];
        for (int i = 0; i < picSizeInMbs; i++) {
            int group = ((i % picWidthInMbs) + (((i / picWidthInMbs) * numSliceGroups) / 2)) % numSliceGroups;
            groups[i] = group;
        }
        return groups;
    }

    public static int[] buildForegroundMap(int picWidthInMbs, int picHeightInMbs, int numSliceGroups, int[] topLeftAddr, int[] bottomRightAddr) {
        int picSizeInMbs = picWidthInMbs * picHeightInMbs;
        int[] groups = new int[picSizeInMbs];
        for (int i = 0; i < picSizeInMbs; i++) {
            groups[i] = numSliceGroups - 1;
        }
        int tot = 0;
        for (int iGroup = numSliceGroups - 2; iGroup >= 0; iGroup--) {
            int yTopLeft = topLeftAddr[iGroup] / picWidthInMbs;
            int xTopLeft = topLeftAddr[iGroup] % picWidthInMbs;
            int yBottomRight = bottomRightAddr[iGroup] / picWidthInMbs;
            int xBottomRight = bottomRightAddr[iGroup] % picWidthInMbs;
            int sz = ((yBottomRight - yTopLeft) + 1) * ((xBottomRight - xTopLeft) + 1);
            tot += sz;
            for (int y = yTopLeft; y <= yBottomRight; y++) {
                for (int x = xTopLeft; x <= xBottomRight; x++) {
                    int mbAddr = (y * picWidthInMbs) + x;
                    groups[mbAddr] = iGroup;
                }
            }
        }
        return groups;
    }

    public static int[] buildBoxOutMap(int picWidthInMbs, int picHeightInMbs, boolean changeDirection, int numberOfMbsInBox) {
        int picSizeInMbs = picWidthInMbs * picHeightInMbs;
        int[] groups = new int[picSizeInMbs];
        int changeDirectionInt = changeDirection ? 1 : 0;
        for (int i = 0; i < picSizeInMbs; i++) {
            groups[i] = 1;
        }
        int x = (picWidthInMbs - changeDirectionInt) / 2;
        int y = (picHeightInMbs - changeDirectionInt) / 2;
        int leftBound = x;
        int topBound = y;
        int rightBound = x;
        int bottomBound = y;
        int xDir = changeDirectionInt - 1;
        int yDir = changeDirectionInt;
        int k = 0;
        while (k < numberOfMbsInBox) {
            int mbAddr = (y * picWidthInMbs) + x;
            boolean mapUnitVacant = groups[mbAddr] == 1;
            if (mapUnitVacant) {
                groups[mbAddr] = 0;
            }
            if (xDir == -1 && x == leftBound) {
                leftBound = Max(leftBound - 1, 0);
                x = leftBound;
                xDir = 0;
                yDir = (changeDirectionInt * 2) - 1;
            } else if (xDir == 1 && x == rightBound) {
                rightBound = Min(rightBound + 1, picWidthInMbs - 1);
                x = rightBound;
                xDir = 0;
                yDir = 1 - (changeDirectionInt * 2);
            } else if (yDir == -1 && y == topBound) {
                topBound = Max(topBound - 1, 0);
                y = topBound;
                xDir = 1 - (changeDirectionInt * 2);
                yDir = 0;
            } else if (yDir == 1 && y == bottomBound) {
                bottomBound = Min(bottomBound + 1, picHeightInMbs - 1);
                y = bottomBound;
                xDir = (changeDirectionInt * 2) - 1;
                yDir = 0;
            } else {
                x += xDir;
                y += yDir;
            }
            k += mapUnitVacant ? 1 : 0;
        }
        return groups;
    }

    private static int Min(int i, int j) {
        return i < j ? i : j;
    }

    private static int Max(int i, int j) {
        return i > j ? i : j;
    }

    public static int[] buildRasterScanMap(int picWidthInMbs, int picHeightInMbs, int sizeOfUpperLeftGroup, boolean changeDirection) {
        int picSizeInMbs = picWidthInMbs * picHeightInMbs;
        int[] groups = new int[picSizeInMbs];
        int changeDirectionInt = changeDirection ? 1 : 0;
        int i = 0;
        while (i < sizeOfUpperLeftGroup) {
            groups[i] = changeDirectionInt;
            i++;
        }
        while (i < picSizeInMbs) {
            groups[i] = 1 - changeDirectionInt;
            i++;
        }
        return groups;
    }

    public static int[] buildWipeMap(int picWidthInMbs, int picHeightInMbs, int sizeOfUpperLeftGroup, boolean changeDirection) {
        int picSizeInMbs = picWidthInMbs * picHeightInMbs;
        int[] groups = new int[picSizeInMbs];
        int changeDirectionInt = changeDirection ? 1 : 0;
        int k = 0;
        int j = 0;
        while (j < picWidthInMbs) {
            int i = 0;
            int k2 = k;
            while (i < picHeightInMbs) {
                int mbAddr = (i * picWidthInMbs) + j;
                int k3 = k2 + 1;
                if (k2 < sizeOfUpperLeftGroup) {
                    groups[mbAddr] = changeDirectionInt;
                } else {
                    groups[mbAddr] = 1 - changeDirectionInt;
                }
                i++;
                k2 = k3;
            }
            j++;
            k = k2;
        }
        return groups;
    }
}
