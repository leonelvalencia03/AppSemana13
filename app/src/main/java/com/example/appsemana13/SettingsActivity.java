package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        setupSystemInsets();
        setupBottomNavigation();
        setupPerfilOption();
        setupLogout();  // Nuevo método
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
        findViewById(R.id.profile_card).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPerfilActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.profile_option).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPerfilActivity.class);
            startActivity(intent);
        });
    }

    // Nuevo método: cierre de sesión
    private void setupLogout() {
        findViewById(R.id.logout_option).setOnClickListener(v -> {
            // Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();
            // Redirigir a LoginActivity y limpiar la pila de actividades
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
