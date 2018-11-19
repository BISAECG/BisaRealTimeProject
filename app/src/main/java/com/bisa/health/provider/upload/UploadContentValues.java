package com.bisa.health.provider.upload;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code upload} table.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class UploadContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return UploadColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable UploadSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The context to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable UploadSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public UploadContentValues putUserGuid(int value) {
        mContentValues.put(UploadColumns.USER_GUID, value);
        return this;
    }


    public UploadContentValues putFilename(@Nullable String value) {
        mContentValues.put(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadContentValues putFilenameNull() {
        mContentValues.putNull(UploadColumns.FILENAME);
        return this;
    }

    public UploadContentValues putUptime(@Nullable String value) {
        mContentValues.put(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadContentValues putUptimeNull() {
        mContentValues.putNull(UploadColumns.UPTIME);
        return this;
    }
}
