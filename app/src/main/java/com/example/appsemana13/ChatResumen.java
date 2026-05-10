package com.example.appsemana13;

public class ChatResumen {
    private String correo;
    private String nombre;
    private String ultimoMensaje = "Sin mensajes todavía";
    private String hora = "";
    private long totalMensajes = 0;

    public ChatResumen(String correo, String nombre) {
        this.correo = correo;
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public long getTotalMensajes() {
        return totalMensajes;
    }

    public void setTotalMensajes(long totalMensajes) {
        this.totalMensajes = totalMensajes;
    }
}
