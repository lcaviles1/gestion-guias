package com.duoc.gestionguias.dto;

public class GuiaRequest {

    private String transportista;
    private String cliente;
    private String direccionDestino;
    private String producto;
    private Integer cantidad;
    private String usuarioAutorizado;

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
