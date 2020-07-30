package com.fastbuy.fastbuyempresas.Entidades;

public class RDPedidos2 {
    private String empresa;
    private String id;
    private String estado;

    public RDPedidos2() {

    }

    public RDPedidos2(String empresa, String id, String estado) {
        this.empresa = empresa;
        this.id = id;
        this.estado = estado;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
