package com.bisa.health.provider.device;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Bean for the {@code device} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class DeviceBean implements DeviceModel {
    private long mId;
    private Integer mUserGuid;
    private String mDevname;
    private String mMacadderss;
    private String mDevnum;
    private Integer mConnstatus;
    private String mClzname;
    private Integer mCheckbox;
    private Integer mIcoflag;
    private String mCustName;

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
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getUserGuid() {
        return mUserGuid;
    }

    /**
     * Set the {@code user_guid} value.
     * Can be {@code null}.
     */
    public void setUserGuid(@Nullable Integer userGuid) {
        mUserGuid = userGuid;
    }

    /**
     * Get the {@code devname} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getDevname() {
        return mDevname;
    }

    /**
     * Set the {@code devname} value.
     * Can be {@code null}.
     */
    public void setDevname(@Nullable String devname) {
        mDevname = devname;
    }

    /**
     * Get the {@code macadderss} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getMacadderss() {
        return mMacadderss;
    }

    /**
     * Set the {@code macadderss} value.
     * Can be {@code null}.
     */
    public void setMacadderss(@Nullable String macadderss) {
        mMacadderss = macadderss;
    }

    /**
     * Get the {@code devnum} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getDevnum() {
        return mDevnum;
    }

    /**
     * Set the {@code devnum} value.
     * Can be {@code null}.
     */
    public void setDevnum(@Nullable String devnum) {
        mDevnum = devnum;
    }

    /**
     * Get the {@code connstatus} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getConnstatus() {
        return mConnstatus;
    }

    /**
     * Set the {@code connstatus} value.
     * Can be {@code null}.
     */
    public void setConnstatus(@Nullable Integer connstatus) {
        mConnstatus = connstatus;
    }

    /**
     * Get the {@code clzname} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getClzname() {
        return mClzname;
    }

    /**
     * Set the {@code clzname} value.
     * Can be {@code null}.
     */
    public void setClzname(@Nullable String clzname) {
        mClzname = clzname;
    }

    /**
     * Get the {@code checkbox} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getCheckbox() {
        return mCheckbox;
    }

    /**
     * Set the {@code checkbox} value.
     * Can be {@code null}.
     */
    public void setCheckbox(@Nullable Integer checkbox) {
        mCheckbox = checkbox;
    }

    /**
     * Get the {@code icoflag} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getIcoflag() {
        return mIcoflag;
    }

    /**
     * Set the {@code icoflag} value.
     * Can be {@code null}.
     */
    public void setIcoflag(@Nullable Integer icoflag) {
        mIcoflag = icoflag;
    }


    @Nullable
    @Override
    public String getCustName() {
        return mCustName;
    }

    /**
     * Set the {@code devname} value.
     * Can be {@code null}.
     */
    public void setCustName(@Nullable String custName) {
        mCustName = custName;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceBean bean = (DeviceBean) o;
        return mId == bean.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    /**
     * Instantiate a new DeviceBean with specified values.
     */
    @NonNull
    public static DeviceBean newInstance(long id, @Nullable Integer userGuid, @Nullable String devname, @Nullable String macadderss, @Nullable String devnum, @Nullable Integer connstatus, @Nullable String clzname, @Nullable Integer checkbox, @Nullable Integer icoflag, String custName) {
        DeviceBean res = new DeviceBean();
        res.mId = id;
        res.mUserGuid = userGuid;
        res.mDevname = devname;
        res.mMacadderss = macadderss;
        res.mDevnum = devnum;
        res.mConnstatus = connstatus;
        res.mClzname = clzname;
        res.mCheckbox = checkbox;
        res.mIcoflag = icoflag;
        res.mCustName = custName;
        return res;
    }

    /**
     * Instantiate a new DeviceBean with all the values copied from the given model.
     */
    @NonNull
    public static DeviceBean copy(@NonNull DeviceModel from) {
        DeviceBean res = new DeviceBean();
        res.mId = from.getId();
        res.mUserGuid = from.getUserGuid();
        res.mDevname = from.getDevname();
        res.mMacadderss = from.getMacadderss();
        res.mDevnum = from.getDevnum();
        res.mConnstatus = from.getConnstatus();
        res.mClzname = from.getClzname();
        res.mCheckbox = from.getCheckbox();
        res.mIcoflag = from.getIcoflag();
        res.mCustName = from.getCustName();
        return res;
    }

    public static class Builder {
        private DeviceBean mRes = new DeviceBean();

        /**
         * Primary key.
         */
        public Builder id(long id) {
            mRes.mId = id;
            return this;
        }

        /**
         * Set the {@code user_guid} value.
         * Can be {@code null}.
         */
        public Builder userGuid(@Nullable Integer userGuid) {
            mRes.mUserGuid = userGuid;
            return this;
        }

        /**
         * Set the {@code devname} value.
         * Can be {@code null}.
         */
        public Builder devname(@Nullable String devname) {
            mRes.mDevname = devname;
            return this;
        }

        /**
         * Set the {@code macadderss} value.
         * Can be {@code null}.
         */
        public Builder macadderss(@Nullable String macadderss) {
            mRes.mMacadderss = macadderss;
            return this;
        }

        /**
         * Set the {@code devnum} value.
         * Can be {@code null}.
         */
        public Builder devnum(@Nullable String devnum) {
            mRes.mDevnum = devnum;
            return this;
        }

        /**
         * Set the {@code connstatus} value.
         * Can be {@code null}.
         */
        public Builder connstatus(@Nullable Integer connstatus) {
            mRes.mConnstatus = connstatus;
            return this;
        }

        /**
         * Set the {@code clzname} value.
         * Can be {@code null}.
         */
        public Builder clzname(@Nullable String clzname) {
            mRes.mClzname = clzname;
            return this;
        }

        /**
         * Set the {@code checkbox} value.
         * Can be {@code null}.
         */
        public Builder checkbox(@Nullable Integer checkbox) {
            mRes.mCheckbox = checkbox;
            return this;
        }

        /**
         * Set the {@code icoflag} value.
         * Can be {@code null}.
         */
        public Builder icoflag(@Nullable Integer icoflag) {
            mRes.mIcoflag = icoflag;
            return this;
        }

        public Builder custName(@Nullable String custName) {
            mRes.mCustName = custName;
            return this;
        }

        /**
         * Get a new DeviceBean built with the given values.
         */
        public DeviceBean build() {
            return mRes;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
