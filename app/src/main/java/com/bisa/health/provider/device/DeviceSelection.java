package com.bisa.health.provider.device;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.bisa.health.provider.base.AbstractSelection;

/**
 * Selection for the {@code device} table.
 */
@SuppressWarnings({"unused", "WeakerAccess", "Recycle"})
public class DeviceSelection extends AbstractSelection<DeviceSelection> {
    @Override
    protected Uri baseUri() {
        return DeviceColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code DeviceCursor} object, which is positioned before the first entry, or null.
     */
    public DeviceCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new DeviceCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public DeviceCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code DeviceCursor} object, which is positioned before the first entry, or null.
     */
    public DeviceCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new DeviceCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public DeviceCursor query(Context context) {
        return query(context, null);
    }


    public DeviceSelection id(long... value) {
        addEquals("device." + DeviceColumns._ID, toObjectArray(value));
        return this;
    }

    public DeviceSelection idNot(long... value) {
        addNotEquals("device." + DeviceColumns._ID, toObjectArray(value));
        return this;
    }

    public DeviceSelection orderById(boolean desc) {
        orderBy("device." + DeviceColumns._ID, desc);
        return this;
    }

    public DeviceSelection orderById() {
        return orderById(false);
    }

    public DeviceSelection userGuid(Integer... value) {
        addEquals(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceSelection userGuidNot(Integer... value) {
        addNotEquals(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceSelection userGuidGt(int value) {
        addGreaterThan(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceSelection userGuidGtEq(int value) {
        addGreaterThanOrEquals(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceSelection userGuidLt(int value) {
        addLessThan(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceSelection userGuidLtEq(int value) {
        addLessThanOrEquals(DeviceColumns.USER_GUID, value);
        return this;
    }

    public DeviceSelection orderByUserGuid(boolean desc) {
        orderBy(DeviceColumns.USER_GUID, desc);
        return this;
    }

    public DeviceSelection orderByUserGuid() {
        orderBy(DeviceColumns.USER_GUID, false);
        return this;
    }

    public DeviceSelection devname(String... value) {
        addEquals(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceSelection devnameNot(String... value) {
        addNotEquals(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceSelection devnameLike(String... value) {
        addLike(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceSelection devnameContains(String... value) {
        addContains(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceSelection devnameStartsWith(String... value) {
        addStartsWith(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceSelection devnameEndsWith(String... value) {
        addEndsWith(DeviceColumns.DEVNAME, value);
        return this;
    }

    public DeviceSelection orderByDevname(boolean desc) {
        orderBy(DeviceColumns.DEVNAME, desc);
        return this;
    }

    public DeviceSelection orderByDevname() {
        orderBy(DeviceColumns.DEVNAME, false);
        return this;
    }

    public DeviceSelection macadderss(String... value) {
        addEquals(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceSelection macadderssNot(String... value) {
        addNotEquals(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceSelection macadderssLike(String... value) {
        addLike(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceSelection macadderssContains(String... value) {
        addContains(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceSelection macadderssStartsWith(String... value) {
        addStartsWith(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceSelection macadderssEndsWith(String... value) {
        addEndsWith(DeviceColumns.MACADDERSS, value);
        return this;
    }

    public DeviceSelection orderByMacadderss(boolean desc) {
        orderBy(DeviceColumns.MACADDERSS, desc);
        return this;
    }

    public DeviceSelection orderByMacadderss() {
        orderBy(DeviceColumns.MACADDERSS, false);
        return this;
    }

    public DeviceSelection devnum(String... value) {
        addEquals(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceSelection devnumNot(String... value) {
        addNotEquals(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceSelection devnumLike(String... value) {
        addLike(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceSelection devnumContains(String... value) {
        addContains(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceSelection devnumStartsWith(String... value) {
        addStartsWith(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceSelection devnumEndsWith(String... value) {
        addEndsWith(DeviceColumns.DEVNUM, value);
        return this;
    }

    public DeviceSelection orderByDevnum(boolean desc) {
        orderBy(DeviceColumns.DEVNUM, desc);
        return this;
    }

    public DeviceSelection orderByDevnum() {
        orderBy(DeviceColumns.DEVNUM, false);
        return this;
    }

    public DeviceSelection connstatus(Integer... value) {
        addEquals(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceSelection connstatusNot(Integer... value) {
        addNotEquals(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceSelection connstatusGt(int value) {
        addGreaterThan(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceSelection connstatusGtEq(int value) {
        addGreaterThanOrEquals(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceSelection connstatusLt(int value) {
        addLessThan(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceSelection connstatusLtEq(int value) {
        addLessThanOrEquals(DeviceColumns.CONNSTATUS, value);
        return this;
    }

    public DeviceSelection orderByConnstatus(boolean desc) {
        orderBy(DeviceColumns.CONNSTATUS, desc);
        return this;
    }

    public DeviceSelection orderByConnstatus() {
        orderBy(DeviceColumns.CONNSTATUS, false);
        return this;
    }

    public DeviceSelection clzname(String... value) {
        addEquals(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceSelection clznameNot(String... value) {
        addNotEquals(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceSelection clznameLike(String... value) {
        addLike(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceSelection clznameContains(String... value) {
        addContains(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceSelection clznameStartsWith(String... value) {
        addStartsWith(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceSelection clznameEndsWith(String... value) {
        addEndsWith(DeviceColumns.CLZNAME, value);
        return this;
    }

    public DeviceSelection orderByClzname(boolean desc) {
        orderBy(DeviceColumns.CLZNAME, desc);
        return this;
    }

    public DeviceSelection orderByClzname() {
        orderBy(DeviceColumns.CLZNAME, false);
        return this;
    }

    public DeviceSelection checkbox(Integer... value) {
        addEquals(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceSelection checkboxNot(Integer... value) {
        addNotEquals(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceSelection checkboxGt(int value) {
        addGreaterThan(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceSelection checkboxGtEq(int value) {
        addGreaterThanOrEquals(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceSelection checkboxLt(int value) {
        addLessThan(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceSelection checkboxLtEq(int value) {
        addLessThanOrEquals(DeviceColumns.CHECKBOX, value);
        return this;
    }

    public DeviceSelection orderByCheckbox(boolean desc) {
        orderBy(DeviceColumns.CHECKBOX, desc);
        return this;
    }

    public DeviceSelection orderByCheckbox() {
        orderBy(DeviceColumns.CHECKBOX, false);
        return this;
    }

    public DeviceSelection icoflag(Integer... value) {
        addEquals(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceSelection icoflagNot(Integer... value) {
        addNotEquals(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceSelection icoflagGt(int value) {
        addGreaterThan(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceSelection icoflagGtEq(int value) {
        addGreaterThanOrEquals(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceSelection icoflagLt(int value) {
        addLessThan(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceSelection icoflagLtEq(int value) {
        addLessThanOrEquals(DeviceColumns.ICOFLAG, value);
        return this;
    }

    public DeviceSelection orderByIcoflag(boolean desc) {
        orderBy(DeviceColumns.ICOFLAG, desc);
        return this;
    }

    public DeviceSelection orderByIcoflag() {
        orderBy(DeviceColumns.ICOFLAG, false);
        return this;
    }
}
