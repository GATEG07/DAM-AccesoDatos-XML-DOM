-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS America;
USE America;

-- Crear la tabla Pais
CREATE TABLE IF NOT EXISTS Pais (
    Id INT PRIMARY KEY,
    Nombre VARCHAR(15),
    Tamanio VARCHAR(15)
) ENGINE=InnoDB;

-- Crear la tabla Persona
CREATE TABLE IF NOT EXISTS Persona (
    Id INT PRIMARY KEY,
    Nombre VARCHAR(15),
    Apellido VARCHAR(15),
    Edad INT,
    Pais INT,
    FOREIGN KEY (Pais) REFERENCES Pais(Id)
) ENGINE=InnoDB;




Insert into Pais values (1, 'Chile', 'Grande');
Insert into Pais values (2, 'Costa Rica', 'Pequenio');
Insert into Pais values (3, 'Mexico', 'Mediano');
Insert into Pais values (4, 'Venezuela', 'Mediano');


Insert into Persona values (1, 'Frank', 'Malfoy', 16,1);
Insert into Persona values (2, 'Alis', 'Rodriguez', 30,2);
Insert into Persona values (3, 'Karen', 'Juarez', 22,1);
Insert into Persona values (4, 'Andrea', 'Fernandez', 21,3);
Insert into Persona values (5, 'Alesa', 'Benede', 13,4);
Insert into Persona values (6, 'Henry', 'Gonzalez', 21,2);
Insert into Persona values (7, 'Daniel', 'Hidalgo', 16,1);
Insert into Persona values (8, 'Roy', 'Castro', 20,4);
Insert into Persona values (9, 'Sabrina', 'de la Torre', 18,2);