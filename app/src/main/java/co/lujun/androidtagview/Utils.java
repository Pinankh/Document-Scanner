package co.lujun.androidtagview;

import android.content.Context;

public class Utils {
    public static float dp2px(Context context, float dp) {
        return (dp * context.getResources().getDisplayMetrics().density) + 0.5f;
    }

    public static float sp2px(Context context, float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }
}
