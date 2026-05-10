package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private TextView tvNombre;
    private TextView tvEmail;
    private TextView tvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        tvNombre = findViewById(R.id.settings_nombre);
        tvEmail = findViewById(R.id.settings_email);
        tvAvatar = findViewById(R.id.settings_avatar);

        setupSystemInsets();
        setupBottomNavigation();
        setupPerfilOption();
        cargarDatosPerfil();
        setupLogout();
    }

    private void setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.bottom_chats).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupPerfilOption() {
        findViewById(R.id.profile_card).setOnClickListener(v -> abrirPerfil());
        findViewById(R.id.profile_option).setOnClickListener(v -> abrirPerfil());
    }

    private void abrirPerfil() {
        startActivity(new Intent(this, EditPerfilActivity.class));
    }

    private void cargarDatosPerfil() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            return;
        }

        String email = user.getEmail();
        String nombreAuth = user.getDisplayName();
        pintarPerfil(nombreAuth == null || nombreAuth.isEmpty() ? "Usuario" : nombreAuth, email);

        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(generarKeyUsuario(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            pintarPerfil(nombre, email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void pintarPerfil(String nombre, String email) {
        tvNombre.setText(nombre);
        tvEmail.setText(email);
        tvAvatar.setText(generarIniciales(nombre));
    }

    private void setupLogout() {
        findViewById(R.id.logout_option).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private String generarKeyUsuario(String email) {
        return email.replace(".", ",").replace("@", "_");
    }

    private String generarIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "US";
        }
        String[] palabras = nombre.trim().split("\\s+");
        if (palabras.length >= 2) {
            return (palabras[0].charAt(0) + "" + palabras[1].charAt(0)).toUpperCase();
        }
        return palabras[0].substring(0, Math.min(2, palabras[0].length())).toUpperCase();
    }
}
