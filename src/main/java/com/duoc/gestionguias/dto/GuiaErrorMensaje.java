package com.duoc.gestionguias.dto;

public class GuiaErrorMensaje {

    private String fechaError;
    private String motivo;
    private GuiaMensaje guia;

    public GuiaErrorMensaje() {
    }

    public GuiaErrorMensaje(
            String fechaError,
            String motivo,
            GuiaMensaje guia
    ) {
        this.fechaError = fechaError;
        this.motivo = motivo;
        this.guia = guia;
    }

    public String getFechaError() {
        return fechaError;
    }

    public void setFechaError(String fechaError) {
        this.fechaError = fechaError;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public GuiaMensaje getGuia() {
        return guia;
    }

    public void setGuia(GuiaMensaje guia) {
        this.guia = guia;
    }
}
