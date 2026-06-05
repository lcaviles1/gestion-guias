package com.duoc.gestionguias.dto;

public class ActualizarGuiaRequest {

    private String key;
    private String nuevoContenido;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNuevoContenido() {
        return nuevoContenido;
    }

    public void setNuevoContenido(String nuevoContenido) {
        this.nuevoContenido = nuevoContenido;
    }
}