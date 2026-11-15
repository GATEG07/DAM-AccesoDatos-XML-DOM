import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Ejercicio2Instituto {

    public static void main(String[] args) {
        crearNotasFinales();
        mostrarAlumnos();
    }

    public static void crearNotasFinales() {

        String crear = "CREATE TABLE IF NOT EXISTS notasfinales ("
                + "Mat VARCHAR(10), Cod INT, NotaMedia DOUBLE, "
                + "PRIMARY KEY (Mat, Cod))";

        String consultar = "SELECT Mat, COD, NOTA1, NOTA2, NOTA3 FROM NOTAS";

        String insertar = "INSERT INTO notasfinales "
                + "(Mat, Cod, NotaMedia) VALUES (?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(crear);
            stmt.executeUpdate("DELETE FROM notasfinales");

            try (ResultSet rs = stmt.executeQuery(consultar);
                 PreparedStatement pstmt = con.prepareStatement(insertar)) {

                while (rs.next()) {

                    double media = (rs.getInt("NOTA1")
                            + rs.getInt("NOTA2")
                            + rs.getInt("NOTA3")) / 3.0;

                    pstmt.setString(1, rs.getString("Mat"));
                    pstmt.setInt(2, rs.getInt("COD"));
                    pstmt.setDouble(3, media);

                    pstmt.executeUpdate();
                }
            }

            System.out.println("Tabla notasfinales creada correctamente");

        } catch (SQLException e) {
            System.out.println("Error al crear notasfinales: " + e.getMessage());
        }
    }

    public static void mostrarAlumnos() {

        String alumnos = "SELECT MAT, APEL_NOM, POBLACION FROM ALUMNOS";

        String notas = "SELECT COD, NOTA1, NOTA2 FROM NOTAS WHERE Mat = ?";

        String asignatura = "SELECT NOMBRE FROM ASIGNATURAS WHERE COD = ?";

        String media = "SELECT NotaMedia FROM notasfinales "
                + "WHERE Mat = ? AND Cod = ?";

        try (Connection con = ConexionDB.getConexion();
             Statement stmt = con.createStatement();
             ResultSet rsAlumnos = stmt.executeQuery(alumnos);
             PreparedStatement stmtNotas = con.prepareStatement(notas);
             PreparedStatement stmtAsignatura = con.prepareStatement(asignatura);
             PreparedStatement stmtMedia = con.prepareStatement(media)) {

            System.out.println("NombreAlumno | Localidad | Asignatura | "
                    + "Nota 1 | Nota 2 | NotaMedia");

            while (rsAlumnos.next()) {

                String mat = rsAlumnos.getString("MAT");
                String nombre = rsAlumnos.getString("APEL_NOM");
                String localidad = rsAlumnos.getString("POBLACION");
                boolean tieneNotas = false;

                stmtNotas.setString(1, mat);

                try (ResultSet rsNotas = stmtNotas.executeQuery()) {

                    while (rsNotas.next()) {

                        tieneNotas = true;

                        int cod = rsNotas.getInt("COD");
                        String nombreAsignatura = "";
                        double notaMedia = 0;

                        stmtAsignatura.setInt(1, cod);

                        try (ResultSet rsAsig = stmtAsignatura.executeQuery()) {

                            if (rsAsig.next()) {
                                nombreAsignatura = rsAsig.getString("NOMBRE");
                            }
                        }

                        stmtMedia.setString(1, mat);
                        stmtMedia.setInt(2, cod);

                        try (ResultSet rsMedia = stmtMedia.executeQuery()) {

                            if (rsMedia.next()) {
                                notaMedia = rsMedia.getDouble("NotaMedia");
                            }
                        }

                        System.out.println(nombre + " | " + localidad
                                + " | " + nombreAsignatura + " | "
                                + rsNotas.getInt("NOTA1") + " | "
                                + rsNotas.getInt("NOTA2") + " | "
                                + notaMedia);
                    }
                }

                if (!tieneNotas) {
                    System.out.println(nombre + " | " + localidad
                            + " | Sin asignaturas con notas");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar alumnos: " + e.getMessage());
        }
    }
}
