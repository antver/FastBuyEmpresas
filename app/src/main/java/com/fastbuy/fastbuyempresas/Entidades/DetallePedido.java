package com.fastbuy.fastbuyempresas.Entidades;

import java.io.Serializable;

public class DetallePedido implements Serializable {

    private int numero;
    private Producto producto;
    private boolean esPromocion;
    private int cantidad;
    private double total;
    private float preciounit;
    private Promocion promocion;
    private String personalizacion;
    private int atendido;
    private int estado;
    private String Presentacion;

    public String getPresentacion() {
        return Presentacion;
    }

    public void setPresentacion(String presentacion) {
        Presentacion = presentacion;
    }

    public int getAtendido() {
        return atendido;
    }

    public void setAtendido(int atendido) {
        this.atendido = atendido;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public boolean isEsPromocion() {
        return esPromocion;
    }

    public void setEsPromocion(boolean esPromocion) {
        this.esPromocion = esPromocion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public float getPreciounit() {
        return preciounit;
    }

    public void setPreciounit(float preciounit) {
        this.preciounit = preciounit;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public String getPersonalizacion() {
        return personalizacion;
    }

    public void setPersonalizacion(String personalizacion) {
        this.personalizacion = personalizacion;
    }
}
