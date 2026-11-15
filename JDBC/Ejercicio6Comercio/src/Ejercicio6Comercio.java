import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ejercicio6Comercio {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n***** COMERCIO *****");
            System.out.println("1. Crear pedido");
            System.out.println("2. Ver pedidos de un cliente");
            System.out.println("3. Ver detalles de un pedido");
            System.out.println("4. Consultar stock de un producto");
            System.out.println("0. Salir");

            opcion = leerNumero(sc, "Opcion: ");

            switch (opcion) {
                case 1:
                    crearPedido(sc);
                    break;
                case 2:
                    pedidosCliente(sc);
                    break;
                case 3:
                    detallePedido(sc);
                    break;
                case 4:
                    consultarStock(sc);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion incorrecta");
            }

        } while (opcion != 0);
    }

    public static int leerNumero(Scanner sc, String mensaje) {
        System.out.print(mensaje);

        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Introduce un numero entero");
            return -1;
        }
    }

    public static void crearPedido(Scanner sc) {

        int cliente = leerNumero(sc, "ID del cliente: ");

        if (cliente <= 0) {
            System.out.println("Cliente incorrecto");
            return;
        }

        try (Connection con = ConexionDB.getConexion()) {

            String sqlCliente = "SELECT nombre FROM clientes WHERE clienteID = ?";

            try (PreparedStatement buscar = con.prepareStatement(sqlCliente)) {

                buscar.setInt(1, cliente);

                try (ResultSet rs = buscar.executeQuery()) {

                    if (!rs.next()) {
                        System.out.println("El cliente no existe");
                        return;
                    }

                    System.out.println("Cliente: " + rs.getString("nombre"));
                }
            }

            con.setAutoCommit(false);

            try {
                String sqlPedido = "INSERT INTO pedidos (clienteID, fecha) "
                        + "VALUES (?, CURRENT_DATE)";

                try (PreparedStatement insertar = con.prepareStatement(sqlPedido)) {

                    insertar.setInt(1, cliente);
                    insertar.executeUpdate();
                }

                int pedido = 0;

                try (PreparedStatement buscar = con.prepareStatement(
                        "SELECT MAX(pedidoID) AS id FROM pedidos");
                     ResultSet rs = buscar.executeQuery()) {

                    if (rs.next()) {
                        pedido = rs.getInt("id");
                    }
                }

                double total = 0;
                int numProductos = 0;
                int producto;

                do {
                    producto = leerNumero(sc, "ID del producto (0 para terminar): ");

                    if (producto == 0) {
                        break;
                    }

                    if (producto < 0) {
                        System.out.println("Producto incorrecto. Pedido cancelado");
                        con.rollback();
                        return;
                    }

                    int cantidad = leerNumero(sc, "Cantidad: ");

                    if (cantidad <= 0) {
                        System.out.println("Cantidad incorrecta. Pedido cancelado");
                        con.rollback();
                        return;
                    }

                    double precio;
                    int stock;
                    String nombre;

                    String sqlProducto = "SELECT nombre, precio, stock "
                            + "FROM productos WHERE productoID = ?";

                    try (PreparedStatement buscar = con.prepareStatement(sqlProducto)) {

                        buscar.setInt(1, producto);

                        try (ResultSet rs = buscar.executeQuery()) {

                            if (!rs.next()) {
                                System.out.println("El producto no existe. Pedido cancelado");
                                con.rollback();
                                return;
                            }

                            nombre = rs.getString("nombre");
                            precio = rs.getDouble("precio");
                            stock = rs.getInt("stock");
                        }
                    }

                    if (stock < cantidad) {
                        System.out.println("Stock insuficiente. Pedido cancelado");
                        con.rollback();
                        return;
                    }

                    String sqlRepetido = "SELECT productoID FROM detalle_pedidos "
                            + "WHERE pedidoID = ? AND productoID = ?";

                    try (PreparedStatement comprobar = con.prepareStatement(sqlRepetido)) {

                        comprobar.setInt(1, pedido);
                        comprobar.setInt(2, producto);

                        try (ResultSet rs = comprobar.executeQuery()) {

                            if (rs.next()) {
                                System.out.println("Producto repetido. Pedido cancelado");
                                con.rollback();
                                return;
                            }
                        }
                    }

                    String sqlStock = "UPDATE productos SET stock = stock - ? "
                            + "WHERE productoID = ? AND stock >= ?";

                    try (PreparedStatement actualizar = con.prepareStatement(sqlStock)) {

                        actualizar.setInt(1, cantidad);
                        actualizar.setInt(2, producto);
                        actualizar.setInt(3, cantidad);

                        if (actualizar.executeUpdate() == 0) {
                            System.out.println("Stock insuficiente. Pedido cancelado");
                            con.rollback();
                            return;
                        }
                    }

                    String sqlDetalle = "INSERT INTO detalle_pedidos "
                            + "(pedidoID, productoID, cantidad, precio_unidad) "
                            + "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement insertar = con.prepareStatement(sqlDetalle)) {

                        insertar.setInt(1, pedido);
                        insertar.setInt(2, producto);
                        insertar.setInt(3, cantidad);
                        insertar.setDouble(4, precio);
                        insertar.executeUpdate();
                    }

                    total = total + cantidad * precio;
                    numProductos++;

                    System.out.println(nombre + " | " + cantidad + " unidades | "
                            + (precio * cantidad) + " euros");

                } while (producto != 0);

                if (numProductos == 0) {
                    con.rollback();
                    System.out.println("No se ha creado el pedido porque esta vacio");
                    return;
                }

                con.commit();

                System.out.println("Pedido creado: " + pedido);
                System.out.println("Total: " + total + " euros");

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error al crear pedido: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }

    public static void pedidosCliente(Scanner sc) {

        int cliente = leerNumero(sc, "ID del cliente: ");

        String sql = "SELECT pedidoID, fecha FROM pedidos "
                + "WHERE clienteID = ? ORDER BY pedidoID";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, cliente);

            try (ResultSet rs = stmt.executeQuery()) {

                int cantidad = 0;

                while (rs.next()) {
                    System.out.println("Pedido: " + rs.getInt("pedidoID")
                            + " | Fecha: " + rs.getString("fecha"));
                    cantidad++;
                }

                if (cantidad == 0) {
                    System.out.println("No hay pedidos de este cliente");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar pedidos: " + e.getMessage());
        }
    }

    public static void detallePedido(Scanner sc) {

        int pedido = leerNumero(sc, "ID del pedido: ");

        String sql = "SELECT p.nombre, d.cantidad, d.precio_unidad "
                + "FROM detalle_pedidos d "
                + "JOIN productos p ON d.productoID = p.productoID "
                + "WHERE d.pedidoID = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, pedido);

            try (ResultSet rs = stmt.executeQuery()) {

                double total = 0;
                int contador = 0;

                while (rs.next()) {

                    String nombre = rs.getString("nombre");
                    int cantidad = rs.getInt("cantidad");
                    double precio = rs.getDouble("precio_unidad");

                    double importe = cantidad * precio;

                    System.out.println(nombre + " | " + cantidad + " | "
                            + precio + " euros | Total: " + importe + " euros");

                    total = total + importe;
                    contador++;
                }

                if (contador == 0) {
                    System.out.println("No hay detalles para ese pedido");
                } else {
                    System.out.println("TOTAL DEL PEDIDO: " + total + " euros");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar detalle: " + e.getMessage());
        }
    }

    public static void consultarStock(Scanner sc) {

        int producto = leerNumero(sc, "ID del producto: ");

        String sql = "SELECT nombre, stock FROM productos WHERE productoID = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, producto);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println(rs.getString("nombre") + " | Stock: "
                            + rs.getInt("stock"));
                } else {
                    System.out.println("El producto no existe");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar stock: " + e.getMessage());
        }
    }
}
