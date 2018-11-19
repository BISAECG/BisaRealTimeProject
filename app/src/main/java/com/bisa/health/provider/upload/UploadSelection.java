package com.bisa.health.provider.upload;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.bisa.health.provider.base.AbstractSelection;

/**
 * Selection for the {@code upload} table.
 */
@SuppressWarnings({"unused", "WeakerAccess", "Recycle"})
public class UploadSelection extends AbstractSelection<UploadSelection> {
    @Override
    protected Uri baseUri() {
        return UploadColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code UploadCursor} object, which is positioned before the first entry, or null.
     */
    public UploadCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new UploadCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public UploadCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code UploadCursor} object, which is positioned before the first entry, or null.
     */
    public UploadCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new UploadCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public UploadCursor query(Context context) {
        return query(context, null);
    }


    public UploadSelection id(long... value) {
        addEquals("upload." + UploadColumns._ID, toObjectArray(value));
        return this;
    }

    public UploadSelection idNot(long... value) {
        addNotEquals("upload." + UploadColumns._ID, toObjectArray(value));
        return this;
    }

    public UploadSelection orderById(boolean desc) {
        orderBy("upload." + UploadColumns._ID, desc);
        return this;
    }

    public UploadSelection orderById() {
        return orderById(false);
    }

    public UploadSelection userGuid(int... value) {
        addEquals(UploadColumns.USER_GUID, toObjectArray(value));
        return this;
    }

    public UploadSelection userGuidNot(int... value) {
        addNotEquals(UploadColumns.USER_GUID, toObjectArray(value));
        return this;
    }

    public UploadSelection userGuidGt(int value) {
        addGreaterThan(UploadColumns.USER_GUID, value);
        return this;
    }

    public UploadSelection userGuidGtEq(int value) {
        addGreaterThanOrEquals(UploadColumns.USER_GUID, value);
        return this;
    }

    public UploadSelection userGuidLt(int value) {
        addLessThan(UploadColumns.USER_GUID, value);
        return this;
    }

    public UploadSelection userGuidLtEq(int value) {
        addLessThanOrEquals(UploadColumns.USER_GUID, value);
        return this;
    }

    public UploadSelection orderByUserGuid(boolean desc) {
        orderBy(UploadColumns.USER_GUID, desc);
        return this;
    }

    public UploadSelection orderByUserGuid() {
        orderBy(UploadColumns.USER_GUID, false);
        return this;
    }

    public UploadSelection filename(String... value) {
        addEquals(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadSelection filenameNot(String... value) {
        addNotEquals(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadSelection filenameLike(String... value) {
        addLike(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadSelection filenameContains(String... value) {
        addContains(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadSelection filenameStartsWith(String... value) {
        addStartsWith(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadSelection filenameEndsWith(String... value) {
        addEndsWith(UploadColumns.FILENAME, value);
        return this;
    }

    public UploadSelection orderByFilename(boolean desc) {
        orderBy(UploadColumns.FILENAME, desc);
        return this;
    }

    public UploadSelection orderByFilename() {
        orderBy(UploadColumns.FILENAME, false);
        return this;
    }

    public UploadSelection uptime(String... value) {
        addEquals(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadSelection uptimeNot(String... value) {
        addNotEquals(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadSelection uptimeLike(String... value) {
        addLike(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadSelection uptimeContains(String... value) {
        addContains(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadSelection uptimeStartsWith(String... value) {
        addStartsWith(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadSelection uptimeEndsWith(String... value) {
        addEndsWith(UploadColumns.UPTIME, value);
        return this;
    }

    public UploadSelection orderByUptime(boolean desc) {
        orderBy(UploadColumns.UPTIME, desc);
        return this;
    }

    public UploadSelection orderByUptime() {
        orderBy(UploadColumns.UPTIME, false);
        return this;
    }
}
