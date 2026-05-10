package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etEmail, etPassword;
    private Button btnRegistrar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(this, "No se pudo crear el perfil", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnSuccessListener(unused -> guardarPerfilEnDatabase(nombre, email))
                            .addOnFailureListener(e -> {
                                btnRegistrar.setEnabled(true);
                                Toast.makeText(this, "Error al guardar nombre: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnRegistrar.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarPerfilEnDatabase(String nombre, String email) {
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nombre", nombre);
        perfil.put("correo", email);
        perfil.put("estado", "Disponible");

        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(generarKeyUsuario(email))
                .updateChildren(perfil)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnRegistrar.setEnabled(true);
                    Toast.makeText(this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String generarKeyUsuario(String email) {
        return email.replace(".", ",").replace("@", "_");
    }
}
