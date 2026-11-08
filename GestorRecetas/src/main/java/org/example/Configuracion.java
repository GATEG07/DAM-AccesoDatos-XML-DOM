package org.example;

public class Configuracion {

    private int ultimaOpcion;
    private int totalRecetas;

    public Configuracion() {
    }

    public Configuracion(int ultimaOpcion, int totalRecetas) {
        this.ultimaOpcion = ultimaOpcion;
        this.totalRecetas = totalRecetas;
    }

    public int getUltimaOpcion() {
        return ultimaOpcion;
    }

    public int getTotalRecetas() {
        return totalRecetas;
    }
}
