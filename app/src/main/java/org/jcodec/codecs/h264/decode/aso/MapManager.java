package org.jcodec.codecs.h264.decode.aso;

import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;

/* loaded from: classes.dex */
public class MapManager {
    private MBToSliceGroupMap mbToSliceGroupMap;
    private PictureParameterSet pps;
    private int prevSliceGroupChangeCycle;
    private SeqParameterSet sps;

    public MapManager(SeqParameterSet sps, PictureParameterSet pps) {
        this.sps = sps;
        this.pps = pps;
        this.mbToSliceGroupMap = buildMap(sps, pps);
    }

    private MBToSliceGroupMap buildMap(SeqParameterSet sps, PictureParameterSet pps) {
        int[] map;
        int numGroups = pps.num_slice_groups_minus1 + 1;
        if (numGroups <= 1) {
            return null;
        }
        int picWidthInMbs = sps.pic_width_in_mbs_minus1 + 1;
        int picHeightInMbs = H264Utils.getPicHeightInMbs(sps);
        if (pps.slice_group_map_type == 0) {
            int[] runLength = new int[numGroups];
            for (int i = 0; i < numGroups; i++) {
                runLength[i] = pps.run_length_minus1[i] + 1;
            }
            map = SliceGroupMapBuilder.buildInterleavedMap(picWidthInMbs, picHeightInMbs, runLength);
        } else if (pps.slice_group_map_type == 1) {
            map = SliceGroupMapBuilder.buildDispersedMap(picWidthInMbs, picHeightInMbs, numGroups);
        } else if (pps.slice_group_map_type == 2) {
            map = SliceGroupMapBuilder.buildForegroundMap(picWidthInMbs, picHeightInMbs, numGroups, pps.top_left, pps.bottom_right);
        } else {
            if (pps.slice_group_map_type >= 3 && pps.slice_group_map_type <= 5) {
                return null;
            }
            if (pps.slice_group_map_type == 6) {
                map = pps.slice_group_id;
            } else {
                throw new RuntimeException("Unsupported slice group map type");
            }
        }
        return buildMapIndices(map, numGroups);
    }

    private MBToSliceGroupMap buildMapIndices(int[] map, int numGroups) {
        int[] ind = new int[numGroups];
        int[] indices = new int[map.length];
        for (int i = 0; i < map.length; i++) {
            int i2 = map[i];
            int i3 = ind[i2];
            ind[i2] = i3 + 1;
            indices[i] = i3;
        }
        int[][] inverse = new int[numGroups];
        for (int i4 = 0; i4 < numGroups; i4++) {
            inverse[i4] = new int[ind[i4]];
        }
        int[] ind2 = new int[numGroups];
        for (int i5 = 0; i5 < map.length; i5++) {
            int sliceGroup = map[i5];
            int[] iArr = inverse[sliceGroup];
            int i6 = ind2[sliceGroup];
            ind2[sliceGroup] = i6 + 1;
            iArr[i6] = i5;
        }
        return new MBToSliceGroupMap(map, indices, inverse);
    }

    private void updateMap(SliceHeader sh) {
        int[] map;
        int mapType = this.pps.slice_group_map_type;
        int numGroups = this.pps.num_slice_groups_minus1 + 1;
        if (numGroups <= 1 || mapType < 3 || mapType > 5) {
            return;
        }
        if (sh.slice_group_change_cycle != this.prevSliceGroupChangeCycle || this.mbToSliceGroupMap == null) {
            this.prevSliceGroupChangeCycle = sh.slice_group_change_cycle;
            int picWidthInMbs = this.sps.pic_width_in_mbs_minus1 + 1;
            int picHeightInMbs = H264Utils.getPicHeightInMbs(this.sps);
            int picSizeInMapUnits = picWidthInMbs * picHeightInMbs;
            int mapUnitsInSliceGroup0 = sh.slice_group_change_cycle * (this.pps.slice_group_change_rate_minus1 + 1);
            if (mapUnitsInSliceGroup0 > picSizeInMapUnits) {
                mapUnitsInSliceGroup0 = picSizeInMapUnits;
            }
            int sizeOfUpperLeftGroup = this.pps.slice_group_change_direction_flag ? picSizeInMapUnits - mapUnitsInSliceGroup0 : mapUnitsInSliceGroup0;
            if (mapType == 3) {
                map = SliceGroupMapBuilder.buildBoxOutMap(picWidthInMbs, picHeightInMbs, this.pps.slice_group_change_direction_flag, mapUnitsInSliceGroup0);
            } else if (mapType == 4) {
                map = SliceGroupMapBuilder.buildRasterScanMap(picWidthInMbs, picHeightInMbs, sizeOfUpperLeftGroup, this.pps.slice_group_change_direction_flag);
            } else {
                map = SliceGroupMapBuilder.buildWipeMap(picWidthInMbs, picHeightInMbs, sizeOfUpperLeftGroup, this.pps.slice_group_change_direction_flag);
            }
            this.mbToSliceGroupMap = buildMapIndices(map, numGroups);
        }
    }

    public Mapper getMapper(SliceHeader sh) {
        updateMap(sh);
        int firstMBInSlice = sh.first_mb_in_slice;
        return this.pps.num_slice_groups_minus1 > 0 ? new PrebuiltMBlockMapper(this.mbToSliceGroupMap, firstMBInSlice, this.sps.pic_width_in_mbs_minus1 + 1) : new FlatMBlockMapper(this.sps.pic_width_in_mbs_minus1 + 1, firstMBInSlice);
    }
}
