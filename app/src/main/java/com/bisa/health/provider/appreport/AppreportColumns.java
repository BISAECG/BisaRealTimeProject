package com.bisa.health.provider.appreport;

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
 * Columns for the {@code appreport} table.
 */
@SuppressWarnings("unused")
public class AppreportColumns implements BaseColumns {
    public static final String TABLE_NAME = "appreport";
    public static final Uri CONTENT_URI = Uri.parse(BisaHealthProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String USER_GUID = "user_guid";

    public static final String REPORT_NUMBER = "report_number";

    public static final String REPORT_TYPE = "report_type";

    public static final String REPORT_STATUS = "report_status";

    public static final String START_TIME = "start_time";

    public static final String YEAR = "year";

    public static final String MONTH = "month";

    public static final String DAY = "day";

    public static final String REPORT_COUNT = "report_count";

    public static final String BODY = "body";

    public static final String ECGDAT = "ecgdat";


    public static final String DEFAULT_ORDER = null;

    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_GUID,
            REPORT_NUMBER,
            REPORT_TYPE,
            REPORT_STATUS,
            START_TIME,
            YEAR,
            MONTH,
            DAY,
            REPORT_COUNT,
            BODY,
            ECGDAT
    };

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_GUID) || c.contains("." + USER_GUID)) return true;
            if (c.equals(REPORT_NUMBER) || c.contains("." + REPORT_NUMBER)) return true;
            if (c.equals(REPORT_TYPE) || c.contains("." + REPORT_TYPE)) return true;
            if (c.equals(REPORT_STATUS) || c.contains("." + REPORT_STATUS)) return true;
            if (c.equals(START_TIME) || c.contains("." + START_TIME)) return true;
            if (c.equals(YEAR) || c.contains("." + YEAR)) return true;
            if (c.equals(MONTH) || c.contains("." + MONTH)) return true;
            if (c.equals(DAY) || c.contains("." + DAY)) return true;
            if (c.equals(REPORT_COUNT) || c.contains("." + REPORT_COUNT)) return true;
            if (c.equals(BODY) || c.contains("." + BODY)) return true;
            if (c.equals(ECGDAT) || c.contains("." + ECGDAT)) return true;
        }
        return false;
    }

}
