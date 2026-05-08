package com.example.appsemana13;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {
    private List<Mensaje> listaMensajes;
    private String currentUser; // Nuevo campo

    // Constructor modificado: ahora recibe la lista y el email del usuario actual
    public MensajesAdapter(List<Mensaje> listaMensajes, String currentUser) {
        this.listaMensajes = listaMensajes;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);
        holder.txtMensaje.setText(mensaje.getTexto());

        // Obtener los layout params para cambiar la gravedad
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.txtMensaje.getLayoutParams();

        if (mensaje.getEmisor().equals(currentUser)) {
            // Mensaje propio (outgoing): derecha, fondo azul/verde, texto blanco
            params.gravity = Gravity.END;
            holder.txtMensaje.setBackgroundResource(R.drawable.bg_message_outgoing);
            holder.txtMensaje.setTextColor(Color.WHITE);
        } else {
            // Mensaje recibido (incoming): izquierda, fondo gris, texto negro
            params.gravity = Gravity.START;
            holder.txtMensaje.setBackgroundResource(R.drawable.bg_message_incoming);
            holder.txtMensaje.setTextColor(Color.BLACK);
        }
        holder.txtMensaje.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtContenidoMensaje);
        }
    }
}
