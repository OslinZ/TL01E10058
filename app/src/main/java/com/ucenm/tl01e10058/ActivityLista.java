package com.ucenm.tl01e10058;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ActivityLista extends AppCompatActivity {

    private ListView listContactos;
    private EditText txtBuscar;
    private Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    private SQLiteHelper helper;
    private ArrayList<Contactos> listaContactos;
    private Contactos contactoSeleccionado;
    private int selectedPosition = -1;
    private ContactoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista);

        // Ajuste para botones virtuales y barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_lista), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            selectedPosition = position;
            adapter.notifyDataSetChanged();
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

        String query = "SELECT * FROM " + SQLiteHelper.TABLE_CONTACTOS;
        if (!busqueda.isEmpty()) {
            query += " WHERE " + SQLiteHelper.COLUMN_NOMBRE + " LIKE '%" + busqueda + "%'";
        }

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            listaContactos.add(new Contactos(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getBlob(5)
            ));
        }
        cursor.close();
        db.close();

        selectedPosition = -1;
        contactoSeleccionado = null;
        adapter = new ContactoAdapter();
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
            Toast.makeText(this, "Debe seleccionar un contacto de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Está seguro de eliminar a " + contactoSeleccionado.getNombre() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.delete(SQLiteHelper.TABLE_CONTACTOS, SQLiteHelper.COLUMN_ID + "=?", new String[]{String.valueOf(contactoSeleccionado.getId())});
                    db.close();
                    obtenerContactos("");
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void actualizarContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un contacto de la lista", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Debe seleccionar un contacto de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Nombre: " + contactoSeleccionado.getNombre() + "\nTeléfono: " + contactoSeleccionado.getTelefono());
        startActivity(Intent.createChooser(intent, "Compartir contacto vía:"));
    }

    private void verImagen() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un contacto de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Mostrando perfil de: " + contactoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
    }

    class ContactoAdapter extends BaseAdapter {
        @Override
        public int getCount() { return listaContactos.size(); }
        @Override
        public Object getItem(int position) { return listaContactos.get(position); }
        @Override
        public long getItemId(int position) { return listaContactos.get(position).getId(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ActivityLista.this).inflate(R.layout.item_contacto, parent, false);
            }
            Contactos c = listaContactos.get(position);
            TextView name = convertView.findViewById(R.id.txtItemNombre);
            TextView phone = convertView.findViewById(R.id.txtItemTelefono);
            RadioButton rb = convertView.findViewById(R.id.rbSeleccion);

            name.setText(c.getNombre());
            phone.setText(c.getTelefono());
            rb.setChecked(position == selectedPosition);

            // Permitir que el clic en el RadioButton también seleccione la fila
            rb.setOnClickListener(v -> {
                selectedPosition = position;
                contactoSeleccionado = listaContactos.get(position);
                notifyDataSetChanged();
            });

            return convertView;
        }
    }
}
