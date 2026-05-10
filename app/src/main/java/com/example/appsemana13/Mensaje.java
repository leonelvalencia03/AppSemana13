package com.example.appsemana13;
public class Mensaje {
    private String id; // Nuevo campo para identificar el mensaje en Firebase
    private String texto;
    private String emisor;
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

    // Getters y setters
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
