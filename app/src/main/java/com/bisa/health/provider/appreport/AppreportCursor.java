package com.bisa.health.provider.appreport;

// @formatter:off
import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code appreport} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryLocalVariable"})
public class AppreportCursor extends AbstractCursor implements AppreportModel {
    public AppreportCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        Long res = getLongOrNull(AppreportColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_guid} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getUserGuid() {
        Integer res = getIntegerOrNull(AppreportColumns.USER_GUID);
        return res;
    }

    /**
     * Get the {@code report_number} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getReportNumber() {
        String res = getStringOrNull(AppreportColumns.REPORT_NUMBER);
        return res;
    }

    /**
     * Get the {@code report_type} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getReportType() {
        Integer res = getIntegerOrNull(AppreportColumns.REPORT_TYPE);
        return res;
    }

    /**
     * Get the {@code report_status} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getReportStatus() {
        Integer res = getIntegerOrNull(AppreportColumns.REPORT_STATUS);
        return res;
    }

    /**
     * Get the {@code start_time} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Date getStartTime() {
        Date res = getDateOrNull(AppreportColumns.START_TIME);
        return res;
    }

    /**
     * Get the {@code year} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getYear() {
        Integer res = getIntegerOrNull(AppreportColumns.YEAR);
        return res;
    }

    /**
     * Get the {@code month} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getMonth() {
        Integer res = getIntegerOrNull(AppreportColumns.MONTH);
        return res;
    }

    /**
     * Get the {@code day} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getDay() {
        Integer res = getIntegerOrNull(AppreportColumns.DAY);
        return res;
    }

    /**
     * Get the {@code report_count} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getReportCount() {
        Integer res = getIntegerOrNull(AppreportColumns.REPORT_COUNT);
        return res;
    }

    /**
     * Get the {@code body} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getBody() {
        String res = getStringOrNull(AppreportColumns.BODY);
        return res;
    }

    /**
     * Get the {@code ecgdat} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEcgdat() {
        String res = getStringOrNull(AppreportColumns.ECGDAT);
        return res;
    }
}
