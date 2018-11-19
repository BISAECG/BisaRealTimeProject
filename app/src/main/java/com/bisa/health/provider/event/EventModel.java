package com.bisa.health.provider.event;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Data model for the {@code event} table.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface EventModel extends BaseModel {

    /**
     * Primary key.
     */
    long getId();

    /**
     * Get the {@code user_guid} value.
     */
    int getUserGuid();

    /**
     * Get the {@code event_name} value.
     * Can be {@code null}.
     */
    @Nullable
    String getEventName();

    /**
     * Get the {@code event_num} value.
     * Can be {@code null}.
     */
    @Nullable
    String getEventNum();

    /**
     * Get the {@code event_index} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getEventIndex();

    /**
     * Get the {@code event_type} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getEventType();

    /**
     * Get the {@code event_email} value.
     * Can be {@code null}.
     */
    @Nullable
    String getEventEmail();
}
