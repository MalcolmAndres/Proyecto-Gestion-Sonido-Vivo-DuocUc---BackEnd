-- Se ejecuta solo la primera vez que se crea el contenedor de MySQL.
-- Una base de datos por microservicio.
CREATE DATABASE IF NOT EXISTS inventario_db;
CREATE DATABASE IF NOT EXISTS catalogo_db;
-- Los demás servicios agregan su línea aquí:
-- CREATE DATABASE IF NOT EXISTS usuarios_db;
-- CREATE DATABASE IF NOT EXISTS pedidos_db;
