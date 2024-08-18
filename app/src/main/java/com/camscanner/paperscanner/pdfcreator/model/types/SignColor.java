package com.camscanner.paperscanner.pdfcreator.model.types;

import java.util.HashMap;
import java.util.Map;

public enum SignColor {
    BLACK(0),
    BLUE(1),
    RED(2);
    
    private static Map<Integer, SignColor> map;
    private int value;

    static {
        map = new HashMap();
        for (SignColor signColor : values()) {
            map.put(Integer.valueOf(signColor.value()), signColor);
        }
    }

    private SignColor(int value2) {
        this.value = value2;
    }

    public int value() {
        return this.value;
    }

    public static SignColor valueOf(int value2) {
        return map.get(Integer.valueOf(value2));
    }
}
