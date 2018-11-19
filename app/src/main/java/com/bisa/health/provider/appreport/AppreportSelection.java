package com.bisa.health.provider.appreport;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.bisa.health.provider.base.AbstractSelection;

/**
 * Selection for the {@code appreport} table.
 */
@SuppressWarnings({"unused", "WeakerAccess", "Recycle"})
public class AppreportSelection extends AbstractSelection<AppreportSelection> {
    @Override
    protected Uri baseUri() {
        return AppreportColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code AppreportCursor} object, which is positioned before the first entry, or null.
     */
    public AppreportCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new AppreportCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public AppreportCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code AppreportCursor} object, which is positioned before the first entry, or null.
     */
    public AppreportCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new AppreportCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public AppreportCursor query(Context context) {
        return query(context, null);
    }


    public AppreportSelection id(long... value) {
        addEquals("appreport." + AppreportColumns._ID, toObjectArray(value));
        return this;
    }

    public AppreportSelection idNot(long... value) {
        addNotEquals("appreport." + AppreportColumns._ID, toObjectArray(value));
        return this;
    }

    public AppreportSelection orderById(boolean desc) {
        orderBy("appreport." + AppreportColumns._ID, desc);
        return this;
    }

    public AppreportSelection orderById() {
        return orderById(false);
    }

    public AppreportSelection userGuid(Integer... value) {
        addEquals(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportSelection userGuidNot(Integer... value) {
        addNotEquals(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportSelection userGuidGt(int value) {
        addGreaterThan(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportSelection userGuidGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportSelection userGuidLt(int value) {
        addLessThan(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportSelection userGuidLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.USER_GUID, value);
        return this;
    }

    public AppreportSelection orderByUserGuid(boolean desc) {
        orderBy(AppreportColumns.USER_GUID, desc);
        return this;
    }

    public AppreportSelection orderByUserGuid() {
        orderBy(AppreportColumns.USER_GUID, false);
        return this;
    }

    public AppreportSelection reportNumber(String... value) {
        addEquals(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportSelection reportNumberNot(String... value) {
        addNotEquals(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportSelection reportNumberLike(String... value) {
        addLike(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportSelection reportNumberContains(String... value) {
        addContains(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportSelection reportNumberStartsWith(String... value) {
        addStartsWith(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportSelection reportNumberEndsWith(String... value) {
        addEndsWith(AppreportColumns.REPORT_NUMBER, value);
        return this;
    }

    public AppreportSelection orderByReportNumber(boolean desc) {
        orderBy(AppreportColumns.REPORT_NUMBER, desc);
        return this;
    }

    public AppreportSelection orderByReportNumber() {
        orderBy(AppreportColumns.REPORT_NUMBER, false);
        return this;
    }

    public AppreportSelection reportType(Integer... value) {
        addEquals(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportSelection reportTypeNot(Integer... value) {
        addNotEquals(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportSelection reportTypeGt(int value) {
        addGreaterThan(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportSelection reportTypeGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportSelection reportTypeLt(int value) {
        addLessThan(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportSelection reportTypeLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.REPORT_TYPE, value);
        return this;
    }

    public AppreportSelection orderByReportType(boolean desc) {
        orderBy(AppreportColumns.REPORT_TYPE, desc);
        return this;
    }

    public AppreportSelection orderByReportType() {
        orderBy(AppreportColumns.REPORT_TYPE, false);
        return this;
    }

    public AppreportSelection reportStatus(Integer... value) {
        addEquals(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportSelection reportStatusNot(Integer... value) {
        addNotEquals(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportSelection reportStatusGt(int value) {
        addGreaterThan(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportSelection reportStatusGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportSelection reportStatusLt(int value) {
        addLessThan(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportSelection reportStatusLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.REPORT_STATUS, value);
        return this;
    }

    public AppreportSelection orderByReportStatus(boolean desc) {
        orderBy(AppreportColumns.REPORT_STATUS, desc);
        return this;
    }

    public AppreportSelection orderByReportStatus() {
        orderBy(AppreportColumns.REPORT_STATUS, false);
        return this;
    }

    public AppreportSelection startTime(Date... value) {
        addEquals(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection startTimeNot(Date... value) {
        addNotEquals(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection startTime(Long... value) {
        addEquals(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection startTimeAfter(Date value) {
        addGreaterThan(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection startTimeAfterEq(Date value) {
        addGreaterThanOrEquals(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection startTimeBefore(Date value) {
        addLessThan(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection startTimeBeforeEq(Date value) {
        addLessThanOrEquals(AppreportColumns.START_TIME, value);
        return this;
    }

    public AppreportSelection orderByStartTime(boolean desc) {
        orderBy(AppreportColumns.START_TIME, desc);
        return this;
    }

    public AppreportSelection orderByStartTime() {
        orderBy(AppreportColumns.START_TIME, false);
        return this;
    }

    public AppreportSelection year(Integer... value) {
        addEquals(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportSelection yearNot(Integer... value) {
        addNotEquals(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportSelection yearGt(int value) {
        addGreaterThan(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportSelection yearGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportSelection yearLt(int value) {
        addLessThan(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportSelection yearLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.YEAR, value);
        return this;
    }

    public AppreportSelection orderByYear(boolean desc) {
        orderBy(AppreportColumns.YEAR, desc);
        return this;
    }

    public AppreportSelection orderByYear() {
        orderBy(AppreportColumns.YEAR, false);
        return this;
    }

    public AppreportSelection month(Integer... value) {
        addEquals(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportSelection monthNot(Integer... value) {
        addNotEquals(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportSelection monthGt(int value) {
        addGreaterThan(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportSelection monthGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportSelection monthLt(int value) {
        addLessThan(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportSelection monthLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.MONTH, value);
        return this;
    }

    public AppreportSelection orderByMonth(boolean desc) {
        orderBy(AppreportColumns.MONTH, desc);
        return this;
    }

    public AppreportSelection orderByMonth() {
        orderBy(AppreportColumns.MONTH, false);
        return this;
    }

    public AppreportSelection day(Integer... value) {
        addEquals(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportSelection dayNot(Integer... value) {
        addNotEquals(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportSelection dayGt(int value) {
        addGreaterThan(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportSelection dayGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportSelection dayLt(int value) {
        addLessThan(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportSelection dayLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.DAY, value);
        return this;
    }

    public AppreportSelection orderByDay(boolean desc) {
        orderBy(AppreportColumns.DAY, desc);
        return this;
    }

    public AppreportSelection orderByDay() {
        orderBy(AppreportColumns.DAY, false);
        return this;
    }

    public AppreportSelection reportCount(Integer... value) {
        addEquals(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportSelection reportCountNot(Integer... value) {
        addNotEquals(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportSelection reportCountGt(int value) {
        addGreaterThan(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportSelection reportCountGtEq(int value) {
        addGreaterThanOrEquals(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportSelection reportCountLt(int value) {
        addLessThan(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportSelection reportCountLtEq(int value) {
        addLessThanOrEquals(AppreportColumns.REPORT_COUNT, value);
        return this;
    }

    public AppreportSelection orderByReportCount(boolean desc) {
        orderBy(AppreportColumns.REPORT_COUNT, desc);
        return this;
    }

    public AppreportSelection orderByReportCount() {
        orderBy(AppreportColumns.REPORT_COUNT, false);
        return this;
    }

    public AppreportSelection body(String... value) {
        addEquals(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportSelection bodyNot(String... value) {
        addNotEquals(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportSelection bodyLike(String... value) {
        addLike(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportSelection bodyContains(String... value) {
        addContains(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportSelection bodyStartsWith(String... value) {
        addStartsWith(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportSelection bodyEndsWith(String... value) {
        addEndsWith(AppreportColumns.BODY, value);
        return this;
    }

    public AppreportSelection orderByBody(boolean desc) {
        orderBy(AppreportColumns.BODY, desc);
        return this;
    }

    public AppreportSelection orderByBody() {
        orderBy(AppreportColumns.BODY, false);
        return this;
    }

    public AppreportSelection ecgdat(String... value) {
        addEquals(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportSelection ecgdatNot(String... value) {
        addNotEquals(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportSelection ecgdatLike(String... value) {
        addLike(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportSelection ecgdatContains(String... value) {
        addContains(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportSelection ecgdatStartsWith(String... value) {
        addStartsWith(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportSelection ecgdatEndsWith(String... value) {
        addEndsWith(AppreportColumns.ECGDAT, value);
        return this;
    }

    public AppreportSelection orderByEcgdat(boolean desc) {
        orderBy(AppreportColumns.ECGDAT, desc);
        return this;
    }

    public AppreportSelection orderByEcgdat() {
        orderBy(AppreportColumns.ECGDAT, false);
        return this;
    }
}
