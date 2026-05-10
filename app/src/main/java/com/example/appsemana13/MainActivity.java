package com.example.appsemana13;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Window;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // update Nestor: la pantalla principal ahora administra chats dinamicos en memoria para poder filtrar y actualizar la lista.
    private ChatsAdapter adapter;
    private final Map<String, ChatResumen> chatsPorCorreo = new LinkedHashMap<>();
    private DatabaseReference database;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance().getReference();

        setupSystemInsets();
        setupRecycler();
        setupSearch();
        setupNavigation();
        cargarChats();
    }

    private void setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecycler() {
        // update Nestor: se reemplazo la tarjeta fija por un RecyclerView para mostrar multiples conversaciones.
        RecyclerView recyclerView = findViewById(R.id.chats_recycler);
        adapter = new ChatsAdapter(this::openChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        // update Nestor: el buscador ahora filtra chats por nombre, correo o ultimo mensaje mientras el usuario escribe.
        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupNavigation() {
        findViewById(R.id.btn_new_chat).setOnClickListener(v -> mostrarDialogoNuevoChat());
        findViewById(R.id.bottom_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void cargarChats() {
        // update Nestor: los chats se cargan desde Firebase usando la lista guardada en el perfil del usuario actual.
        database.child("usuarios")
                .child(generarKeyUsuario(currentUserEmail))
                .child("chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatsPorCorreo.clear();
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String correo = chatSnapshot.child("correo").getValue(String.class);
                            String nombre = chatSnapshot.child("nombre").getValue(String.class);
                            if (correo != null && !correo.trim().isEmpty()) {
                                agregarChatEnMemoria(correo, nombre);
                            }
                        }
                        asegurarChatInicialSiHaceFalta();
                        publicarChats();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void asegurarChatInicialSiHaceFalta() {
        // update Nestor: se conserva un chat inicial de respaldo para que la app siga funcionando si aun no hay chats guardados.
        if (!chatsPorCorreo.isEmpty()) {
            return;
        }
        String receptor = currentUserEmail.equals(Constantes.USUARIO_1) ? Constantes.USUARIO_2 : Constantes.USUARIO_1;
        agregarChatEnMemoria(receptor, receptor);
    }

    private void agregarChatEnMemoria(String correo, String nombre) {
        String nombreVisible = nombre == null || nombre.trim().isEmpty() ? correo : nombre;
        ChatResumen chat = new ChatResumen(correo, nombreVisible);
        chatsPorCorreo.put(correo, chat);
        cargarPerfilContacto(chat);
        cargarResumenMensajes(chat);
    }

    private void cargarPerfilContacto(ChatResumen chat) {
        database.child("usuarios")
                .child(generarKeyUsuario(chat.getCorreo()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            chat.setNombre(nombre);
                            adapter.actualizarChat(chat);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void cargarResumenMensajes(ChatResumen chat) {
        // update Nestor: cada tarjeta escucha su sala de mensajes para mostrar contador, ultimo mensaje y hora reales.
        String salaId = generarSalaId(currentUserEmail, chat.getCorreo());
        database.child("mensajes")
                .child(salaId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long total = snapshot.getChildrenCount();
                        chat.setTotalMensajes(total);

                        if (total <= 0) {
                            chat.setUltimoMensaje("Sin mensajes todavía");
                            chat.setHora("");
                        } else {
                            Mensaje ultimoMensaje = null;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                ultimoMensaje = data.getValue(Mensaje.class);
                            }
                            if (ultimoMensaje != null) {
                                chat.setUltimoMensaje(ultimoMensaje.getTexto());
                                chat.setHora(formatHora(ultimoMensaje.getTimestamp()));
                            }
                        }
                        adapter.actualizarChat(chat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void publicarChats() {
        adapter.setChats(new ArrayList<>(chatsPorCorreo.values()));
    }

    private void mostrarDialogoNuevoChat() {
        // update Nestor: el boton + abre un dialogo oscuro para buscar usuarios existentes por correo.
        Dialog dialog = crearDialogo(R.layout.dialog_new_chat);
        EditText input = dialog.findViewById(R.id.dialog_email_input);

        dialog.findViewById(R.id.dialog_cancel_new_chat).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.dialog_create_new_chat).setOnClickListener(v -> {
            String correo = input.getText().toString().trim();
            if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (correo.equalsIgnoreCase(currentUserEmail)) {
                Toast.makeText(this, "No puedes crear un chat contigo mismo", Toast.LENGTH_SHORT).show();
                return;
            }
            buscarUsuarioParaChat(correo, dialog);
        });

        dialog.show();
    }

    private void buscarUsuarioParaChat(String correo, Dialog dialog) {
        // update Nestor: antes de crear un chat se valida que el correo exista como usuario registrado en Firebase.
        database.child("usuarios")
                .child(generarKeyUsuario(correo))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(MainActivity.this, "No existe un usuario con ese correo", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String nombre = snapshot.child("nombre").getValue(String.class);
                        if (nombre == null || nombre.trim().isEmpty()) {
                            nombre = correo;
                        }
                        guardarChat(correo, nombre, dialog);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error al buscar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarChat(String correo, String nombre, Dialog dialog) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("correo", correo);
        chat.put("nombre", nombre);

        database.child("usuarios")
                .child(generarKeyUsuario(currentUserEmail))
                .child("chats")
                .child(generarKeyUsuario(correo))
                .updateChildren(chat)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    abrirChat(correo);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al crear chat", Toast.LENGTH_SHORT).show());
    }

    private void openChat(ChatResumen chat) {
        abrirChat(chat.getCorreo());
    }

    private void abrirChat(String receptorEmail) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receptor_email", receptorEmail);
        startActivity(intent);
    }

    private Dialog crearDialogo(int layoutResId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutResId);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    private String generarSalaId(String email1, String email2) {
        String e1 = generarKeyUsuario(email1);
        String e2 = generarKeyUsuario(email2);
        if (e1.compareTo(e2) < 0) {
            return e1 + "_" + e2;
        }
        return e2 + "_" + e1;
    }

    private String generarKeyUsuario(String email) {
        return email.replace(".", ",").replace("@", "_");
    }

    private String formatHora(long timestamp) {
        if (timestamp <= 0) {
            return "";
        }
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
    }
}
