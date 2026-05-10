package com.example.appsemana13;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class EditPerfilActivity extends AppCompatActivity {
    private TextView avatarPreview;
    private EditText emailInput;
    private EditText nameInput;
    private EditText statusInput;
    private Button saveButton;
    private ImageView backButton;
    private ImageView changeAvatar;
    private LinearLayout bottomChats;

    private String currentEmail = "";
    private String userName = "";
    private String userStatus = "Disponible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        initViews();
        setupListeners();
        loadUserData();
        applySystemInsets();
    }

    private void initViews() {
        avatarPreview = findViewById(R.id.avatar_preview);
        emailInput = findViewById(R.id.email_input);
        nameInput = findViewById(R.id.name_input);
        statusInput = findViewById(R.id.status_input);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        changeAvatar = findViewById(R.id.change_avatar);
        bottomChats = findViewById(R.id.bottom_chats);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveProfile());
        backButton.setOnClickListener(v -> goBack());

        changeAvatar.setOnClickListener(v ->
                Toast.makeText(this, "Cambiar foto de perfil (Funcionalidad futura)", Toast.LENGTH_SHORT).show()
        );

        bottomChats.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            return;
        }

        currentEmail = user.getEmail();
        userName = user.getDisplayName() == null ? "" : user.getDisplayName();
        emailInput.setText(currentEmail);
        nameInput.setText(userName);
        statusInput.setText(userStatus);
        avatarPreview.setText(generateInitials(userName));

        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(generarKeyUsuario(currentEmail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String correo = snapshot.child("correo").getValue(String.class);
                        String estado = snapshot.child("estado").getValue(String.class);

                        if (correo != null && !correo.trim().isEmpty()) {
                            currentEmail = correo;
                            emailInput.setText(correo);
                        }
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            userName = nombre;
                            nameInput.setText(nombre);
                            avatarPreview.setText(generateInitials(nombre));
                        }
                        if (estado != null && !estado.trim().isEmpty()) {
                            userStatus = estado;
                            statusInput.setText(estado);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void applySystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveProfile() {
        String newName = nameInput.getText().toString().trim();
        String newEmail = emailInput.getText().toString().trim();
        String newStatus = statusInput.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "No hay usuario activo", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldEmail = user.getEmail();
        if (!newEmail.equalsIgnoreCase(oldEmail)) {
            mostrarConfirmacionCambioCorreo(user, oldEmail, newEmail, newName, newStatus);
            return;
        }

        guardarNombreYPerfil(user, oldEmail, oldEmail, newName, newStatus);
    }

    private void mostrarConfirmacionCambioCorreo(
            FirebaseUser user,
            String oldEmail,
            String newEmail,
            String newName,
            String newStatus
    ) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar cambio de correo")
                .setMessage("¿Quieres cambiar tu correo de inicio de sesión de:\n\n"
                        + oldEmail + "\n\na:\n\n" + newEmail + "?")
                .setPositiveButton("Sí, cambiar", (dialog, which) -> {
                    saveButton.setEnabled(false);
                    user.updateEmail(newEmail)
                            .addOnSuccessListener(unused -> guardarNombreYPerfil(user, oldEmail, newEmail, newName, newStatus))
                            .addOnFailureListener(e -> {
                                saveButton.setEnabled(true);
                                Toast.makeText(this, "Error al cambiar correo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarNombreYPerfil(FirebaseUser user, String oldEmail, String newEmail, String newName, String newStatus) {
        saveButton.setEnabled(false);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnSuccessListener(unused -> guardarPerfilEnDatabase(oldEmail, newEmail, newName, newStatus))
                .addOnFailureListener(e -> {
                    saveButton.setEnabled(true);
                    Toast.makeText(this, "Error al guardar nombre: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarPerfilEnDatabase(String oldEmail, String newEmail, String nombre, String estado) {
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nombre", nombre);
        perfil.put("correo", newEmail);
        perfil.put("estado", estado.isEmpty() ? "Disponible" : estado);

        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(generarKeyUsuario(newEmail))
                .updateChildren(perfil)
                .addOnSuccessListener(unused -> {
                    if (!newEmail.equalsIgnoreCase(oldEmail)) {
                        FirebaseDatabase.getInstance().getReference("usuarios")
                                .child(generarKeyUsuario(oldEmail))
                                .removeValue();
                    }
                    currentEmail = newEmail;
                    userName = nombre;
                    userStatus = estado.isEmpty() ? "Disponible" : estado;
                    emailInput.setText(currentEmail);
                    avatarPreview.setText(generateInitials(userName));
                    Toast.makeText(this, "Perfil guardado", Toast.LENGTH_SHORT).show();
                    goBack();
                })
                .addOnFailureListener(e -> {
                    saveButton.setEnabled(true);
                    Toast.makeText(this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String generarKeyUsuario(String email) {
        return email.replace(".", ",").replace("@", "_");
    }

    private String generateInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "US";
        }

        String[] words = name.trim().split("\\s+");
        if (words.length >= 2) {
            return (words[0].charAt(0) + "" + words[1].charAt(0)).toUpperCase();
        }
        return words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
    }

    private void goBack() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
