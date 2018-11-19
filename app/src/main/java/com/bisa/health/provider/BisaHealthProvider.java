package com.bisa.health.provider;

// @formatter:off
import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bisa.health.BuildConfig;
import com.bisa.health.provider.base.BaseContentProvider;
import com.bisa.health.provider.appreport.AppreportColumns;
import com.bisa.health.provider.device.DeviceColumns;
import com.bisa.health.provider.event.EventColumns;
import com.bisa.health.provider.upload.UploadColumns;

public class BisaHealthProvider extends BaseContentProvider {
    private static final String TAG = BisaHealthProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "com.bisa.health.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_APPREPORT = 0;
    private static final int URI_TYPE_APPREPORT_ID = 1;

    private static final int URI_TYPE_DEVICE = 2;
    private static final int URI_TYPE_DEVICE_ID = 3;

    private static final int URI_TYPE_EVENT = 4;
    private static final int URI_TYPE_EVENT_ID = 5;

    private static final int URI_TYPE_UPLOAD = 6;
    private static final int URI_TYPE_UPLOAD_ID = 7;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, AppreportColumns.TABLE_NAME, URI_TYPE_APPREPORT);
        URI_MATCHER.addURI(AUTHORITY, AppreportColumns.TABLE_NAME + "/#", URI_TYPE_APPREPORT_ID);
        URI_MATCHER.addURI(AUTHORITY, DeviceColumns.TABLE_NAME, URI_TYPE_DEVICE);
        URI_MATCHER.addURI(AUTHORITY, DeviceColumns.TABLE_NAME + "/#", URI_TYPE_DEVICE_ID);
        URI_MATCHER.addURI(AUTHORITY, EventColumns.TABLE_NAME, URI_TYPE_EVENT);
        URI_MATCHER.addURI(AUTHORITY, EventColumns.TABLE_NAME + "/#", URI_TYPE_EVENT_ID);
        URI_MATCHER.addURI(AUTHORITY, UploadColumns.TABLE_NAME, URI_TYPE_UPLOAD);
        URI_MATCHER.addURI(AUTHORITY, UploadColumns.TABLE_NAME + "/#", URI_TYPE_UPLOAD_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return BisaHealthSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_APPREPORT:
                return TYPE_CURSOR_DIR + AppreportColumns.TABLE_NAME;
            case URI_TYPE_APPREPORT_ID:
                return TYPE_CURSOR_ITEM + AppreportColumns.TABLE_NAME;

            case URI_TYPE_DEVICE:
                return TYPE_CURSOR_DIR + DeviceColumns.TABLE_NAME;
            case URI_TYPE_DEVICE_ID:
                return TYPE_CURSOR_ITEM + DeviceColumns.TABLE_NAME;

            case URI_TYPE_EVENT:
                return TYPE_CURSOR_DIR + EventColumns.TABLE_NAME;
            case URI_TYPE_EVENT_ID:
                return TYPE_CURSOR_ITEM + EventColumns.TABLE_NAME;

            case URI_TYPE_UPLOAD:
                return TYPE_CURSOR_DIR + UploadColumns.TABLE_NAME;
            case URI_TYPE_UPLOAD_ID:
                return TYPE_CURSOR_ITEM + UploadColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_APPREPORT:
            case URI_TYPE_APPREPORT_ID:
                res.table = AppreportColumns.TABLE_NAME;
                res.idColumn = AppreportColumns._ID;
                res.tablesWithJoins = AppreportColumns.TABLE_NAME;
                res.orderBy = AppreportColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_DEVICE:
            case URI_TYPE_DEVICE_ID:
                res.table = DeviceColumns.TABLE_NAME;
                res.idColumn = DeviceColumns._ID;
                res.tablesWithJoins = DeviceColumns.TABLE_NAME;
                res.orderBy = DeviceColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_EVENT:
            case URI_TYPE_EVENT_ID:
                res.table = EventColumns.TABLE_NAME;
                res.idColumn = EventColumns._ID;
                res.tablesWithJoins = EventColumns.TABLE_NAME;
                res.orderBy = EventColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_UPLOAD:
            case URI_TYPE_UPLOAD_ID:
                res.table = UploadColumns.TABLE_NAME;
                res.idColumn = UploadColumns._ID;
                res.tablesWithJoins = UploadColumns.TABLE_NAME;
                res.orderBy = UploadColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_APPREPORT_ID:
            case URI_TYPE_DEVICE_ID:
            case URI_TYPE_EVENT_ID:
            case URI_TYPE_UPLOAD_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
