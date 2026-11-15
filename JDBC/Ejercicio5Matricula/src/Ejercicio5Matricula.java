import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ejercicio5Matricula {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n***** MATRICULA *****");
            System.out.println("1. Crear alumno");
            System.out.println("2. Eliminar alumno");
            System.out.println("3. Matricular alumno en asignaturas");
            System.out.println("4. Consultar alumno y asignaturas");
            System.out.println("0. Salir");

            opcion = leerNumero(sc, "Opcion: ");

            switch (opcion) {
                case 1:
                    altaAlumno(sc);
                    break;
                case 2:
                    eliminarAlumno(leerNumero(sc, "ID del alumno: "));
                    break;
                case 3:
                    matricularAlumno(leerNumero(sc, "ID del alumno: "), sc);
                    break;
                case 4:
                    consultarAlumno(leerNumero(sc, "ID del alumno: "));
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        } while (opcion != 0);
    }

    public static int leerNumero(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Tienes que escribir un numero entero");
            return -1;
        }
    }

    public static void altaAlumno(Scanner sc) {
        int id = leerNumero(sc, "ID: ");
        System.out.print("Apellidos: ");
        String apellidos = sc.nextLine();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        int curso = leerNumero(sc, "Curso: ");
        int titulacion = leerNumero(sc, "Titulacion: ");

        if (id <= 0 || apellidos.trim().equals("") || nombre.trim().equals("")
                || apellidos.length() > 24 || nombre.length() > 18
                || curso <= 0 || titulacion <= 0) {
            System.out.println("Datos incorrectos. No se ha creado el alumno");
            return;
        }

        if (insertarAlumno(id, apellidos, nombre, curso, titulacion)) {
            System.out.println("Alumno creado correctamente");
            consultarAlumno(id);
        }
    }

    public static boolean insertarAlumno(int id, String apellidos,
                                         String nombre, int curso, int titulacion) {
        String sql = "INSERT INTO alumnos (id_alumno, apellidos, nombre, curso, titulacion) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, apellidos);
            stmt.setString(3, nombre);
            stmt.setInt(4, curso);
            stmt.setInt(5, titulacion);
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar alumno: " + e.getMessage());
            return false;
        }
    }

    public static void eliminarAlumno(int id) {
        if (id <= 0) {
            System.out.println("ID incorrecto");
            return;
        }

        String sqlBuscar = "SELECT id_alumno FROM alumnos WHERE id_alumno = ?";
        String sqlBorrar = "DELETE FROM alumnos WHERE id_alumno = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement buscar = con.prepareStatement(sqlBuscar);
             PreparedStatement borrar = con.prepareStatement(sqlBorrar)) {

            buscar.setInt(1, id);

            try (ResultSet rs = buscar.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("El alumno no existe");
                    return;
                }
            }

            borrar.setInt(1, id);
            borrar.executeUpdate();

            System.out.println("Alumno eliminado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al eliminar alumno: " + e.getMessage());
        }
    }

    public static void matricularAlumno(int id, Scanner sc) {
        if (id <= 0) {
            System.out.println("ID incorrecto");
            return;
        }

        String sqlAlumno = "SELECT id_alumno FROM alumnos WHERE id_alumno = ?";

        String sqlAsignatura = "SELECT id_asignatura FROM asignaturas WHERE id_asignatura = ?";

        String sqlMatricula = "SELECT cursada FROM alumnos_asignaturas "
                + "WHERE id_alumno = ? AND id_asignatura = ?";

        String sqlInsertar = "INSERT INTO alumnos_asignaturas "
                + "(id_alumno, id_asignatura, cursada) VALUES (?, ?, ?)";

        String sqlActualizar = "UPDATE alumnos_asignaturas SET cursada = ? "
                + "WHERE id_alumno = ? AND id_asignatura = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement alumno = con.prepareStatement(sqlAlumno);
             PreparedStatement asignatura = con.prepareStatement(sqlAsignatura);
             PreparedStatement matricula = con.prepareStatement(sqlMatricula);
             PreparedStatement insertar = con.prepareStatement(sqlInsertar);
             PreparedStatement actualizar = con.prepareStatement(sqlActualizar)) {

            alumno.setInt(1, id);

            try (ResultSet rsAlumno = alumno.executeQuery()) {
                if (!rsAlumno.next()) {
                    System.out.println("El alumno no existe");
                    return;
                }
            }

            int codigo;

            do {
                codigo = leerNumero(sc, "ID asignatura (0 para terminar): ");

                if (codigo > 0) {
                    asignatura.setInt(1, codigo);

                    try (ResultSet rsAsignatura = asignatura.executeQuery()) {

                        if (!rsAsignatura.next()) {
                            System.out.println("La asignatura no existe");
                        } else {
                            matricula.setInt(1, id);
                            matricula.setInt(2, codigo);

                            try (ResultSet rsMatricula = matricula.executeQuery()) {

                                if (rsMatricula.next()) {

                                    if (rsMatricula.getBoolean("cursada")) {
                                        System.out.println("El alumno ya cursa esta asignatura");
                                    } else {
                                        actualizar.setBoolean(1, true);
                                        actualizar.setInt(2, id);
                                        actualizar.setInt(3, codigo);
                                        actualizar.executeUpdate();

                                        System.out.println("Asignatura matriculada");
                                    }

                                } else {
                                    insertar.setInt(1, id);
                                    insertar.setInt(2, codigo);
                                    insertar.setBoolean(3, true);
                                    insertar.executeUpdate();

                                    System.out.println("Asignatura matriculada");
                                }
                            }
                        }
                    }

                } else if (codigo < 0) {
                    System.out.println("ID de asignatura incorrecto");
                }

            } while (codigo != 0);

        } catch (SQLException e) {
            System.out.println("Error al matricular: " + e.getMessage());
        }
    }

    public static void consultarAlumno(int id) {
        if (id <= 0) {
            System.out.println("ID incorrecto");
            return;
        }

        String sql = "SELECT * FROM alumnos WHERE id_alumno = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nID: " + rs.getInt("id_alumno"));
                    System.out.println("Apellidos: " + rs.getString("apellidos"));
                    System.out.println("Nombre: " + rs.getString("nombre"));
                    System.out.println("Curso: " + rs.getInt("curso"));
                    System.out.println("Titulacion: " + rs.getInt("titulacion"));
                } else {
                    System.out.println("Alumno no encontrado");
                    return;
                }
            }

            mostrarAsignaturas(id, true);
            mostrarAsignaturas(id, false);

        } catch (SQLException e) {
            System.out.println("Error al consultar alumno: " + e.getMessage());
        }
    }

    public static void mostrarAsignaturas(int id, boolean cursa) {

        String sqlAsignaturas = "SELECT id_asignatura, nombre "
                + "FROM asignaturas ORDER BY id_asignatura";

        String sqlMatricula = "SELECT cursada FROM alumnos_asignaturas "
                + "WHERE id_alumno = ? AND id_asignatura = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement asignaturas = con.prepareStatement(sqlAsignaturas);
             PreparedStatement matricula = con.prepareStatement(sqlMatricula);
             ResultSet rs = asignaturas.executeQuery()) {

            if (cursa) {
                System.out.println("\nAsignaturas que cursa:");
            } else {
                System.out.println("\nAsignaturas que no cursa:");
            }

            int cantidad = 0;

            while (rs.next()) {
                int cod = rs.getInt("id_asignatura");

                matricula.setInt(1, id);
                matricula.setInt(2, cod);

                boolean estaCursando = false;

                try (ResultSet rsMatricula = matricula.executeQuery()) {
                    if (rsMatricula.next()) {
                        estaCursando = rsMatricula.getBoolean("cursada");
                    }
                }

                if (estaCursando == cursa) {
                    System.out.println(cod + " | " + rs.getString("nombre"));
                    cantidad++;
                }
            }

            if (cantidad == 0) {
                System.out.println("Ninguna");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar asignaturas: " + e.getMessage());
        }
    }
}