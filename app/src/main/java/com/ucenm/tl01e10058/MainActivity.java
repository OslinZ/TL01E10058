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

    // Componentes de la interfaz de usuario
    private Spinner spnPais;
    private EditText txtNombre, txtTelefono, txtNota;
    private Button btnSalvar, btnContactos;
    private ImageView imgContacto;
    
    // Ayudante para operaciones con la base de datos
    private SQLiteHelper helper;
    
    // Identificador del contacto para determinar si se está creando o editando
    private int idContacto = -1; 

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

        // Inicialización del helper de base de datos
        helper = new SQLiteHelper(this);

        // Vinculación de componentes con el layout XML
        spnPais = findViewById(R.id.spnPais);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNota = findViewById(R.id.txtNota);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnContactos = findViewById(R.id.btnContactos);
        imgContacto = findViewById(R.id.imgContacto);

        // Configuración del Spinner con la lista de países y sus códigos de área
        String[] paises = {"Honduras (504)", "Costa Rica (506)", "Guatemala (502)", "El Salvador (503)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPais.setAdapter(adapter);

        // Evento para validar y guardar la información
        btnSalvar.setOnClickListener(v -> validarYGuardar());

        // Evento para navegar a la lista de contactos guardados
        btnContactos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityLista.class);
            startActivity(intent);
        });
        
        // Verifica si la actividad se abrió para editar un contacto existente
        checkIntentExtras();
    }

    /**
     * Revisa si se han recibido datos adicionales a través del Intent.
     * Si el Intent contiene un "id", carga los datos en los campos para edición.
     */
    private void checkIntentExtras() {
        if (getIntent().hasExtra("id")) {
            idContacto = getIntent().getIntExtra("id", -1);
            txtNombre.setText(getIntent().getStringExtra("nombre"));
            txtTelefono.setText(getIntent().getStringExtra("telefono"));
            txtNota.setText(getIntent().getStringExtra("nota"));
            
            // Cambia el texto del botón para reflejar que es una actualización
            btnSalvar.setText("Actualizar Contacto");
            
            // Selecciona automáticamente el país correspondiente en el Spinner
            String paisRecibido = getIntent().getStringExtra("pais");
            ArrayAdapter adapter = (ArrayAdapter) spnPais.getAdapter();
            if (paisRecibido != null) {
                int spinnerPosition = adapter.getPosition(paisRecibido);
                spnPais.setSelection(spinnerPosition);
            }
        }
    }

    /**
     * Valida que los campos no estén vacíos y cumplan con los formatos requeridos.
     */
    private void validarYGuardar() {
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nota = txtNota.getText().toString().trim();
        String pais = spnPais.getSelectedItem().toString();

        // Validación de campos obligatorios
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) || TextUtils.isEmpty(nota)) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de nombre (solo letras y espacios)
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            txtNombre.setError("Nombre inválido");
            return;
        }

        // Validación de teléfono (longitud de 8 a 15 dígitos)
        if (!telefono.matches("^[0-9]{8,15}$")) {
            txtTelefono.setError("Teléfono inválido");
            return;
        }

        // Procede a guardar o actualizar en la base de datos
        guardarContacto(pais, nombre, telefono, nota);
    }

    /**
     * Inserta o actualiza un registro en la tabla de contactos.
     */
    private void guardarContacto(String pais, String nombre, String telefono, String nota) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_PAIS, pais);
        values.put(SQLiteHelper.COLUMN_NOMBRE, nombre);
        values.put(SQLiteHelper.COLUMN_TELEFONO, telefono);
        values.put(SQLiteHelper.COLUMN_NOTA, nota);

        if (idContacto != -1) {
            // Caso: Actualización de contacto existente
            int rows = db.update(SQLiteHelper.TABLE_CONTACTOS, values, SQLiteHelper.COLUMN_ID + "=?", new String[]{String.valueOf(idContacto)});
            if (rows > 0) {
                Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad para volver a la lista
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Caso: Inserción de un nuevo contacto
            db.insert(SQLiteHelper.TABLE_CONTACTOS, null, values);
            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
            limpiarCampos(); // Limpia el formulario para un nuevo ingreso
        }
        db.close();
    }

    /**
     * Restablece los campos del formulario a sus valores iniciales.
     */
    private void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
        spnPais.setSelection(0);
    }
}
