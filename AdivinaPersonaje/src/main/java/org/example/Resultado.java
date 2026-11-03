package org.example;

public class Resultado {

    private String jugador;
    private int puntos;

    public Resultado() {
    }

    public Resultado(String jugador, int puntos) {
        this.jugador = jugador;
        this.puntos = puntos;
    }

    public String getJugador() {
        return jugador;
    }

    public int getPuntos() {
        return puntos;
    }
}