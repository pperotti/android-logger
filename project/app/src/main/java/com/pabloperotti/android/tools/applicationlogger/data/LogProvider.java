package com.pabloperotti.android.tools.applicationlogger.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Add entries to a log
 */
public class LogProvider extends ContentProvider {

    // Use it when you need all the items available in the storage
    public static final int ITEMS = 1;
    // Use it when you need to retrieve only 1 item from the storage
    public static final int ITEM = 2;
    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;

    /*
     * Instantiates and sets static objects
     */
    static {
        /*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(DataContract.AUTHORITY, "items", ITEMS);
        sUriMatcher.addURI(DataContract.AUTHORITY, "items/*", ITEM);
    }

    //Database that contains the log entries
    protected LogDatabase mLocalDatabase;
    protected SQLiteDatabase mDB;

    @Override
    public boolean onCreate() {
        //Ensure that the DB is ready to be used
        mLocalDatabase = new LogDatabase(getContext());
        mDB = mLocalDatabase.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                return mDB.query(LogDatabase.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder, null);
            case ITEM:
                String key = uri.getPathSegments().get(DataContract.PATH_ITEM_ID_POSITION);
                if (key != null) {
                    String itemSelection = LogDatabase.COLUMN_ID + " = ?";
                    String[] itemSelectionArgs = {key};
                    return mDB.query(LogDatabase.TABLE_NAME, projection, itemSelection,
                        itemSelectionArgs, null, null, null, null);
                } else {
                    throw new IllegalArgumentException("Bad URI " + uri);
                }

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            // If the pattern is for notes or live folders, returns the general content type.
            case ITEMS:
                return DataContract.CONTENT_TYPE;
            case ITEM:
                return DataContract.CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                long id = mDB.insertOrThrow(LogDatabase.TABLE_NAME, null, values);
                Uri newUri = Uri.withAppendedPath(DataContract.SINGLE_ITEM_CONTENT_URI,
                    String.valueOf(id));
                getContext().getContentResolver().notifyChange(newUri, null);
                return newUri;
            default:
                throw new IllegalArgumentException("Unsupported operation");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                //Delete 1 or more items in the table depending on the selection
                int ret = mDB.delete(LogDatabase.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return ret;
            case ITEM:
                //Delete the item identified by the KEY
                String key = uri.getPathSegments().get(DataContract.PATH_ITEM_ID_POSITION);
                if (key != null) {
                    String where = LogDatabase.COLUMN_ID + "=?";
                    String[] whereArgs = {key};
                    int id = mDB.delete(LogDatabase.TABLE_NAME, where, whereArgs);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return id;
                } else {
                    return 0;
                }
            default:
                throw new IllegalArgumentException("Unsupported operation");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new IllegalArgumentException("Unsupported operation");
    }

    /**
     * Defines the contract for this provider
     */
    public static final class DataContract {

        public static final String SCHEME = "content://";
        public static final String AUTHORITY = "com.pabloperotti.android.tools.log.data.provider";
        public static final String PATH_ITEMS = "/items";
        public static final String PATH_ITEM = "/items/";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_ITEMS);
        public static final Uri SINGLE_ITEM_CONTENT_URI =
            Uri.parse(SCHEME + AUTHORITY + PATH_ITEM);

        public static final int PATH_ITEM_ID_POSITION = 1;

        //Content Type
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.item";
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.google.item";

        // This class cannot be instantiated
        private DataContract() {
        }
    }
}
