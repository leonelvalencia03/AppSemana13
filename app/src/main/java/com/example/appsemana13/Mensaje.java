package com.example.appsemana13;
public class Mensaje {
    private String texto;
    private String emisor;
    private long timestamp;

    public Mensaje() {}

    public Mensaje(String texto, String emisor) {
        this.texto = texto;
        this.emisor = emisor;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters y setters
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
