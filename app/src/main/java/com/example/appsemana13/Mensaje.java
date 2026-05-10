package com.example.appsemana13;
public class Mensaje {
    private String id; // update Nestor: campo usado para identificar el mensaje en Firebase al editar o eliminar.
    private String texto;
    private String emisor;
    // update Nestor: nombre visible separado del correo para mostrar remitentes sin exponer el login.
    private String nombreEmisor;
    private long timestamp;

    public Mensaje() {}

    public Mensaje(String texto, String emisor) {
        this.texto = texto;
        this.emisor = emisor;
        this.timestamp = System.currentTimeMillis();
    }

    public Mensaje(String texto, String emisor, String nombreEmisor) {
        this.texto = texto;
        this.emisor = emisor;
        this.nombreEmisor = nombreEmisor;
        this.timestamp = System.currentTimeMillis();
    }

    // update Nestor: getters y setters necesarios para que Firebase serialice y lea el modelo completo.
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }
    public String getNombreEmisor() { return nombreEmisor; }
    public void setNombreEmisor(String nombreEmisor) { this.nombreEmisor = nombreEmisor; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
