package com.example.appsemana13;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity de Chat individual.
 * Muestra la conversación con un contacto específico.
 *
 * Estructura actual:
 * - El encabezado y la barra inferior son estáticos.
 * - El cuerpo de la conversación ahora usa un RecyclerView como contenedor.
 * - Todavía no existe Adapter ni fuente de datos; la pantalla queda lista
 *   para que otro integrante conecte la lógica sin rehacer el layout.
 * 
 * Navegación:
 * - Botón atrás -> Regresa a la lista de chats (MainActivity)
 */
public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Configurar insets del sistema
        setupSystemInsets();

        // Configurar navegación
        setupNavigation();
    }

    /**
     * Configura los insets del sistema.
     */
    private void setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Configura los listeners de navegación.
     *
     * Si luego se agregan acciones para menú, envío de mensajes o carga
     * de historial, conviene mantenerlas en métodos separados para no mezclar
     * la navegación básica con la lógica del chat.
     */
    private void setupNavigation() {
        // Botón atrás para regresar
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
}
