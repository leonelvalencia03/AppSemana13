package com.example.appsemana13;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

        // Obtener usuario actual
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserEmail = auth.getCurrentUser().getEmail();
        // Reemplazar caracteres no válidos para Firebase (opcional)
        String safeEmail = currentUserEmail.replace(".", ",");

        // Referencia a Firebase específica para este chat (por ahora todos los mensajes juntos)
        dataReference = FirebaseDatabase.getInstance().getReference("mensajes");

        listaMensajes = new ArrayList<>();
        adapter = new MensajesAdapter(listaMensajes, currentUserEmail); // Pasamos el email
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
}