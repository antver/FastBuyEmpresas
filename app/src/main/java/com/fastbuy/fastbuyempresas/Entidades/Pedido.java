package com.fastbuy.fastbuyempresas.Entidades;

import java.io.Serializable;

public class Pedido implements Serializable {
    private int codigo;
    private float vendido;
    private String HoraPedido;
    private String FechaPedido;
    private String tiempopreparacion;
    private int item;
    private String atendido;
    private String fpago;

    public String getFpago() {
        return fpago;
    }

    public void setFpago(String fpago) {
        this.fpago = fpago;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getAtendido() {
        return atendido;
    }

    public void setAtendido(String atendido) {
        this.atendido = atendido;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public float getVendido() {
        return vendido;
    }

    public void setVendido(float vendido) {
        this.vendido = vendido;
    }

    public String getHoraPedido() {
        return HoraPedido;
    }

    public void setHoraPedido(String horaPedido) {
        HoraPedido = horaPedido;
    }

    public String getTiempopreparacion() {
        return tiempopreparacion;
    }

    public void setTiempopreparacion(String tiempopreparacion) {
        this.tiempopreparacion = tiempopreparacion;
    }

    public String getFechaPedido() {
        return FechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        FechaPedido = fechaPedido;
    }
}
