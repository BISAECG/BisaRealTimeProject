package com.bisa.health.provider.appreport;

// @formatter:off
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Bean for the {@code appreport} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class AppreportBean implements AppreportModel {
    private long mId;
    private Integer mUserGuid;
    private String mReportNumber;
    private Integer mReportType;
    private Integer mReportStatus;
    private Date mStartTime;
    private Integer mYear;
    private Integer mMonth;
    private Integer mDay;
    private Integer mReportCount;
    private String mBody;
    private String mEcgdat;

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
     * Get the {@code report_number} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getReportNumber() {
        return mReportNumber;
    }

    /**
     * Set the {@code report_number} value.
     * Can be {@code null}.
     */
    public void setReportNumber(@Nullable String reportNumber) {
        mReportNumber = reportNumber;
    }

    /**
     * Get the {@code report_type} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getReportType() {
        return mReportType;
    }

    /**
     * Set the {@code report_type} value.
     * Can be {@code null}.
     */
    public void setReportType(@Nullable Integer reportType) {
        mReportType = reportType;
    }

    /**
     * Get the {@code report_status} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getReportStatus() {
        return mReportStatus;
    }

    /**
     * Set the {@code report_status} value.
     * Can be {@code null}.
     */
    public void setReportStatus(@Nullable Integer reportStatus) {
        mReportStatus = reportStatus;
    }

    /**
     * Get the {@code start_time} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Date getStartTime() {
        return mStartTime;
    }

    /**
     * Set the {@code start_time} value.
     * Can be {@code null}.
     */
    public void setStartTime(@Nullable Date startTime) {
        mStartTime = startTime;
    }

    /**
     * Get the {@code year} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getYear() {
        return mYear;
    }

    /**
     * Set the {@code year} value.
     * Can be {@code null}.
     */
    public void setYear(@Nullable Integer year) {
        mYear = year;
    }

    /**
     * Get the {@code month} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getMonth() {
        return mMonth;
    }

    /**
     * Set the {@code month} value.
     * Can be {@code null}.
     */
    public void setMonth(@Nullable Integer month) {
        mMonth = month;
    }

    /**
     * Get the {@code day} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getDay() {
        return mDay;
    }

    /**
     * Set the {@code day} value.
     * Can be {@code null}.
     */
    public void setDay(@Nullable Integer day) {
        mDay = day;
    }

    /**
     * Get the {@code report_count} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getReportCount() {
        return mReportCount;
    }

    /**
     * Set the {@code report_count} value.
     * Can be {@code null}.
     */
    public void setReportCount(@Nullable Integer reportCount) {
        mReportCount = reportCount;
    }

    /**
     * Get the {@code body} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getBody() {
        return mBody;
    }

    /**
     * Set the {@code body} value.
     * Can be {@code null}.
     */
    public void setBody(@Nullable String body) {
        mBody = body;
    }

    /**
     * Get the {@code ecgdat} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEcgdat() {
        return mEcgdat;
    }

    /**
     * Set the {@code ecgdat} value.
     * Can be {@code null}.
     */
    public void setEcgdat(@Nullable String ecgdat) {
        mEcgdat = ecgdat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppreportBean bean = (AppreportBean) o;
        return mId == bean.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    /**
     * Instantiate a new AppreportBean with specified values.
     */
    @NonNull
    public static AppreportBean newInstance(long id, @Nullable Integer userGuid, @Nullable String reportNumber, @Nullable Integer reportType, @Nullable Integer reportStatus, @Nullable Date startTime, @Nullable Integer year, @Nullable Integer month, @Nullable Integer day, @Nullable Integer reportCount, @Nullable String body, @Nullable String ecgdat) {
        AppreportBean res = new AppreportBean();
        res.mId = id;
        res.mUserGuid = userGuid;
        res.mReportNumber = reportNumber;
        res.mReportType = reportType;
        res.mReportStatus = reportStatus;
        res.mStartTime = startTime;
        res.mYear = year;
        res.mMonth = month;
        res.mDay = day;
        res.mReportCount = reportCount;
        res.mBody = body;
        res.mEcgdat = ecgdat;
        return res;
    }

    /**
     * Instantiate a new AppreportBean with all the values copied from the given model.
     */
    @NonNull
    public static AppreportBean copy(@NonNull AppreportModel from) {
        AppreportBean res = new AppreportBean();
        res.mId = from.getId();
        res.mUserGuid = from.getUserGuid();
        res.mReportNumber = from.getReportNumber();
        res.mReportType = from.getReportType();
        res.mReportStatus = from.getReportStatus();
        res.mStartTime = from.getStartTime();
        res.mYear = from.getYear();
        res.mMonth = from.getMonth();
        res.mDay = from.getDay();
        res.mReportCount = from.getReportCount();
        res.mBody = from.getBody();
        res.mEcgdat = from.getEcgdat();
        return res;
    }

    public static class Builder {
        private AppreportBean mRes = new AppreportBean();

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
         * Set the {@code report_number} value.
         * Can be {@code null}.
         */
        public Builder reportNumber(@Nullable String reportNumber) {
            mRes.mReportNumber = reportNumber;
            return this;
        }

        /**
         * Set the {@code report_type} value.
         * Can be {@code null}.
         */
        public Builder reportType(@Nullable Integer reportType) {
            mRes.mReportType = reportType;
            return this;
        }

        /**
         * Set the {@code report_status} value.
         * Can be {@code null}.
         */
        public Builder reportStatus(@Nullable Integer reportStatus) {
            mRes.mReportStatus = reportStatus;
            return this;
        }

        /**
         * Set the {@code start_time} value.
         * Can be {@code null}.
         */
        public Builder startTime(@Nullable Date startTime) {
            mRes.mStartTime = startTime;
            return this;
        }

        /**
         * Set the {@code year} value.
         * Can be {@code null}.
         */
        public Builder year(@Nullable Integer year) {
            mRes.mYear = year;
            return this;
        }

        /**
         * Set the {@code month} value.
         * Can be {@code null}.
         */
        public Builder month(@Nullable Integer month) {
            mRes.mMonth = month;
            return this;
        }

        /**
         * Set the {@code day} value.
         * Can be {@code null}.
         */
        public Builder day(@Nullable Integer day) {
            mRes.mDay = day;
            return this;
        }

        /**
         * Set the {@code report_count} value.
         * Can be {@code null}.
         */
        public Builder reportCount(@Nullable Integer reportCount) {
            mRes.mReportCount = reportCount;
            return this;
        }

        /**
         * Set the {@code body} value.
         * Can be {@code null}.
         */
        public Builder body(@Nullable String body) {
            mRes.mBody = body;
            return this;
        }

        /**
         * Set the {@code ecgdat} value.
         * Can be {@code null}.
         */
        public Builder ecgdat(@Nullable String ecgdat) {
            mRes.mEcgdat = ecgdat;
            return this;
        }

        /**
         * Get a new AppreportBean built with the given values.
         */
        public AppreportBean build() {
            return mRes;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
