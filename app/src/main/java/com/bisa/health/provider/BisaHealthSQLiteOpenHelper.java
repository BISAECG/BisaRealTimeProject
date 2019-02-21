package com.bisa.health.provider;

// @formatter:off
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.bisa.health.BuildConfig;
import com.bisa.health.provider.appreport.AppreportColumns;
import com.bisa.health.provider.device.DeviceColumns;
import com.bisa.health.provider.event.EventColumns;
import com.bisa.health.provider.upload.UploadColumns;

public class BisaHealthSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = BisaHealthSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "bisahealth.db";
    private static final int DATABASE_VERSION = 1;
    private static BisaHealthSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final BisaHealthSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    public static final String SQL_CREATE_TABLE_APPREPORT = "CREATE TABLE IF NOT EXISTS "
            + AppreportColumns.TABLE_NAME + " ( "
            + AppreportColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AppreportColumns.USER_GUID + " INTEGER, "
            + AppreportColumns.REPORT_NUMBER + " TEXT, "
            + AppreportColumns.REPORT_TYPE + " INTEGER, "
            + AppreportColumns.REPORT_STATUS + " INTEGER, "
            + AppreportColumns.START_TIME + " INTEGER, "
            + AppreportColumns.YEAR + " INTEGER, "
            + AppreportColumns.MONTH + " INTEGER, "
            + AppreportColumns.DAY + " INTEGER, "
            + AppreportColumns.REPORT_COUNT + " INTEGER, "
            + AppreportColumns.BODY + " TEXT, "
            + AppreportColumns.ECGDAT + " TEXT "
            + " );";

    public static final String SQL_CREATE_INDEX_APPREPORT_USER_GUID = "CREATE INDEX IDX_APPREPORT_USER_GUID "
            + " ON " + AppreportColumns.TABLE_NAME + " ( " + AppreportColumns.USER_GUID + " );";

    public static final String SQL_CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS "
            + DeviceColumns.TABLE_NAME + " ( "
            + DeviceColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DeviceColumns.USER_GUID + " INTEGER, "
            + DeviceColumns.DEVNAME + " TEXT, "
            + DeviceColumns.MACADDERSS + " TEXT, "
            + DeviceColumns.DEVNUM + " TEXT, "
            + DeviceColumns.CONNSTATUS + " INTEGER, "
            + DeviceColumns.CLZNAME + " TEXT, "
            + DeviceColumns.CHECKBOX + " INTEGER, "
            + DeviceColumns.ICOFLAG + " INTEGER "
            + " );";

    public static final String SQL_CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS "
            + EventColumns.TABLE_NAME + " ( "
            + EventColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EventColumns.USER_GUID + " INTEGER NOT NULL, "
            + EventColumns.EVENT_NAME + " TEXT, "
            + EventColumns.EVENT_NUM + " TEXT, "
            + EventColumns.EVENT_INDEX + " INTEGER, "
            + EventColumns.EVENT_TYPE + " INTEGER, "
            + EventColumns.EVENT_EMAIL + " TEXT "
            + " );";

    public static final String SQL_CREATE_TABLE_UPLOAD = "CREATE TABLE IF NOT EXISTS "
            + UploadColumns.TABLE_NAME + " ( "
            + UploadColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UploadColumns.USER_GUID + " INTEGER NOT NULL, "
            + UploadColumns.FILENAME + " TEXT, "
            + UploadColumns.UPTIME + " TEXT "
            + " );";


    public static BisaHealthSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static BisaHealthSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static BisaHealthSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new BisaHealthSQLiteOpenHelper(context);
    }

    private BisaHealthSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new BisaHealthSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static BisaHealthSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new BisaHealthSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private BisaHealthSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new BisaHealthSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.i(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_APPREPORT);
        db.execSQL(SQL_CREATE_INDEX_APPREPORT_USER_GUID);
        db.execSQL(SQL_CREATE_TABLE_DEVICE);
        db.execSQL(SQL_CREATE_TABLE_EVENT);
        db.execSQL(SQL_CREATE_TABLE_UPLOAD);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
