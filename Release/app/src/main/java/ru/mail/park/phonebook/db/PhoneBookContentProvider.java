package ru.mail.park.phonebook.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;


public class PhoneBookContentProvider extends ContentProvider {

    final String LOG_TAG = "myLogs";

    private static final String DB_NAME = "phone_book_sqllite_db";
    private static final int DB_VERSION = 1;
    private static final String CATEGORY_TABLE = "category_table";
    private static final String CONTACT_TABLE = "contact_table";
    private static final String LINK_TABLE = "link_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";

    public static final String COLUMN_PHONE_NUMBER = "phone_number";

    public static final String COLUMN_CATEGORY_ID = "categoty_id";
    public static final String COLUMN_CONTACT_ID = "contact_id";

    private static final String DEFAULT_CATEGORY_VALUE = ".......";

    private static final String CREATE_CATEGORY_TABLE =
            "create table " + CATEGORY_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " text, " +
                    COLUMN_COLOR + " integer " +
                    ");";

    private static final String CREATE_CONTACT_TABLE =
            "create table " + CONTACT_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " text, " +
                    COLUMN_PHONE_NUMBER + " text " +
                    ");";

    private static final String CREATE_LINK_TABLE =
            "create table " + LINK_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_CONTACT_ID + " integer, " +
                    COLUMN_CATEGORY_ID + " integer,  " +
                    "foreign key(" + COLUMN_CONTACT_ID + ") REFERENCES "
                        + CONTACT_TABLE + "(" + COLUMN_ID + ")," +
                    "foreign key(" + COLUMN_CATEGORY_ID + ") REFERENCES "
                    + CATEGORY_TABLE + "(" + COLUMN_ID + ") " +
                    ");";

    // authority
    static final String AUTHORITY = "ru.mail.park.phonebook.db.PhoneBookContentProvider";

    // path
    static final String CATEGORY_SOURCE_PATH = "category_source";
    static final String CONTACT_SOURCE_PATH = "contact_source";
    static final String CONTACT_SOURCE_BY_CATEGORY_PATH = CONTACT_SOURCE_PATH + "_by_category";
    static final String LINK_SOURCE_PATH = "link_source";

    // Общий Uri
    public static final Uri CATEGORY_SOURCE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CATEGORY_SOURCE_PATH);
    public static final Uri CONTACT_SOURCE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CONTACT_SOURCE_PATH);
    public static final Uri CONTACT_SOURCE_BY_CATEGORY_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CONTACT_SOURCE_BY_CATEGORY_PATH);
    public static final Uri LINK_SOURCE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + LINK_SOURCE_PATH);

    // набор строк
    static final String CATEGORY_SOURCE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + CATEGORY_SOURCE_PATH;
    static final String CONTACT_SOURCE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + CONTACT_SOURCE_PATH;
    static final String LINK_SOURCE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + LINK_SOURCE_PATH;

    // одна строка
    static final String CATEGOTY_SOURCE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + CATEGORY_SOURCE_PATH;
    static final String CONTACT_SOURCE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + CONTACT_SOURCE_PATH;
    static final String LINK_SOURCE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + LINK_SOURCE_PATH;


    // общий Uri
    static final int URI_CATEGORY_SOURCES = 1;
    static final int URI_CONTACT_SOURCES = 3;
    static final int URI_LINK_SOURCES = 5;
    static final int URI_CONTACT_SOURCES_BY_CATEGORY = 7;
    // Uri с указанным ID
    static final int URI_CATEGORY_SOURCE_ID = 2;
    static final int URI_CONTACT_SOURCE_ID = 4;
    static final int URI_LINK_SOURCE_ID = 6;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CATEGORY_SOURCE_PATH, URI_CATEGORY_SOURCES);
        uriMatcher.addURI(AUTHORITY, CATEGORY_SOURCE_PATH + "/#", URI_CATEGORY_SOURCE_ID);
        uriMatcher.addURI(AUTHORITY, CONTACT_SOURCE_PATH, URI_CONTACT_SOURCES);
        uriMatcher.addURI(AUTHORITY, CONTACT_SOURCE_BY_CATEGORY_PATH, URI_CONTACT_SOURCES_BY_CATEGORY);
        uriMatcher.addURI(AUTHORITY, CONTACT_SOURCE_PATH + "/#", URI_CONTACT_SOURCE_ID);
        uriMatcher.addURI(AUTHORITY, LINK_SOURCE_PATH, URI_LINK_SOURCES);
        uriMatcher.addURI(AUTHORITY, LINK_SOURCE_PATH + "/#", URI_LINK_SOURCE_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    // Данные для демонстации

    private static final String str1 = "INSERT INTO " + CATEGORY_TABLE + " VALUES ('2', 'Cемья', '" + String.valueOf(Color.RED) + "');";
    private static final String str2 = "INSERT INTO " + CATEGORY_TABLE + " VALUES ('3', 'Друзья', '" + String.valueOf(Color.GREEN) + "');";
    private static final String str3 = "INSERT INTO " + CATEGORY_TABLE + " VALUES ('4', 'Работа', '" + String.valueOf(Color.CYAN) + "');";

    private static final String str4 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('1', 'Иван Колесниченко', '8-918-56-71-871');";
    private static final String str5= "INSERT INTO " + CONTACT_TABLE + " VALUES ('2', 'Павлуша Сидоров', '8-611-51-51-132');";
    private static final String str6 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('3', 'Марья Петровна', '1-9355-1-77-234');";
    private static final String str7 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('4', 'Газпром', '8-456-5456-12-8');";
    private static final String str8 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('5', 'Кукольный театр', '2515114');";
    private static final String str9 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('6', 'Прудников Станислав', '83686527634');";
    private static final String str10 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('7', 'Полина Гагарина', '8-918-56-71-871');";
    private static final String str11 = "INSERT INTO " + CONTACT_TABLE + " VALUES ('8', 'Белый Мишка', '8-918-5226-21-888');";

    private static final String str12 = "INSERT INTO " + LINK_TABLE + " VALUES ('1', '1', '2');";
    private static final String str13 = "INSERT INTO " + LINK_TABLE + " VALUES ('2', '1', '4');";
    private static final String str14 = "INSERT INTO " + LINK_TABLE + " VALUES ('3', '2', '2');";
    private static final String str15 = "INSERT INTO " + LINK_TABLE + " VALUES ('4', '2', '3');";
    private static final String str16 = "INSERT INTO " + LINK_TABLE + " VALUES ('5', '3', '2');";
    private static final String str17 = "INSERT INTO " + LINK_TABLE + " VALUES ('6', '5', '3');";
    private static final String str18 = "INSERT INTO " + LINK_TABLE + " VALUES ('7', '6', '2');";
    private static final String str19 = "INSERT INTO " + LINK_TABLE + " VALUES ('8', '6', '2');";
    private static final String str20 = "INSERT INTO " + LINK_TABLE + " VALUES ('9', '6', '3');";
    private static final String str21 = "INSERT INTO " + LINK_TABLE + " VALUES ('10', '5', '2');";
    private static final String str22 = "INSERT INTO " + LINK_TABLE + " VALUES ('11', '7', '3');";
    private static final String str23 = "INSERT INTO " + LINK_TABLE + " VALUES ('12', '8', '4');";
    private static final String str24 = "INSERT INTO " + LINK_TABLE + " VALUES ('13', '8', '2');";
    private static final String str25 = "INSERT INTO " + LINK_TABLE + " VALUES ('14', '9', '3');";
    private static final String str26 = "INSERT INTO " + LINK_TABLE + " VALUES ('15', '9', '4');";


//    INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country)
//    VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CATEGORY_TABLE);
            ContentValues cvh = new ContentValues();
            cvh.put(COLUMN_NAME, DEFAULT_CATEGORY_VALUE);
            cvh.put(COLUMN_COLOR, Color.WHITE);
            db.insert(CATEGORY_TABLE, null, cvh);
            db.execSQL(CREATE_CONTACT_TABLE);
            db.execSQL(CREATE_LINK_TABLE);

            // Заполнение БД тестовыми данными
            db.execSQL(str1);
            db.execSQL(str2);
            db.execSQL(str3);
            db.execSQL(str4);
            db.execSQL(str5);
            db.execSQL(str6);
            db.execSQL(str7);
            db.execSQL(str8);
            db.execSQL(str9);
            db.execSQL(str10);
            db.execSQL(str11);
            db.execSQL(str12);
            db.execSQL(str13);
            db.execSQL(str14);
            db.execSQL(str15);
            db.execSQL(str16);
            db.execSQL(str17);
            db.execSQL(str18);
            db.execSQL(str19);
            db.execSQL(str20);
            db.execSQL(str21);
            db.execSQL(str22);
            db.execSQL(str23);
            db.execSQL(str24);
            db.execSQL(str25);
            db.execSQL(str26);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = CATEGORY_TABLE;
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case URI_CATEGORY_SOURCES:
                table = CATEGORY_TABLE;
                break;
            case URI_CATEGORY_SOURCE_ID:
                break;
            case URI_LINK_SOURCES:
                table = LINK_TABLE;
                break;
            case URI_CONTACT_SOURCES_BY_CATEGORY:
                String sqlQuery =
                        "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_PHONE_NUMBER +
                        " FROM " + CONTACT_TABLE +
                        " WHERE " + COLUMN_ID + " IN ( SELECT " + COLUMN_CONTACT_ID + " FROM " +
                                LINK_TABLE + " WHERE " + COLUMN_CATEGORY_ID + " = ?);";
//                String sqlQuery = "SELECT " +
//                        CONTACT_TABLE + "." + COLUMN_ID + ", " +
//                        CONTACT_TABLE + "." + COLUMN_NAME + ", " +
//                        CONTACT_TABLE + "." + COLUMN_PHONE_NUMBER +
//                        " FROM " +
//                        CONTACT_TABLE + " LEFT OUTER JOIN " +
//                        LINK_TABLE + " ON " +
//                        CONTACT_TABLE + "." + COLUMN_ID + " = " + LINK_TABLE + "." + COLUMN_CONTACT_ID
//                        + " WHERE " + LINK_TABLE + "." + COLUMN_CATEGORY_ID + " = ? ";

                cursor = db.rawQuery(sqlQuery, selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        CONTACT_SOURCE_BY_CATEGORY_CONTENT_URI);
                return cursor;
            case URI_CONTACT_SOURCES:
                table = CONTACT_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);

        }
        db = dbHelper.getWritableDatabase();
        if (table.equals(CATEGORY_TABLE)) {
            cursor = db.query(CATEGORY_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            // просим ContentResolver уведомлять этот курсор
            // об изменениях данных в CONTACT_CONTENT_URI
            cursor.setNotificationUri(getContext().getContentResolver(),
                    CATEGORY_SOURCE_CONTENT_URI);
        }
        if (table.equals(CONTACT_TABLE)) {
            cursor = db.query(CONTACT_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            // просим ContentResolver уведомлять этот курсор
            // об изменениях данных в CONTACT_CONTENT_URI
            cursor.setNotificationUri(getContext().getContentResolver(),
                    CONTACT_SOURCE_CONTENT_URI);
        }
        if (table.equals(LINK_TABLE)) {
            cursor = db.query(LINK_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            // просим ContentResolver уведомлять этот курсор
            // об изменениях данных в CONTACT_CONTENT_URI
            cursor.setNotificationUri(getContext().getContentResolver(),
                    LINK_SOURCE_CONTENT_URI);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_CATEGORY_SOURCES:
                return CATEGORY_SOURCE_CONTENT_TYPE;
            case URI_CATEGORY_SOURCE_ID:
                return CATEGOTY_SOURCE_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID;
        Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case URI_CATEGORY_SOURCES:
                db = dbHelper.getWritableDatabase();
                rowID = db.insert(CATEGORY_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(CATEGORY_SOURCE_CONTENT_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            case URI_CONTACT_SOURCES:
                db = dbHelper.getWritableDatabase();
                rowID = db.insert(CONTACT_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(CONTACT_SOURCE_CONTENT_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            case URI_LINK_SOURCES:
                db = dbHelper.getWritableDatabase();
                rowID = db.insert(LINK_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(LINK_SOURCE_CONTENT_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        String id;
        switch (uriMatcher.match(uri)) {
            case URI_CATEGORY_SOURCES:
                table = CATEGORY_TABLE;
                break;
            case URI_CATEGORY_SOURCE_ID:
                id = uri.getLastPathSegment();
                table = CATEGORY_TABLE;
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                break;
            case URI_CONTACT_SOURCE_ID:
                table = CONTACT_TABLE;
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id;
        String table;
        switch (uriMatcher.match(uri)) {
            case URI_CATEGORY_SOURCES:
                table = CATEGORY_TABLE;
                break;
            case URI_CATEGORY_SOURCE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                table = CATEGORY_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();

        int cnt = -1;
        if (table.equals(CATEGORY_TABLE)) {
            cnt = db.update(CATEGORY_TABLE, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return cnt;
    }
}
