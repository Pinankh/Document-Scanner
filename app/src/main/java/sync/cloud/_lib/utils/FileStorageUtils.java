package sync.cloud._lib.utils;

import android.os.Environment;
import java.io.File;
import timber.log.Timber;

public final class FileStorageUtils {
    public static final char EXTENSION_SEPARATOR = '.';
    private static final int NOT_FOUND = -1;
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    public static String getRecordsFilePath(String name) {
        return getSubFolderDir().getPath() + "/" + name;
    }

    public static File getSubFolderDir() {
        File storageDir = new File(getAppDir(), "IMG");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public static File getAppDir() {

        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/"+Environment.DIRECTORY_DOWNLOADS);
        //File dir = new File(sb.toString());

        File storageDir = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            storageDir = new File(sb.toString(), "DocScanner");
            //storageDir = new File(Environment.getExternalStorageDirectory(), "DocScanner");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        } else {
            Timber.e("External storage is not mounted READ/WRITE.", new Object[0]);
        }
        return storageDir;
    }

    public static String getFileExtensionName(String filename) {
        if (filename == null) {
            return null;
        }
        return filename.substring(indexOfLastSeparator(filename) + 1);
    }

    public static String getFileName(String filename) {
        if (filename == null) {
            return null;
        }
        return filename.substring(indexOfLastSeparator(filename) + 1, indexOfExtension(filename));
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        }
        return filename.substring(index);
    }

    public static int indexOfExtension(String filename) {
        int extensionPos;
        if (filename != null && indexOfLastSeparator(filename) <= (extensionPos = filename.lastIndexOf(46))) {
            return extensionPos;
        }
        return -1;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        return Math.max(filename.lastIndexOf(47), filename.lastIndexOf(92));
    }
}
