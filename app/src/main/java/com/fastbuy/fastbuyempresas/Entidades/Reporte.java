package com.fastbuy.fastbuyempresas.Entidades;

import java.io.Serializable;

public class Reporte implements Serializable {
    private String fecha;
    private String nproducto;
    private String nprecio;
    private String hora;

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNproducto() {
        return nproducto;
    }

    public void setNproducto(String nproducto) {
        this.nproducto = nproducto;
    }

    public String getNprecio() {
        return nprecio;
    }

    public void setNprecio(String nprecio) {
        this.nprecio = nprecio;
    }
}
