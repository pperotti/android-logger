package com.pabloperotti.android.tools.applicationlogger.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Store all collected data until Sync process occur
 */
public class LogDatabase extends SQLiteOpenHelper {

    private static final String TAG = LogDatabase.class.getSimpleName();
    public static final String TABLE_NAME = "entries";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_ID = "_id";

    public static final String DATABASE_NAME = "log.sqlite";

    private static final int CUR_DATABASE_VERSION = 1;

    private static final String STATEMENT_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String STATEMENT_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TIMESTAMP + " DATETIME, "
            + COLUMN_TAG + " TEXT,"
            + COLUMN_VALUE + " TEXT)";

    private static final String INDEX_TS_TAG = "CREATE UNIQUE INDEX IF NOT EXISTS IDX_TS_TAG "
            + "on " + TABLE_NAME + " (" + COLUMN_TIMESTAMP + ", " + COLUMN_TAG + ")";
    private static final String INDEX_TAG = "CREATE UNIQUE INDEX IF NOT EXISTS IDX_TAG "
            + "on " + TABLE_NAME + " (" + COLUMN_TIMESTAMP + ")";
    private static final String INDEX_ID = "CREATE UNIQUE INDEX IF NOT EXISTS IDX_ID "
            + "on " + TABLE_NAME + " (" + COLUMN_ID + ")";

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public LogDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STATEMENT_DROP);
        db.execSQL(STATEMENT_CREATE);
        db.execSQL(INDEX_TS_TAG);
        db.execSQL(INDEX_TAG);
        db.execSQL(INDEX_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade to the new version when new data is available.
        // There is no need to implement this method by default.
    }
}
