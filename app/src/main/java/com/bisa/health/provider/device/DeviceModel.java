package com.bisa.health.provider.device;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Data model for the {@code device} table.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface DeviceModel extends BaseModel {

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
     * Get the {@code devname} value.
     * Can be {@code null}.
     */
    @Nullable
    String getDevname();

    /**
     * Get the {@code macadderss} value.
     * Can be {@code null}.
     */
    @Nullable
    String getMacadderss();

    /**
     * Get the {@code devnum} value.
     * Can be {@code null}.
     */
    @Nullable
    String getDevnum();

    /**
     * Get the {@code connstatus} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getConnstatus();

    /**
     * Get the {@code clzname} value.
     * Can be {@code null}.
     */
    @Nullable
    String getClzname();

    /**
     * Get the {@code checkbox} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getCheckbox();

    /**
     * Get the {@code icoflag} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getIcoflag();


    @Nullable
    String getCustName();
}
