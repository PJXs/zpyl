package com.hankexu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hanke on 2015-10-23.
 *
 * 数据库管理
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "filter.db";
    public static final String TABLE_NAME_KEYWORDS = "keywords";
    public static final String TABLE_NAME_FROMADDRESS = "fromaddress";
    public static final String TABLE_NAME_SMS = "sms";

    public static final String COLUMN_NAME_KEYWORD = "keyword";
    public static final String COLUMN_NAME_FROMADDRESS = "fromaddress";
    public static final String COLUMN_NAME_RECEIVER_TIME = "received_time";
    public static final String COLUMN_NAME_BODY = "body";
    public static final String COLUMN_NAME_ID = "_id";

    /*数据库名，表名，列名常量*/
    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME_KEYWORDS+" ("+COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME_KEYWORD + " TEXT (20));");
        db.execSQL("CREATE TABLE "+TABLE_NAME_FROMADDRESS+" ("+COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME_FROMADDRESS + " TEXT (20));");
        db.execSQL("CREATE TABLE "+TABLE_NAME_SMS+" ("+COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME_FROMADDRESS + " TEXT (20) NOT NULL, " + COLUMN_NAME_RECEIVER_TIME + " TIME, " + COLUMN_NAME_BODY + " TEXT (1024));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
