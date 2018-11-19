package com.bisa.health.provider.event;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code event} table.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class EventContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return EventColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable EventSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The context to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable EventSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public EventContentValues putUserGuid(int value) {
        mContentValues.put(EventColumns.USER_GUID, value);
        return this;
    }


    public EventContentValues putEventName(@Nullable String value) {
        mContentValues.put(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventContentValues putEventNameNull() {
        mContentValues.putNull(EventColumns.EVENT_NAME);
        return this;
    }

    public EventContentValues putEventNum(@Nullable String value) {
        mContentValues.put(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventContentValues putEventNumNull() {
        mContentValues.putNull(EventColumns.EVENT_NUM);
        return this;
    }

    public EventContentValues putEventIndex(@Nullable Integer value) {
        mContentValues.put(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventContentValues putEventIndexNull() {
        mContentValues.putNull(EventColumns.EVENT_INDEX);
        return this;
    }

    public EventContentValues putEventType(@Nullable Integer value) {
        mContentValues.put(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventContentValues putEventTypeNull() {
        mContentValues.putNull(EventColumns.EVENT_TYPE);
        return this;
    }

    public EventContentValues putEventEmail(@Nullable String value) {
        mContentValues.put(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventContentValues putEventEmailNull() {
        mContentValues.putNull(EventColumns.EVENT_EMAIL);
        return this;
    }
}
