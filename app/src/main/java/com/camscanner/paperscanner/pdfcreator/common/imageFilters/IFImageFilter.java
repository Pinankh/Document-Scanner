package com.camscanner.paperscanner.pdfcreator.common.imageFilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils;

public class IFImageFilter extends GPUImageFilter {
    private int filterInputTextureUniform2;
    private int filterInputTextureUniform3;
    private int filterInputTextureUniform4;
    private int filterInputTextureUniform5;
    private int filterInputTextureUniform6;
    private int filterIntensityLocation;
    public int filterSourceTexture2 = -1;
    public int filterSourceTexture3 = -1;
    public int filterSourceTexture4 = -1;
    public int filterSourceTexture5 = -1;
    public int filterSourceTexture6 = -1;
    /* access modifiers changed from: private */
    public Context mContext;
    private float mIntensity;
    /* access modifiers changed from: private */
    public List<Integer> mResIds;
    Handler mHandler;
    public IFImageFilter(Context context, String str) {
        super(GPUImageFilter.NO_FILTER_VERTEX_SHADER, str);
        this.mContext = context;
    }

    @Override // jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
    public void onInit() {
        super.onInit();
        this.filterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        this.filterInputTextureUniform3 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture3");
        this.filterInputTextureUniform4 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture4");
        this.filterInputTextureUniform5 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture5");
        this.filterInputTextureUniform6 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture6");
        this.filterIntensityLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
        initInputTexture();
    }

    public void setIntensity(float f) {
        this.mIntensity = f;
        setFloat(this.filterIntensityLocation, this.mIntensity);
    }

    @Override // jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
    public void onDestroy() {
        super.onDestroy();
        int i = this.filterSourceTexture2;
        if (i != -1) {
            GLES20.glDeleteTextures(1, new int[]{i}, 0);
            this.filterSourceTexture2 = -1;
        }
        int i2 = this.filterSourceTexture3;
        if (i2 != -1) {
            GLES20.glDeleteTextures(1, new int[]{i2}, 0);
            this.filterSourceTexture3 = -1;
        }
        int i3 = this.filterSourceTexture4;
        if (i3 != -1) {
            GLES20.glDeleteTextures(1, new int[]{i3}, 0);
            this.filterSourceTexture4 = -1;
        }
        int i4 = this.filterSourceTexture5;
        if (i4 != -1) {
            GLES20.glDeleteTextures(1, new int[]{i4}, 0);
            this.filterSourceTexture5 = -1;
        }
        int i5 = this.filterSourceTexture6;
        if (i5 != -1) {
            GLES20.glDeleteTextures(1, new int[]{i5}, 0);
            this.filterSourceTexture6 = -1;
        }
    }

    @Override // jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        if (this.filterSourceTexture2 != -1) {
            GLES20.glActiveTexture(33987);
            GLES20.glBindTexture(3553, this.filterSourceTexture2);
            GLES20.glUniform1i(this.filterInputTextureUniform2, 3);
        }
        if (this.filterSourceTexture3 != -1) {
            GLES20.glActiveTexture(33988);
            GLES20.glBindTexture(3553, this.filterSourceTexture3);
            GLES20.glUniform1i(this.filterInputTextureUniform3, 4);
        }
        if (this.filterSourceTexture4 != -1) {
            GLES20.glActiveTexture(33989);
            GLES20.glBindTexture(3553, this.filterSourceTexture4);
            GLES20.glUniform1i(this.filterInputTextureUniform4, 5);
        }
        if (this.filterSourceTexture5 != -1) {
            GLES20.glActiveTexture(33990);
            GLES20.glBindTexture(3553, this.filterSourceTexture5);
            GLES20.glUniform1i(this.filterInputTextureUniform5, 6);
        }
        if (this.filterSourceTexture6 != -1) {
            GLES20.glActiveTexture(33991);
            GLES20.glBindTexture(3553, this.filterSourceTexture6);
            GLES20.glUniform1i(this.filterInputTextureUniform6, 7);
        }
    }

    public void addInputTexture(int i) {
        if (this.mResIds == null) {
            this.mResIds = new ArrayList();
        }
        this.mResIds.add(Integer.valueOf(i));
    }

    public void initInputTexture() {
        List<Integer> list = this.mResIds;
        if (list != null) {
            if (list.size() > 0) {
//                new Thread(new Runnable() {
//                    public void run() {
//                        // a potentially time consuming task
//                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(0)).intValue());
//                        System.out.println("janvi======="+decodeResource);
//
//                        IFImageFilter.this.filterSourceTexture2 = OpenGlUtils.loadTexture(decodeResource, -1, true);
//                    }
//                }).start();

//                mHandler=new Handler();
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(0)).intValue());
//                        System.out.println("janvi======="+decodeResource);
//
//                        IFImageFilter.this.filterSourceTexture2 = OpenGlUtils.loadTexture(decodeResource, -1, true);
//                    }
//                });

                Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        public void run() {
//                            Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(0)).intValue());
//                            System.out.println("janvi======="+decodeResource);
//
//                            IFImageFilter.this.filterSourceTexture2 = OpenGlUtils.loadTexture(decodeResource, -1, true);
                        }
                    }, 2000);

//                runOnDraw(new Runnable() {
//
//                    public void run() {
//                        System.out.println("janvi======="+mResIds.get(0).intValue());
//
//
//                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(0)).intValue());
//                        System.out.println("janvi======="+decodeResource);
//
//                        IFImageFilter.this.filterSourceTexture2 = OpenGlUtils.loadTexture(decodeResource, -1, true);
//                    }
//                });
            }
            if (this.mResIds.size() > 1) {
                runOnDraw(new Runnable() {

                    public void run() {
                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(1)).intValue());
                        IFImageFilter.this.filterSourceTexture3 = OpenGlUtils.loadTexture(decodeResource, -1, true);
                    }
                });
            }
            if (this.mResIds.size() > 2) {
                runOnDraw(new Runnable() {

                    public void run() {
                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(2)).intValue());
                        IFImageFilter.this.filterSourceTexture4 = OpenGlUtils.loadTexture(decodeResource, -1, true);
                    }
                });
            }
            if (this.mResIds.size() > 3) {
                runOnDraw(new Runnable() {

                    public void run() {
                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(3)).intValue());
                        IFImageFilter.this.filterSourceTexture5 = OpenGlUtils.loadTexture(decodeResource, -1, true);
                    }
                });
            }
            if (this.mResIds.size() > 4) {
                runOnDraw(new Runnable() {

                    public void run() {
                        Bitmap decodeResource = BitmapFactory.decodeResource(IFImageFilter.this.mContext.getResources(), ((Integer) IFImageFilter.this.mResIds.get(4)).intValue());
                        IFImageFilter.this.filterSourceTexture6 = OpenGlUtils.loadTexture(decodeResource, -1, true);
                    }
                });
            }
        }
    }
}
