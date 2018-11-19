package com.bisa.health.provider.upload;

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
 * Columns for the {@code upload} table.
 */
@SuppressWarnings("unused")
public class UploadColumns implements BaseColumns {
    public static final String TABLE_NAME = "upload";
    public static final Uri CONTENT_URI = Uri.parse(BisaHealthProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String USER_GUID = "user_guid";

    public static final String FILENAME = "filename";

    public static final String UPTIME = "uptime";


    public static final String DEFAULT_ORDER = null;

    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_GUID,
            FILENAME,
            UPTIME
    };

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_GUID) || c.contains("." + USER_GUID)) return true;
            if (c.equals(FILENAME) || c.contains("." + FILENAME)) return true;
            if (c.equals(UPTIME) || c.contains("." + UPTIME)) return true;
        }
        return false;
    }

}
