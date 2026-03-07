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

/**
 * Actividad que muestra la lista de contactos almacenados.
 * Permite buscar, seleccionar, eliminar, actualizar y compartir información de contactos.
 */
public class ActivityLista extends AppCompatActivity {

    // Componentes de la interfaz de usuario
    private ListView listContactos;
    private EditText txtBuscar;
    private Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    
    // Variables de control y datos
    private SQLiteHelper helper;
    private ArrayList<Contactos> listaContactos;
    private Contactos contactoSeleccionado;
    private int selectedPosition = -1; // Rastrea el elemento seleccionado en la lista
    private ContactoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilita el modo de pantalla completa (borde a borde)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista);

        // Ajusta el padding para evitar que el contenido quede oculto bajo las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_lista), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa el ayudante de base de datos
        helper = new SQLiteHelper(this);
        
        // Inicializa las referencias a las vistas
        listContactos = findViewById(R.id.listContactos);
        txtBuscar = findViewById(R.id.txtBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);

        // Carga los contactos al iniciar la actividad
        obtenerContactos("");

        // Acción para regresar a la pantalla anterior
        btnAtras.setOnClickListener(v -> finish());

        // Maneja el clic en un elemento de la lista para seleccionar un contacto
        listContactos.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionado = listaContactos.get(position);
            selectedPosition = position;
            adapter.notifyDataSetChanged(); // Refresca para mostrar la selección visual (RadioButton)
            mostrarDialogoLlamada(); // Pregunta si se desea llamar al contacto
        });

        // Agrega un escuchador al campo de búsqueda para filtrar la lista en tiempo real
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                obtenerContactos(s.toString()); // Filtra los contactos por nombre
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configura los eventos de clic para los botones de acción
        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnActualizar.setOnClickListener(v -> actualizarContacto());
        btnCompartir.setOnClickListener(v -> compartirContacto());
        btnVerImagen.setOnClickListener(v -> verImagen());
    }

    /**
     * Consulta la base de datos para obtener los contactos, aplicando un filtro si es necesario.
     * @param busqueda Texto para filtrar por el nombre del contacto.
     */
    private void obtenerContactos(String busqueda) {
        SQLiteDatabase db = helper.getReadableDatabase();
        listaContactos = new ArrayList<>();

        // Construcción de la consulta SQL con filtro LIKE si existe búsqueda
        String query = "SELECT * FROM " + SQLiteHelper.TABLE_CONTACTOS;
        if (!busqueda.isEmpty()) {
            query += " WHERE " + SQLiteHelper.COLUMN_NOMBRE + " LIKE '%" + busqueda + "%'";
        }

        // Ejecución de la consulta
        Cursor cursor = db.rawQuery(query, null);
        
        // Obtenemos los índices de las columnas para mayor seguridad
        int idIdx = cursor.getColumnIndex(SQLiteHelper.COLUMN_ID);
        int nombreIdx = cursor.getColumnIndex(SQLiteHelper.COLUMN_NOMBRE);
        int telefonoIdx = cursor.getColumnIndex(SQLiteHelper.COLUMN_TELEFONO);
        int notaIdx = cursor.getColumnIndex(SQLiteHelper.COLUMN_NOTA);
        int paisIdx = cursor.getColumnIndex(SQLiteHelper.COLUMN_PAIS);
        int imagenIdx = cursor.getColumnIndex(SQLiteHelper.COLUMN_IMAGEN);

        while (cursor.moveToNext()) {
            // Extraemos los datos explícitamente para evitar confusiones de posición
            int id = cursor.getInt(idIdx);
            String nombre = cursor.getString(nombreIdx);
            String telefono = cursor.getString(telefonoIdx);
            String nota = cursor.getString(notaIdx);
            String pais = cursor.getString(paisIdx);
            byte[] imagen = cursor.getBlob(imagenIdx);

            listaContactos.add(new Contactos(id, nombre, telefono, nota, pais, imagen));
        }
        cursor.close();
        db.close();

        // Limpia la selección previa y actualiza el adaptador
        selectedPosition = -1;
        contactoSeleccionado = null;
        adapter = new ContactoAdapter();
        listContactos.setAdapter(adapter);
    }

    /**
     * Muestra un diálogo para confirmar si el usuario desea llamar al contacto seleccionado.
     */
    private void mostrarDialogoLlamada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acción");
        builder.setMessage("¿Desea llamar a " + contactoSeleccionado.getNombre() + "?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Abre la actividad de llamada pasando el número telefónico
            Intent intent = new Intent(this, ActivityLlamar.class);
            intent.putExtra("telefono", contactoSeleccionado.getTelefono());
            startActivity(intent);
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    /**
     * Elimina el contacto seleccionado de la base de datos previa confirmación.
     */
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
                    // Ejecuta el borrado por ID
                    db.delete(SQLiteHelper.TABLE_CONTACTOS, SQLiteHelper.COLUMN_ID + "=?", new String[]{String.valueOf(contactoSeleccionado.getId())});
                    db.close();
                    obtenerContactos(""); // Recarga la lista completa
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Abre MainActivity para editar la información del contacto seleccionado.
     */
    private void actualizarContacto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un contacto de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        // Pasa todos los datos del contacto mediante extras
        intent.putExtra("id", contactoSeleccionado.getId());
        intent.putExtra("nombre", contactoSeleccionado.getNombre());
        intent.putExtra("telefono", contactoSeleccionado.getTelefono());
        intent.putExtra("nota", contactoSeleccionado.getNota());
        intent.putExtra("pais", contactoSeleccionado.getPais());
        startActivity(intent);
    }

    /**
     * Comparte la información básica del contacto seleccionado (nombre y teléfono).
     */
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

    /**
     * Simula la visualización de la imagen de perfil del contacto seleccionado.
     */
    private void verImagen() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un contacto de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Mostrando perfil de: " + contactoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresca la lista de contactos cada vez que la actividad vuelve al primer plano
        obtenerContactos(""); 
    }

    /**
     * Adaptador personalizado para mostrar los contactos en el ListView utilizando un diseño propio.
     */
    class ContactoAdapter extends BaseAdapter {
        @Override
        public int getCount() { return listaContactos.size(); }
        
        @Override
        public Object getItem(int position) { return listaContactos.get(position); }
        
        @Override
        public long getItemId(int position) { return listaContactos.get(position).getId(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Infla el layout para cada elemento de la lista
            if (convertView == null) {
                convertView = LayoutInflater.from(ActivityLista.this).inflate(R.layout.item_contacto, parent, false);
            }
            
            Contactos c = listaContactos.get(position);
            
            // Obtiene referencias a las vistas dentro del ítem
            TextView name = convertView.findViewById(R.id.txtItemNombre);
            TextView phone = convertView.findViewById(R.id.txtItemTelefono);
            RadioButton rb = convertView.findViewById(R.id.rbSeleccion);

            // Asignación de datos: Nombre arriba (Texto principal), Teléfono abajo (Texto secundario)
            name.setText(c.getNombre());
            phone.setText(c.getTelefono());
            
            // Marca el RadioButton si esta posición es la seleccionada
            rb.setChecked(position == selectedPosition);

            // Maneja el clic en el RadioButton para seleccionar el contacto
            rb.setOnClickListener(v -> {
                selectedPosition = position;
                contactoSeleccionado = listaContactos.get(position);
                notifyDataSetChanged(); // Notifica cambios para actualizar visualmente la selección
            });

            return convertView;
        }
    }
}
