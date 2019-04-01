package com.bisa.health.provider.device;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code device} table.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class DeviceContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return DeviceColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable DeviceSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The context to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable DeviceSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public DeviceContentValues putUserGuid(@Nullable Integer value) {
        mContentValues.put(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceContentValues putUserGuidNull() {
        mContentValues.putNull(DeviceColumns.USER_GUID);
        return this;
    }

    public DeviceContentValues putDevname(@Nullable String value) {
        mContentValues.put(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceContentValues putDevnameNull() {
        mContentValues.putNull(DeviceColumns.DEVNAME);
        return this;
    }

    public DeviceContentValues putMacadderss(@Nullable String value) {
        mContentValues.put(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceContentValues putMacadderssNull() {
        mContentValues.putNull(DeviceColumns.MACADDERSS);
        return this;
    }

    public DeviceContentValues putDevnum(@Nullable String value) {
        mContentValues.put(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceContentValues putDevnumNull() {
        mContentValues.putNull(DeviceColumns.DEVNUM);
        return this;
    }

    public DeviceContentValues putConnstatus(@Nullable Integer value) {
        mContentValues.put(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceContentValues putConnstatusNull() {
        mContentValues.putNull(DeviceColumns.CONNSTATUS);
        return this;
    }

    public DeviceContentValues putClzname(@Nullable String value) {
        mContentValues.put(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceContentValues putClznameNull() {
        mContentValues.putNull(DeviceColumns.CLZNAME);
        return this;
    }

    public DeviceContentValues putCheckbox(@Nullable Integer value) {
        mContentValues.put(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceContentValues putCheckboxNull() {
        mContentValues.putNull(DeviceColumns.CHECKBOX);
        return this;
    }

    public DeviceContentValues putIcoflag(@Nullable Integer value) {
        mContentValues.put(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceContentValues putIcoflagNull() {
        mContentValues.putNull(DeviceColumns.ICOFLAG);
        return this;
    }


    public DeviceContentValues putCustName(@Nullable String value) {
        mContentValues.put(DeviceColumns.CUSTNAME, value);
        return this;
    }

    public DeviceContentValues putCustNameNull() {
        mContentValues.putNull(DeviceColumns.CUSTNAME);
        return this;
    }
}
