    import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Ejercicio3Deportes {

    public static void main(String[] args) {
        crearClasificacion();
        mostrarJugadores();
        mostrarPartidos();
        mostrarClasificacion();
    }

    public static void crearClasificacion() {

        String crear = "CREATE TABLE IF NOT EXISTS clasificacion ("
                + "id_clasifiacion INT AUTO_INCREMENT PRIMARY KEY, "
                + "id_equipo INT, "
                + "posicion INT, "
                + "puntos INT, "
                + "FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo))";

        String insertar = "INSERT INTO clasificacion "
                + "(id_equipo, posicion, puntos) VALUES "
                + "(1, 1, 20), "
                + "(2, 2, 15), "
                + "(3, 3, 12), "
                + "(4, 4, 10), "
                + "(5, 5, 8)";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(crear);
            stmt.executeUpdate("DELETE FROM clasificacion");
            stmt.executeUpdate(insertar);

            System.out.println("Tabla clasificacion creada y rellenada correctamente");

        } catch (SQLException e) {
            System.out.println("Error al crear clasificacion: " + e.getMessage());
        }
    }

    public static void mostrarJugadores() {

        String buscarEquipo = "SELECT id_equipo FROM equipos "
                + "WHERE nombre_equipo = 'Equipo C'";

        String buscarJugadores = "SELECT nombre_jugador, posicion, edad "
                + "FROM jugadores WHERE id_equipo = ?";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rsEquipo = stmt.executeQuery(buscarEquipo);
             PreparedStatement pstmt = con.prepareStatement(buscarJugadores)) {

            System.out.println("\n***** JUGADORES DEL EQUIPO C *****");

            if (rsEquipo.next()) {

                int id = rsEquipo.getInt("id_equipo");
                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {

                        System.out.println(
                                rs.getString("nombre_jugador")
                                        + " | " + rs.getString("posicion")
                                        + " | " + rs.getInt("edad") + " años");
                    }
                }

            } else {
                System.out.println("Equipo C no encontrado");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar jugadores: " + e.getMessage());
        }
    }

    public static String buscarNombreEquipo(Connection con, int id)
            throws SQLException {

        String sql = "SELECT nombre_equipo FROM equipos WHERE id_equipo = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("nombre_equipo");
                }
            }
        }

        return "Equipo no encontrado";
    }

    public static void mostrarPartidos() {

        String sql = "SELECT equipo_local_id, equipo_visitante_id, "
                + "fecha_partido, resultado FROM partidos "
                + "WHERE resultado IS NOT NULL";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n***** RESULTADOS DE LOS PARTIDOS *****");

            while (rs.next()) {

                int local = rs.getInt("equipo_local_id");
                int visitante = rs.getInt("equipo_visitante_id");

                String nombreLocal = buscarNombreEquipo(con, local);
                String nombreVisitante = buscarNombreEquipo(con, visitante);

                System.out.println(
                        nombreLocal + " - " + nombreVisitante
                                + " | Fecha: " + rs.getString("fecha_partido")
                                + " | Resultado: " + rs.getString("resultado"));
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar partidos: " + e.getMessage());
        }
    }

    public static void mostrarClasificacion() {

        String sql = "SELECT id_equipo, posicion, puntos "
                + "FROM clasificacion ORDER BY posicion";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n***** CLASIFICACION ACTUAL *****");

            while (rs.next()) {

                int id = rs.getInt("id_equipo");
                String equipo = buscarNombreEquipo(con, id);

                System.out.println(
                        rs.getInt("posicion") + ". "
                                + equipo + " | "
                                + rs.getInt("puntos") + " puntos");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar clasificacion: " + e.getMessage());
        }
    }
}
