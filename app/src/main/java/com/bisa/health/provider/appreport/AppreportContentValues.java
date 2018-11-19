package com.bisa.health.provider.appreport;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code appreport} table.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class AppreportContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return AppreportColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable AppreportSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The context to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable AppreportSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public AppreportContentValues putUserGuid(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportContentValues putUserGuidNull() {
        mContentValues.putNull(AppreportColumns.USER_GUID);
        return this;
    }

    public AppreportContentValues putReportNumber(@Nullable String value) {
        mContentValues.put(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportContentValues putReportNumberNull() {
        mContentValues.putNull(AppreportColumns.REPORT_NUMBER);
        return this;
    }

    public AppreportContentValues putReportType(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportContentValues putReportTypeNull() {
        mContentValues.putNull(AppreportColumns.REPORT_TYPE);
        return this;
    }

    public AppreportContentValues putReportStatus(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportContentValues putReportStatusNull() {
        mContentValues.putNull(AppreportColumns.REPORT_STATUS);
        return this;
    }

    public AppreportContentValues putStartTime(@Nullable Date value) {
        mContentValues.put(AppreportColumns.START_TIME, value == null ? null : value.getTime());
        return this;
    }

    public AppreportContentValues putStartTimeNull() {
        mContentValues.putNull(AppreportColumns.START_TIME);
        return this;
    }

    public AppreportContentValues putStartTime(@Nullable Long value) {
        mContentValues.put(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportContentValues putYear(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportContentValues putYearNull() {
        mContentValues.putNull(AppreportColumns.YEAR);
        return this;
    }

    public AppreportContentValues putMonth(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportContentValues putMonthNull() {
        mContentValues.putNull(AppreportColumns.MONTH);
        return this;
    }

    public AppreportContentValues putDay(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportContentValues putDayNull() {
        mContentValues.putNull(AppreportColumns.DAY);
        return this;
    }

    public AppreportContentValues putReportCount(@Nullable Integer value) {
        mContentValues.put(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportContentValues putReportCountNull() {
        mContentValues.putNull(AppreportColumns.REPORT_COUNT);
        return this;
    }

    public AppreportContentValues putBody(@Nullable String value) {
        mContentValues.put(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportContentValues putBodyNull() {
        mContentValues.putNull(AppreportColumns.BODY);
        return this;
    }

    public AppreportContentValues putEcgdat(@Nullable String value) {
        mContentValues.put(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportContentValues putEcgdatNull() {
        mContentValues.putNull(AppreportColumns.ECGDAT);
        return this;
    }
}
