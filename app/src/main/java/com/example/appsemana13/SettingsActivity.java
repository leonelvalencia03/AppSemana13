package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity de Ajustes (Settings).
 * Muestra las opciones de configuración de la aplicación.
 * Actualmente solo muestra la opción de Perfil.
 *
 * Estructura actual:
 * - La parte superior contiene accesos de configuración.
 * - La barra inferior replica la navegación principal de la app.
 * - Si se agregan más módulos de ajustes, esta activity puede seguir
 *   funcionando como menú de entrada hacia pantallas específicas.
 * 
 * Navegación:
 * - Perfil -> EditPerfilActivity (para editar perfil del usuario)
 * - Chats -> MainActivity (pantalla principal de chats)
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Configurar manejo de insets del sistema (barras de estado/navegación)
        setupSystemInsets();

        // Configurar navegación inferior (botón Chats)
        setupBottomNavigation();

        // Configurar clic en la opción de Perfil
        setupPerfilOption();
    }

    /**
     * Configura los insets del sistema para un correcto posicionamiento
     * debajo de las barras del sistema.
     */
    private void setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Configura el listener para el botón de navegación "Chats"
     * en la barra inferior.
     */
    private void setupBottomNavigation() {
        findViewById(R.id.bottom_chats).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Configura el listener para la opción "Perfil" en las opciones de ajustes.
     * Al hacer clic, abre EditPerfilActivity para editar el perfil.
     *
     * Tanto la tarjeta superior como la opción de lista llevan al mismo destino
     * para mantener una navegación consistente desde distintos puntos de la UI.
     */
    private void setupPerfilOption() {
        // Abrir edición de perfil al presionar la tarjeta de perfil
        findViewById(R.id.profile_card).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPerfilActivity.class);
            startActivity(intent);
        });

        // También hacer clicable la opción "Perfil" en la lista
        findViewById(R.id.profile_option).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPerfilActivity.class);
            startActivity(intent);
        });
    }
}
