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

public class ActivityLlamar extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private String numeroTelefono;
    private TextView lblNumeroLlamar;
    private FloatingActionButton btnActionCall;
    private Button btnAtrasLlamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        lblNumeroLlamar = findViewById(R.id.lblNumeroLlamar);
        btnActionCall = findViewById(R.id.btnActionCall);
        btnAtrasLlamar = findViewById(R.id.btnAtrasLlamar);

        numeroTelefono = getIntent().getStringExtra("telefono");
        lblNumeroLlamar.setText(numeroTelefono);

        btnAtrasLlamar.setOnClickListener(v -> finish());

        btnActionCall.setOnClickListener(v -> makePhoneCall());
    }

    private void makePhoneCall() {
        if (numeroTelefono != null && numeroTelefono.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(ActivityLlamar.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityLlamar.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            } else {
                String dial = "tel:" + numeroTelefono;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(ActivityLlamar.this, "Número no válido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permiso denegado para realizar llamadas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
