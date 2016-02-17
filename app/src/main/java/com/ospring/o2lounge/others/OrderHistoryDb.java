package com.ospring.o2lounge.others;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * Created by Vetero on 24-12-2015.
 */
public class OrderHistoryDb {

    private static final String TAG = OrderHistoryDb.class.getSimpleName();

    // DB Fields
    public static final String KEY_ROWID = "_id";

    // TODO: Setup your fields here:
    public static final String KEY_ORDER_TIME = "order_time";
    public static final String KEY_ORDER_ITEMS = "order_items";
    public static final String KEY_ORDER_PRICE = "order_total";

    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_ORDER_ITEMS, KEY_ORDER_TIME,
            KEY_ORDER_PRICE};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "db_order_history";
    public static final String DATABASE_TABLE = "table_order_history";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 5;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

                    // TODO: Place your fields here!
                    // + KEY_{...} + " {type} not null"
                    //	- Key is the column name you created above.
                    //	- {type} is one of: text, integer, real, blob
                    //		(http://www.sqlite.org/datatype3.html)
                    //  - "not null" means it is a required field (must be given a value).
                    // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
                    + KEY_ORDER_ITEMS + " text not null, "
                    + KEY_ORDER_TIME + " string not null, "
                    + KEY_ORDER_PRICE + " string not null "

                    // Rest  of creation:
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public OrderHistoryDb(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public OrderHistoryDb open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    public long insertRow(List<String> name, String date, int price) {

        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        String s = "";
        for (int i = 0; i < name.size(); i++){
            s = s + String.valueOf(i + 1) + "." + name.get(i) + "\b";
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ORDER_ITEMS, s);
        initialValues.put(KEY_ORDER_PRICE, price);
        initialValues.put(KEY_ORDER_TIME, date);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Cursor getAllRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.


    public void deleteProduct(String itemName) {
        String where = KEY_ORDER_TIME + "= '" + itemName + "'";
        boolean b = db.delete(DATABASE_TABLE, where, null) != 0;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(db);
        }
    }
}