package com.ucenm.tl01e10058;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Spinner spnPais;
    private EditText txtNombre, txtTelefono, txtNota;
    private Button btnSalvar, btnContactos;
    private ImageView imgContacto;
    private SQLiteHelper helper;
    private int idContacto = -1; // Variable para almacenar el ID en modo edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = new SQLiteHelper(this);

        spnPais = findViewById(R.id.spnPais);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNota = findViewById(R.id.txtNota);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnContactos = findViewById(R.id.btnContactos);
        imgContacto = findViewById(R.id.imgContacto);

        String[] paises = {"Honduras (504)", "Costa Rica (506)", "Guatemala (502)", "El Salvador (503)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPais.setAdapter(adapter);

        btnSalvar.setOnClickListener(v -> validarYGuardar());

        btnContactos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityLista.class);
            startActivity(intent);
        });
        
        checkIntentExtras();
    }

    private void checkIntentExtras() {
        if (getIntent().hasExtra("id")) {
            idContacto = getIntent().getIntExtra("id", -1);
            txtNombre.setText(getIntent().getStringExtra("nombre"));
            txtTelefono.setText(getIntent().getStringExtra("telefono"));
            txtNota.setText(getIntent().getStringExtra("nota"));
            btnSalvar.setText("Actualizar Contacto");
            
            // Seleccionar el país en el spinner
            String paisRecibido = getIntent().getStringExtra("pais");
            ArrayAdapter adapter = (ArrayAdapter) spnPais.getAdapter();
            if (paisRecibido != null) {
                int spinnerPosition = adapter.getPosition(paisRecibido);
                spnPais.setSelection(spinnerPosition);
            }
        }
    }

    private void validarYGuardar() {
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nota = txtNota.getText().toString().trim();
        String pais = spnPais.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) || TextUtils.isEmpty(nota)) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            txtNombre.setError("Nombre inválido");
            return;
        }

        if (!telefono.matches("^[0-9]{8,15}$")) {
            txtTelefono.setError("Teléfono inválido");
            return;
        }

        guardarContacto(pais, nombre, telefono, nota);
    }

    private void guardarContacto(String pais, String nombre, String telefono, String nota) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_PAIS, pais);
        values.put(SQLiteHelper.COLUMN_NOMBRE, nombre);
        values.put(SQLiteHelper.COLUMN_TELEFONO, telefono);
        values.put(SQLiteHelper.COLUMN_NOTA, nota);

        if (idContacto != -1) {
            // Modo Actualización
            int rows = db.update(SQLiteHelper.TABLE_CONTACTOS, values, SQLiteHelper.COLUMN_ID + "=?", new String[]{String.valueOf(idContacto)});
            if (rows > 0) {
                Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a la lista
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Modo Guardado nuevo
            db.insert(SQLiteHelper.TABLE_CONTACTOS, null, values);
            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        }
        db.close();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
        spnPais.setSelection(0);
    }
}
