package android.p000os;

import com.applisto.appcloner.classes.util.Log;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public class Environmenu {
    public static String DIRECTORY_ALARMS = null;
    public static String DIRECTORY_DCIM = null;
    public static String DIRECTORY_DOCUMENTS = null;
    public static String DIRECTORY_DOWNLOADS = null;
    public static String DIRECTORY_MOVIES = null;
    public static String DIRECTORY_MUSIC = null;
    public static String DIRECTORY_NOTIFICATIONS = null;
    public static String DIRECTORY_PICTURES = null;
    public static String DIRECTORY_PODCASTS = null;
    public static String DIRECTORY_RINGTONES = null;
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_EJECTING = "ejecting";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_UNKNOWN = "unknown";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    public static final String MEDIA_UNMOUNTED = "unmounted";
    public static final String[] STANDARD_DIRECTORIES;
    private static final String TAG = Environmenu.class.getSimpleName();
    private static String externalStorageEncapsulationName;
    private static Set<File> sExistingDirectories;

    static {
        externalStorageEncapsulationName = "//////////////////////////////////////////////////";
        Log.m15i(TAG, "static initializer; externalStorageEncapsulationName (1): " + externalStorageEncapsulationName);
        int i = 0;
        while (true) {
            if (i >= externalStorageEncapsulationName.length()) {
                i = -1;
                break;
            } else if (externalStorageEncapsulationName.charAt(i) != '/') {
                break;
            } else {
                i++;
            }
        }
        if (i != -1) {
            externalStorageEncapsulationName = externalStorageEncapsulationName.substring(i);
        } else {
            externalStorageEncapsulationName = null;
        }
        Log.m15i(TAG, "static initializer; externalStorageEncapsulationName (2): " + externalStorageEncapsulationName);
        sExistingDirectories = new HashSet();
        DIRECTORY_MUSIC = "Music";
        DIRECTORY_PODCASTS = "Podcasts";
        DIRECTORY_RINGTONES = "Ringtones";
        DIRECTORY_ALARMS = "Alarms";
        DIRECTORY_NOTIFICATIONS = "Notifications";
        DIRECTORY_PICTURES = "Pictures";
        DIRECTORY_MOVIES = "Movies";
        DIRECTORY_DOWNLOADS = "Download";
        DIRECTORY_DCIM = "DCIM";
        DIRECTORY_DOCUMENTS = "Documents";
        STANDARD_DIRECTORIES = new String[]{DIRECTORY_MUSIC, DIRECTORY_PODCASTS, DIRECTORY_RINGTONES, DIRECTORY_ALARMS, DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, DIRECTORY_MOVIES, DIRECTORY_DOWNLOADS, DIRECTORY_DCIM, DIRECTORY_DOCUMENTS};
    }

    public static boolean isStandardDirectory(String str) {
        for (String str2 : STANDARD_DIRECTORIES) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    public static File getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    public static File getExternalStorageDirectory() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        synchronized (Environmenu.class) {
            if (externalStorageEncapsulationName != null) {
                File file = new File(externalStorageDirectory, externalStorageEncapsulationName);
                if (!sExistingDirectories.contains(file)) {
                    Log.m15i(TAG, "getExternalStorageDirectory; file: " + file);
                    if (file.exists() && file.isDirectory()) {
                        sExistingDirectories.add(file);
                    } else if (file.mkdirs()) {
                        sExistingDirectories.add(file);
                    } else {
                        Log.m15i(TAG, "getExternalStorageDirectory; mkdirs() failed");
                    }
                }
                externalStorageDirectory = file;
            } else {
                Log.m15i(TAG, "getExternalStorageDirectory; externalStorageEncapsulationName == null");
            }
        }
        return externalStorageDirectory;
    }

    public static File getExternalStoragePublicDirectory(String str) {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(str);
        Log.m15i(TAG, "getExternalStoragePublicDirectory; type: " + str + ", directory: " + externalStoragePublicDirectory);
        File file = new File(getExternalStorageDirectory(), externalStoragePublicDirectory.getName());
        Log.m15i(TAG, "getExternalStoragePublicDirectory; newDirectory: " + file);
        return file;
    }

    public static String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    public static String getExternalStorageState(File file) {
        return Environment.getExternalStorageState(file);
    }

    public static File getRootDirectory() {
        return Environment.getRootDirectory();
    }

    public static String getStorageState(File file) {
        return Environment.getStorageState(file);
    }

    public static boolean isExternalStorageEmulated() {
        return Environment.isExternalStorageEmulated();
    }

    public static boolean isExternalStorageEmulated(File file) {
        return Environment.isExternalStorageEmulated(file);
    }

    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    public static boolean isExternalStorageRemovable(File file) {
        return Environment.isExternalStorageRemovable(file);
    }
}
