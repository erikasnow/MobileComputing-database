package wpi.essnow.project3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class LogContract {
    private LogContract() {}

    public static class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "logEntries";
        //public static final String COLUMN_NAME_ENTRY = "entry"; //for part 1
        // add image path and temperature from Project 2
        public static final String IMAGE_ENTRY = "image";
        public static final String TEMP_ENTRY = "temperature";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LogEntry.TABLE_NAME + " (" +
                    LogEntry._ID + " INTEGER PRIMARY KEY," +
                    /*LogEntry.COLUMN_NAME_ENTRY + " TEXT," +*/ //for part 1
                    LogEntry.IMAGE_ENTRY + " TEXT," +  // add image path and temperatures from Project 2
                    LogEntry.TEMP_ENTRY + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LogEntry.TABLE_NAME;

    public static class LogEntryDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 3; //change to 2 for Project 3
        public static final String DATABASE_NAME = "LogEntries.db";

        public LogEntryDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }
}
