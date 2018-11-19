package com.bisa.health.provider.upload;

// @formatter:off
import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code upload} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryLocalVariable"})
public class UploadCursor extends AbstractCursor implements UploadModel {
    public UploadCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        Long res = getLongOrNull(UploadColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_guid} value.
     */
    @Override
    public int getUserGuid() {
        Integer res = getIntegerOrNull(UploadColumns.USER_GUID);
        if (res == null)
            throw new NullPointerException("The value of 'user_guid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code filename} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getFilename() {
        String res = getStringOrNull(UploadColumns.FILENAME);
        return res;
    }

    /**
     * Get the {@code uptime} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getUptime() {
        String res = getStringOrNull(UploadColumns.UPTIME);
        return res;
    }
}
