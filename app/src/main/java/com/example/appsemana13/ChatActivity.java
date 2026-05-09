package com.example.appsemana13;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText txtEscribirMensaje;
    private ImageButton btnEnviarMensaje;
    private RecyclerView chatMessages;
    private MensajesAdapter adapter;
    private List<Mensaje> listaMensajes;
    private DatabaseReference dataReference;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        txtEscribirMensaje = findViewById(R.id.txtEscribirMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        chatMessages = findViewById(R.id.chat_messages);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserEmail = auth.getCurrentUser().getEmail();

        String receptor = getIntent().getStringExtra("receptor_email");
        if (receptor == null) {
            if (currentUserEmail.equals(Constantes.USUARIO_1)) {
                receptor = Constantes.USUARIO_2;
            } else {
                receptor = Constantes.USUARIO_1;
            }
        }
        String salaId = generarSalaId(currentUserEmail, receptor);
        dataReference = FirebaseDatabase.getInstance().getReference("mensajes").child(salaId);

        TextView tvHeaderNombre = findViewById(R.id.chat_header_nombre);
        TextView tvHeaderAvatar = findViewById(R.id.chat_header_avatar);
        tvHeaderNombre.setText(receptor);
        tvHeaderAvatar.setText(receptor.substring(0, 2).toUpperCase());

        listaMensajes = new ArrayList<>();
        // Pasamos una interfaz para manejar los eventos de editar y eliminar
        adapter = new MensajesAdapter(listaMensajes, currentUserEmail, new MensajesAdapter.OnMessageClickListener() {
            @Override
            public void onMessageLongClick(Mensaje mensaje) {
                if (mensaje.getEmisor().equals(currentUserEmail)) {
                    mostrarOpcionesMensaje(mensaje);
                }
            }
        });
        chatMessages.setLayoutManager(new LinearLayoutManager(this));
        chatMessages.setAdapter(adapter);

        btnEnviarMensaje.setOnClickListener(v -> {
            String texto = txtEscribirMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                Mensaje mensajeObj = new Mensaje(texto, currentUserEmail);
                dataReference.push().setValue(mensajeObj);
                txtEscribirMensaje.setText("");
            }
        });

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Mensaje m = data.getValue(Mensaje.class);
                    if (m != null) {
                        m.setId(data.getKey()); // Guardamos el ID de Firebase
                        listaMensajes.add(m);
                    }
                }
                adapter.notifyDataSetChanged();
                if (listaMensajes.size() > 0) {
                    chatMessages.scrollToPosition(listaMensajes.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al leer mensajes", error.toException());
            }
        });

        setupSystemInsets();
        setupNavigation();
    }

    private void mostrarOpcionesMensaje(Mensaje mensaje) {
        String[] opciones = {"Editar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones de mensaje");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                mostrarDialogoEditar(mensaje);
            } else {
                eliminarMensaje(mensaje);
            }
        });
        builder.show();
    }

    private void mostrarDialogoEditar(Mensaje mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar mensaje");

        final EditText input = new EditText(this);
        input.setText(mensaje.getTexto());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoTexto = input.getText().toString().trim();
            if (!nuevoTexto.isEmpty()) {
                editarMensaje(mensaje, nuevoTexto);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void editarMensaje(Mensaje mensaje, String nuevoTexto) {
        if (mensaje.getId() != null) {
            dataReference.child(mensaje.getId()).child("texto").setValue(nuevoTexto)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Mensaje editado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al editar", Toast.LENGTH_SHORT).show());
        }
    }

    private void eliminarMensaje(Mensaje mensaje) {
        if (mensaje.getId() != null) {
            dataReference.child(mensaje.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Mensaje eliminado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupNavigation() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private String generarSalaId(String email1, String email2) {
        String e1 = email1.replace(".", ",").replace("@", "_");
        String e2 = email2.replace(".", ",").replace("@", "_");
        if (e1.compareTo(e2) < 0) {
            return e1 + "_" + e2;
        } else {
            return e2 + "_" + e1;
        }
    }
}
