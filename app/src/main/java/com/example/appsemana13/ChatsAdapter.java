package com.example.appsemana13;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    public interface OnChatClickListener {
        void onChatClick(ChatResumen chat);
    }

    private final List<ChatResumen> chats = new ArrayList<>();
    private final List<ChatResumen> chatsFiltrados = new ArrayList<>();
    private final OnChatClickListener listener;
    private String query = "";

    public ChatsAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatResumen chat = chatsFiltrados.get(position);
        holder.avatar.setText(generarIniciales(chat.getNombre()));
        holder.nombre.setText(chat.getNombre());
        holder.ultimoMensaje.setText(chat.getUltimoMensaje());
        holder.hora.setText(chat.getHora());

        if (chat.getTotalMensajes() > 0) {
            holder.contador.setVisibility(View.VISIBLE);
            holder.contador.setText(chat.getTotalMensajes() > 99 ? "99+" : String.valueOf(chat.getTotalMensajes()));
        } else {
            holder.contador.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chatsFiltrados.size();
    }

    public void setChats(List<ChatResumen> nuevosChats) {
        chats.clear();
        chats.addAll(nuevosChats);
        filtrar(query);
    }

    public void actualizarChat(ChatResumen chatActualizado) {
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getCorreo().equalsIgnoreCase(chatActualizado.getCorreo())) {
                chats.set(i, chatActualizado);
                filtrar(query);
                return;
            }
        }
    }

    public void filtrar(String texto) {
        query = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        chatsFiltrados.clear();

        for (ChatResumen chat : chats) {
            String nombre = chat.getNombre() == null ? "" : chat.getNombre().toLowerCase(Locale.ROOT);
            String correo = chat.getCorreo() == null ? "" : chat.getCorreo().toLowerCase(Locale.ROOT);
            String ultimo = chat.getUltimoMensaje() == null ? "" : chat.getUltimoMensaje().toLowerCase(Locale.ROOT);

            if (query.isEmpty() || nombre.contains(query) || correo.contains(query) || ultimo.contains(query)) {
                chatsFiltrados.add(chat);
            }
        }
        notifyDataSetChanged();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView avatar;
        TextView nombre;
        TextView ultimoMensaje;
        TextView hora;
        TextView contador;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.chat_avatar);
            nombre = itemView.findViewById(R.id.chat_nombre);
            ultimoMensaje = itemView.findViewById(R.id.chat_last_message);
            hora = itemView.findViewById(R.id.chat_time);
            contador = itemView.findViewById(R.id.chat_message_count);
        }
    }
}
