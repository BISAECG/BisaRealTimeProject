package com.bisa.health.provider.upload;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Data model for the {@code upload} table.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface UploadModel extends BaseModel {

    /**
     * Primary key.
     */
    long getId();

    /**
     * Get the {@code user_guid} value.
     */
    int getUserGuid();

    /**
     * Get the {@code filename} value.
     * Can be {@code null}.
     */
    @Nullable
    String getFilename();

    /**
     * Get the {@code uptime} value.
     * Can be {@code null}.
     */
    @Nullable
    String getUptime();
}
