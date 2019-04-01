package com.bisa.health.provider.device;

// @formatter:off
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.bisa.health.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code device} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryLocalVariable"})
public class DeviceCursor extends AbstractCursor implements DeviceModel {
    public DeviceCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        Long res = getLongOrNull(DeviceColumns._ID);
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
        Integer res = getIntegerOrNull(DeviceColumns.USER_GUID);
        return res;
    }

    /**
     * Get the {@code devname} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getDevname() {
        String res = getStringOrNull(DeviceColumns.DEVNAME);
        return res;
    }

    /**
     * Get the {@code macadderss} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getMacadderss() {
        String res = getStringOrNull(DeviceColumns.MACADDERSS);
        return res;
    }

    /**
     * Get the {@code devnum} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getDevnum() {
        String res = getStringOrNull(DeviceColumns.DEVNUM);
        return res;
    }

    /**
     * Get the {@code connstatus} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getConnstatus() {
        Integer res = getIntegerOrNull(DeviceColumns.CONNSTATUS);
        return res;
    }

    /**
     * Get the {@code clzname} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getClzname() {
        String res = getStringOrNull(DeviceColumns.CLZNAME);
        return res;
    }

    /**
     * Get the {@code checkbox} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getCheckbox() {
        Integer res = getIntegerOrNull(DeviceColumns.CHECKBOX);
        return res;
    }

    /**
     * Get the {@code icoflag} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getIcoflag() {
        Integer res = getIntegerOrNull(DeviceColumns.ICOFLAG);
        return res;
    }

    public String getCustName() {
        String res = getStringOrNull(DeviceColumns.CUSTNAME);
        return res;
    }
}
