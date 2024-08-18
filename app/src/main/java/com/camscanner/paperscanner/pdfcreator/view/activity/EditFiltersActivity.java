package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camscanner.paperscanner.pdfcreator.ads.InterstialListner;
import com.camscanner.paperscanner.pdfcreator.ads.InterstitialHelper;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.ortiz.touchview.TouchImageView;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.ActivityUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.GPUImageFilterTools;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PolygonUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.views.verticalseekbar.VerticalSeekBar;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.CacheFilter;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.FilterData;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.GroupTuner;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.SavedImage;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.TuneData;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.Tuner;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.UpdateFilter;
import com.camscanner.paperscanner.pdfcreator.features.filters.view.FilterSeekBarChangeListener;
import com.camscanner.paperscanner.pdfcreator.features.ocr.OcrHelper;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.TutorialManager;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialBitmapInfo;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.types.EditFilter;
import com.camscanner.paperscanner.pdfcreator.model.types.FilterFlag;
import com.camscanner.paperscanner.pdfcreator.view.activity.main.MainActivity;
import com.camscanner.paperscanner.pdfcreator.view.adapter.HorizontalAdapter;
import timber.log.Timber;

public class EditFiltersActivity extends BaseMainActivity implements HorizontalAdapter.OnFilterClickListener, FilterSeekBarChangeListener.OnFilterSeekBarListener, TutorialManager.OnTutorialListener {
    private static final String ALERT_REASON_DONE = "reas_done";
    private static final String ALERT_REASON_INIT_OOM = "reas_init_oom";
    private static final String ALERT_REASON_INIT_VARIABLE = "reas_init_start";
    private static final String ALERT_REASON_SAVING_FAIL = "reas_saving_failure";
    private static final int DEFAULT_BRIGHTNESS = 50;
    private static final int DEFAULT_BRIGHTNESS_AUTO = 52;
    private static final int DEFAULT_CONTRAST = 50;
    private static final int DEFAULT_CONTRAST_AUTO = 60;
    /* access modifiers changed from: private */
    public static int FILTER_SPACING = 15;
    public static final String LOG_TAG = EditFiltersActivity.class.getSimpleName();
    public static int currentAngle;
    private List<GPUImageFilterTools.FilterAdjuster> adjusterList;
    private boolean autoFilter = true;
    String brightnessInfo;
    private FilterFlag chosenFilter;
    String contrastInfo;
    private final CompositeDisposable createDisposable = new CompositeDisposable();
    private Document document;
    private boolean editFilterVisible;
    private boolean equalizerTouched = false;
    /* access modifiers changed from: private */
    public HorizontalAdapter filtersAdapter;
    private List<GPUImageFilter> filtersList;
    private int filtersOpened = 0;
    ViewGroup filtersView;
    private boolean finishing = false;
    private GPUImage gpuImage;
    TouchImageView imagePreview;
    private Bitmap imgAuto;
    private Bitmap imgBW;
    private Bitmap imgBW2;
    private Bitmap imgGray;
    private Bitmap imgLighten;
    private Bitmap imgMagic;
    private Bitmap imgOriginal;
    private int initialColor;
    private EditFilter lastShownFilter = null;
    private int lastShownProgress = 0;
    String loading;
    ImageView m_ivMenuLeft;
    ImageView m_ivMenuRight;
    private String m_strName;
    private String m_strParent;
    int marginFilters;
    private Mat matOptimized;
    private Mat matOriginal;
    private Mat matThumb;
    private Bitmap optimizedSource;
    private Bitmap originalSource;
    RecyclerView rcFilters;
    private BehaviorRelay<CacheFilter> relayCacheFilter;
    private BehaviorRelay<FilterFlag> relayFilter;
    private BehaviorRelay<Bitmap> relayPreviewTuned;
    private BehaviorRelay<Integer> relayRotate;
    private BehaviorRelay<Tuner> relayTune;
    private Disposable saving;
    private boolean savingProcess = false;
    VerticalSeekBar sbBrightness;
    VerticalSeekBar sbContrast;
    private String strGroupName;
    private Bitmap thumbSource;
    ViewGroup tuneAlert;
    TextView tuneType;
    TextView tuneValue;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
        } else if (!initVariable()) {
            finish();
        } else {
            setContentView((int) R.layout.activity_filters);

            LinearLayout relative_ad = (LinearLayout) findViewById(R.id.relative_ad);
            ScanApplication app = (ScanApplication) getApplication();
            app.adaptiveBannerView(relative_ad);

            filtersView = (ViewGroup)findViewById(R.id.filters);
            imagePreview = (TouchImageView) findViewById(R.id.image_preview);
            m_ivMenuLeft = (ImageView) findViewById(R.id.iv_menu_left);
            m_ivMenuRight = (ImageView) findViewById(R.id.iv_menu_right);
            ImageView  btn_back = (ImageView) findViewById(R.id.btn_back);
            LinearLayout  btn_done = (LinearLayout) findViewById(R.id.btn_done);
            LinearLayout  btn_ocr = (LinearLayout) findViewById(R.id.btn_ocr);
            LinearLayout  btn_rotate_left = (LinearLayout) findViewById(R.id.btn_rotate_left);
            LinearLayout  btn_settings = (LinearLayout) findViewById(R.id.btn_settings);


            tuneType = (TextView) findViewById(R.id.tune_title);
            tuneValue = (TextView) findViewById(R.id.tune_value);
            rcFilters = (RecyclerView) findViewById(R.id.horizontal_filter_recycler_view);
            sbBrightness = (VerticalSeekBar) findViewById(R.id.tune_brightness);
            sbContrast = (VerticalSeekBar) findViewById(R.id.tune_contrast);
            tuneAlert = (ViewGroup) findViewById(R.id.tune_alert);

            brightnessInfo = getResources().getString(R.string.gpu_brightness);
            contrastInfo = getResources().getString(R.string.gpu_contrast);
            loading = getResources().getString(R.string.str_waiting);
            marginFilters = (int) getResources().getDimension(R.dimen.filter_item_distance);

            m_ivMenuLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveLeft();
                }
            });

            m_ivMenuRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveRight();
                }
            });

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btn_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDone();
                }
            });

            btn_ocr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recognizeDocument();

                }
            });

            btn_rotate_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotateImage();
                }
            });

            btn_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeEditFilterVisible();
                }
            });



            initChangesListeners();
            initUI();
            applyInitialState();
            showOverlay();
        }
    }


    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logFilterScreen();
    }

    private void showOverlay() {
        if (this.filtersOpened == 1) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return EditFiltersActivity.this.isFiltersReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    EditFiltersActivity.this.showFiltersTutorial();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean isFiltersReady() {
        HorizontalAdapter horizontalAdapter;
        RecyclerView recyclerView;
        RecyclerView.LayoutManager manager;
        View child;
        ImageView imageView;
        if (isFinishing() || this.filtersView == null || (horizontalAdapter = this.filtersAdapter) == null || horizontalAdapter.getItemCount() == 0 || (recyclerView = this.rcFilters) == null || (manager = recyclerView.getLayoutManager()) == null || (child = manager.findViewByPosition(0)) == null || (imageView = (ImageView) child.findViewById(R.id.iv_filter)) == null || imageView.getDrawable() == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void showFiltersTutorial() {
        TutorialManager.showTutorial(getSupportFragmentManager(), getFiltersTutorialInfo());
    }

    private TutorialInfo getFiltersTutorialInfo() {
        Bitmap screenshot = Bitmap.createBitmap(this.filtersView.getWidth(), this.filtersView.getHeight(), Bitmap.Config.ARGB_8888);
        this.filtersView.draw(new Canvas(screenshot));
        return new TutorialBitmapInfo(R.layout.tutorial_editfilters_filters, R.id.filters, R.id.filters_outside, ImageStorageUtils.saveImageTutorial(screenshot), this.filtersView.getX(), this.filtersView.getY(), this.filtersView.getWidth(), this.filtersView.getHeight());
    }

    public void onTutorialViewClicked(View v) {
    }

    public void onTutorialClosed(TutorialInfo tutorialInfo, boolean targetHit) {
        if (tutorialInfo.layoutId == R.layout.tutorial_editfilters_filters) {
            SharedPrefsUtils.setFiltersOpened(this, 1);
//            Analytics.get().logTutorFilters();
        }
    }

    private void applyInitialState() {
        updateFilter(this.chosenFilter);
    }

    /* access modifiers changed from: package-private */
    public boolean initVariable() {
        if (!ScanApplication.imageHolder().hasCroppedAndOptimized()) {
            showAlert(ALERT_REASON_INIT_VARIABLE);
            Timber.tag(LOG_TAG).e(new Throwable("Cropped or optimized are null"));
            return false;
        }
        this.document = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        this.originalSource = ScanApplication.imageHolder().getCroppedPicture();
        this.optimizedSource = ScanApplication.imageHolder().getOptimizedPicture();
        this.autoFilter = getIntent().getBooleanExtra(Constant.NEED_AUTO_FILTER, true);
        this.filtersOpened = SharedPrefsUtils.getFiltersOpened(this);
        this.initialColor = this.autoFilter ? SharedPrefsUtils.getSingleColorMode(this) : 1;
        currentAngle = 0;
        this.strGroupName = this.document.name;
        this.editFilterVisible = true;
        this.chosenFilter = getFilterByPosition(this.initialColor);
        this.adjusterList = new ArrayList();
        this.filtersList = new ArrayList();
        this.filtersList.add(new GPUImageContrastFilter(1.0f));
        this.filtersList.add(new GPUImageBrightnessFilter(0.0f));
        this.adjusterList.add(new GPUImageFilterTools.FilterAdjuster(this.filtersList.get(0)));
        this.adjusterList.add(new GPUImageFilterTools.FilterAdjuster(this.filtersList.get(1)));
        this.relayFilter = BehaviorRelay.create();
        this.relayRotate = BehaviorRelay.create();
        this.relayTune = BehaviorRelay.create();
        this.relayCacheFilter = BehaviorRelay.create();
        this.relayPreviewTuned = BehaviorRelay.create();
        try {
            this.thumbSource = BitmapUtils.createScaledBitmap(this.optimizedSource, 300);
            this.imgAuto = applyFilter(this.thumbSource, getThumbMat(), FilterFlag.Auto, false);
            this.imgOriginal = this.thumbSource.copy(this.thumbSource.getConfig(), false);
            this.imgLighten = applyFilter(this.thumbSource, getThumbMat(), FilterFlag.Lighten, false);
            this.imgMagic = applyFilter(this.thumbSource, getThumbMat(), FilterFlag.Magic, false);
            this.imgGray = applyFilter(this.thumbSource, getThumbMat(), FilterFlag.Gray, false);
            this.imgBW = applyFilter(this.thumbSource, getThumbMat(), FilterFlag.BW1, false);
            this.imgBW2 = applyFilter(this.thumbSource, getThumbMat(), FilterFlag.BW2, false);
            return true;
        } catch (OutOfMemoryError ex) {
            System.gc();
            Timber.tag(LOG_TAG).e(ex);
            showAlert(ALERT_REASON_INIT_OOM);
            return false;
        }
    }

    private Mat getThumbMat() {
        if (this.matThumb == null) {
            synchronized (this) {
                if (this.matThumb == null) {
                    this.matThumb = createMatrixForBitmap(this.thumbSource);
                }
            }
        }
        return this.matThumb;
    }

    private Mat getOptimizedMat() {
        if (this.matOptimized == null) {
            synchronized (this) {
                if (this.matOptimized == null) {
                    this.matOptimized = createMatrixForBitmap(this.optimizedSource);
                }
            }
        }
        return this.matOptimized;
    }


    private synchronized Mat getOriginalMat() {
        if (this.matOriginal == null) {
            synchronized (this) {
                if (this.matOriginal == null) {
                    this.matOriginal = createMatrixForBitmap(this.originalSource);
                }
            }
        }
        return this.matOriginal;

    }

    private Mat createMatrixForBitmap(Bitmap bitmap) {
        Mat matrix = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        Mat matTemp = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, matTemp, false);
        Imgproc.cvtColor(matTemp, matrix, 3);
        matTemp.release();
        return matrix;
    }

    private void initChangesListeners() {
        Observable<R> observeOn = this.relayPreviewTuned.subscribeOn(Schedulers.io()).filter(new Predicate<Bitmap>() {
            @Override
            public boolean test(Bitmap bitmap) throws Exception {
                try {
                    return EditFiltersActivity.lambda$initChangesListeners$0(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        }).map(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return ((Bitmap) o).copy(((Bitmap) o).getConfig(), false);
            }
        }).observeOn(AndroidSchedulers.mainThread());
        final TouchImageView touchImageView = this.imagePreview;
        touchImageView.getClass();
        Disposable updatePreviewFlow = observeOn.subscribe(new Consumer() {
            public final void accept(Object obj) {
                touchImageView.setImageBitmap((Bitmap) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$2$EditFiltersActivity((Throwable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Disposable updatePreviewFromCacheFlow = this.relayCacheFilter.subscribeOn(Schedulers.io()).filter(new Predicate<CacheFilter>() {
            @Override
            public boolean test(CacheFilter cacheFilter) throws Exception {
                return (cacheFilter).newest;
            }
        }).map(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return ((CacheFilter) o).bitmap;
            }
        }).map(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return ((Bitmap) o).copy(((Bitmap) o).getConfig(), false);
            }
        }).subscribe(this.relayPreviewTuned, new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$6$EditFiltersActivity((Throwable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Disposable filtersFlow = this.relayFilter.doOnNext(new Consumer<FilterFlag>() {
            @Override
            public void accept(FilterFlag filterFlag) throws Exception {
                Timber.tag(EditFiltersActivity.LOG_TAG).d("New filter %s", filterFlag);
            }
        }).subscribeOn(Schedulers.io()).distinctUntilChanged().observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$8$EditFiltersActivity((FilterFlag) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).observeOn(Schedulers.computation()).flatMap(new Function() {
            public final Object apply(Object obj) {
                try {
                    return lambda$initChangesListeners$12$EditFiltersActivity((FilterFlag) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnEach(new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$13$EditFiltersActivity((Notification) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribe(new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$14$EditFiltersActivity((UpdateFilter) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$15$EditFiltersActivity((Throwable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Disposable rotatesMainFlow = this.relayRotate.doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Timber.tag(EditFiltersActivity.LOG_TAG).d("New rotate main %s", integer);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$17$EditFiltersActivity((Integer) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).observeOn(Schedulers.computation()).withLatestFrom(this.relayPreviewTuned, new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) throws Exception {
                return BitmapUtils.rotate((Bitmap) o2, (float) ((Integer) o).intValue(), true);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnEach(new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$19$EditFiltersActivity((Notification) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribe(this.relayPreviewTuned, new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$20$EditFiltersActivity((Throwable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Disposable rotateCacheFlow = this.relayRotate.subscribeOn(Schedulers.io()).withLatestFrom(this.relayCacheFilter, new BiFunction<Integer, CacheFilter, Object>() {
            @Override
            public Object apply(Integer integer, CacheFilter cacheFilter) throws Exception {
                return BitmapUtils.rotate((cacheFilter).bitmap, (float) (integer).intValue(), true);
            }
        }).subscribe(new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$22$EditFiltersActivity((Bitmap) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$23$EditFiltersActivity((Throwable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Disposable tuneFlow = this.relayTune.subscribeOn(Schedulers.io()).throttleLast(64, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).map(new Function() {
            public final Object apply(Object obj) {
                return EditFiltersActivity.this.applyTuneToGpu((Tuner) obj);
            }
        }).observeOn(Schedulers.computation()).withLatestFrom(this.relayCacheFilter, new BiFunction() {
            public final Object apply(Object obj, Object obj2) {
                try {
                    return lambda$initChangesListeners$24$EditFiltersActivity((GPUImage) obj, (CacheFilter) obj2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).subscribe(this.relayPreviewTuned, new Consumer() {
            public final void accept(Object obj) {
                try {
                    lambda$initChangesListeners$25$EditFiltersActivity((Throwable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.createDisposable.add(updatePreviewFlow);
        this.createDisposable.add(updatePreviewFromCacheFlow);
        this.createDisposable.add(filtersFlow);
        this.createDisposable.add(rotatesMainFlow);
        this.createDisposable.add(rotateCacheFlow);
        this.createDisposable.add(tuneFlow);
    }

    static /* synthetic */ boolean lambda$initChangesListeners$0(Bitmap bmp) throws Exception {
        return !bmp.isRecycled();
    }

    public /* synthetic */ void lambda$initChangesListeners$2$EditFiltersActivity(Throwable th) throws Exception {
        errorInFlow(th, "updatePreviewFlow");
    }

    public /* synthetic */ void lambda$initChangesListeners$6$EditFiltersActivity(Throwable th) throws Exception {
        errorInFlow(th, "updatePreviewFromCacheFlow");
    }

    public /* synthetic */ void lambda$initChangesListeners$8$EditFiltersActivity(FilterFlag t) throws Exception {
        showLoading();
    }

    public /* synthetic */ ObservableSource lambda$initChangesListeners$12$EditFiltersActivity(FilterFlag filter) throws Exception {
        Observable<FilterFlag> obsFilter = Observable.just(filter);
        return Observable.zip(obsFilter, obsFilter.subscribeOn(Schedulers.computation()).map(new Function() {
            public final Object apply(Object obj) {
                try {
                    return lambda$null$9$EditFiltersActivity((FilterFlag) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).map(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return BitmapUtils.rotate((Bitmap) o, (float) EditFiltersActivity.currentAngle, true);
            }
        }), new BiFunction<FilterFlag, Object, Object>() {
            @Override
            public Object apply(FilterFlag filterFlag, Object o) throws Exception {
                try {
                    return EditFiltersActivity.lambda$null$11(filterFlag, (Bitmap) o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public /* synthetic */ Bitmap lambda$null$9$EditFiltersActivity(FilterFlag fil) throws Exception {
        return applyFilter(getOptimizedCopy(), getOptimizedMat(), fil, true);
    }

    static /* synthetic */ UpdateFilter lambda$null$11(FilterFlag fil, Bitmap bmp) throws Exception {
        return new UpdateFilter(bmp, fil);
    }

    public /* synthetic */ void lambda$initChangesListeners$13$EditFiltersActivity(Notification t) throws Exception {
        hideLoading();
    }

    public /* synthetic */ void lambda$initChangesListeners$14$EditFiltersActivity(UpdateFilter update) throws Exception {
        boolean needTune = isFilterHasCustomDefaultTune(update.filter);
        this.relayCacheFilter.accept(new CacheFilter(update.bitmap, !needTune));
        resetTuneFilterMenu(needTune, update.filter);
    }

    public /* synthetic */ void lambda$initChangesListeners$15$EditFiltersActivity(Throwable th) throws Exception {
        errorInFlow(th, "filtersFlow");
    }

    public /* synthetic */ void lambda$initChangesListeners$17$EditFiltersActivity(Integer t) throws Exception {
        showLoading();
    }

    public /* synthetic */ void lambda$initChangesListeners$19$EditFiltersActivity(Notification t) throws Exception {
        hideLoading();
    }

    public /* synthetic */ void lambda$initChangesListeners$20$EditFiltersActivity(Throwable th) throws Exception {
        errorInFlow(th, "rotatesMainFlow");
    }

    public /* synthetic */ void lambda$initChangesListeners$22$EditFiltersActivity(Bitmap bmp) throws Exception {
        this.relayCacheFilter.accept(new CacheFilter(bmp, false));
    }

    public /* synthetic */ void lambda$initChangesListeners$23$EditFiltersActivity(Throwable th) throws Exception {
        errorInFlow(th, "rotateCacheFlow");
    }

    public /* synthetic */ Bitmap lambda$initChangesListeners$24$EditFiltersActivity(GPUImage gpuImage2, CacheFilter cache) throws Exception {
        return applyGPUFilter(gpuImage2, cache.bitmap, false);
    }

    public /* synthetic */ void lambda$initChangesListeners$25$EditFiltersActivity(Throwable th) throws Exception {
        errorInFlow(th, "tuneFlow");
    }

    private boolean isFilterHasCustomDefaultTune(FilterFlag filter) {
        return filter.equals(FilterFlag.Auto);
    }

    private void errorInFlow(Throwable th, String flowName) {
        Timber.tag(LOG_TAG).e(th, flowName, new Object[0]);
    }

    /* access modifiers changed from: private */
    public GPUImage applyTuneToGpu(Tuner tuner) {
        tuner.tune(this.adjusterList);
        return this.gpuImage;
    }

    private Bitmap getOptimizedCopy() {
        Bitmap bitmap = this.optimizedSource;
        return bitmap.copy(bitmap.getConfig(), false);
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle((CharSequence) this.strGroupName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        updateEditFilterMenuVisibility();
        this.gpuImage = new GPUImage(this);
        this.gpuImage.setFilter(new GPUImageFilterGroup(this.filtersList));
        this.rcFilters.post(new Runnable() {
            public final void run() {
                EditFiltersActivity.this.lambda$initUI$26$EditFiltersActivity();
            }
        });
        this.rcFilters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (EditFiltersActivity.this.filtersAdapter != null) {
                    int nItemWidth = EditFiltersActivity.this.filtersAdapter.getItemWidth() + EditFiltersActivity.FILTER_SPACING;
                    int nParentWidth = EditFiltersActivity.this.filtersAdapter.getParentWidth();
                    if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0 && recyclerView.computeHorizontalScrollOffset() == 0) {
                        EditFiltersActivity.this.m_ivMenuLeft.setClickable(false);
                    } else {
                        EditFiltersActivity.this.m_ivMenuLeft.setClickable(true);
                    }
                    int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    int numItems = recyclerView.getAdapter().getItemCount();
                    if (numItems <= nParentWidth / nItemWidth) {
                        EditFiltersActivity.this.m_ivMenuRight.setClickable(false);
                        return;
                    }
                    int remainWidth = (recyclerView.computeHorizontalScrollOffset() % nItemWidth) - EditFiltersActivity.FILTER_SPACING;
                    int roffset = EditFiltersActivity.this.filtersAdapter.getItemWidth() - (nParentWidth % nItemWidth);
                    if (lastVisibleItem < numItems - 1 || remainWidth != roffset) {
                        EditFiltersActivity.this.m_ivMenuRight.setClickable(true);
                    } else {
                        EditFiltersActivity.this.m_ivMenuRight.setClickable(false);
                    }
                }
            }
        });
        this.sbContrast.setOnSeekBarChangeListener(new FilterSeekBarChangeListener(EditFilter.CONTRAST, this));
        this.sbBrightness.setOnSeekBarChangeListener(new FilterSeekBarChangeListener(EditFilter.BRIGHTNESS, this));
    }

    public /* synthetic */ void lambda$initUI$26$EditFiltersActivity() {
        int scrollTo = 0;
        this.rcFilters.setLayoutManager(new LinearLayoutManager(this, 0, false));
        this.filtersAdapter = new HorizontalAdapter(getFiltersList(), getResources(), this.rcFilters.getWidth(), this.initialColor, this);
        this.rcFilters.setAdapter(this.filtersAdapter);
        this.rcFilters.addItemDecoration(new RecycleViewDecoration(this.marginFilters));
        if (this.autoFilter) {
            scrollTo = this.initialColor - 1;
        }
        if (scrollTo < 0) {
            scrollTo = 0;
        }
        this.rcFilters.scrollToPosition(scrollTo);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.createDisposable.clear();
        BitmapUtils.recycleBitmap(this.thumbSource);
        BitmapUtils.recycleBitmap(this.imgAuto);
        BitmapUtils.recycleBitmap(this.imgOriginal);
        BitmapUtils.recycleBitmap(this.imgLighten);
        BitmapUtils.recycleBitmap(this.imgMagic);
        BitmapUtils.recycleBitmap(this.imgGray);
        BitmapUtils.recycleBitmap(this.imgBW);
        BitmapUtils.recycleBitmap(this.imgBW2);
    }

    public void onTrackStarted(EditFilter filter, int progress) {
        showAndHideTuneAlert(filter, progress, 300, 400, true);
    }

    public void onGpuFilterProgressChanged(EditFilter filter, int progress) {
        showAndHideTuneAlert(filter, progress, 300, 400, false);
        this.relayTune.accept(new Tuner(new TuneData(filter, progress)));
        this.equalizerTouched = true;
    }

    public void onTrackEnded(EditFilter filter, int progress) {
        showAndHideTuneAlert(filter, progress, 0, 400, false);
    }

    private void showAndHideTuneAlert(EditFilter filter, int progress, int delay, int duration, boolean force) {
        if (force || progress != this.lastShownProgress || !filter.equals(this.lastShownFilter)) {
            this.lastShownProgress = progress;
            this.lastShownFilter = filter;
            TransitionManager.endTransitions(this.tuneAlert);
            TextView textView = this.tuneValue;
            textView.setText(progress + " %");
            this.tuneType.setText(getTuneTitle(filter));
            this.tuneAlert.setVisibility(0);
            Fade fade = new Fade(2);
            fade.setInterpolator(new DecelerateInterpolator());
            fade.addTarget((View) this.tuneAlert);
            fade.setStartDelay((long) delay);
            fade.setDuration((long) duration);
            TransitionManager.beginDelayedTransition(this.tuneAlert, fade);
            this.tuneAlert.setVisibility(4);
        }
    }

    public List<FilterData> getFiltersList() {
        List<FilterData> data = new ArrayList<>();
        data.add(new FilterData(this.imgAuto, getString(R.string.filter_auto)));
        data.add(new FilterData(this.imgOriginal, getString(R.string.filter_original)));
        data.add(new FilterData(this.imgLighten, getString(R.string.filter_light)));
        data.add(new FilterData(this.imgMagic, getString(R.string.filter_polish)));
        data.add(new FilterData(this.imgGray, getString(R.string.filter_gray)));
        data.add(new FilterData(this.imgBW, getString(R.string.filter_bw)));
        //data.add(new FilterData(this.imgBW2, getString(R.string.filter_bw2)));
        return data;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }



    private void changeEditFilterVisible() {
        this.editFilterVisible = !this.editFilterVisible;
        updateEditFilterMenuVisibility();
    }

    private String getTuneTitle(EditFilter filter) {
        int i = C68292.MyEditFilter[filter.ordinal()];
        if (i == 1) {
            return this.contrastInfo;
        }
        if (i == 2) {
            return this.brightnessInfo;
        }
        throw new IllegalStateException("Invalid filter");
    }

    /* access modifiers changed from: package-private */
    public void onDone() {
        if (!this.savingProcess && !this.finishing) {
            this.savingProcess = true;
            if (BitmapUtils.isRecycled(this.originalSource)) {
                Timber.tag(LOG_TAG).e(new Throwable("originalSource is reycled!"));
                showAlert(ALERT_REASON_DONE);
                this.savingProcess = false;
                return;
            }
            this.saving = Single.just(this.originalSource).subscribeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer() {
                public final void accept(Object obj) {
                    try {
                        lambda$onDone$27$EditFiltersActivity((Disposable) obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).observeOn(Schedulers.computation()).map(new Function() {
                public final Object apply(Object obj) {
                    try {
                        return lambda$onDone$28$EditFiltersActivity((Bitmap) obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).flatMap(new Function() {
                public final Object apply(Object obj) {
                    try {
                        return lambda$onDone$29$EditFiltersActivity((Bitmap) obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).map(new Function() {
                @Override
                public Object apply(Object o) throws Exception {
                    return BitmapUtils.rotate((Bitmap) o, (float) EditFiltersActivity.currentAngle, true);

                }
            }).observeOn(Schedulers.io()).map(new Function() {
                public final Object apply(Object obj) {
                    return EditFiltersActivity.this.saveImage((Bitmap) obj);
                }
            }).doOnSuccess(new Consumer() {
                public final void accept(Object obj) {
                    EditFiltersActivity.this.createDocument((SavedImage) obj);
                }
            }).toCompletable().doOnComplete(new Action() {
                public final void run() {
                    EditFiltersActivity.this.syncNewFile();
                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnTerminate(new Action() {
                public final void run() {
                    EditFiltersActivity.this.hideProgressDialog();
                }
            }).subscribe(new Action() {
                public final void run() {
                    EditFiltersActivity.this.saveSuccess();
                }
            }, new Consumer() {
                public final void accept(Object obj) {
                    EditFiltersActivity.this.saveFailure((Throwable) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onDone$27$EditFiltersActivity(Disposable bmp) throws Exception {
        showProgressDialog(getString(R.string.str_saving));
    }

    public /* synthetic */ Bitmap lambda$onDone$28$EditFiltersActivity(Bitmap bmp) throws Exception {
        return applyFilter(bmp, getOriginalMat(), this.chosenFilter, false);
    }

    public /* synthetic */ SingleSource lambda$onDone$29$EditFiltersActivity(Bitmap bmp) throws Exception {
        return applyGPUFilterWithCheck(bmp, true);
    }

    /* access modifiers changed from: private */
    public void saveSuccess() {
//        Analytics.get().logEditDoc(currentAngle != 0, this.equalizerTouched, this.chosenFilter.toString());
        finishWithResultIntent();
        this.savingProcess = false;
    }

    /* access modifiers changed from: private */
    public void saveFailure(Throwable throwable) {
        if (throwable instanceof OutOfMemoryError) {
            System.gc();
        }
        Timber.tag(LOG_TAG).e(throwable);
        showAlert(ALERT_REASON_SAVING_FAIL);
        this.savingProcess = false;
    }

    private Bitmap applyFilter(Bitmap bmp, Mat mat, FilterFlag chosenFilter2, boolean recycleSource) {
        Bitmap bmpAfterCropAndFilter = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int i = C68292.MyFilterFlag[chosenFilter2.ordinal()];
        if (i == 1) {
            PolygonUtil.getAutoBrightContrast(bmpAfterCropAndFilter, mat);
        } else if (i == 3) {
            PolygonUtil.getLighten(bmpAfterCropAndFilter, mat);
        } else if (i == 4) {
            PolygonUtil.getMagicColor(bmpAfterCropAndFilter, mat);
        } else if (i == 5) {
            PolygonUtil.getGrayImage(bmpAfterCropAndFilter, mat);
        } else if (i == 6) {
            bmpAfterCropAndFilter = PolygonUtil.getPixBinarization(mat);
        } else if (i == 7) {
            bmpAfterCropAndFilter = PolygonUtil.getInkwellFilter(EditFiltersActivity.this, bmpAfterCropAndFilter, mat);
        }
        if (recycleSource && !bmpAfterCropAndFilter.equals(bmp) && !this.optimizedSource.equals(bmp)) {
            BitmapUtils.recycleBitmap(bmp);
        }
        return bmpAfterCropAndFilter;
    }

    static /* synthetic */ class C68292 {
        static final /* synthetic */ int[] MyEditFilter = new int[EditFilter.values().length];
        static final /* synthetic */ int[] MyFilterFlag = new int[FilterFlag.values().length];

        static {
            try {
                MyFilterFlag[FilterFlag.Auto.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MyFilterFlag[FilterFlag.Original.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MyFilterFlag[FilterFlag.Lighten.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                MyFilterFlag[FilterFlag.Magic.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                MyFilterFlag[FilterFlag.Gray.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                MyFilterFlag[FilterFlag.BW1.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                MyFilterFlag[FilterFlag.BW2.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                MyEditFilter[EditFilter.CONTRAST.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                MyEditFilter[EditFilter.BRIGHTNESS.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    private Bitmap applyGPUFilter(GPUImage gpuImage2, Bitmap bmp, boolean recycleSource) {
        return gpuImage2.getBitmapWithFilterApplied(bmp, recycleSource);
    }

    private Single<Bitmap> applyGPUFilterWithCheck(Bitmap bitmapImage, final boolean recycleSource) {
        return Single.just(bitmapImage).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function() {


            public final Object apply(Object obj) {
                try {
                    return lambda$applyGPUFilterWithCheck$32$EditFiltersActivity(recycleSource, (Bitmap) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public /* synthetic */ SingleSource lambda$applyGPUFilterWithCheck$32$EditFiltersActivity(final boolean recycleSource, final Bitmap bitmap) throws Exception {
        if (isDefaultSettings()) {
            return Single.just(bitmap).subscribeOn(Schedulers.computation());
        }
        return Single.create(new SingleOnSubscribe() {


            public final void subscribe(SingleEmitter singleEmitter) {
                try {
                    lambda$null$31$EditFiltersActivity(bitmap, recycleSource, singleEmitter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.computation());
    }

    public /* synthetic */ void lambda$null$31$EditFiltersActivity(Bitmap bitmap, boolean recycleSource, SingleEmitter emitter) throws Exception {
        emitter.onSuccess(applyGPUFilter(this.gpuImage, bitmap, recycleSource));
    }

    private boolean isDefaultSettings() {
        return this.sbContrast.getProgress() == 50 && this.sbBrightness.getProgress() == 50;
    }

    /* access modifiers changed from: private */
    public SavedImage saveImage(Bitmap bitmap) {
        return new SavedImage(bitmap, ImageStorageUtils.saveImageToAppFolder(bitmap));
    }

    /* access modifiers changed from: private */
    public void syncNewFile() {
        if (this.document.m_bNew && !SharedPrefsUtils.getCloudSynced(this)) {
            int scanDocCnt = SharedPrefsUtils.getScanDocCnt(this) + 1;
            SharedPrefsUtils.setScanDocCnt(this, scanDocCnt);
            if (scanDocCnt >= 5) {
                SharedPrefsUtils.setCloudShouldSync(this, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void createDocument(SavedImage savedImage) {
        String pathThumb = ImageStorageUtils.saveImageToAppFolder(BitmapUtils.createThumb(savedImage.bitmap));
        long date = new Date().getTime();
        if (!this.document.m_bNew) {
            ImageStorageUtils.deleteImage(this, this.document.thumb);
            ImageStorageUtils.deleteImage(this, this.document.path);
            this.document.path = savedImage.path;
            Document document2 = this.document;
            document2.date = date;
            document2.thumb = pathThumb;
            DBManager.getInstance().updateDocument(this.document);
            this.m_strParent = this.document.parent;
            this.m_strName = this.document.name;
        } else if (this.document.notFirstInDoc) {
            int sortId = getIntent().getIntExtra(Constant.EXTRA_SORTID, -1);
            int lastSortID = sortId != -1 ? sortId : 1 + DBManager.getInstance().getLastSortID(this.document.parent);
            Document document3 = this.document;
            document3.date = date;
            document3.thumb = pathThumb;
            document3.imgPath = ScanApplication.imageHolder().getOriginalPath();
            this.document.path = savedImage.path;
            this.document.sortID = lastSortID;
            DBManager.getInstance().addDocument(this.document);
            this.m_strParent = this.document.parent;
            this.m_strName = this.document.name;
        } else {
            Document document4 = this.document;
            document4.date = date;
            document4.thumb = pathThumb;
            document4.imgPath = ScanApplication.imageHolder().getOriginalPath();
            this.document.path = savedImage.path;
            DBManager.getInstance().addDocument(this.document);
            Document child = new Document(this.document.uid);
            child.path = this.document.path;
            child.date = this.document.date;
            child.imgPath = this.document.imgPath;
            child.thumb = pathThumb;
            child.textPath = this.document.textPath;
            child.cropPoints = this.document.cropPoints;
            child.sortID = 1;
            DBManager.getInstance().addDocument(child);
            this.m_strParent = this.document.uid;
            this.m_strName = this.document.name;
        }
    }

    private void ResultIntent()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constant.EXTRA_MPARENT, this.m_strParent);
        resultIntent.putExtra(Constant.EXTRA_MNAME, this.m_strName);
        setResult(-1, resultIntent);
        this.finishing = true;
        finish();
    }
    private void finishWithResultIntent() {

        if(InterstitialHelper.isLoaded)
        {
            InterstitialHelper.showInterstitial(this, new InterstialListner() {
                @Override
                public void onAddClose() {
                    ResultIntent();
                }
            });
        }
        else {
            ResultIntent();
        }
    }

    /* access modifiers changed from: package-private */
    public void resetTuneFilterMenu(boolean updatePreview, FilterFlag filter) {
        int brightnessValue = 50;
        int contrastValue = filter.equals(FilterFlag.Auto) ? 60 : 50;
        if (filter.equals(FilterFlag.Auto)) {
            brightnessValue = 52;
        }
        List<TuneData> defaultTune = new ArrayList<>();
        defaultTune.add(new TuneData(EditFilter.CONTRAST, contrastValue));
        defaultTune.add(new TuneData(EditFilter.BRIGHTNESS, brightnessValue));
        Tuner tuner = new GroupTuner(defaultTune);
        this.sbContrast.setProgress(contrastValue);
        this.sbBrightness.setProgress(brightnessValue);
        if (updatePreview) {
            this.relayTune.accept(tuner);
        } else {
            applyTuneToGpu(tuner);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateEditFilterMenuVisibility() {
        int i = 0;
        this.sbContrast.setVisibility(this.editFilterVisible ? 0 : 4);
        VerticalSeekBar verticalSeekBar = this.sbBrightness;
        if (!this.editFilterVisible) {
            i = 4;
        }
        verticalSeekBar.setVisibility(i);
    }

    public void onFilterItemClicked(int position) {
        updateFilter(getFilterByPosition(position));
    }

    private void updateFilter(FilterFlag filter) {
        this.chosenFilter = filter;
        this.relayFilter.accept(this.chosenFilter);
    }

    private FilterFlag getFilterByPosition(int position) {
        switch (position) {
            case 0:
                return FilterFlag.Auto;
            case 1:
                return FilterFlag.Original;
            case 2:
                return FilterFlag.Lighten;
            case 3:
                return FilterFlag.Magic;
            case 4:
                return FilterFlag.Gray;
            case 5:
                return FilterFlag.BW1;
            case 6:
                return FilterFlag.BW2;
            default:
                throw new RuntimeException("Invalid position");
        }
    }

    private void showAlert(String reason) {
        Toast.makeText(this, getString(R.string.alert_sorry), 0).show();
        Throwable throwable = new Throwable(reason);
        Timber.tag(LOG_TAG).e(throwable);
    }

    private void recognizeDocument() {
        if (this.document.m_bNew) {
            this.document.path = ImageStorageUtils.saveImagePreOcr(this.originalSource);
        }
        OcrHelper.recognizeDocument(this, this.document);
    }

    public void moveRight() {
        int nItemWidth = this.filtersAdapter.getItemWidth() + FILTER_SPACING;
        int offset = this.rcFilters.computeHorizontalScrollOffset();
        int dx = (((((offset / nItemWidth) + 1) * nItemWidth) - offset) + (nItemWidth - (this.filtersAdapter.getParentWidth() % nItemWidth))) % nItemWidth;
        if (dx <= 0) {
            dx += nItemWidth;
        }
        this.rcFilters.smoothScrollBy(dx, 0);
    }

    public void moveLeft() {
        int nItemWidth = this.filtersAdapter.getItemWidth() + FILTER_SPACING;
        int offset = this.rcFilters.computeHorizontalScrollOffset();
        this.rcFilters.smoothScrollBy((((offset / nItemWidth) - 1) * nItemWidth) - offset, 0);
    }

    private void rotateImage() {
        currentAngle -= 90;
        if (currentAngle == -360) {
            currentAngle = 0;
        }
        this.relayRotate.accept(-90);
    }

    private void showLoading() {
        showProgressDialog(this.loading);
    }

    private void hideLoading() {
        hideProgressDialog();
    }

    public class RecycleViewDecoration extends RecyclerView.ItemDecoration {
        private final int divWidth;

        public RecycleViewDecoration(int divWidth2) {
            this.divWidth = divWidth2;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = this.divWidth;
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1021) {
            this.document = (data == null || !data.hasExtra(Constant.EXTRA_DOCUMENT)) ? this.document : (Document) data.getSerializableExtra(Constant.EXTRA_DOCUMENT);
        } else if (requestCode != 1022) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            this.document = (data == null || !data.hasExtra(Constant.EXTRA_DOCUMENT)) ? this.document : (Document) data.getSerializableExtra(Constant.EXTRA_DOCUMENT);
            if (data != null && data.getBooleanExtra(Constant.RETAKE_OCR, false)) {
                recognizeDocument();
            }
        }
    }
}
