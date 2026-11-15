import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Ejercicio4America {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        crearPersonasPaises();
        mostrarPersonasPaises();

        int opcion;

        do {
            System.out.println("\n***** AMERICA *****");
            System.out.println("1. Consultar edad de una persona");
            System.out.println("2. Listar personas por edad");
            System.out.println("3. Listar personas por orden alfabetico");
            System.out.println("4. Insertar persona");
            System.out.println("5. Salir");
            System.out.print("Opcion: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    consultarEdad(sc);
                    break;
                case 2:
                    listarPorEdad(sc);
                    break;
                case 3:
                    listarOrdenAlfabetico();
                    break;
                case 4:
                    insertarPersona(sc);
                    break;
                case 5:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion incorrecta");
            }

        } while (opcion != 5);
    }

    public static void crearPersonasPaises() {

        String crear = "CREATE TABLE IF NOT EXISTS PersonasPaises ("
                + "Id INT PRIMARY KEY, "
                + "Nombre VARCHAR(15), "
                + "Apellido VARCHAR(15), "
                + "Edad INT, "
                + "NombrePais VARCHAR(15), "
                + "Tamanio VARCHAR(15))";

        String personas = "SELECT * FROM Persona";

        String pais = "SELECT Nombre, Tamanio FROM Pais WHERE Id = ?";

        String insertar = "INSERT INTO PersonasPaises "
                + "(Id, Nombre, Apellido, Edad, NombrePais, Tamanio) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(crear);
            stmt.executeUpdate("DELETE FROM PersonasPaises");

            try (ResultSet rs = stmt.executeQuery(personas);
                 PreparedStatement buscarPais = con.prepareStatement(pais);
                 PreparedStatement insertarDatos = con.prepareStatement(insertar)) {

                while (rs.next()) {

                    int id = rs.getInt("Id");
                    String nombre = rs.getString("Nombre");
                    String apellido = rs.getString("Apellido");
                    int edad = rs.getInt("Edad");
                    int idPais = rs.getInt("Pais");

                    buscarPais.setInt(1, idPais);

                    try (ResultSet rsPais = buscarPais.executeQuery()) {

                        if (rsPais.next()) {

                            insertarDatos.setInt(1, id);
                            insertarDatos.setString(2, nombre);
                            insertarDatos.setString(3, apellido);
                            insertarDatos.setInt(4, edad);
                            insertarDatos.setString(5, rsPais.getString("Nombre"));
                            insertarDatos.setString(6, rsPais.getString("Tamanio"));

                            insertarDatos.executeUpdate();
                        }
                    }
                }
            }

            stmt.executeUpdate(
                    "UPDATE PersonasPaises SET Edad = Edad + 1 "
                            + "WHERE NombrePais = 'Costa Rica'");

            System.out.println("Tabla PersonasPaises creada correctamente");

        } catch (SQLException e) {
            System.out.println("Error al crear tabla: " + e.getMessage());
        }
    }

    public static void mostrarPersonasPaises() {

        String sql = "SELECT * FROM PersonasPaises";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n***** PERSONAS Y PAISES *****");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("Id") + " | "
                                + rs.getString("Nombre") + " | "
                                + rs.getString("Apellido") + " | "
                                + rs.getInt("Edad") + " años | "
                                + rs.getString("NombrePais") + " | "
                                + rs.getString("Tamanio"));
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar datos: " + e.getMessage());
        }
    }

    public static void consultarEdad(Scanner sc) {

        System.out.print("Nombre de la persona: ");
        String nombre = sc.nextLine();

        String sql = "SELECT Nombre, Apellido, Edad FROM Persona "
                + "WHERE Nombre = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nombre);

            try (ResultSet rs = stmt.executeQuery()) {

                boolean encontrado = false;

                while (rs.next()) {

                    encontrado = true;

                    System.out.println(
                            rs.getString("Nombre") + " "
                                    + rs.getString("Apellido") + " | Edad: "
                                    + rs.getInt("Edad"));
                }

                if (!encontrado) {
                    System.out.println("Persona no encontrada");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar edad: " + e.getMessage());
        }
    }

    public static void listarPorEdad(Scanner sc) {

        System.out.print("Edad: ");
        int edad = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT * FROM Persona WHERE Edad = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, edad);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    System.out.println(
                            rs.getInt("Id") + " | "
                                    + rs.getString("Nombre") + " "
                                    + rs.getString("Apellido") + " | "
                                    + rs.getInt("Edad") + " años");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar personas: " + e.getMessage());
        }
    }

    public static void listarOrdenAlfabetico() {

        String sql = "SELECT * FROM Persona ORDER BY Nombre, Apellido";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n***** PERSONAS ORDENADAS *****");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("Id") + " | "
                                + rs.getString("Nombre") + " "
                                + rs.getString("Apellido") + " | "
                                + rs.getInt("Edad") + " años");
            }

        } catch (SQLException e) {
            System.out.println("Error al ordenar: " + e.getMessage());
        }
    }

    public static void insertarPersona(Scanner sc) {

        System.out.print("ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Apellido: ");
        String apellido = sc.nextLine();

        System.out.print("Edad: ");
        int edad = sc.nextInt();

        System.out.print("ID del pais: ");
        int pais = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO Persona "
                + "(Id, Nombre, Apellido, Edad, Pais) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido);
            stmt.setInt(4, edad);
            stmt.setInt(5, pais);

            int insertados = stmt.executeUpdate();

            System.out.println("Personas insertadas: " + insertados);

        } catch (SQLException e) {
            System.out.println("Error al insertar persona: " + e.getMessage());
        }
    }
}
