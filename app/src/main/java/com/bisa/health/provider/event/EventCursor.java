package com.bisa.health.provider.event;

// @formatter:off
import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code event} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryLocalVariable"})
public class EventCursor extends AbstractCursor implements EventModel {
    public EventCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        Long res = getLongOrNull(EventColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_guid} value.
     */
    @Override
    public int getUserGuid() {
        Integer res = getIntegerOrNull(EventColumns.USER_GUID);
        if (res == null)
            throw new NullPointerException("The value of 'user_guid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code event_name} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEventName() {
        String res = getStringOrNull(EventColumns.EVENT_NAME);
        return res;
    }

    /**
     * Get the {@code event_num} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEventNum() {
        String res = getStringOrNull(EventColumns.EVENT_NUM);
        return res;
    }

    /**
     * Get the {@code event_index} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getEventIndex() {
        Integer res = getIntegerOrNull(EventColumns.EVENT_INDEX);
        return res;
    }

    /**
     * Get the {@code event_type} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getEventType() {
        Integer res = getIntegerOrNull(EventColumns.EVENT_TYPE);
        return res;
    }

    /**
     * Get the {@code event_email} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEventEmail() {
        String res = getStringOrNull(EventColumns.EVENT_EMAIL);
        return res;
    }
}
