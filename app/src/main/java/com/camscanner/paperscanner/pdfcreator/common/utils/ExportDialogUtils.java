package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.PDFGenerator.PDFBoxGenerator;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.features.export.ExportDialogFragment;
import com.camscanner.paperscanner.pdfcreator.features.export.ExportFormat;
import com.camscanner.paperscanner.pdfcreator.features.export.ExportType;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.PDFViewImageActivity;
import timber.log.Timber;

public class ExportDialogUtils {
    public static void showShareDialog(@NonNull FragmentActivity activity, List<Document> documents, String fileName) {
        showShareDialog(activity, (List<List<Document>>) Collections.singletonList(documents), (List<String>) Collections.singletonList(fileName));
    }

    public static void showShareDialogWithDirs(@NonNull FragmentActivity activity, List<Document> documents) {
        List<List<Document>> documentsLists = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        for (Document document : documents) {
            if (document.isDir) {
                for (Document subDoc : getSubDocs(document)) {
                    documentsLists.add(getSubDocs(subDoc));
                    fileNames.add(document.name + "-" + subDoc.name);
                }
            } else {
                documentsLists.add(getSubDocs(document));
                fileNames.add(document.name);
            }
        }
        showShareDialog(activity, documentsLists, fileNames);
    }

    private static List<Document> getSubDocs(Document document) {
        ArrayList<Document> docLists = new ArrayList<>();
        DBManager.getInstance().getDocumentsBySortID(docLists, document.uid);
        return docLists;
    }

    private static void showShareDialog(@NonNull final FragmentActivity activity, final List<List<Document>> documents, final List<String> names) {
        ExportDialogFragment.newInstance(ExportType.SHARE).setExportListener(new ExportDialogFragment.OnExportDialogListener() {


            public final void onShareClicked(ExportFormat exportFormat) {
                new ExportDialogUtils.ShareTask(activity, documents, exportFormat, names).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }).showDialog(activity.getSupportFragmentManager());
    }

    public static void showSaveModeDialog(@NonNull final FragmentActivity activity, final List<Document> documents, final String fileName) {
        ExportDialogFragment.newInstance(ExportType.SAVE).setExportListener(new ExportDialogFragment.OnExportDialogListener() {

            public final void onShareClicked(ExportFormat exportFormat) {
                new ExportDialogUtils.SaveTask(activity, Collections.singletonList(documents), exportFormat, Collections.singletonList(fileName)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }).showDialog(activity.getSupportFragmentManager());
    }

    private static class SaveTask extends FileTask {
        public SaveTask(@NonNull Activity activity, @NonNull List<List<Document>> documents, @NonNull ExportFormat exportFormat, @NonNull List<String> names) {
            super(activity, documents, exportFormat, names);
        }

        /* access modifiers changed from: protected */
        public int getProgressTitle(ExportFormat exportFormat) {
            return exportFormat == ExportFormat.PDF ? R.string.title_create_pdf : R.string.title_save_image;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean success) {
            super.onPostExecute(success);
//            Analytics.get().logSave();
            if (!success.booleanValue()) {
                showTaskError();
            } else if (this.exportFormat == ExportFormat.PDF) {
                MediaScannerUtils.notifySystemAboutFile((Context) this.activity, (String) this.pdfPaths.get(0));
                showToastAndOpenPdf();
            } else {
                showImagesToast();
            }
        }

        protected String doInBackground(String... strings) {
            return null;
        }

        /* access modifiers changed from: protected */
        public void prepareImage(String imagePath) {
            String path;
            Bitmap outputImage = null;
            try {
                outputImage = BitmapUtils.resizeOutputImage(this.activity, imagePath);
            } catch (OutOfMemoryError ex) {
                Timber.e(ex);
            }
            if (outputImage != null && (path = ImageStorageUtils.saveImageToGallery(outputImage)) != null) {
                MediaScannerUtils.notifySystemAboutFile((Context) this.activity, path);
            }
        }

        private void showImagesToast() {
            showToast(this.activity.getString(R.string.save_image_alert), "8BC34A", (SuperToast.OnDismissListener) null);
        }

        private void showToastAndOpenPdf() {
            showToast(this.activity.getString(R.string.save_pdf_alert) + " \n" + this.pdfDestination, "8BC34A", new SuperToast.OnDismissListener() {
                public final void onDismiss(View view, Parcelable parcelable) {
                    ExportDialogUtils.SaveTask.this.showPdfIfPossible(view, parcelable);
                }
            });
        }

        /* access modifiers changed from: private */
        public void showPdfIfPossible(View view, Parcelable token) {
            if (canDisplayPdf(this.activity)) {
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                Uri pathUri = UriUtils.getFileUri(this.activity, (String) this.pdfPaths.get(0));
                openIntent.setDataAndType(pathUri, "application/pdf");
                openIntent.addFlags(1073741825);
                if (Build.VERSION.SDK_INT >= 21) {
                    openIntent.addFlags(524288);
                } else {
                    openIntent.setFlags(268484608);
                }
                openIntent.putExtra("android.intent.extra.SUBJECT", "");
                openIntent.putExtra("android.intent.extra.TEXT", "");

               /* Intent chooser = Intent.createChooser(openIntent, this.activity.getString(R.string.str_open));

                List<ResolveInfo> resInfoList = this.activity.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.activity.grantUriPermission(packageName, pathUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                this.activity.startActivity(chooser);*/
                this.activity.startActivity(Intent.createChooser(openIntent, this.activity.getString(R.string.str_open)));
            } else if (Build.VERSION.SDK_INT < 21) {
                showToast(this.activity.getString(R.string.pdf_reader_alert), PaletteUtils.MATERIAL_DEEP_ORANGE, (SuperToast.OnDismissListener) null);
            } else if (!SharedPrefsUtils.getPDFPassword(this.activity).isEmpty()) {
                showToast(this.activity.getString(R.string.pdf_password_alert), PaletteUtils.MATERIAL_DEEP_ORANGE, (SuperToast.OnDismissListener) null);
            } else {
                Intent intent = new Intent(this.activity, PDFViewImageActivity.class);
                intent.putExtra(Constant.EXTRA_FILE_NAME, (String) this.pdfPaths.get(0));
                this.activity.startActivity(intent);
            }
        }

        private void showToast(String text, String color, @Nullable SuperToast.OnDismissListener listener) {
            SuperActivityToast.create((Context) this.activity, new Style(), 1).setText(text).setDuration(2000).setFrame(2).setOnDismissListener(listener).setColor(PaletteUtils.getSolidColor(color)).setAnimations(4).show();
        }

        public boolean canDisplayPdf(Context context) {
            PackageManager packageManager = context.getPackageManager();
            Intent testIntent = new Intent("android.intent.action.VIEW");
            testIntent.setType("application/pdf");
            return packageManager.queryIntentActivities(testIntent, 65536).size() > 0;
        }
    }

    private static class ShareTask extends FileTask {
        private final List<String> tempImages = new ArrayList();

        public ShareTask(@NonNull Activity activity, @NonNull List<List<Document>> documents, @NonNull ExportFormat exportFormat, @NonNull List<String> names) {
            super(activity, documents, exportFormat, names);
        }

        /* access modifiers changed from: protected */
        public int getProgressTitle(ExportFormat exportFormat) {
            return exportFormat == ExportFormat.PDF ? R.string.title_create_pdf : R.string.title_compressing;
        }

        /* access modifiers changed from: protected */
        public void prepareImage(String imagePath) {
            Bitmap outputImage = null;
            try {
                outputImage = BitmapUtils.resizeOutputImage(this.activity, imagePath);
            } catch (OutOfMemoryError ex) {
                Timber.e(ex);
            }
            if (outputImage != null && ImageStorageUtils.saveImageToShareFolder(outputImage) != null) {
                this.tempImages.add(imagePath);
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean success) {
            super.onPostExecute(success);
//            Analytics.get().logShare();
            if (success.booleanValue()) {
                Intent shareIntent = new Intent("android.intent.action.SEND_MULTIPLE");
                ArrayList<Uri> shareFiles = new ArrayList<>();
                if (this.exportFormat == ExportFormat.PDF) {
                    shareIntent.setType("application/pdf");
                    for (String path : this.pdfPaths) {
                        shareFiles.add(UriUtils.getFileUri(this.activity, path));
                    }
                } else {
                    shareIntent.setType("image/*");
                    for (String imagePath : this.tempImages) {
                        shareFiles.add(UriUtils.getFileUri(this.activity, imagePath));
                    }
                }
                shareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", shareFiles);
                shareIntent.putExtra("android.intent.extra.SUBJECT", "");
                shareIntent.putExtra("android.intent.extra.TEXT", "");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                this.activity.startActivityForResult(Intent.createChooser(shareIntent, this.activity.getString(R.string.share_chooser_title)), 1010);
                return;
            }
            showTaskError();
        }
    }

    private static abstract class FileTask extends AsyncTask<Void, Void, Boolean> implements PDFBoxGenerator.PDFBoxGeneratorListener {
        @NonNull
        protected final Activity activity;
        private ProgressDialog dlgProgress;
        @NonNull
        private final List<List<Document>> documents;
        @NonNull
        protected final ExportFormat exportFormat;
        protected String pdfDestination = "";
        @NonNull
        private final List<String> pdfNames;
        protected List<String> pdfPaths = new ArrayList();
        private final int totalFiles;

        /* access modifiers changed from: protected */
        public abstract int getProgressTitle(ExportFormat exportFormat2);

        /* access modifiers changed from: protected */
        public abstract void prepareImage(String str);

        public FileTask(@NonNull Activity activity2, @NonNull List<List<Document>> documents2, @NonNull ExportFormat exportFormat2, @NonNull List<String> pdfNames2) {
            this.activity = activity2;
            this.documents = new ArrayList();
            int tempTotalFiles = 0;
            for (List<Document> list : documents2) {
                this.documents.add(new ArrayList(list));
                tempTotalFiles += list.size();
            }
            this.totalFiles = tempTotalFiles;
            this.exportFormat = exportFormat2;
            this.pdfNames = new ArrayList(pdfNames2);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.dlgProgress = new ProgressDialog(this.activity);
            this.dlgProgress.setTitle(this.activity.getString(getProgressTitle(this.exportFormat)));
            this.dlgProgress.setProgressStyle(1);
            this.dlgProgress.setCancelable(false);
            this.dlgProgress.show();
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voids) {
            if (this.exportFormat == ExportFormat.PDF) {
                boolean abc= preparePdf();

                return abc;
            }
            boolean abc= prepareImages();
            return abc;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean aBoolean) {
            ProgressDialog progressDialog;
            if (!this.activity.isFinishing() && (progressDialog = this.dlgProgress) != null && progressDialog.isShowing()) {
                this.dlgProgress.dismiss();
            }
        }




        private boolean preparePdf() {
            boolean success = false;
            File dir = ImageStorageUtils.getAppFolder();
            this.pdfDestination = dir.getPath() + File.separator;
            List<File> uniqueFiles = prepareUniqueFiles(dir, this.pdfNames, Extensions.PDF);
            int baseProgress = 0;
            for (int i = 0; i < this.documents.size(); i++) {
                List<Document> list = this.documents.get(i);
                File outPdf = uniqueFiles.get(i);
                List<String> pdfImages = new ArrayList<>();
                for (Document document : list) {
                    if (!document.path.isEmpty()) {
                        pdfImages.add(document.path);
                    }
                }
                try {
                    final int finalBaseProgress = baseProgress;
                    PDFBoxGenerator.createPDF(this.activity, pdfImages, outPdf.getAbsolutePath(), new PDFBoxGenerator.PDFBoxGeneratorListener() {


                        public final void onNewPage(int i) {
                            ExportDialogUtils.FileTask.this.lambda$preparePdf$0$ExportDialogUtils$FileTask(finalBaseProgress, i);
                        }
                    });
                    this.pdfPaths.add(outPdf.getPath());
                    success = true;
                } catch (IOException | OutOfMemoryError | RuntimeException ex) {
                }
                baseProgress += list.size();
            }
            return success;
        }

        public /* synthetic */ void lambda$preparePdf$0$ExportDialogUtils$FileTask(int currBaseProgress, int page) {
            updateProgress(currBaseProgress + page);
        }

        private List<File> prepareUniqueFiles(File dest, List<String> pdfNames2, String ext) {
            List<File> uniqueFiles = new ArrayList<>();
            Map<String, Integer> mapNameQuantity = new HashMap<>();
            for (String name : pdfNames2) {
                String validName = StringHelper.clearFilename(name.trim(), true);
                File uniqueFile = new File(dest, validName + ext);
                if (mapNameQuantity.containsKey(validName) || uniqueFile.exists()) {
                    int quantity = getOrDefault(mapNameQuantity, validName, 0);
                    do {
                        quantity++;
                        uniqueFile = new File(dest, (validName + Constant.STR_BRACKET_OPEN + quantity + Constant.STR_BRACKET_CLOSE) + ext);
                    } while (uniqueFile.exists());
                    mapNameQuantity.put(validName, Integer.valueOf(quantity));
                } else {
                    mapNameQuantity.put(validName, 0);
                }
                uniqueFiles.add(uniqueFile);
            }
            return uniqueFiles;
        }

        private int getOrDefault(Map<String, Integer> map, String key, Integer defaultValue) {
            Integer value = map.get(key);
            return (value != null ? value : defaultValue).intValue();
        }

        private boolean prepareImages() {
            int progress = 0;
            for (int i = 0; i < this.documents.size(); i++) {
                List<Document> list = this.documents.get(i);
                for (int j = 0; j < list.size(); j++) {
                    prepareImage(list.get(j).path);
                    progress++;
                    updateProgress(progress);
                }
            }
            return true;
        }

        public void onNewPage(int pageNumber) {
            updateProgress(pageNumber);
        }

        private void updateProgress(final int value) {
            this.activity.runOnUiThread(new Runnable() {


                public final void run() {
                    ExportDialogUtils.FileTask.this.lambda$updateProgress$1$ExportDialogUtils$FileTask((int) (((((float) value) * 1.0f) / ((float) totalFiles)) * 100.0f));
                }
            });
        }

        public /* synthetic */ void lambda$updateProgress$1$ExportDialogUtils$FileTask(int progress) {
            this.dlgProgress.setProgress(progress);
        }

        /* access modifiers changed from: protected */
        public void showTaskError() {
            if (!this.activity.isFinishing()) {
                Activity activity2 = this.activity;
                Toast.makeText(activity2, activity2.getString(R.string.alert_pdf_fail), 1).show();
            }
        }
    }
}
