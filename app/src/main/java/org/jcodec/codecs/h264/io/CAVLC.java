package org.jcodec.codecs.h264.io;

import org.jcodec.codecs.h264.H264Const;
import org.jcodec.codecs.h264.decode.CAVLCReader;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.io.VLC;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class CAVLC {
    public static int[] NO_ZIGZAG = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private ColorSpace color;
    private int mbMask;
    private int mbWidth;
    private int[] tokensTop;
    private VLC chromaDCVLC = codeTableChromaDC();
    private int[] tokensLeft = new int[4];

    public CAVLC(SeqParameterSet sps, PictureParameterSet pps, int mbW, int mbH) {
        this.color = sps.chroma_format_idc;
        this.mbWidth = sps.pic_width_in_mbs_minus1 + 1;
        this.mbMask = (1 << mbH) - 1;
        this.tokensTop = new int[this.mbWidth << mbW];
    }

    public void writeACBlock(BitWriter out, int blkIndX, int blkIndY, MBType leftMBType, MBType topMBType, int[] coeff, VLC[] totalZerosTab, int firstCoeff, int maxCoeff, int[] scan) {
        VLC coeffTokenTab = getCoeffTokenVLCForLuma(blkIndX != 0, leftMBType, this.tokensLeft[this.mbMask & blkIndY], blkIndY != 0, topMBType, this.tokensTop[blkIndX]);
        int coeffToken = writeBlockGen(out, coeff, totalZerosTab, firstCoeff, maxCoeff, scan, coeffTokenTab);
        this.tokensLeft[this.mbMask & blkIndY] = coeffToken;
        this.tokensTop[blkIndX] = coeffToken;
    }

    public void writeChrDCBlock(BitWriter out, int[] coeff, VLC[] totalZerosTab, int firstCoeff, int maxCoeff, int[] scan) {
        writeBlockGen(out, coeff, totalZerosTab, firstCoeff, maxCoeff, scan, getCoeffTokenVLCForChromaDC());
    }

    public void writeLumaDCBlock(BitWriter out, int blkIndX, int blkIndY, MBType leftMBType, MBType topMBType, int[] coeff, VLC[] totalZerosTab, int firstCoeff, int maxCoeff, int[] scan) {
        VLC coeffTokenTab = getCoeffTokenVLCForLuma(blkIndX != 0, leftMBType, this.tokensLeft[this.mbMask & blkIndY], blkIndY != 0, topMBType, this.tokensTop[blkIndX]);
        writeBlockGen(out, coeff, totalZerosTab, firstCoeff, maxCoeff, scan, coeffTokenTab);
    }

    private int writeBlockGen(BitWriter out, int[] coeff, VLC[] totalZerosTab, int firstCoeff, int maxCoeff, int[] scan, VLC coeffTokenTab) {
        int totalCoeff;
        int totalCoeff2 = 0;
        int totalZeros = 0;
        int[] runBefore = new int[maxCoeff];
        int[] levels = new int[maxCoeff];
        int i = 0;
        while (true) {
            totalCoeff = totalCoeff2;
            if (i >= maxCoeff) {
                break;
            }
            int c = coeff[scan[i + firstCoeff]];
            if (c == 0) {
                runBefore[totalCoeff] = runBefore[totalCoeff] + 1;
                totalZeros++;
                totalCoeff2 = totalCoeff;
            } else {
                totalCoeff2 = totalCoeff + 1;
                levels[totalCoeff] = c;
            }
            i++;
        }
        if (totalCoeff < maxCoeff) {
            totalZeros -= runBefore[totalCoeff];
        }
        int trailingOnes = 0;
        while (trailingOnes < totalCoeff && trailingOnes < 3 && Math.abs(levels[(totalCoeff - trailingOnes) - 1]) == 1) {
            trailingOnes++;
        }
        int coeffToken = coeffToken(totalCoeff, trailingOnes);
        coeffTokenTab.writeVLC(out, coeffToken);
        if (totalCoeff > 0) {
            writeTrailingOnes(out, levels, totalCoeff, trailingOnes);
            writeLevels(out, levels, totalCoeff, trailingOnes);
            if (totalCoeff < maxCoeff) {
                totalZerosTab[totalCoeff - 1].writeVLC(out, totalZeros);
                writeRuns(out, runBefore, totalCoeff, totalZeros);
            }
        }
        return coeffToken;
    }

    private void writeTrailingOnes(BitWriter out, int[] levels, int totalCoeff, int trailingOne) {
        for (int i = totalCoeff - 1; i >= totalCoeff - trailingOne; i--) {
            out.write1Bit(levels[i] >>> 31);
        }
    }

    private void writeLevels(BitWriter out, int[] levels, int totalCoeff, int trailingOnes) {
        int code;
        int suffixLen = (totalCoeff <= 10 || trailingOnes >= 3) ? 0 : 1;
        for (int i = (totalCoeff - trailingOnes) - 1; i >= 0; i--) {
            int absLev = unsigned(levels[i]);
            if (i == (totalCoeff - trailingOnes) - 1 && trailingOnes < 3) {
                absLev -= 2;
            }
            int prefix = absLev >> suffixLen;
            if ((suffixLen == 0 && prefix < 14) || (suffixLen > 0 && prefix < 15)) {
                out.writeNBit(1, prefix + 1);
                out.writeNBit(absLev, suffixLen);
            } else if (suffixLen == 0 && absLev < 30) {
                out.writeNBit(1, 15);
                out.writeNBit(absLev - 14, 4);
            } else {
                if (suffixLen == 0) {
                    absLev -= 15;
                }
                int len = 12;
                while (true) {
                    code = ((absLev - ((len + 3) << suffixLen)) - (1 << len)) + 4096;
                    if (code < (1 << len)) {
                        break;
                    } else {
                        len++;
                    }
                }
                out.writeNBit(1, len + 4);
                out.writeNBit(code, len);
            }
            if (suffixLen == 0) {
                suffixLen = 1;
            }
            if (MathUtil.abs(levels[i]) > (3 << (suffixLen - 1)) && suffixLen < 6) {
                suffixLen++;
            }
        }
    }

    private final int unsigned(int signed) {
        int sign = signed >>> 31;
        int s = signed >> 31;
        return ((((signed ^ s) - s) << 1) + sign) - 2;
    }

    private void writeRuns(BitWriter out, int[] run, int totalCoeff, int totalZeros) {
        for (int i = totalCoeff - 1; i > 0 && totalZeros > 0; i--) {
            H264Const.run[Math.min(6, totalZeros - 1)].writeVLC(out, run[i]);
            totalZeros -= run[i];
        }
    }

    public VLC getCoeffTokenVLCForLuma(boolean leftAvailable, MBType leftMBType, int leftToken, boolean topAvailable, MBType topMBType, int topToken) {
        int nc = codeTableLuma(leftAvailable, leftMBType, leftToken, topAvailable, topMBType, topToken);
        return H264Const.coeffToken[Math.min(nc, 8)];
    }

    public VLC getCoeffTokenVLCForChromaDC() {
        return this.chromaDCVLC;
    }

    protected int codeTableLuma(boolean leftAvailable, MBType leftMBType, int leftToken, boolean topAvailable, MBType topMBType, int topToken) {
        int nA = leftMBType == null ? 0 : totalCoeff(leftToken);
        int nB = topMBType == null ? 0 : totalCoeff(topToken);
        if (leftAvailable && topAvailable) {
            return ((nA + nB) + 1) >> 1;
        }
        if (!leftAvailable) {
            if (topAvailable) {
                return nB;
            }
            return 0;
        }
        return nA;
    }

    protected VLC codeTableChromaDC() {
        if (this.color == ColorSpace.YUV420) {
            return H264Const.coeffTokenChromaDCY420;
        }
        if (this.color == ColorSpace.YUV422) {
            return H264Const.coeffTokenChromaDCY422;
        }
        if (this.color == ColorSpace.YUV444) {
            return H264Const.coeffToken[0];
        }
        return null;
    }

    public int readCoeffs(BitReader in, VLC coeffTokenTab, VLC[] totalZerosTab, int[] coeffLevel, int firstCoeff, int nCoeff, int[] zigzag) {
        int zerosLeft;
        int coeffToken = coeffTokenTab.readVLC(in);
        int totalCoeff = totalCoeff(coeffToken);
        int trailingOnes = trailingOnes(coeffToken);
        if (totalCoeff > 0) {
            int suffixLength = (totalCoeff <= 10 || trailingOnes >= 3) ? 0 : 1;
            int[] level = new int[totalCoeff];
            int i = 0;
            while (i < trailingOnes) {
                level[i] = 1 - (in.read1Bit() * 2);
                i++;
            }
            while (i < totalCoeff) {
                int level_prefix = CAVLCReader.readZeroBitCount(in, "");
                int levelSuffixSize = suffixLength;
                if (level_prefix == 14 && suffixLength == 0) {
                    levelSuffixSize = 4;
                }
                if (level_prefix >= 15) {
                    levelSuffixSize = level_prefix - 3;
                }
                int levelCode = Min(15, level_prefix) << suffixLength;
                if (levelSuffixSize > 0) {
                    int level_suffix = CAVLCReader.readU(in, levelSuffixSize, "RB: level_suffix");
                    levelCode += level_suffix;
                }
                if (level_prefix >= 15 && suffixLength == 0) {
                    levelCode += 15;
                }
                if (level_prefix >= 16) {
                    levelCode += (1 << (level_prefix - 3)) - 4096;
                }
                if (i == trailingOnes && trailingOnes < 3) {
                    levelCode += 2;
                }
                if (levelCode % 2 == 0) {
                    level[i] = (levelCode + 2) >> 1;
                } else {
                    level[i] = ((-levelCode) - 1) >> 1;
                }
                if (suffixLength == 0) {
                    suffixLength = 1;
                }
                if (Abs(level[i]) > (3 << (suffixLength - 1)) && suffixLength < 6) {
                    suffixLength++;
                }
                i++;
            }
            if (totalCoeff < nCoeff) {
                if (coeffLevel.length == 4) {
                    zerosLeft = H264Const.totalZeros4[totalCoeff - 1].readVLC(in);
                } else if (coeffLevel.length == 8) {
                    zerosLeft = H264Const.totalZeros8[totalCoeff - 1].readVLC(in);
                } else {
                    zerosLeft = H264Const.totalZeros16[totalCoeff - 1].readVLC(in);
                }
            } else {
                zerosLeft = 0;
            }
            int[] runs = new int[totalCoeff];
            int r = 0;
            while (r < totalCoeff - 1 && zerosLeft > 0) {
                int run = H264Const.run[Math.min(6, zerosLeft - 1)].readVLC(in);
                zerosLeft -= run;
                runs[r] = run;
                r++;
            }
            runs[r] = zerosLeft;
            int j = totalCoeff - 1;
            int cn = 0;
            while (j >= 0 && cn < nCoeff) {
                int cn2 = cn + runs[j];
                coeffLevel[zigzag[cn2 + firstCoeff]] = level[j];
                j--;
                cn = cn2 + 1;
            }
        }
        return coeffToken;
    }

    private static int Min(int i, int level_prefix) {
        return i < level_prefix ? i : level_prefix;
    }

    private static int Abs(int i) {
        return i < 0 ? -i : i;
    }

    public static final int coeffToken(int totalCoeff, int trailingOnes) {
        return (totalCoeff << 4) | trailingOnes;
    }

    public static final int totalCoeff(int coeffToken) {
        return coeffToken >> 4;
    }

    public static final int trailingOnes(int coeffToken) {
        return coeffToken & 15;
    }

    public void readChromaDCBlock(BitReader reader, int[] coeff, boolean leftAvailable, boolean topAvailable) {
        VLC[] vlcArr;
        VLC coeffTokenTab = getCoeffTokenVLCForChromaDC();
        if (coeff.length == 16) {
            vlcArr = H264Const.totalZeros16;
        } else {
            vlcArr = coeff.length == 8 ? H264Const.totalZeros8 : H264Const.totalZeros4;
        }
        readCoeffs(reader, coeffTokenTab, vlcArr, coeff, 0, coeff.length, NO_ZIGZAG);
    }

    public void readLumaDCBlock(BitReader reader, int[] coeff, int mbX, boolean leftAvailable, MBType leftMbType, boolean topAvailable, MBType topMbType, int[] zigzag4x4) {
        VLC coeffTokenTab = getCoeffTokenVLCForLuma(leftAvailable, leftMbType, this.tokensLeft[0], topAvailable, topMbType, this.tokensTop[mbX << 2]);
        readCoeffs(reader, coeffTokenTab, H264Const.totalZeros16, coeff, 0, 16, zigzag4x4);
    }

    public int readACBlock(BitReader reader, int[] coeff, int blkIndX, int blkIndY, boolean leftAvailable, MBType leftMbType, boolean topAvailable, MBType topMbType, int firstCoeff, int nCoeff, int[] zigzag4x4) {
        VLC coeffTokenTab = getCoeffTokenVLCForLuma(leftAvailable, leftMbType, this.tokensLeft[this.mbMask & blkIndY], topAvailable, topMbType, this.tokensTop[blkIndX]);
        int readCoeffs = readCoeffs(reader, coeffTokenTab, H264Const.totalZeros16, coeff, firstCoeff, nCoeff, zigzag4x4);
        int[] iArr = this.tokensLeft;
        int i = this.mbMask & blkIndY;
        this.tokensTop[blkIndX] = readCoeffs;
        iArr[i] = readCoeffs;
        return totalCoeff(readCoeffs);
    }

    public void setZeroCoeff(int blkIndX, int blkIndY) {
        int[] iArr = this.tokensLeft;
        int i = this.mbMask & blkIndY;
        this.tokensTop[blkIndX] = 0;
        iArr[i] = 0;
    }
}
