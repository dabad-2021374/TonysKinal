package org.denisabad.bean;

public class Plato {
    private int codigoPlato;
    private int cantidad; 
    private String nombrePlato; 
    private String descripcion;
    private double precioPlato;
    private int codigoTipoPlato;

    public Plato() {
    }

    public Plato(int codigoPlato, int cantidad, String nombrePlato, String descripcion, double precioPlato, int codigoTipoPlato) {
        this.codigoPlato = codigoPlato;
        this.cantidad = cantidad;
        this.nombrePlato = nombrePlato;
        this.descripcion = descripcion;
        this.precioPlato = precioPlato;
        this.codigoTipoPlato = codigoTipoPlato;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombrePlato() {
        return nombrePlato;
    }

    public void setNombrePlato(String nombrePlato) {
        this.nombrePlato = nombrePlato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioPlato() {
        return precioPlato;
    }

    public void setPrecioPlato(double precioPlato) {
        this.precioPlato = precioPlato;
    }

    public int getCodigoTipoPlato() {
        return codigoTipoPlato;
    }

    public void setCodigoTipoPlato(int codigoTipoPlato) {
        this.codigoTipoPlato = codigoTipoPlato;
    }
    
    @Override
    public String toString() {
        return codigoPlato + " - " + nombrePlato;
    }
}
