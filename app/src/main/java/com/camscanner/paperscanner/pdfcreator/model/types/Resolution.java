package com.camscanner.paperscanner.pdfcreator.model.types;

public enum Resolution {
    LOW(0),
    MEDIUM(1),
    HIGH(2),
    FULL(3);
    
    public static final int DEFAULT_MAX_INPUT = 2500;
    public static final int DEFAULT_MAX_IN_APP_OPTIMIZE = 1024;
    public static final int DEFAULT_MAX_OCR = 2000;
    public static final int DEFAULT_MAX_OUTPUT = 2000;
    private final int pos;

    private Resolution(int pos2) {
        this.pos = pos2;
    }

    public int pos() {
        return this.pos;
    }

    static /* synthetic */ class C68131 {
        static /* synthetic */ int[] MyResolution = null;

        static {
            MyResolution = new int[Resolution.values().length];
            try {
                MyResolution[Resolution.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MyResolution[Resolution.MEDIUM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MyResolution[Resolution.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                MyResolution[Resolution.FULL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public float size() {
        int i = C68131.MyResolution[ordinal()];
        if (i == 1) {
            return 0.3f;
        }
        if (i == 2) {
            return 0.5f;
        }
        if (i != 4) {
            return 0.7f;
        }
        return 1.0f;
    }

    public static Resolution get(int pos2) {
        if (pos2 >= 0 && pos2 <= 3) {
            return values()[pos2];
        }
        throw new RuntimeException("Invalid value");
    }
}
