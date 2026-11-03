package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class AdivinaPersonaje {

    private static final String RUTA_FICHERO = "src/main/resources/";
    private static final String NOMBRE_PERSONAJES = "personajes.json";
    private static final String NOMBRE_RANKING = "ranking.json";

    public static void main(String[] args) {

        Gson gson = new Gson();
        Personaje[] personajes;

        try (FileReader fichero = new FileReader(RUTA_FICHERO + NOMBRE_PERSONAJES)) {

            personajes = gson.fromJson(fichero, Personaje[].class);

        } catch (IOException e) {
            System.out.println("Error al leer personajes.json");
            return;
        }

        if (personajes == null || personajes.length == 0) {
            System.out.println("No hay personajes en el fichero");
            return;
        }

        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        System.out.println("***** ADIVINA EL PERSONAJE *****");

        System.out.print("Nombre del jugador: ");
        String jugador = sc.nextLine();

        int puntuacion = 0;
        String repetir;

        do {

            Personaje personaje = personajes[random.nextInt(personajes.length)];

            boolean acertado = false;

            System.out.println();
            System.out.println("Nuevo personaje. Intenta adivinarlo ");

            for (int i = 0; i < personaje.getPistas().length; i++) {

                System.out.println("Pista " + (i + 1) + ": " + personaje.getPistas()[i]);

                System.out.print("Tu respuesta: ");
                String respuesta = sc.nextLine();

                if (respuesta.trim().equalsIgnoreCase(personaje.getNombre())) {

                    int puntos = personaje.getPistas().length - i;

                    puntuacion = puntuacion + puntos;
                    acertado = true;

                    System.out.println("Correcto. Has ganado " + puntos + " puntos.");
                    break;

                } else {
                    System.out.println("Respuesta incorrecta");
                }
            }

            if (!acertado) {
                System.out.println("No has acertado. Era " + personaje.getNombre() + ".");
            }

            System.out.println("Puntuacion total: " + puntuacion);

            System.out.print("Quieres volver a jugar? (si/no): ");
            repetir = sc.nextLine();

        } while (repetir.equalsIgnoreCase("si"));

        guardarRanking(jugador, puntuacion);

        sc.close();
    }

    public static void guardarRanking(String jugador, int puntuacion) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File archivo = new File(RUTA_FICHERO + NOMBRE_RANKING);

        Resultado[] ranking = new Resultado[0];

        if (archivo.exists()) {

            try (FileReader fichero = new FileReader(archivo)) {

                Resultado[] anteriores = gson.fromJson(fichero, Resultado[].class);

                if (anteriores != null) {
                    ranking = anteriores;
                }

            } catch (IOException e) {
                System.out.println("Error al leer el ranking ");
                return;
            }
        }

        Resultado[] nuevoRanking = new Resultado[ranking.length + 1];

        for (int i = 0; i < ranking.length; i++) {
            nuevoRanking[i] = ranking[i];
        }

        nuevoRanking[ranking.length] = new Resultado(jugador, puntuacion);

        for (int i = 0; i < nuevoRanking.length - 1; i++) {

            for (int j = i + 1; j < nuevoRanking.length; j++) {

                if (nuevoRanking[j].getPuntos() > nuevoRanking[i].getPuntos()) {

                    Resultado auxiliar = nuevoRanking[i];
                    nuevoRanking[i] = nuevoRanking[j];
                    nuevoRanking[j] = auxiliar;
                }
            }
        }

        try (FileWriter fichero = new FileWriter(archivo)) {

            gson.toJson(nuevoRanking, fichero);

            System.out.println();
            System.out.println("Ranking guardado en ranking.json.");

            System.out.println("***** RANKING *****");

            for (int i = 0; i < nuevoRanking.length; i++) {

                System.out.println((i + 1) + ". "
                        + nuevoRanking[i].getJugador()
                        + " - " + nuevoRanking[i].getPuntos() + " puntos");
            }

        } catch (IOException e) {
            System.out.println("Error al guardar el ranking ");
        }
    }
}