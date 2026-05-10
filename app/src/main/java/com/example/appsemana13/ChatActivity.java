package com.example.appsemana13;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    // update Nestor: el chat usa nombre visible del usuario y dialogos personalizados para mantener la linea visual oscura.

    private EditText txtEscribirMensaje;
    private ImageButton btnEnviarMensaje;
    private RecyclerView chatMessages;
    private MensajesAdapter adapter;
    private List<Mensaje> listaMensajes;
    private DatabaseReference dataReference;
    private String currentUserEmail;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        txtEscribirMensaje = findViewById(R.id.txtEscribirMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        chatMessages = findViewById(R.id.chat_messages);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        currentUserEmail = currentUser.getEmail();
        currentUserName = obtenerNombreUsuario(currentUser);

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
        cargarPerfilReceptor(receptor, tvHeaderNombre, tvHeaderAvatar);

        listaMensajes = new ArrayList<>();
        // update Nestor: se conecta el gesto de mantener presionado para editar o eliminar mensajes propios.
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
                Mensaje mensajeObj = new Mensaje(texto, currentUserEmail, currentUserName);
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
                        m.setId(data.getKey()); // update Nestor: se guarda el ID de Firebase para editar o eliminar el mensaje correcto.
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
        // update Nestor: se reemplazo el AlertDialog blanco por un dialogo oscuro personalizado para opciones del mensaje.
        Dialog dialog = crearDialogo(R.layout.dialog_message_options);

        dialog.findViewById(R.id.dialog_edit_message).setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoEditar(mensaje);
        });

        dialog.findViewById(R.id.dialog_delete_message).setOnClickListener(v -> {
            dialog.dismiss();
            eliminarMensaje(mensaje);
        });

        dialog.show();
    }

    private void mostrarDialogoEditar(Mensaje mensaje) {
        // update Nestor: el editor de mensajes ahora usa un dialogo personalizado acorde al diseno de la app.
        Dialog dialog = crearDialogo(R.layout.dialog_edit_message);
        EditText input = dialog.findViewById(R.id.dialog_message_input);
        input.setText(mensaje.getTexto());
        input.setSelection(input.getText().length());

        dialog.findViewById(R.id.dialog_cancel_edit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.dialog_save_edit).setOnClickListener(v -> {
            String nuevoTexto = input.getText().toString().trim();
            if (!nuevoTexto.isEmpty()) {
                editarMensaje(mensaje, nuevoTexto);
                dialog.dismiss();
            }
        });

        dialog.show();
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

    private void cargarPerfilReceptor(String email, TextView tvHeaderNombre, TextView tvHeaderAvatar) {
        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(generarKeyUsuario(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            tvHeaderNombre.setText(nombre);
                            tvHeaderAvatar.setText(generarIniciales(nombre));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private String obtenerNombreUsuario(FirebaseUser user) {
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().trim().isEmpty()) {
            return user.getDisplayName();
        }
        return currentUserEmail;
    }

    private String generarKeyUsuario(String email) {
        return email.replace(".", ",").replace("@", "_");
    }

    private String generarIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "US";
        }
        String[] palabras = nombre.trim().split("\\s+");
        if (palabras.length >= 2) {
            return (palabras[0].charAt(0) + "" + palabras[1].charAt(0)).toUpperCase();
        }
        return palabras[0].substring(0, Math.min(2, palabras[0].length())).toUpperCase();
    }
}
