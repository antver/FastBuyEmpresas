package com.fastbuy.fastbuyempresas.Entidades;

import java.io.Serializable;

public class RDPedidos {
    private String empresa;
    private String id;
    private String ubicacion;

    public RDPedidos() {

    }

    public RDPedidos(String empresa, String id, String ubicacion) {
        this.empresa = empresa;
        this.id = id;
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
