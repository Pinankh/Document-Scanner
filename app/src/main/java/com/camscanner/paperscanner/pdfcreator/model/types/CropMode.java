package com.camscanner.paperscanner.pdfcreator.model.types;

import java.util.HashMap;
import java.util.Map;

public enum CropMode {
    NO_CROP(0),
    SMART_CROP(1),
    ALWAYS_CROP(2);
    
    private static Map<Integer, CropMode> map;
    private int mode;

    static {
        map = new HashMap();
        for (CropMode cropMode : values()) {
            map.put(Integer.valueOf(cropMode.value()), cropMode);
        }
    }

    private CropMode(int mode2) {
        this.mode = mode2;
    }

    public int value() {
        return this.mode;
    }

    public static CropMode valueOf(int pageType) {
        return map.get(Integer.valueOf(pageType));
    }
}
