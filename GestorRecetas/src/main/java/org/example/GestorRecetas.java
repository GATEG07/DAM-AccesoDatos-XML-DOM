package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GestorRecetas {

    private static final String RUTA_FICHERO = "src/main/resources/";
    private static final String NOMBRE_FICHERO = "recetas.dat";
    private static final int TAM_REGISTRO = 188;
    private static final int POS_TIEMPO = 184;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        File archivo = new File(RUTA_FICHERO + NOMBRE_FICHERO);
        if (!archivo.exists()) {
            crearCatalogo();
        }

        cargarConfiguracion();

        int opcion;
        int ultimaOpcion = 0;

        do {
            System.out.println("\n***** GESTOR DE RECETAS *****");
            System.out.println("1. Mostrar recetas");
            System.out.println("2. Buscar receta por ID");
            System.out.println("3. Modificar tiempo de preparacion");
            System.out.println("4. Añadir receta");
            System.out.println("5. Exportar recetas a XML");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            try {
                opcion = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                sc.nextLine();
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    mostrarRecetas();
                    ultimaOpcion = opcion;
                    break;
                case 2:
                    buscarReceta(sc);
                    ultimaOpcion = opcion;
                    break;
                case 3:
                    modificarTiempo(sc);
                    ultimaOpcion = opcion;
                    break;
                case 4:
                    anadirReceta(sc);
                    ultimaOpcion = opcion;
                    break;
                case 5:
                    exportarXML();
                    ultimaOpcion = opcion;
                    break;
                case 0:
                    guardarConfiguracion(ultimaOpcion, contarRecetas());
                    System.out.println("Fin del programa");
                    break;
                default:
                    System.out.println("Opcion no válida");
            }
        } while (opcion != 0);

        sc.close();
    }

    // Crear el catálogo inicial de recetas
    public static void crearCatalogo() {
        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "rw")) {
            escribirReceta(fichero, new Receta(1, "Tortilla de patatas", "Patatas, huevos, cebolla", 30));
            escribirReceta(fichero, new Receta(2, "Ensalada mixta", "Lechuga, tomate, atún", 10));
            escribirReceta(fichero, new Receta(3, "Pasta con tomate", "Pasta, tomate, queso", 20));
            System.out.println("Catalogo inicial creado correctamente");
        } catch (IOException e) {
            System.out.println("Error al crear el catalogo: " + e.getMessage());
        }
    }

    public static void escribirReceta(RandomAccessFile fichero, Receta receta) throws IOException {
        fichero.writeInt(receta.getId());

        StringBuffer nombre = new StringBuffer(receta.getNombre());
        nombre.setLength(30);
        fichero.writeChars(nombre.toString());

        StringBuffer ingredientes = new StringBuffer(receta.getIngredientes());
        ingredientes.setLength(60);
        fichero.writeChars(ingredientes.toString());

        fichero.writeInt(receta.getTiempoPreparacion());
    }

    public static Receta leerReceta(RandomAccessFile fichero) throws IOException {
        int id = fichero.readInt();

        char[] nombre = new char[30];
        for (int i = 0; i < nombre.length; i++) {
            nombre[i] = fichero.readChar();
        }

        char[] ingredientes = new char[60];
        for (int i = 0; i < ingredientes.length; i++) {
            ingredientes[i] = fichero.readChar();
        }

        int tiempo = fichero.readInt();

        return new Receta(id, new String(nombre).trim(), new String(ingredientes).trim(), tiempo);
    }

    public static void mostrarRecetas() {
        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "r")) {
            while (fichero.getFilePointer() < fichero.length()) {
                Receta receta = leerReceta(fichero);
                System.out.println("\n" + receta);
            }
        } catch (IOException e) {
            System.out.println("Error al mostrar las recetas: " + e.getMessage());
        }
    }

    public static void buscarReceta(Scanner sc) {
        System.out.print("ID de la receta: ");
        int id;
        try {
            id = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("ID no valido");
            return;
        }

        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "r")) {
            while (fichero.getFilePointer() < fichero.length()) {
                Receta receta = leerReceta(fichero);
                if (receta.getId() == id) {
                    System.out.println("\n" + receta);
                    return;
                }
            }
            System.out.println("Receta no encontrada");
        } catch (IOException e) {
            System.out.println("Error al buscar la receta: " + e.getMessage());
        }
    }

    // Modificar únicamente el tiempo de preparación
    public static void modificarTiempo(Scanner sc) {
        System.out.print("ID de la receta: ");
        int id;
        try {
            id = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("ID no valido");
            return;
        }

        System.out.print("Nuevo tiempo en minutos: ");
        int nuevoTiempo;
        try {
            nuevoTiempo = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("Tiempo no valido.");
            return;
        }

        if (nuevoTiempo <= 0) {
            System.out.println("El tiempo debe ser mayor que 0");
            return;
        }

        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "rw")) {
            while (fichero.getFilePointer() < fichero.length()) {
                long posicion = fichero.getFilePointer();
                int idLeido = fichero.readInt();

                if (idLeido == id) {
                    fichero.seek(posicion + POS_TIEMPO);
                    fichero.writeInt(nuevoTiempo);
                    System.out.println("Tiempo modificado correctamente");
                    return;
                }
                fichero.seek(posicion + TAM_REGISTRO);
            }
            System.out.println("Receta no encontrada");
        } catch (IOException e) {
            System.out.println("Error al modificar el tiempo: " + e.getMessage());
        }
    }

    public static void anadirReceta(Scanner sc) {
        System.out.print("ID de la nueva receta: ");
        int id;
        try {
            id = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            sc.nextLine();
            System.out.println("ID no valido");
            return;
        }

        if (id <= 0) {
            System.out.println("El ID debe ser mayor que 0");
            return;
        }

        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "rw")) {
            while (fichero.getFilePointer() < fichero.length()) {
                long posicion = fichero.getFilePointer();
                int idLeido = fichero.readInt();
                if (idLeido == id) {
                    System.out.println("Ya existe una receta con ese ID");
                    return;
                }
                fichero.seek(posicion + TAM_REGISTRO);
            }

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Ingredientes: ");
            String ingredientes = sc.nextLine();
            System.out.print("Tiempo de preparacion en minutos: ");

            int tiempo;
            try {
                tiempo = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                sc.nextLine();
                System.out.println("Tiempo no valido");
                return;
            }

            if (nombre.length() == 0 || ingredientes.length() == 0 || nombre.length() > 30
                    || ingredientes.length() > 60 || tiempo <= 0) {
                System.out.println("Datos no validos. Nombre máximo 30 caracteres, "
                        + "ingredientes maximo 60 y tiempo mayor que 0");
                return;
            }

            fichero.seek(fichero.length());
            escribirReceta(fichero, new Receta(id, nombre, ingredientes, tiempo));
            System.out.println("Receta añadida correctamente");
        } catch (IOException e) {
            System.out.println("Error al añadir la receta: " + e.getMessage());
        }
    }

    public static int contarRecetas() {
        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "r")) {
            return (int) (fichero.length() / TAM_REGISTRO);
        } catch (IOException e) {
            System.out.println("Error al contar las recetas: " + e.getMessage());
            return 0;
        }
    }

    public static void exportarXML() {
        try (RandomAccessFile fichero = new RandomAccessFile(RUTA_FICHERO + NOMBRE_FICHERO, "r")) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.newDocument();

            Element recetas = documento.createElement("Recetas");
            documento.appendChild(recetas);

            while (fichero.getFilePointer() < fichero.length()) {
                Receta receta = leerReceta(fichero);

                Element elementoReceta = documento.createElement("Receta");
                elementoReceta.setAttribute("id", "" + receta.getId());
                recetas.appendChild(elementoReceta);

                Element nombre = documento.createElement("Nombre");
                nombre.setTextContent(receta.getNombre());
                elementoReceta.appendChild(nombre);

                Element ingredientes = documento.createElement("Ingredientes");
                ingredientes.setTextContent(receta.getIngredientes());
                elementoReceta.appendChild(ingredientes);

                Element tiempo = documento.createElement("TiempoPreparacion");
                tiempo.setTextContent("" + receta.getTiempoPreparacion());
                elementoReceta.appendChild(tiempo);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(documento),
                    new StreamResult(new File(RUTA_FICHERO + "recetas.xml")));

            System.out.println("recetas.xml creado correctamente");
        } catch (Exception e) {
            System.out.println("Error al exportar el XML: " + e.getMessage());
        }
    }

    public static void guardarConfiguracion(int ultimaOpcion, int totalRecetas) {
        Configuracion configuracion = new Configuracion(ultimaOpcion, totalRecetas);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter fichero = new FileWriter(RUTA_FICHERO + "configuracion.json")) {
            gson.toJson(configuracion, fichero);
            System.out.println("Configuracion guardada correctamente");
        } catch (IOException e) {
            System.out.println("Error al guardar la configuracion: " + e.getMessage());
        }
    }

    public static void cargarConfiguracion() {
        File archivo = new File(RUTA_FICHERO + "configuracion.json");
        if (!archivo.exists()) {
            return;
        }

        Gson gson = new Gson();
        try (FileReader fichero = new FileReader(archivo)) {
            Configuracion configuracion = gson.fromJson(fichero, Configuracion.class);
            if (configuracion != null) {
                System.out.println("Ultima opción utilizada: " + configuracion.getUltimaOpcion());
                System.out.println("Recetas de la última ejecucion: " + configuracion.getTotalRecetas());
            }
        } catch (Exception e) {
            System.out.println("No se ha podido leer la configuracion anterior");
        }
    }
}
