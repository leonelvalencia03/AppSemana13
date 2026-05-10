package com.example.appsemana13;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity principal (Main).
 * Muestra la lista de chats del usuario.
 * Es el punto de entrada de la aplicación.
 *
 * Estructura actual:
 * - La lista visible de chats sigue siendo estática en el layout.
 * - Esta pantalla hoy solo resuelve navegación entre vistas.
 * - Si luego se conecta una fuente de datos real, este es el punto para
 *   cargar la lista y vincular cada chat con su detalle.
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

        // Verificar si hay un usuario autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupSystemInsets();
        setupNavigation();
        cargarNombreReceptor();
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
     *
     * Nota para trabajo colaborativo:
     * cada opción clicable del layout se conecta aquí para mantener
     * separada la inicialización visual de la lógica de navegación.
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
     *
     * Por ahora no se envían datos del contacto porque la UI base trabaja
     * con contenido fijo. Cuando el proyecto maneje múltiples conversaciones,
     * aquí se podrán pasar extras con id, nombre o avatar del chat.
     *
     * @param view La vista que fue presionada
     */
    private void openChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String receptor;
        if (currentUser.equals(Constantes.USUARIO_1)) {
            receptor = Constantes.USUARIO_2;
        } else {
            receptor = Constantes.USUARIO_1;
        }
        intent.putExtra("receptor_email", receptor);
        startActivity(intent);
    }

    private void cargarNombreReceptor() {
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String receptor;
        if (currentUser.equals(Constantes.USUARIO_1)) {
            receptor = Constantes.USUARIO_2;
        } else {
            receptor = Constantes.USUARIO_1;
        }
        String iniciales = receptor.substring(0, 2).toUpperCase();
        TextView tvNombre = findViewById(R.id.chat_nombre);
        TextView tvAvatar = findViewById(R.id.chat_avatar);
        tvNombre.setText(receptor);
        tvAvatar.setText(iniciales);
        cargarContadorMensajes(currentUser, receptor);

        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(generarKeyUsuario(receptor))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            tvNombre.setText(nombre);
                            tvAvatar.setText(generarIniciales(nombre));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void cargarContadorMensajes(String currentUser, String receptor) {
        TextView tvContador = findViewById(R.id.chat_message_count);
        TextView tvUltimoMensaje = findViewById(R.id.chat_last_message);
        TextView tvHora = findViewById(R.id.chat_time);
        String salaId = generarSalaId(currentUser, receptor);

        FirebaseDatabase.getInstance().getReference("mensajes")
                .child(salaId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long total = snapshot.getChildrenCount();
                        if (total <= 0) {
                            tvContador.setVisibility(View.GONE);
                            tvUltimoMensaje.setText("Sin mensajes todavía");
                            tvHora.setText("");
                            return;
                        }

                        Mensaje ultimoMensaje = null;
                        for (DataSnapshot data : snapshot.getChildren()) {
                            ultimoMensaje = data.getValue(Mensaje.class);
                        }

                        tvContador.setVisibility(View.VISIBLE);
                        tvContador.setText(total > 99 ? "99+" : String.valueOf(total));
                        if (ultimoMensaje != null) {
                            tvUltimoMensaje.setText(ultimoMensaje.getTexto());
                            tvHora.setText(formatHora(ultimoMensaje.getTimestamp()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private String generarSalaId(String email1, String email2) {
        String e1 = email1.replace(".", ",").replace("@", "_");
        String e2 = email2.replace(".", ",").replace("@", "_");
        if (e1.compareTo(e2) < 0) {
            return e1 + "_" + e2;
        }
        return e2 + "_" + e1;
    }

    private String formatHora(long timestamp) {
        if (timestamp <= 0) {
            return "";
        }
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
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
