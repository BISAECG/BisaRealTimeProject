package com.bisa.health.provider.device;

// @formatter:off
import android.net.Uri;
import android.provider.BaseColumns;

import com.bisa.health.provider.BisaHealthProvider;

/**
 * Columns for the {@code device} table.
 */
@SuppressWarnings("unused")
public class DeviceColumns implements BaseColumns {
    public static final String TABLE_NAME = "device";
    public static final Uri CONTENT_URI = Uri.parse(BisaHealthProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String USER_GUID = "user_guid";

    public static final String DEVNAME = "devname";

    public static final String MACADDERSS = "macadderss";

    public static final String DEVNUM = "devnum";

    public static final String CONNSTATUS = "connstatus";

    public static final String CLZNAME = "clzName";

    public static final String CHECKBOX = "checkbox";

    public static final String ICOFLAG = "icoflag";

    public static final String CUSTNAME = "custName";


    public static final String DEFAULT_ORDER = null;

    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_GUID,
            DEVNAME,
            MACADDERSS,
            DEVNUM,
            CONNSTATUS,
            CLZNAME,
            CHECKBOX,
            ICOFLAG,
            CUSTNAME
    };

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_GUID) || c.contains("." + USER_GUID)) return true;
            if (c.equals(DEVNAME) || c.contains("." + DEVNAME)) return true;
            if (c.equals(MACADDERSS) || c.contains("." + MACADDERSS)) return true;
            if (c.equals(DEVNUM) || c.contains("." + DEVNUM)) return true;
            if (c.equals(CONNSTATUS) || c.contains("." + CONNSTATUS)) return true;
            if (c.equals(CLZNAME) || c.contains("." + CLZNAME)) return true;
            if (c.equals(CHECKBOX) || c.contains("." + CHECKBOX)) return true;
            if (c.equals(ICOFLAG) || c.contains("." + ICOFLAG)) return true;
            if (c.equals(CUSTNAME) || c.contains("." + CUSTNAME)) return true;
        }
        return false;
    }

}
