package com.camscanner.paperscanner.pdfcreator.common.PDFGenerator;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;
import com.camscanner.paperscanner.pdfcreator.model.types.Qualities;
import timber.log.Timber;

public class PDFBoxGenerator {

    public interface PDFBoxGeneratorListener {
        void onNewPage(int i);
    }

    public static void createPDF(Context context, List<String> imgPaths, String fileName, PDFBoxGeneratorListener listener) throws IOException, OutOfMemoryError {
        PDRectangle pdRectangle;
        PDDocument document = new PDDocument();
        PDRectangle pdRectangle2 = getCurrentPDFSize(context);
        int curPage = 0;
        for (String imgPath : imgPaths) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapUtils.resizeOutputImage(context, imgPath);
            } catch (OutOfMemoryError e) {
                OutOfMemoryError ex = e;
                Timber.e(ex);
            }
            if (bitmap != null) {
                PDPage page = new PDPage(pdRectangle2);
                document.addPage(page);
                PDRectangle pdRect = page.getMediaBox();
                float pageWidth = pdRect.getWidth();
                float pageHeight = pdRect.getHeight();
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                PDImageXObject pdImage = JPEGFactory.createFromStream(document, BitmapUtils.bitmap2InputStream(bitmap, Qualities.PDF.value()));
                bitmap.recycle();
                float imgWidth = (float) pdImage.getWidth();
                float imgHeight = (float) pdImage.getHeight();
                pdRectangle = pdRectangle2;
                float scale = Math.min(pageWidth / imgWidth, (pageHeight - 10.0f) / imgHeight);
                contentStream.drawImage(pdImage, (pageWidth - (imgWidth * scale)) / 2.0f, (((pageHeight - (imgHeight * scale)) - 10.0f) / 2.0f) + 10.0f, imgWidth * scale, imgHeight * scale);
                contentStream.close();
            } else {
                pdRectangle = pdRectangle2;
            }
            curPage++;
            listener.onNewPage(curPage);
            pdRectangle2 = pdRectangle;
        }
        document.save(fileName);
        document.close();
    }

    public static boolean isEncrypted(String fileName) {
        try {
            PDDocument load = PDDocument.load(new File(fileName));
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }



    public static PDRectangle getCurrentPDFSize(Context context) {
        List<PDFSize> pdfSizesList = new ArrayList<>();
        DBManager.getInstance().getPDFSize(pdfSizesList);
        String strPDFPageSeleted = SharedPrefsUtils.getPDFPageSelected(context);
        int direction = SharedPrefsUtils.getPDFDirecttion(context);
        int curPos = -1;
        int i = 0;
        while (true) {
            if (i >= pdfSizesList.size()) {
                break;
            } else if (pdfSizesList.get(i).name.equals(strPDFPageSeleted)) {
                curPos = i;
                break;
            } else {
                i++;
            }
        }
        if (curPos != -1) {
            PDFSize pdfSize = pdfSizesList.get(curPos);
            if (direction == 1) {
                return new PDRectangle((float) pdfSize.pxWidth, (float) pdfSize.pxHeight);
            }
            return new PDRectangle((float) pdfSize.pxHeight, (float) pdfSize.pxWidth);
        } else if (direction == 1) {
            return PDRectangle.A4;
        } else {
            return new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        }
    }
}
