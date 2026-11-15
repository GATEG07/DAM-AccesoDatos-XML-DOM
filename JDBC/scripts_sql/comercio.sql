DROP DATABASE IF EXISTS Comercio;
CREATE DATABASE comercio;
USE comercio;

-- CLIENTES
CREATE TABLE clientes (
  clienteID INT AUTO_INCREMENT PRIMARY KEY,
  nombre   VARCHAR(60) NOT NULL,
  telefono VARCHAR(20),
  email    VARCHAR(80)
);

-- PRODUCTOS
CREATE TABLE productos (
  productoID INT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(80) NOT NULL,
  precio     DECIMAL(10,2) NOT NULL,
  stock      INT NOT NULL
);

-- PEDIDOS
CREATE TABLE pedidos (
  pedidoID  INT AUTO_INCREMENT PRIMARY KEY,
  clienteID INT NOT NULL,
  fecha     DATE NOT NULL,
  FOREIGN KEY (clienteID) REFERENCES clientes(clienteID)
);

-- DETALLE DE PEDIDOS
CREATE TABLE detalle_pedidos (
  pedidoID       INT NOT NULL,
  productoID     INT NOT NULL,
  cantidad       INT NOT NULL,
  precio_unidad DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (pedidoID, productoID),
  FOREIGN KEY (pedidoID) REFERENCES pedidos(pedidoID),
  FOREIGN KEY (productoID) REFERENCES productos(productoID)
);

-- CLIENTES
INSERT INTO clientes (nombre, telefono, email) VALUES
('Ana Martínez', '612345678', 'ana@gmail.com'),
('Diego López', '698745231', 'diego@gmail.com'),
('Lucía Herrera', '623987451', 'lucia@gmail.com'),
('Carlos Medina', '677889900', 'carlos@gmail.com'),
('Sara Romero', '699112233', 'sara@gmail.com'),
('Javier Torres', '655221144', 'javier@gmail.com');

-- PRODUCTOS (tienda de ropa)
INSERT INTO productos (nombre, precio, stock) VALUES
('Pantalón vaquero hombre', 39.99, 80),
('Camiseta básica blanca', 12.95, 200),
('Sudadera capucha unisex', 29.90, 120),
('Vestido floral mujer', 45.50, 60),
('Zapatillas deportivas', 59.99, 90),
('Chaqueta vaquera', 49.99, 40),
('Pantalón jogger', 24.99, 150),
('Camisa cuadros', 28.50, 70),
('Gorra algodón', 9.99, 180),
('Calcetines deportivos (pack 3)', 6.99, 300);

-- PEDIDOS
INSERT INTO pedidos (clienteID, fecha) VALUES
(1, '2025-02-01'),
(2, '2025-02-02'),
(1, '2025-02-05'),
(3, '2025-02-07'),
(4, '2025-02-08'),
(2, '2025-02-10'),
(5, '2025-02-12'),
(6, '2025-02-12'),
(3, '2025-02-14'),
(1, '2025-02-15');

-- DETALLES 
INSERT INTO detalle_pedidos(pedidoID, productoID, cantidad, precio_unidad) VALUES
(1, 2, 3, 12.95),    
(1, 5, 1, 59.99),

(2, 1, 1, 39.99),
(2, 2, 2, 12.95),
(2, 9, 1, 9.99),

(3, 3, 1, 29.90),
(3, 2, 1, 12.95),

(4, 4, 1, 45.50),
(4, 5, 1, 59.99),

(5, 6, 1, 49.99),
(5, 10, 2, 6.99),

(6, 2, 4, 12.95),
(6, 7, 1, 24.99),

(7, 4, 2, 45.50),

(8, 8, 1, 28.50),
(8, 9, 2, 9.99),

(9, 3, 1, 29.90),
(9, 1, 1, 39.99),

(10, 2, 2, 12.95),
(10, 5, 1, 59.99),
(10, 10, 3, 6.99);
