package com.bisa.health.provider.upload;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Bean for the {@code upload} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class UploadBean implements UploadModel {
    private long mId;
    private int mUserGuid;
    private String mFilename;
    private String mUptime;

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        return mId;
    }

    /**
     * Primary key.
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     * Get the {@code user_guid} value.
     */
    @Override
    public int getUserGuid() {
        return mUserGuid;
    }

    /**
     * Set the {@code user_guid} value.
     */
    public void setUserGuid(int userGuid) {
        mUserGuid = userGuid;
    }

    /**
     * Get the {@code filename} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getFilename() {
        return mFilename;
    }

    /**
     * Set the {@code filename} value.
     * Can be {@code null}.
     */
    public void setFilename(@Nullable String filename) {
        mFilename = filename;
    }

    /**
     * Get the {@code uptime} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getUptime() {
        return mUptime;
    }

    /**
     * Set the {@code uptime} value.
     * Can be {@code null}.
     */
    public void setUptime(@Nullable String uptime) {
        mUptime = uptime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadBean bean = (UploadBean) o;
        return mId == bean.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    /**
     * Instantiate a new UploadBean with specified values.
     */
    @NonNull
    public static UploadBean newInstance(long id, int userGuid, @Nullable String filename, @Nullable String uptime) {
        UploadBean res = new UploadBean();
        res.mId = id;
        res.mUserGuid = userGuid;
        res.mFilename = filename;
        res.mUptime = uptime;
        return res;
    }

    /**
     * Instantiate a new UploadBean with all the values copied from the given model.
     */
    @NonNull
    public static UploadBean copy(@NonNull UploadModel from) {
        UploadBean res = new UploadBean();
        res.mId = from.getId();
        res.mUserGuid = from.getUserGuid();
        res.mFilename = from.getFilename();
        res.mUptime = from.getUptime();
        return res;
    }

    public static class Builder {
        private UploadBean mRes = new UploadBean();

        /**
         * Primary key.
         */
        public Builder id(long id) {
            mRes.mId = id;
            return this;
        }

        /**
         * Set the {@code user_guid} value.
         */
        public Builder userGuid(int userGuid) {
            mRes.mUserGuid = userGuid;
            return this;
        }

        /**
         * Set the {@code filename} value.
         * Can be {@code null}.
         */
        public Builder filename(@Nullable String filename) {
            mRes.mFilename = filename;
            return this;
        }

        /**
         * Set the {@code uptime} value.
         * Can be {@code null}.
         */
        public Builder uptime(@Nullable String uptime) {
            mRes.mUptime = uptime;
            return this;
        }

        /**
         * Get a new UploadBean built with the given values.
         */
        public UploadBean build() {
            return mRes;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
