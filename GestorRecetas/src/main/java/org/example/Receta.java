package org.example;

public class Receta {

    private int id;
    private String nombre;
    private String ingredientes;
    private int tiempoPreparacion;

    public Receta(int id, String nombre, String ingredientes, int tiempoPreparacion) {
        this.id = id;
        this.nombre = nombre;
        this.ingredientes = ingredientes;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    @Override
    public String toString() {
        return  "ID: " + id +
                "\nNombre: " + nombre +
                "\nIngredientes: " + ingredientes +
                "\nTiempo: " + tiempoPreparacion + " minutos";
    }
}
