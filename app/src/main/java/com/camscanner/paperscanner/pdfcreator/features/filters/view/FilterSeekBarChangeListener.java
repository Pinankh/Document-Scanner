package com.camscanner.paperscanner.pdfcreator.features.filters.view;

import com.camscanner.paperscanner.pdfcreator.common.views.verticalseekbar.VerticalSeekBar;
import com.camscanner.paperscanner.pdfcreator.model.types.EditFilter;

public class FilterSeekBarChangeListener implements VerticalSeekBar.OnSeekBarChangeListener {
    private final EditFilter filter;
    private final OnFilterSeekBarListener listener;

    public interface OnFilterSeekBarListener {
        void onGpuFilterProgressChanged(EditFilter editFilter, int i);

        void onTrackEnded(EditFilter editFilter, int i);

        void onTrackStarted(EditFilter editFilter, int i);
    }

    public FilterSeekBarChangeListener(EditFilter filter2, OnFilterSeekBarListener listener2) {
        this.filter = filter2;
        this.listener = listener2;
    }

    public void onTrackStarted(int progress) {
        this.listener.onTrackStarted(this.filter, progress);
    }

    public void onProgressChanged(int progress, boolean fromUser) {
        if (fromUser) {
            this.listener.onGpuFilterProgressChanged(this.filter, progress);
        }
    }

    public void onTrackEnded(int progress) {
        this.listener.onTrackEnded(this.filter, progress);
    }
}
