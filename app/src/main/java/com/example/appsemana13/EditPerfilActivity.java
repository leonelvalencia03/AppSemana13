package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity que permite al usuario editar su perfil.
 * El usuario puede cambiar su nombre y estado.
 * 
 * Esta actividad es llamada desde SettingsActivity cuando el usuario
 * selecciona la opción "Perfil".
 */
public class EditPerfilActivity extends AppCompatActivity {

    // Elementos de la UI
    private TextView avatarPreview;
    private EditText nameInput;
    private EditText statusInput;
    private Button saveButton;
    private ImageView backButton;
    private ImageView changeAvatar;
    private LinearLayout bottomChats;

    // Datos del usuario (en una app real, estos vendrían de una base de datos o API)
    private String userName = "Andrés Morales";
    private String userStatus = "Disponible";
    private String userInitials = "AM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Inicializar vistas
        initViews();

        // Configurar listeners de eventos
        setupListeners();

        // Cargar datos del usuario
        loadUserData();

        // Aplicar insets del sistema
        applySystemInsets();
    }

    /**
     * Inicializa las referencias a las vistas del layout.
     */
    private void initViews() {
        avatarPreview = findViewById(R.id.avatar_preview);
        nameInput = findViewById(R.id.name_input);
        statusInput = findViewById(R.id.status_input);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        changeAvatar = findViewById(R.id.change_avatar);
        bottomChats = findViewById(R.id.bottom_chats);
    }

    /**
     * Configura los listeners para los elementos interactivos.
     */
    private void setupListeners() {
        // Botón Guardar - guarda los cambios del perfil
        saveButton.setOnClickListener(v -> saveProfile());

        // Botón atrás - vuelve a ajustes
        backButton.setOnClickListener(v -> goBack());

        // Change avatar - en una app real, abriría un selector de imagen
        changeAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "Cambiar foto de perfil (Funcionalidad futura)", Toast.LENGTH_SHORT).show();
        });

        // Bottom Chats - vuelve a la pantalla principal
        bottomChats.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Carga los datos actuales del usuario en los campos de texto.
     */
    private void loadUserData() {
        nameInput.setText(userName);
        statusInput.setText(userStatus);
        avatarPreview.setText(userInitials);
    }

    /**
     * Aplica los insets del sistema (barras de estado y navegación).
     */
    private void applySystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Guarda los cambios del perfil.
     * En una app real, esto enviaría los datos a un servidor o base de datos.
     */
    private void saveProfile() {
        String newName = nameInput.getText().toString().trim();
        String newStatus = statusInput.getText().toString().trim();

        // Validar que el nombre no esté vacío
        if (newName.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar datos locales (en una app real, guardarías en base de datos)
        userName = newName;
        userStatus = newStatus;

        // Generar iniciales para el avatar
        userInitials = generateInitials(userName);
        avatarPreview.setText(userInitials);

        // Mostrar confirmación
        Toast.makeText(this, "Perfil guardado", Toast.LENGTH_SHORT).show();

        // Volver a ajustes después de guardar
        goBack();
    }

    /**
     * Genera las iniciales del nombre para mostrar en el avatar.
     * @param name El nombre completo del usuario
     * @return Las iniciales (máximo 2 caracteres)
     */
    private String generateInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "??";
        }

        String[] words = name.trim().split("\\s+");
        if (words.length >= 2) {
            return (words[0].charAt(0) + "" + words[1].charAt(0)).toUpperCase();
        } else {
            return words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
        }
    }

    /**
     * Navega de vuelta a la pantalla de ajustes.
     */
    private void goBack() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}