package com.saltechdigital.pizzeria.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DAOBase extends SQLiteOpenHelper {

    private static int VERSION = 1;

    private SQLiteDatabase database = null;

    public final static String NOM = "pizzeriapepejoe.db";

    public static int getVERSION() {
        return VERSION;
    }

    public static void setVERSION(int version) {
        DAOBase.VERSION = version;
    }

    private static final String TABLE_CREATE_CATEGORIE = "";

    private static final String TABLE_DROP_CATEGORIE = "";

    public DAOBase(Context context) {
        super(context, NOM, null, VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_CATEGORIE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_DROP_CATEGORIE);
        onCreate(db);
    }

    protected SQLiteDatabase open() {
        database = getWritableDatabase();
        return database;
    }

    protected SQLiteDatabase read() {
        database = getReadableDatabase();
        return database;
    }

    public void close() {
        read();
        close();
    }

    protected SQLiteDatabase getDatabase() {
        return database;
    }
}
