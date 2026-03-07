package com.ucenm.tl01e10058;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Actividad encargada de realizar llamadas telefónicas al contacto seleccionado.
 * Gestiona los permisos necesarios para realizar la llamada.
 */
public class ActivityLlamar extends AppCompatActivity {

    // Código de solicitud para el permiso de llamada
    private static final int REQUEST_CALL_PERMISSION = 1;
    private String numeroTelefono;
    
    // Componentes de la interfaz
    private TextView lblNumeroLlamar;
    private FloatingActionButton btnActionCall;
    private Button btnAtrasLlamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        // Vinculación de componentes
        lblNumeroLlamar = findViewById(R.id.lblNumeroLlamar);
        btnActionCall = findViewById(R.id.btnActionCall);
        btnAtrasLlamar = findViewById(R.id.btnAtrasLlamar);

        // Recupera el número de teléfono pasado desde la actividad anterior
        numeroTelefono = getIntent().getStringExtra("telefono");
        lblNumeroLlamar.setText(numeroTelefono);

        // Configura el botón para regresar
        btnAtrasLlamar.setOnClickListener(v -> finish());

        // Configura el botón de acción para iniciar la llamada
        btnActionCall.setOnClickListener(v -> makePhoneCall());
    }

    /**
     * Intenta realizar la llamada telefónica.
     * Verifica si la aplicación tiene los permisos necesarios antes de proceder.
     */
    private void makePhoneCall() {
        if (numeroTelefono != null && numeroTelefono.trim().length() > 0) {
            // Verifica si el permiso CALL_PHONE ya ha sido otorgado
            if (ContextCompat.checkSelfPermission(ActivityLlamar.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Solicita el permiso al usuario si no ha sido otorgado
                ActivityCompat.requestPermissions(ActivityLlamar.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            } else {
                // Si el permiso está otorgado, realiza la llamada
                String dial = "tel:" + numeroTelefono;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(ActivityLlamar.this, "Número no válido", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Maneja la respuesta del usuario a la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            // Verifica si el usuario aceptó el permiso
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(); // Reintenta la llamada
            } else {
                Toast.makeText(this, "Permiso denegado para realizar llamadas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
