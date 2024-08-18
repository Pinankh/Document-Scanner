package org.opencv.android;

public interface InstallCallbackInterface {


    void cancel();

    String getPackageName();

    void install();

    void wait_install();
}
