package com.ucenm.tl01e10058;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ActivityLista extends AppCompatActivity {

    private ListView listContactos;
    private EditText txtBuscar;
    private Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    private SQLiteHelper helper;
    private ArrayList<Contactos> listaContactos;
    private ArrayList<String> listaString;
    private Contactos contactoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        helper = new SQLiteHelper(this);
        listContactos = findViewById(R.id.listContactos);
        txtBuscar = findViewById(R.id.txtBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);

        obtenerContactos("");

        btnAtras.setOnClickListener(v -> finish());

        listContactos.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionado = listaContactos.get(position);
            mostrarDialogoLlamada();
        });

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                obtenerContactos(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnActualizar.setOnClickListener(v -> actualizarContacto());
        btnCompartir.setOnClickListener(v -> compartirContacto());
        btnVerImagen.setOnClickListener(v -> verImagen());
    }

    private void obtenerContactos(String busqueda) {
        SQLiteDatabase db = helper.getReadableDatabase();
        listaContactos = new ArrayList<>();
        listaString = new ArrayList<>();

        String query = "SELECT * FROM " + SQLiteHelper.TABLE_CONTACTOS;
        if (!busqueda.isEmpty()) {
            query += " WHERE " + SQLiteHelper.COLUMN_NOMBRE + " LIKE '%" + busqueda + "%'";
        }

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Contactos contacto = new Contactos(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getBlob(5)
            );
            listaContactos.add(contacto);
            listaString.add(contacto.getNombre() + " | " + contacto.getTelefono());
        }
        cursor.close();
        db.close();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, listaString);
        listContactos.setAdapter(adapter);
    }

    private void mostrarDialogoLlamada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acción");
        builder.setMessage("¿Desea llamar a " + contactoSeleccionado.getNombre() + "?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, ActivityLlamar.class);
            intent.putExtra("telefono", contactoSeleccionado.getTelefono());
            startActivity(intent);
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void eliminarContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_CONTACTOS, SQLiteHelper.COLUMN_ID + "=?", new String[]{String.valueOf(contactoSeleccionado.getId())});
        db.close();
        contactoSeleccionado = null;
        obtenerContactos("");
        Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
    }

    private void actualizarContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", contactoSeleccionado.getId());
        intent.putExtra("nombre", contactoSeleccionado.getNombre());
        intent.putExtra("telefono", contactoSeleccionado.getTelefono());
        intent.putExtra("nota", contactoSeleccionado.getNota());
        startActivity(intent);
    }

    private void compartirContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Contacto: " + contactoSeleccionado.getNombre() + "\nTel: " + contactoSeleccionado.getTelefono());
        startActivity(Intent.createChooser(intent, "Compartir con"));
    }

    private void verImagen() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Mostrando imagen de " + contactoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
    }
}
