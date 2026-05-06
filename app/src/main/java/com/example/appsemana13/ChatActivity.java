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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity de Chat individual.
 * Muestra la conversación con un contacto específico.
 */
public class ChatActivity extends AppCompatActivity {

    private EditText txtEscribirMensaje;
    private ImageButton btnEnviarMensaje;
    private RecyclerView chatMessages;
    
    private MensajesAdapter adapter;
    private List<Mensaje> listaMensajes;

    private DatabaseReference dataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Asignar widgets
        txtEscribirMensaje = findViewById(R.id.txtEscribirMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        chatMessages = findViewById(R.id.chat_messages);

        // Referencia a Firebase
        dataReference = FirebaseDatabase.getInstance().getReference("mensajes");

        // Configurar RecyclerView
        listaMensajes = new ArrayList<>();
        adapter = new MensajesAdapter(listaMensajes);
        chatMessages.setLayoutManager(new LinearLayoutManager(this));
        chatMessages.setAdapter(adapter);

        // Configuración del botón de enviar
        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = txtEscribirMensaje.getText().toString().trim();
                if (!texto.isEmpty()) {
                    // Creamos el objeto Mensaje
                    Mensaje mensajeObj = new Mensaje(texto, "Yo");
                    // Guardamos en Firebase
                    dataReference.push().setValue(mensajeObj);
                    // Limpiamos el campo
                    txtEscribirMensaje.setText("");
                }
            }
        });

        // Escuchar mensajes en tiempo real
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
                // Desplazar al último mensaje
                if (listaMensajes.size() > 0) {
                    chatMessages.scrollToPosition(listaMensajes.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error al leer datos de Firebase", error.toException());
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
