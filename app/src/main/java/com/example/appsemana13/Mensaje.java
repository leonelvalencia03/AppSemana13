package com.example.appsemana13;

public class Mensaje {
    private String texto;
    private String emisor; // para distinguir quién envía el mensaje

    public Mensaje() {
        // Requerido por Firebase
    }

    public Mensaje(String texto, String emisor) {
        this.texto = texto;
        this.emisor = emisor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }
}
