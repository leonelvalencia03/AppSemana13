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
    private String currentUser;
    private OnMessageClickListener listener;

    public interface OnMessageClickListener {
        void onMessageLongClick(Mensaje mensaje);
    }

    public MensajesAdapter(List<Mensaje> listaMensajes, String currentUser, OnMessageClickListener listener) {
        this.listaMensajes = listaMensajes;
        this.currentUser = currentUser;
        this.listener = listener;
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

        LinearLayout.LayoutParams paramsNombre = (LinearLayout.LayoutParams) holder.txtNombreEmisor.getLayoutParams();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.txtMensaje.getLayoutParams();

        if (mensaje.getEmisor().equals(currentUser)) {
            paramsNombre.gravity = Gravity.END;
            params.gravity = Gravity.END;
            holder.txtNombreEmisor.setVisibility(View.GONE);
            holder.txtMensaje.setBackgroundResource(R.drawable.bg_message_outgoing);
            holder.txtMensaje.setTextColor(Color.WHITE);
        } else {
            paramsNombre.gravity = Gravity.START;
            params.gravity = Gravity.START;
            holder.txtNombreEmisor.setVisibility(View.VISIBLE);
            holder.txtNombreEmisor.setText(mensaje.getEmisor());
            holder.txtMensaje.setBackgroundResource(R.drawable.bg_message_incoming);
            holder.txtMensaje.setTextColor(Color.BLACK);
        }
        holder.txtNombreEmisor.setLayoutParams(paramsNombre);
        holder.txtMensaje.setLayoutParams(params);

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onMessageLongClick(mensaje);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;
        TextView txtNombreEmisor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtContenidoMensaje);
            txtNombreEmisor = itemView.findViewById(R.id.txtNombreEmisor);
        }
    }
}
