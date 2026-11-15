-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS deportes;
USE deportes;

-- Crear la tabla de equipos
CREATE TABLE IF NOT EXISTS equipos (
    id_equipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_equipo VARCHAR(100),
    ciudad VARCHAR(50),
    deporte VARCHAR(50)
);

-- Crear la tabla de jugadores
CREATE TABLE IF NOT EXISTS jugadores (
    id_jugador INT AUTO_INCREMENT PRIMARY KEY,
    nombre_jugador VARCHAR(100),
    posicion VARCHAR(50),
    edad INT,
    id_equipo INT,
    FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo)
);

-- Crear la tabla de partidos
CREATE TABLE IF NOT EXISTS partidos (
    id_partido INT AUTO_INCREMENT PRIMARY KEY,
    equipo_local_id INT,
    equipo_visitante_id INT,
    fecha_partido DATE,
    resultado VARCHAR(20),
    FOREIGN KEY (equipo_local_id) REFERENCES equipos(id_equipo),
    FOREIGN KEY (equipo_visitante_id) REFERENCES equipos(id_equipo)
);

-- Insertar datos en la tabla de equipos
INSERT INTO equipos (nombre_equipo, ciudad, deporte) VALUES
    ('Equipo A', 'Ciudad A', 'Fútbol'),
    ('Equipo B', 'Ciudad B', 'Baloncesto'),
    ('Equipo C', 'Ciudad C', 'Tenis'),
    ('Equipo D', 'Ciudad D', 'Voleibol'),
    ('Equipo E', 'Ciudad E', 'Atletismo');

-- Insertar datos en la tabla de jugadores
INSERT INTO jugadores (nombre_jugador, posicion, edad, id_equipo) VALUES
    ('Jugador 1', 'Delantero', 25, 1),
    ('Jugador 2', 'Base', 28, 2),
    ('Jugador 3', 'Tenista', 30, 3),
    ('Jugador 4', 'Central', 22, 4),
    ('Jugador 5', 'Corredor', 26, 5);

-- Insertar datos en la tabla de partidos
INSERT INTO partidos (equipo_local_id, equipo_visitante_id, fecha_partido, resultado) VALUES
    (1, 2, '2024-01-12', '3-2'),
    (3, 4, '2024-01-15', '2-1'),
    (2, 5, '2024-01-18', '78-75'),
    (1, 3, '2024-01-20', '1-0'),
    (4, 5, '2024-01-22', '3-0');
