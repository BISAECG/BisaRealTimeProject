package com.bisa.health.provider.event;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.bisa.health.provider.base.AbstractSelection;

/**
 * Selection for the {@code event} table.
 */
@SuppressWarnings({"unused", "WeakerAccess", "Recycle"})
public class EventSelection extends AbstractSelection<EventSelection> {
    @Override
    protected Uri baseUri() {
        return EventColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code EventCursor} object, which is positioned before the first entry, or null.
     */
    public EventCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new EventCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public EventCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code EventCursor} object, which is positioned before the first entry, or null.
     */
    public EventCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new EventCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public EventCursor query(Context context) {
        return query(context, null);
    }


    public EventSelection id(long... value) {
        addEquals("event." + EventColumns._ID, toObjectArray(value));
        return this;
    }

    public EventSelection idNot(long... value) {
        addNotEquals("event." + EventColumns._ID, toObjectArray(value));
        return this;
    }

    public EventSelection orderById(boolean desc) {
        orderBy("event." + EventColumns._ID, desc);
        return this;
    }

    public EventSelection orderById() {
        return orderById(false);
    }

    public EventSelection userGuid(int... value) {
        addEquals(EventColumns.USER_GUID, toObjectArray(value));
        return this;
    }

    public EventSelection userGuidNot(int... value) {
        addNotEquals(EventColumns.USER_GUID, toObjectArray(value));
        return this;
    }

    public EventSelection userGuidGt(int value) {
        addGreaterThan(EventColumns.USER_GUID, value);
        return this;
    }

    public EventSelection userGuidGtEq(int value) {
        addGreaterThanOrEquals(EventColumns.USER_GUID, value);
        return this;
    }

    public EventSelection userGuidLt(int value) {
        addLessThan(EventColumns.USER_GUID, value);
        return this;
    }

    public EventSelection userGuidLtEq(int value) {
        addLessThanOrEquals(EventColumns.USER_GUID, value);
        return this;
    }

    public EventSelection orderByUserGuid(boolean desc) {
        orderBy(EventColumns.USER_GUID, desc);
        return this;
    }

    public EventSelection orderByUserGuid() {
        orderBy(EventColumns.USER_GUID, false);
        return this;
    }

    public EventSelection eventName(String... value) {
        addEquals(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventSelection eventNameNot(String... value) {
        addNotEquals(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventSelection eventNameLike(String... value) {
        addLike(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventSelection eventNameContains(String... value) {
        addContains(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventSelection eventNameStartsWith(String... value) {
        addStartsWith(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventSelection eventNameEndsWith(String... value) {
        addEndsWith(EventColumns.EVENT_NAME, value);
        return this;
    }

    public EventSelection orderByEventName(boolean desc) {
        orderBy(EventColumns.EVENT_NAME, desc);
        return this;
    }

    public EventSelection orderByEventName() {
        orderBy(EventColumns.EVENT_NAME, false);
        return this;
    }

    public EventSelection eventNum(String... value) {
        addEquals(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventSelection eventNumNot(String... value) {
        addNotEquals(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventSelection eventNumLike(String... value) {
        addLike(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventSelection eventNumContains(String... value) {
        addContains(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventSelection eventNumStartsWith(String... value) {
        addStartsWith(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventSelection eventNumEndsWith(String... value) {
        addEndsWith(EventColumns.EVENT_NUM, value);
        return this;
    }

    public EventSelection orderByEventNum(boolean desc) {
        orderBy(EventColumns.EVENT_NUM, desc);
        return this;
    }

    public EventSelection orderByEventNum() {
        orderBy(EventColumns.EVENT_NUM, false);
        return this;
    }

    public EventSelection eventIndex(Integer... value) {
        addEquals(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventSelection eventIndexNot(Integer... value) {
        addNotEquals(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventSelection eventIndexGt(int value) {
        addGreaterThan(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventSelection eventIndexGtEq(int value) {
        addGreaterThanOrEquals(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventSelection eventIndexLt(int value) {
        addLessThan(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventSelection eventIndexLtEq(int value) {
        addLessThanOrEquals(EventColumns.EVENT_INDEX, value);
        return this;
    }

    public EventSelection orderByEventIndex(boolean desc) {
        orderBy(EventColumns.EVENT_INDEX, desc);
        return this;
    }

    public EventSelection orderByEventIndex() {
        orderBy(EventColumns.EVENT_INDEX, false);
        return this;
    }

    public EventSelection eventType(Integer... value) {
        addEquals(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventSelection eventTypeNot(Integer... value) {
        addNotEquals(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventSelection eventTypeGt(int value) {
        addGreaterThan(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventSelection eventTypeGtEq(int value) {
        addGreaterThanOrEquals(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventSelection eventTypeLt(int value) {
        addLessThan(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventSelection eventTypeLtEq(int value) {
        addLessThanOrEquals(EventColumns.EVENT_TYPE, value);
        return this;
    }

    public EventSelection orderByEventType(boolean desc) {
        orderBy(EventColumns.EVENT_TYPE, desc);
        return this;
    }

    public EventSelection orderByEventType() {
        orderBy(EventColumns.EVENT_TYPE, false);
        return this;
    }

    public EventSelection eventEmail(String... value) {
        addEquals(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventSelection eventEmailNot(String... value) {
        addNotEquals(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventSelection eventEmailLike(String... value) {
        addLike(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventSelection eventEmailContains(String... value) {
        addContains(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventSelection eventEmailStartsWith(String... value) {
        addStartsWith(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventSelection eventEmailEndsWith(String... value) {
        addEndsWith(EventColumns.EVENT_EMAIL, value);
        return this;
    }

    public EventSelection orderByEventEmail(boolean desc) {
        orderBy(EventColumns.EVENT_EMAIL, desc);
        return this;
    }

    public EventSelection orderByEventEmail() {
        orderBy(EventColumns.EVENT_EMAIL, false);
        return this;
    }
}
