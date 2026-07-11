package com.duoc.gestionguias.dto;

public class GuiaMensaje {

    private String idMensaje;
    private String fechaEncolado;
    private String archivo;
    private String transportista;
    private String cliente;
    private String direccionDestino;
    private String producto;
    private Integer cantidad;
    private String usuarioAutorizado;

    public GuiaMensaje() {
    }

    public GuiaMensaje(
            String idMensaje,
            String fechaEncolado,
            String archivo,
            String transportista,
            String cliente,
            String direccionDestino,
            String producto,
            Integer cantidad,
            String usuarioAutorizado
    ) {
        this.idMensaje = idMensaje;
        this.fechaEncolado = fechaEncolado;
        this.archivo = archivo;
        this.transportista = transportista;
        this.cliente = cliente;
        this.direccionDestino = direccionDestino;
        this.producto = producto;
        this.cantidad = cantidad;
        this.usuarioAutorizado = usuarioAutorizado;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getFechaEncolado() {
        return fechaEncolado;
    }

    public void setFechaEncolado(String fechaEncolado) {
        this.fechaEncolado = fechaEncolado;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUsuarioAutorizado() {
        return usuarioAutorizado;
    }

    public void setUsuarioAutorizado(String usuarioAutorizado) {
        this.usuarioAutorizado = usuarioAutorizado;
    }
}
