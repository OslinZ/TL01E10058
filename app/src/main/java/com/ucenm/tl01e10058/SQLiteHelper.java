package com.ucenm.tl01e10058;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase que gestiona la creación y actualización de la base de datos local SQLite.
 * Define la estructura de las tablas y proporciona métodos para interactuar con el motor de BD.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    // Nombre de la base de datos y versión
    public static final String DATABASE_NAME = "ContactosDB";
    public static final int DATABASE_VERSION = 1;

    // Constantes para los nombres de la tabla y sus columnas
    public static final String TABLE_CONTACTOS = "contactos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PAIS = "pais";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_NOTA = "nota";
    public static final String COLUMN_IMAGEN = "imagen";

    // Sentencia SQL para la creación de la tabla de contactos
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CONTACTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PAIS + " TEXT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_TELEFONO + " TEXT, " +
                    COLUMN_NOTA + " TEXT, " +
                    COLUMN_IMAGEN + " BLOB);";

    /**
     * Constructor del ayudante de la base de datos.
     * @param context Contexto de la aplicación.
     */
    public SQLiteHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Se ejecuta cuando la base de datos se crea por primera vez.
     * Define la estructura inicial (tablas).
     */
    @Override
    public void onCreate(SQLiteDatabase db) {db.execSQL(TABLE_CREATE);
    }

    /**
     * Se ejecuta cuando hay un cambio en la versión de la base de datos.
     * Permite realizar migraciones o recrear tablas.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En este caso, eliminamos la tabla anterior y la volvemos a crear
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }
}
