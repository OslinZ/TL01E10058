package com.ucenm.tl01e10058;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ContactosDB";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTOS = "contactos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PAIS = "pais";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_NOTA = "nota";
    public static final String COLUMN_IMAGEN = "imagen";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CONTACTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PAIS + " TEXT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_TELEFONO + " TEXT, " +
                    COLUMN_NOTA + " TEXT, " +
                    COLUMN_IMAGEN + " BLOB);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }
}
