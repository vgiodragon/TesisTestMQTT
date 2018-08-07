package com.smartcity.giodev.tesistestmqtt.AccessDataBase;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String COLUMN_NAME_NTP = "hora_ntp";
        public static final String COLUMN_HORA_LLEGADA = "hora_llegada";
        public static final String COLUMN_NAME_SUBTITLE = "mensaje";
    }
}

