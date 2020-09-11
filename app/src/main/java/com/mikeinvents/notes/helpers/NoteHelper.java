package com.mikeinvents.notes.helpers;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mikeinvents.notes.models.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NoteHelper extends SQLiteOpenHelper {
    //private static final String TAG = NoteHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    public static final String NOTES_TABLE = "notes_entries";
    private static final String DATABASE_NAME = "notes_db";

    //column names
    public static final String KEY_ID = "_id";
    private static final String TITLE = "title";
    private static final String DETAILS = "details";
    private static final String DATE = "date";

    //String array of columns...
   // private static final String[] COLUMNS = {KEY_ID, TITLE, DETAILS, DATE};

    //SQL query that creates table
    private static final String NOTES_TABLE_CREATE =
            "CREATE TABLE " + NOTES_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    DETAILS + " TEXT, " +
                    DATE + " TEXT );";

    //for database referencing...
    public SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public NoteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       // Log.i(TAG, "onCreate: NotesHelper = noteHelper onCreate");
        db.execSQL(NOTES_TABLE_CREATE);
        fillDatabaseWithSample(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.w(TAG,
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
        onCreate(db);
    }

    /**
     * Used to fill database with sample data
     */
    private void fillDatabaseWithSample(SQLiteDatabase db) {
        String title = "You can add a Title";
        String details = "Details";
        String date = getDate();


        //container for the data
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(DETAILS, details);
        contentValues.put(DATE, date);

        //Log.i(TAG, "TITLE = " + title + " DETAILS = " + details + "Date = " + DATE);


        db.insert(NOTES_TABLE, null, contentValues);
    }

    /**
     * Returns the date of the local device
     */
    public String getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df1 = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat df3 = new SimpleDateFormat("yyyy", Locale.getDefault());
        HashMap<String, String> map = new HashMap<>();
        map.put("01", "Jan");
        map.put("02", "Feb");
        map.put("03", "Mar");
        map.put("04", "Apr");
        map.put("05", "May");
        map.put("06", "Jun");
        map.put("07", "Jul");
        map.put("08", "Aug");
        map.put("09", "Sep");
        map.put("10", "Oct");
        map.put("11", "Nov");
        map.put("12", "Dec");

        String key = df2.format(date);
        String theVal = map.get(key);

        return df1.format(date) + " " + theVal + " " + df3.format(date);
    }

    /**
     * Returns a Note Object from the database
     */
    public Note query(int position) {
        String query = "SELECT * FROM " + NOTES_TABLE +
                " ORDER BY " + KEY_ID + " DESC " +
                "LIMIT " + position + ",1";

        Cursor cursor = null;

        Note aNote = new Note();
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            cursor = mReadableDB.rawQuery(query, null); //execute query
            //Log.i(TAG, "query: Cursor Result" + cursor.toString());

            cursor.moveToFirst();
            aNote.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            aNote.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
            aNote.setDetails(cursor.getString(cursor.getColumnIndex(DETAILS)));
            aNote.setDate(cursor.getString(cursor.getColumnIndex(DATE)));

        } catch (Exception e) {
            System.out.println( "QUERY EXCEPTION! " + e.getMessage());
            //Log.i(TAG, "QUERY EXCEPTION! " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();

            }
        }
        return aNote;
    }

    /**
     * Used to insert into the database and returns an id
     */
    public long insert(String title, String details, String date) {
        long newId = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(DETAILS, details);
        contentValues.put(DATE, date);

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(NOTES_TABLE, null, contentValues);
        } catch (Exception e) {
            System.out.println( "INSERT EXCEPTION! " + e.getMessage());
        }

        return newId;
    }

    /**
     * Returns a the no. of items in the database
     */
    public long count() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        return DatabaseUtils.queryNumEntries(mReadableDB, NOTES_TABLE);
    }

    /**
     * Used to update the database and returns num of rows updated which is 1 if things go well :)
     */
    public int update(int id, String title, String details, String date) {
        int mNumOfRowsUpdated = -1;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(TITLE, title);
            contentValues.put(DETAILS, details);
            contentValues.put(DATE, date);

            mNumOfRowsUpdated = mWritableDB.update(NOTES_TABLE, contentValues,
                    KEY_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mNumOfRowsUpdated; //(-1) => update failed, (0) => nothing happened (1) => success
    }

    /**
     * Returns max no. of entries in TABlE
     */
    public int maxID() {
        String query = "SELECT max(" + KEY_ID + ") " + "FROM " + NOTES_TABLE;
        int max_id = -99;
        Cursor cursor = null;

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            cursor = mReadableDB.rawQuery(query, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    max_id = cursor.getInt(0);
                    cursor.moveToNext();
                }
            }


        } catch (Exception e) {
            System.out.println( "MAXID: AN EXCEPTION OCCURRED = " + e.getMessage());
//            Log.i(TAG, "MAXID: AN EXCEPTION OCCURRED =" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return max_id;
    }
}
