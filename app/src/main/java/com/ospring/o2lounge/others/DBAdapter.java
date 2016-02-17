package com.ospring.o2lounge.others;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vetero on 03-10-2015.
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    // TODO: Setup your fields here:
    public static final String KEY_NAME = "item_name";
    public static final String KEY_PRICE = "item_price";
    public static final String KEY_COUNT = "item_count";
    public static final String KEY_IMG = "item_image";

    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_NAME = 1;
    public static final int COL_PRICE = 2;
    public static final int COL_COUNT = 3;
    public static final int COL_IMG = 4;


    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_NAME, KEY_PRICE,
            KEY_COUNT, KEY_IMG};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "db_cart";
    public static final String DATABASE_TABLE = "table_cart";
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
                    + KEY_NAME + " text unique not null, "
                    + KEY_PRICE + " string not null, "
                    + KEY_COUNT + " string not null, "
                    + KEY_IMG + " integer not null "

                    // Rest  of creation:
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    public long insertRow(String name, int price, int count, int img) {

        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_COUNT, count);
        initialValues.put(KEY_IMG, img);

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
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                null, null, null, null, null, null, null);
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
    public boolean updateRow(String name, String count) {
        String where = KEY_NAME + "= '" + name + "'";

        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_COUNT, count);
        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    public String getSingleEntry(String name) {
        String where = KEY_NAME + " = '" + name + "'";
        Cursor cursor = db.query(true, DATABASE_TABLE, new String[]{KEY_COUNT}, where, null, null,
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "0";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_COUNT));
        cursor.close();
        return password;
    }

    public void deleteProduct(String itemName) {
        String where = KEY_NAME + "= '" + itemName + "'";
        boolean b = db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public String getGrandTotal() {
        Cursor cursor = db.rawQuery("Select " + KEY_PRICE + " , " + KEY_COUNT + " from " +
                DATABASE_TABLE, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            do {
                int s = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_PRICE)));
                int i = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_COUNT)));
                total = total + (s * i);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return String.valueOf(total);
    }

    public String getCartCount() {
        String countQuery = "SELECT  * FROM " + DATABASE_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return String.valueOf(cnt);
    }

    public boolean removeProduct(String s) {
        return db.delete(DATABASE_TABLE, KEY_NAME + "= '" + s + "'", null) > 0;
    }

    public List<String> getOrderItems() {
        Cursor cursor = db.rawQuery("Select " + KEY_NAME + " from " + DATABASE_TABLE, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
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