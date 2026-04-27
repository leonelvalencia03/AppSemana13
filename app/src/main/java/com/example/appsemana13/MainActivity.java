package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity principal (Main).
 * Muestra la lista de chats del usuario.
 * Es el punto de entrada de la aplicación.
 * 
 * Navegación:
 * - Chat individual -> ChatActivity
 * - Ajustes -> SettingsActivity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configurar insets del sistema para un correcto posicionamiento
        setupSystemInsets();

        // Configurar listeners de navegación
        setupNavigation();
    }

    /**
     * Configura los insets del sistema.
     */
    private void setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Configura los listeners para la navegación entre pantallas.
     */
    private void setupNavigation() {
        // Al hacer clic en un chat, abrir ChatActivity
        findViewById(R.id.chat_camila).setOnClickListener(this::openChat);

        // Al hacer clic en Ajustes, abrir SettingsActivity
        findViewById(R.id.bottom_settings).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Abre la actividad de chat para el chat seleccionado.
     * @param view La vista que fue presionada
     */
    private void openChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
