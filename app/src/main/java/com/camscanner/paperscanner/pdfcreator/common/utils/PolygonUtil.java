package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.widget.Toast;
import com.tapscanner.polygondetect.DetectionResult;
import java.util.Date;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.ImageFiltersManagement;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.types.CropMode;
import timber.log.Timber;

public class PolygonUtil {
    public static void saveImageWithAutoCrop(Context context, String str, List<Document> list) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decodeFile = BitmapFactory.decodeFile(str, options);
        if (decodeFile != null) {
            createDocumentWithAutoCropAndColor(context, str, list, decodeFile);
        }
    }

    public static void copyImageFromGalleryWithAutoCrop(Context context, Uri uri, List<Document> list) {
        Bitmap decodeUri = BitmapUtils.decodeUri(context, uri);
        if (decodeUri != null) {
            createDocumentWithAutoCropAndColor(context, ImageStorageUtils.saveImageToAppFolder(decodeUri), list, decodeUri);
        }
    }

    public static void createDocumentWithAutoCropAndColor(Context context, String str, List<Document> list, Bitmap bitmap) {
        DetectionResult detectionResult;
        Document document = new Document("");
        document.m_bNew = true;
        try {
            detectionResult = getDetectInfo(context, bitmap);
        } catch (CvException e) {
            DetectionResult detectionResult2 = new DetectionResult();
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.alert_sorry), 1).show();
            detectionResult = detectionResult2;
        }
        document.setCropPoints(detectionResult.m_ptfInfo);
        double width = (double) bitmap.getWidth();
        Double.isNaN(width);
        try {
            applyPerspectiveAndColorFilter(context, bitmap, detectionResult.m_ptfInfo, (int) (width * 0.8d), detectionResult, document);
            document.imgPath = str;
            document.date = new Date().getTime();
            list.add(document);
        } catch (OutOfMemoryError | CvException e2) {
            Timber.e(e2);
            System.gc();
        }
    }

    public static void applyPerspectiveAndColorFilter(Context context, Bitmap bitmap, PointF[] pointFArr, int i, DetectionResult detectionResult, Document document) {
        Rect rect = new Rect(0, 0, i, i);
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        Mat mat2 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat2, false);
        Imgproc.cvtColor(mat2, mat, 3);
        mat2.release();
        detectionResult.saveDetectInfo(pointFArr, rect);
        RectF coverRect = detectionResult.getCoverRect();
        detectionResult.saveDetectInfo(pointFArr, new Rect(0, 0, (int) (((float) bitmap.getWidth()) * coverRect.width()), (int) (((float) bitmap.getHeight()) * coverRect.height())));
        ScanApplication.getDetectionEngine().cropPerspective(mat.getNativeObjAddr(), detectionResult);
        Mat mat3 = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(mat, mat3, 4);
        Bitmap createBitmap = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat3, createBitmap);
        mat3.release();
        switch (SharedPrefsUtils.getBatchColorMode(context)) {
            case 1:
                break;
            case 2:
                getLighten(createBitmap, mat);
                break;
            case 3:
                getMagicColor(createBitmap, mat);
                break;
            case 4:
                getGrayImage(createBitmap, mat);
                break;
            case 5:
                createBitmap = getPixBinarization(mat);
                break;
            case 6:
                getInkwellFilter(context, createBitmap, mat);
                break;
            default:
                getAutoBrightContrast(createBitmap, mat);
                break;
        }
        mat.release();
        String saveImageToAppFolder = ImageStorageUtils.saveImageToAppFolder(createBitmap);
        String saveImageToAppFolder2 = ImageStorageUtils.saveImageToAppFolder(BitmapUtils.createThumb(createBitmap));
        document.path = saveImageToAppFolder;
        document.thumb = saveImageToAppFolder2;
    }

    public static DetectionResult getDetectInfo(Context context, Bitmap bitmap) {
        CropMode valueOf = CropMode.valueOf(SharedPrefsUtils.getBatchCropMode(context));
        DetectionResult detectionResult = new DetectionResult();
        switch (valueOf) {
            case SMART_CROP:
                try {
                    Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(bitmap, mat, false);
                    Mat mat2 = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
                    Imgproc.cvtColor(mat, mat2, 11);
                    mat.release();
                    if (!ScanApplication.getDetectionEngine().process(mat2.getNativeObjAddr(), detectionResult)) {
                        detectionResult.fixSmallRect(DetectionResult.FIX_RECT_MODE.FIX_RECT_GALLERY);
                    }
                    mat2.release();
                } catch (CvException e) {
                    Timber.e(e);
                }
                return detectionResult;
            case ALWAYS_CROP:
                detectionResult.saveDetectInfo(new PointF[]{new PointF(0.1f, 0.1f), new PointF(0.9f, 0.1f), new PointF(0.9f, 0.9f), new PointF(0.1f, 0.9f)}, new Rect());
                return detectionResult;
            default:
                detectionResult.saveDetectInfo(new PointF[]{new PointF(0.001f, 0.001f), new PointF(0.999f, 0.001f), new PointF(0.999f, 0.999f), new PointF(0.001f, 0.999f)}, new Rect());
                return detectionResult;
        }
    }

    public static void getAutoBrightContrast(Bitmap bitmap, Mat mat) {
        Mat clone = mat.clone();
        ScanApplication.getDetectionEngine().magicColor(clone.getNativeObjAddr());
        Mat mat2 = new Mat(clone.rows(), clone.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(clone, mat2, 4);
        Utils.matToBitmap(mat2, bitmap);
        clone.release();
        mat2.release();
    }

    public static void getLighten(Bitmap bitmap, Mat mat) {
        Mat clone = mat.clone();
        ScanApplication.getDetectionEngine().lighten(clone.getNativeObjAddr());
        Mat mat2 = new Mat(clone.rows(), clone.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(clone, mat2, 4);
        Utils.matToBitmap(mat2, bitmap);
        clone.release();
        mat2.release();
    }

    public static void getMagicColor(Bitmap bitmap, Mat mat) {
        Mat clone = mat.clone();
        ScanApplication.getDetectionEngine().autoBrightContrast(clone.getNativeObjAddr(), 0.0f);
        Mat mat2 = new Mat(clone.rows(), clone.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(clone, mat2, 4);
        Utils.matToBitmap(mat2, bitmap);
        clone.release();
        mat2.release();
    }

    public static void getGrayImage(Bitmap bitmap, Mat mat) {
        Mat clone = mat.clone();
        ScanApplication.getDetectionEngine().convertGray(clone.getNativeObjAddr());
        Utils.matToBitmap(clone, bitmap);
        clone.release();
    }

    public static Bitmap getPixBinarization(Mat mat) {
        return BitmapUtils.getBinaryFromMat(mat);
    }

    public static Bitmap getInkwellFilter(Context context, Bitmap bitmap, Mat mat) {
        Mat clone = mat.clone();
        ScanApplication.getDetectionEngine().autoBrightContrast(clone.getNativeObjAddr(), 0.0f);
        Mat mat2 = new Mat(clone.rows(), clone.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(clone, mat2, 4);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
        }
        Utils.matToBitmap(mat2, bitmap);
        clone.release();
        mat2.release();
        GPUImage gPUImage = new GPUImage(context);
        gPUImage.setImage(bitmap);
        gPUImage.setFilter(ImageFiltersManagement.createFilterForType(context, ImageFiltersManagement.FilterType.I_INKWELL));
        return gPUImage.getBitmapWithFilterApplied();
    }

    public static Document InsertBatchData(Context context, List<Document> list, String str) {
        if (list.size() < 1) {
            return null;
        }
        String nameTemplate = TagUtils.getNameTemplate(SharedPrefsUtils.getNameTemplate(context), context);
        Document document = new Document("");
        if (str == null) {
            str = "";
        }
        document.parent = str;
        document.name = nameTemplate;
        int i = 0;
        document.path = list.get(0).path;
        document.date = list.get(0).date;
        document.thumb = list.get(0).thumb;
        document.cropPoints = list.get(0).cropPoints;
        document.m_bNew = list.get(0).m_bNew;
        document.notFirstInDoc = list.get(0).notFirstInDoc;
        document.imgPath = list.get(0).imgPath;
        while (i < list.size()) {
            list.get(i).parent = document.uid;
            int i2 = i + 1;
            list.get(i).sortID = i2;
            DBManager.getInstance().addDocument(list.get(i));
            i = i2;
        }
        DBManager.getInstance().addDocument(document);
        return document;
    }

    public static void insertBatch2Grid(Context context, List<Document> list, String str, int i) {
        for (int i2 = 0; i2 < list.size(); i2++) {
            list.get(i2).parent = str;
            list.get(i2).sortID = i2 + i;
            DBManager.getInstance().addDocument(list.get(i2));
        }
    }

    public static void importGalleryImage(Context context, Uri uri) {
        Bitmap decodeUri = BitmapUtils.decodeUri(context, uri);
        if (decodeUri != null) {
            ScanApplication.imageHolder().setOriginalPicture(decodeUri, ImageStorageUtils.saveImageToAppFolder(decodeUri));
        }
    }
}
