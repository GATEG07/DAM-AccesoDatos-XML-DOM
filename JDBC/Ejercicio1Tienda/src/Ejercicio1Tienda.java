import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Ejercicio1Tienda {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        crearArtFab();
        mostrarArtFab();

        int opcion;

        do {
            System.out.println("\n***** TIENDA *****");
            System.out.println("1. Listar artículos por precio");
            System.out.println("2. Agregar fabricante");
            System.out.println("3. Reducir precio de Disco Duro 300Gb");
            System.out.println("4. Eliminar artículo por nombre");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    listarArticulos(sc);
                    break;
                case 2:
                    agregarFabricante(sc);
                    break;
                case 3:
                    modificarPrecio(sc);
                    break;
                case 4:
                    eliminarArticulo(sc);
                    break;
                case 0:
                    System.out.println("Fin");
                    break;
                default:
                    System.out.println("Opción no válida");
            }

        } while (opcion != 0);

        sc.close();
    }

    public static void crearArtFab() {

        String sqlCrear = "CREATE TABLE IF NOT EXISTS ArtFab ("
                + "NombreArticulo VARCHAR(30), NombreFabricante VARCHAR(30), "
                + "Precio INT, iva DOUBLE)";

        String sqlArticulos = "SELECT Nombre, Precio, CLFAB FROM Articulos";
        String sqlFabricante = "SELECT Nombre FROM Fabricantes WHERE CLFAB = ?";
        String sqlInsertar = "INSERT INTO ArtFab "
                + "(NombreArticulo, NombreFabricante, Precio, iva) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sqlCrear);
            stmt.executeUpdate("DELETE FROM ArtFab");

            try (ResultSet rs = stmt.executeQuery(sqlArticulos);
                 PreparedStatement buscarFab = con.prepareStatement(sqlFabricante);
                 PreparedStatement insertar = con.prepareStatement(sqlInsertar)) {

                while (rs.next()) {

                    String articulo = rs.getString("Nombre");
                    int precio = rs.getInt("Precio");
                    int codigoFab = rs.getInt("CLFAB");
                    String fabricante = "";

                    buscarFab.setInt(1, codigoFab);

                    try (ResultSet rsFab = buscarFab.executeQuery()) {
                        if (rsFab.next()) {
                            fabricante = rsFab.getString("Nombre");
                        }
                    }

                    double iva = 0;

                    if (precio < 200) {
                        iva = precio * 0.10;
                    } else if (precio < 500) {
                        iva = precio * 0.08;
                    } else if (precio < 700) {
                        iva = precio * 0.06;
                    }

                    insertar.setString(1, articulo);
                    insertar.setString(2, fabricante);
                    insertar.setInt(3, precio);
                    insertar.setDouble(4, iva);

                    insertar.executeUpdate();
                }
            }

            System.out.println("Tabla ArtFab creada y rellenada correctamente");

        } catch (SQLException e) {
            System.out.println("Error al crear ArtFab: " + e.getMessage());
        }
    }

    public static void mostrarArtFab() {

        String sql = "SELECT * FROM ArtFab";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n***** TABLA ARTFAB *****");

            while (rs.next()) {
                System.out.println(rs.getString("NombreArticulo") + " | "
                        + rs.getString("NombreFabricante") + " | "
                        + rs.getInt("Precio") + " euros | IVA: "
                        + rs.getDouble("iva") + " euros");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar ArtFab: " + e.getMessage());
        }
    }

    public static void listarArticulos(Scanner sc) {

        try {
            System.out.print("Precio máximo: ");
            double precio = Double.parseDouble(sc.nextLine());

            String sql = "SELECT Nombre, Precio FROM Articulos WHERE Precio < ?";

            try (Connection con = ConexionDB.getConexion();
                 PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setDouble(1, precio);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getString("Nombre") + " | "
                                + rs.getInt("Precio") + " euros");
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Precio no válido");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public static void agregarFabricante(Scanner sc) {

        System.out.print("Nombre del fabricante: ");
        String nombre = sc.nextLine();

        if (nombre.length() == 0) {
            System.out.println("El nombre está vacío");
            return;
        }

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CLFAB FROM Fabricantes")) {

            int mayor = 0;

            while (rs.next()) {
                int codigo = rs.getInt("CLFAB");

                if (codigo > mayor) {
                    mayor = codigo;
                }
            }

            String sql = "INSERT INTO Fabricantes (CLFAB, Nombre) VALUES (?, ?)";

            try (PreparedStatement insertar = con.prepareStatement(sql)) {
                insertar.setInt(1, mayor + 1);
                insertar.setString(2, nombre);

                System.out.println("Fabricantes añadidos: "
                        + insertar.executeUpdate());
            }

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public static void modificarPrecio(Scanner sc) {

        try {
            System.out.print("Porcentaje de descuento: ");
            double porcentaje = Double.parseDouble(sc.nextLine());

            if (porcentaje < 0 || porcentaje > 100) {
                System.out.println("Porcentaje incorrecto");
                return;
            }

            String sql = "UPDATE Articulos SET Precio = Precio * (1 - ? / 100) "
                    + "WHERE Nombre = 'Disco Duro 300Gb'";

            try (Connection con = ConexionDB.getConexion();
                 PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setDouble(1, porcentaje);

                System.out.println("Artículos modificados: "
                        + stmt.executeUpdate());
            }

        } catch (NumberFormatException e) {
            System.out.println("Porcentaje incorrecto");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public static void eliminarArticulo(Scanner sc) {

        System.out.print("Nombre del artículo: ");
        String nombre = sc.nextLine();

        String sql = "DELETE FROM Articulos WHERE Nombre = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nombre);

            System.out.println("Artículos eliminados: "
                    + stmt.executeUpdate());

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }
}
