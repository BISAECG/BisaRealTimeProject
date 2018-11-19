package com.bisa.health.provider.event;

// @formatter:off
import com.bisa.health.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Bean for the {@code event} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class EventBean implements EventModel {
    private long mId;
    private int mUserGuid;
    private String mEventName;
    private String mEventNum;
    private Integer mEventIndex;
    private Integer mEventType;
    private String mEventEmail;

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
     * Get the {@code event_name} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEventName() {
        return mEventName;
    }

    /**
     * Set the {@code event_name} value.
     * Can be {@code null}.
     */
    public void setEventName(@Nullable String eventName) {
        mEventName = eventName;
    }

    /**
     * Get the {@code event_num} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEventNum() {
        return mEventNum;
    }

    /**
     * Set the {@code event_num} value.
     * Can be {@code null}.
     */
    public void setEventNum(@Nullable String eventNum) {
        mEventNum = eventNum;
    }

    /**
     * Get the {@code event_index} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getEventIndex() {
        return mEventIndex;
    }

    /**
     * Set the {@code event_index} value.
     * Can be {@code null}.
     */
    public void setEventIndex(@Nullable Integer eventIndex) {
        mEventIndex = eventIndex;
    }

    /**
     * Get the {@code event_type} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Integer getEventType() {
        return mEventType;
    }

    /**
     * Set the {@code event_type} value.
     * Can be {@code null}.
     */
    public void setEventType(@Nullable Integer eventType) {
        mEventType = eventType;
    }

    /**
     * Get the {@code event_email} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public String getEventEmail() {
        return mEventEmail;
    }

    /**
     * Set the {@code event_email} value.
     * Can be {@code null}.
     */
    public void setEventEmail(@Nullable String eventEmail) {
        mEventEmail = eventEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBean bean = (EventBean) o;
        return mId == bean.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    /**
     * Instantiate a new EventBean with specified values.
     */
    @NonNull
    public static EventBean newInstance(long id, int userGuid, @Nullable String eventName, @Nullable String eventNum, @Nullable Integer eventIndex, @Nullable Integer eventType, @Nullable String eventEmail) {
        EventBean res = new EventBean();
        res.mId = id;
        res.mUserGuid = userGuid;
        res.mEventName = eventName;
        res.mEventNum = eventNum;
        res.mEventIndex = eventIndex;
        res.mEventType = eventType;
        res.mEventEmail = eventEmail;
        return res;
    }

    /**
     * Instantiate a new EventBean with all the values copied from the given model.
     */
    @NonNull
    public static EventBean copy(@NonNull EventModel from) {
        EventBean res = new EventBean();
        res.mId = from.getId();
        res.mUserGuid = from.getUserGuid();
        res.mEventName = from.getEventName();
        res.mEventNum = from.getEventNum();
        res.mEventIndex = from.getEventIndex();
        res.mEventType = from.getEventType();
        res.mEventEmail = from.getEventEmail();
        return res;
    }

    public static class Builder {
        private EventBean mRes = new EventBean();

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
         * Set the {@code event_name} value.
         * Can be {@code null}.
         */
        public Builder eventName(@Nullable String eventName) {
            mRes.mEventName = eventName;
            return this;
        }

        /**
         * Set the {@code event_num} value.
         * Can be {@code null}.
         */
        public Builder eventNum(@Nullable String eventNum) {
            mRes.mEventNum = eventNum;
            return this;
        }

        /**
         * Set the {@code event_index} value.
         * Can be {@code null}.
         */
        public Builder eventIndex(@Nullable Integer eventIndex) {
            mRes.mEventIndex = eventIndex;
            return this;
        }

        /**
         * Set the {@code event_type} value.
         * Can be {@code null}.
         */
        public Builder eventType(@Nullable Integer eventType) {
            mRes.mEventType = eventType;
            return this;
        }

        /**
         * Set the {@code event_email} value.
         * Can be {@code null}.
         */
        public Builder eventEmail(@Nullable String eventEmail) {
            mRes.mEventEmail = eventEmail;
            return this;
        }

        /**
         * Get a new EventBean built with the given values.
         */
        public EventBean build() {
            return mRes;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
