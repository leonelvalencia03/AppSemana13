package com.example.appsemana13;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {
    private List<Mensaje> listaMensajes;

    public MensajesAdapter(List<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
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
