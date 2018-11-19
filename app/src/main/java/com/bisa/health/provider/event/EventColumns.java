package com.bisa.health.provider.event;

// @formatter:off
import android.net.Uri;
import android.provider.BaseColumns;

import com.bisa.health.provider.BisaHealthProvider;
import com.bisa.health.provider.base.AbstractSelection;
import com.bisa.health.provider.appreport.AppreportColumns;
import com.bisa.health.provider.device.DeviceColumns;
import com.bisa.health.provider.event.EventColumns;
import com.bisa.health.provider.upload.UploadColumns;

/**
 * Columns for the {@code event} table.
 */
@SuppressWarnings("unused")
public class EventColumns implements BaseColumns {
    public static final String TABLE_NAME = "event";
    public static final Uri CONTENT_URI = Uri.parse(BisaHealthProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String USER_GUID = "user_guid";

    public static final String EVENT_NAME = "event_name";

    public static final String EVENT_NUM = "event_num";

    public static final String EVENT_INDEX = "event_index";

    public static final String EVENT_TYPE = "event_type";

    public static final String EVENT_EMAIL = "event_email";


    public static final String DEFAULT_ORDER = null;

    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_GUID,
            EVENT_NAME,
            EVENT_NUM,
            EVENT_INDEX,
            EVENT_TYPE,
            EVENT_EMAIL
    };

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_GUID) || c.contains("." + USER_GUID)) return true;
            if (c.equals(EVENT_NAME) || c.contains("." + EVENT_NAME)) return true;
            if (c.equals(EVENT_NUM) || c.contains("." + EVENT_NUM)) return true;
            if (c.equals(EVENT_INDEX) || c.contains("." + EVENT_INDEX)) return true;
            if (c.equals(EVENT_TYPE) || c.contains("." + EVENT_TYPE)) return true;
            if (c.equals(EVENT_EMAIL) || c.contains("." + EVENT_EMAIL)) return true;
        }
        return false;
    }

}
