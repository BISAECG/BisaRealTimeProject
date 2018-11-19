package com.bisa.health.provider.appreport;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Data model for the {@code appreport} table.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface AppreportModel extends BaseModel {

    /**
     * Primary key.
     */
    long getId();

    /**
     * Get the {@code user_guid} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getUserGuid();

    /**
     * Get the {@code report_number} value.
     * Can be {@code null}.
     */
    @Nullable
    String getReportNumber();

    /**
     * Get the {@code report_type} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getReportType();

    /**
     * Get the {@code report_status} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getReportStatus();

    /**
     * Get the {@code start_time} value.
     * Can be {@code null}.
     */
    @Nullable
    Date getStartTime();

    /**
     * Get the {@code year} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getYear();

    /**
     * Get the {@code month} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getMonth();

    /**
     * Get the {@code day} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getDay();

    /**
     * Get the {@code report_count} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getReportCount();

    /**
     * Get the {@code body} value.
     * Can be {@code null}.
     */
    @Nullable
    String getBody();

    /**
     * Get the {@code ecgdat} value.
     * Can be {@code null}.
     */
    @Nullable
    String getEcgdat();
}
