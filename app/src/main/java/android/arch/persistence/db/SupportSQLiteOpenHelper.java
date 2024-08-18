package android.arch.persistence.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface SupportSQLiteOpenHelper {

    public interface Factory {
        SupportSQLiteOpenHelper create(Configuration configuration);
    }

    void close();

    String getDatabaseName();

    SupportSQLiteDatabase getReadableDatabase();

    SupportSQLiteDatabase getWritableDatabase();

    @RequiresApi(api = 16)
    void setWriteAheadLoggingEnabled(boolean z);

    public static abstract class Callback {
        private static final String TAG = "SupportSQLite";
        public final int version;

        public void onConfigure(SupportSQLiteDatabase supportSQLiteDatabase) {
        }

        public abstract void onCreate(SupportSQLiteDatabase supportSQLiteDatabase);

        public void onOpen(SupportSQLiteDatabase supportSQLiteDatabase) {
        }

        public abstract void onUpgrade(SupportSQLiteDatabase supportSQLiteDatabase, int i, int i2);

        public Callback(int i) {
            this.version = i;
        }

        public void onDowngrade(SupportSQLiteDatabase supportSQLiteDatabase, int i, int i2) {
            throw new SQLiteException("Can't downgrade database from version " + i + " to " + i2);
        }


        public void onCorruption(SupportSQLiteDatabase supportSQLiteDatabase) {
            Log.e(TAG, "Corruption reported by sqlite on database: " + supportSQLiteDatabase.getPath());
            if (!supportSQLiteDatabase.isOpen()) {
                deleteDatabaseFile(supportSQLiteDatabase.getPath());
                return;
            }
            List<android.util.Pair<String, String>> list = null;
            try {
                list = supportSQLiteDatabase.getAttachedDbs();
                supportSQLiteDatabase.close();
            } catch (IOException unused) {
            } catch (Throwable th) {
            }
            if (list == null) {
                for (Pair<String, String> pair : list) {
                    deleteDatabaseFile((String) pair.second);
                }
                return;
            }
            deleteDatabaseFile(supportSQLiteDatabase.getPath());
        }


        private void deleteDatabaseFile(String str) {
            if (!str.equalsIgnoreCase(":memory:") && str.trim().length() != 0) {
                Log.w(TAG, "deleting the database file: " + str);
                try {
                    if (Build.VERSION.SDK_INT >= 16) {
                        SQLiteDatabase.deleteDatabase(new File(str));
                        return;
                    }
                    try {
                        if (!new File(str).delete()) {
                            Log.e(TAG, "Could not delete the database file " + str);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "error while deleting corrupted database file", e);
                    }
                } catch (Exception e2) {
                    Log.w(TAG, "delete failed: ", e2);
                }
            }
        }
    }

    public static class Configuration {
        @NonNull
        public final Callback callback;
        @NonNull
        public final Context context;
        @Nullable
        public final String name;

        Configuration(@NonNull Context context2, @Nullable String str, @NonNull Callback callback2) {
            this.context = context2;
            this.name = str;
            this.callback = callback2;
        }

        public static Builder builder(Context context2) {
            return new Builder(context2);
        }

        public static class Builder {
            Callback mCallback;
            Context mContext;
            String mName;

            public Configuration build() {
                Callback callback = this.mCallback;
                if (callback != null) {
                    Context context = this.mContext;
                    if (context != null) {
                        return new Configuration(context, this.mName, callback);
                    }
                    throw new IllegalArgumentException("Must set newa non-null context to create the configuration.");
                }
                throw new IllegalArgumentException("Must set newa callback to create the configuration.");
            }

            Builder(@NonNull Context context) {
                this.mContext = context;
            }

            public Builder name(@Nullable String str) {
                this.mName = str;
                return this;
            }

            public Builder callback(@NonNull Callback callback) {
                this.mCallback = callback;
                return this;
            }
        }
    }
}
