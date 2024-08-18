package co.lujun.androidtagview;

import android.graphics.Color;

/* renamed from: co.lujun.androidtagview.ColorFactory */
public class ColorFactory {
    public static final String AMBER = "FFC107";
    public static final String BD_COLOR_ALPHA = "88";
    public static final String BG_COLOR_ALPHA = "33";
    public static final String BLUE = "2196F3";
    private static final String[] COLORS = {"F44336", "03A9F4", "FFC107", "FF9800", "FFEB3B", "CDDC39", "2196F3", "3F51B5", "8BC34A", "9E9E9E", "673AB7", "009688", "00BCD4"};
    public static final String CYAN = "00BCD4";
    public static final String DEEPPURPLE = "673AB7";
    public static final String GREY = "9E9E9E";
    public static final String INDIGO = "3F51B5";
    public static final String LIGHTBLUE = "03A9F4";
    public static final String LIGHTGREEN = "8BC34A";
    public static final String LIME = "CDDC39";
    public static final int NONE = -1;
    public static final String ORANGE = "FF9800";
    public static final int PURE_CYAN = 1;
    public static final int PURE_TEAL = 2;
    public static final int RANDOM = 0;
    public static final String RED = "F44336";
    public static final int SHARP666666 = Color.parseColor("#FF666666");
    public static final int SHARP727272 = Color.parseColor("#FF727272");
    public static final String TEAL = "009688";
    public static final String YELLOW = "FFEB3B";

    /* renamed from: co.lujun.androidtagview.ColorFactory$PURE_COLOR */
    public enum PURE_COLOR {
        CYAN,
        TEAL
    }

    public static int[] onRandomBuild() {
        double random = Math.random();
        double length = (double) COLORS.length;
        Double.isNaN(length);
        int random2 = (int) (random * length);
        int bgColor = Color.parseColor("#33" + COLORS[random2]);
        return new int[]{bgColor, Color.parseColor("#88" + COLORS[random2]), SHARP666666};
    }

    public static int[] onPureBuild(PURE_COLOR type) {
        String color = type == PURE_COLOR.CYAN ? "00BCD4" : "009688";
        int bgColor = Color.parseColor("#33" + color);
        return new int[]{bgColor, Color.parseColor("#88" + color), SHARP727272};
    }
}
