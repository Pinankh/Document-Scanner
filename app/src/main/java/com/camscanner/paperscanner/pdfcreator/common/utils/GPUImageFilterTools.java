package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.opengl.Matrix;
import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3TextureSamplingFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBilateralBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLevelsFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOpacityFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTransformFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter;
import tap.lib.rateus.dialog.Circle;

public class GPUImageFilterTools {

    public static class FilterAdjuster {
        private final Adjuster<? extends GPUImageFilter> adjuster;

        public FilterAdjuster(GPUImageFilter filter) {
            if (filter instanceof GPUImageSharpenFilter) {
                this.adjuster = new SharpnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSepiaToneFilter) {
                this.adjuster = new SepiaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageContrastFilter) {
                this.adjuster = new ContrastAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGammaFilter) {
                this.adjuster = new GammaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBrightnessFilter) {
                this.adjuster = new BrightnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSobelEdgeDetectionFilter) {
                this.adjuster = new SobelAdjuster().filter(filter);
            } else if (filter instanceof GPUImageEmbossFilter) {
                this.adjuster = new EmbossAdjuster().filter(filter);
            } else if (filter instanceof GPUImage3x3TextureSamplingFilter) {
                this.adjuster = new GPU3x3TextureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHueFilter) {
                this.adjuster = new HueAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePosterizeFilter) {
                this.adjuster = new PosterizeAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePixelationFilter) {
                this.adjuster = new PixelationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSaturationFilter) {
                this.adjuster = new SaturationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageExposureFilter) {
                this.adjuster = new ExposureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHighlightShadowFilter) {
                this.adjuster = new HighlightShadowAdjuster().filter(filter);
            } else if (filter instanceof GPUImageMonochromeFilter) {
                this.adjuster = new MonochromeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageOpacityFilter) {
                this.adjuster = new OpacityAdjuster().filter(filter);
            } else if (filter instanceof GPUImageRGBFilter) {
                this.adjuster = new RGBAdjuster().filter(filter);
            } else if (filter instanceof GPUImageWhiteBalanceFilter) {
                this.adjuster = new WhiteBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageVignetteFilter) {
                this.adjuster = new VignetteAdjuster().filter(filter);
            } else if (filter instanceof GPUImageDissolveBlendFilter) {
                this.adjuster = new DissolveBlendAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGaussianBlurFilter) {
                this.adjuster = new GaussianBlurAdjuster().filter(filter);
            } else if (filter instanceof GPUImageCrosshatchFilter) {
                this.adjuster = new CrosshatchBlurAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBulgeDistortionFilter) {
                this.adjuster = new BulgeDistortionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGlassSphereFilter) {
                this.adjuster = new GlassSphereAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHazeFilter) {
                this.adjuster = new HazeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSphereRefractionFilter) {
                this.adjuster = new SphereRefractionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSwirlFilter) {
                this.adjuster = new SwirlAdjuster().filter(filter);
            } else if (filter instanceof GPUImageColorBalanceFilter) {
                this.adjuster = new ColorBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageLevelsFilter) {
                this.adjuster = new LevelsMinMidAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBilateralBlurFilter) {
                this.adjuster = new BilateralAdjuster().filter(filter);
            } else if (filter instanceof GPUImageTransformFilter) {
                this.adjuster = new RotateAdjuster().filter(filter);
            } else {
                this.adjuster = null;
            }
        }

        public void adjust(int percentage) {
            Adjuster<? extends GPUImageFilter> adjuster2 = this.adjuster;
            if (adjuster2 != null) {
                adjuster2.adjust(percentage);
            }
        }

        private abstract class Adjuster<T extends GPUImageFilter> {
            private T filter;

            public abstract void adjust(int i);

            private Adjuster() {
            }

            public Adjuster<T> filter(GPUImageFilter filter2) {
                this.filter = (T) filter2;
                return this;
            }

            public T getFilter() {
                return this.filter;
            }

            /* access modifiers changed from: protected */
            public float range(int percentage, float start, float end) {
                return (((end - start) * ((float) percentage)) / 100.0f) + start;
            }

            /* access modifiers changed from: protected */
            public int range(int percentage, int start, int end) {
                return (((end - start) * percentage) / 100) + start;
            }
        }

        private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
            private SharpnessAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageSharpenFilter) getFilter()).setSharpness(range(percentage, -4.0f, 4.0f));
            }
        }

        private class PixelationAdjuster extends Adjuster<GPUImagePixelationFilter> {
            private PixelationAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImagePixelationFilter) getFilter()).setPixel(range(percentage, 1.0f, 100.0f));
            }
        }

        private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
            private HueAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageHueFilter) getFilter()).setHue(range(percentage, 0.0f, 360.0f));
            }
        }

        private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
            private ContrastAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageContrastFilter) getFilter()).setContrast(range(percentage, 0.05f, 1.95f));
            }
        }

        private class GammaAdjuster extends Adjuster<GPUImageGammaFilter> {
            private GammaAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageGammaFilter) getFilter()).setGamma(range(percentage, 0.0f, 2.0f));
            }
        }

        private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
            private BrightnessAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageBrightnessFilter) getFilter()).setBrightness(range(percentage, -0.6f, 0.6f));
            }
        }

        private class SepiaAdjuster extends Adjuster<GPUImageSepiaToneFilter> {
            private SepiaAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageSepiaToneFilter) getFilter()).setIntensity(range(percentage, 0.0f, 2.0f));
            }
        }

        private class SobelAdjuster extends Adjuster<GPUImageSobelEdgeDetectionFilter> {
            private SobelAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageSobelEdgeDetectionFilter) getFilter()).setLineSize(range(percentage, 0.0f, 5.0f));
            }
        }

        private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
            private EmbossAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageEmbossFilter) getFilter()).setIntensity(range(percentage, 0.0f, 4.0f));
            }
        }

        private class PosterizeAdjuster extends Adjuster<GPUImagePosterizeFilter> {
            private PosterizeAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImagePosterizeFilter) getFilter()).setColorLevels(range(percentage, 1, 50));
            }
        }

        private class GPU3x3TextureAdjuster extends Adjuster<GPUImage3x3TextureSamplingFilter> {
            private GPU3x3TextureAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImage3x3TextureSamplingFilter) getFilter()).setLineSize(range(percentage, 0.0f, 5.0f));
            }
        }

        private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
            private SaturationAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageSaturationFilter) getFilter()).setSaturation(range(percentage, 0.0f, 2.0f));
            }
        }

        private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
            private ExposureAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageExposureFilter) getFilter()).setExposure(range(percentage, -10.0f, 10.0f));
            }
        }

        private class HighlightShadowAdjuster extends Adjuster<GPUImageHighlightShadowFilter> {
            private HighlightShadowAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageHighlightShadowFilter) getFilter()).setShadows(range(percentage, 0.0f, 1.0f));
                ((GPUImageHighlightShadowFilter) getFilter()).setHighlights(range(percentage, 0.0f, 1.0f));
            }
        }

        private class MonochromeAdjuster extends Adjuster<GPUImageMonochromeFilter> {
            private MonochromeAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageMonochromeFilter) getFilter()).setIntensity(range(percentage, 0.0f, 1.0f));
            }
        }

        private class OpacityAdjuster extends Adjuster<GPUImageOpacityFilter> {
            private OpacityAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageOpacityFilter) getFilter()).setOpacity(range(percentage, 0.0f, 1.0f));
            }
        }

        private class RGBAdjuster extends Adjuster<GPUImageRGBFilter> {
            private RGBAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageRGBFilter) getFilter()).setRed(range(percentage, 0.0f, 1.0f));
            }
        }

        private class WhiteBalanceAdjuster extends Adjuster<GPUImageWhiteBalanceFilter> {
            private WhiteBalanceAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageWhiteBalanceFilter) getFilter()).setTemperature(range(percentage, 2000.0f, 8000.0f));
            }
        }

        private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
            private VignetteAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageVignetteFilter) getFilter()).setVignetteStart(range(percentage, 0.0f, 1.0f));
            }
        }

        private class DissolveBlendAdjuster extends Adjuster<GPUImageDissolveBlendFilter> {
            private DissolveBlendAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageDissolveBlendFilter) getFilter()).setMix(range(percentage, 0.0f, 1.0f));
            }
        }

        private class GaussianBlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
            private GaussianBlurAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageGaussianBlurFilter) getFilter()).setBlurSize(range(percentage, 0.0f, 1.0f));
            }
        }

        private class CrosshatchBlurAdjuster extends Adjuster<GPUImageCrosshatchFilter> {
            private CrosshatchBlurAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageCrosshatchFilter) getFilter()).setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
                ((GPUImageCrosshatchFilter) getFilter()).setLineWidth(range(percentage, 0.0f, 0.006f));
            }
        }

        private class BulgeDistortionAdjuster extends Adjuster<GPUImageBulgeDistortionFilter> {
            private BulgeDistortionAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageBulgeDistortionFilter) getFilter()).setRadius(range(percentage, 0.0f, 1.0f));
                ((GPUImageBulgeDistortionFilter) getFilter()).setScale(range(percentage, -1.0f, 1.0f));
            }
        }

        private class GlassSphereAdjuster extends Adjuster<GPUImageGlassSphereFilter> {
            private GlassSphereAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageGlassSphereFilter) getFilter()).setRadius(range(percentage, 0.0f, 1.0f));
            }
        }

        private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
            private HazeAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageHazeFilter) getFilter()).setDistance(range(percentage, -0.3f, 0.3f));
                ((GPUImageHazeFilter) getFilter()).setSlope(range(percentage, -0.3f, 0.3f));
            }
        }

        private class SphereRefractionAdjuster extends Adjuster<GPUImageSphereRefractionFilter> {
            private SphereRefractionAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageSphereRefractionFilter) getFilter()).setRadius(range(percentage, 0.0f, 1.0f));
            }
        }

        private class SwirlAdjuster extends Adjuster<GPUImageSwirlFilter> {
            private SwirlAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageSwirlFilter) getFilter()).setAngle(range(percentage, 0.0f, 2.0f));
            }
        }

        private class ColorBalanceAdjuster extends Adjuster<GPUImageColorBalanceFilter> {
            private ColorBalanceAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageColorBalanceFilter) getFilter()).setMidtones(new float[]{range(percentage, 0.0f, 1.0f), range(percentage / 2, 0.0f, 1.0f), range(percentage / 3, 0.0f, 1.0f)});
            }
        }

        private class LevelsMinMidAdjuster extends Adjuster<GPUImageLevelsFilter> {
            private LevelsMinMidAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageLevelsFilter) getFilter()).setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f);
            }
        }

        private class BilateralAdjuster extends Adjuster<GPUImageBilateralBlurFilter> {
            private BilateralAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                ((GPUImageBilateralBlurFilter) getFilter()).setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f));
            }
        }

        private class RotateAdjuster extends Adjuster<GPUImageTransformFilter> {
            private RotateAdjuster() {
                super();
            }

            public void adjust(int percentage) {
                float[] transform = new float[16];
                Matrix.setRotateM(transform, 0, (float) ((percentage * Circle.FULL) / 100), 0.0f, 0.0f, 1.0f);
                ((GPUImageTransformFilter) getFilter()).setTransform3D(transform);
            }
        }
    }
}
