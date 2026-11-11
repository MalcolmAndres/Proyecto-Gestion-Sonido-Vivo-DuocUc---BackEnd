-- Se ejecuta solo la primera vez que se crea el contenedor de MySQL.
-- Una base de datos por microservicio (ms-auth no tiene base).
CREATE DATABASE IF NOT EXISTS inventario_db;
CREATE DATABASE IF NOT EXISTS catalogo_db;
CREATE DATABASE IF NOT EXISTS usuarios_db;
CREATE DATABASE IF NOT EXISTS ubicaciones_db;
CREATE DATABASE IF NOT EXISTS pedidos_db;
CREATE DATABASE IF NOT EXISTS pagos_db;
CREATE DATABASE IF NOT EXISTS carrito_db;
