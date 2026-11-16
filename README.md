   ____     ____      _   _   ____    U  ___ u             U  ___ u  __  __     ____   
U /"___|uU |  _"\ uU |"|u| |U|  _"\ u  \/"_ \/              \/"_ \/U|' \/ '|uU /"___|u 
\| |  _ / \| |_) |/ \| |\| |\| |_) |/  | | | |     U  u     | | | |\| |\/| |/\| |  _ / 
 | |_| |   |  _ <    | |_| | |  __/.-,_| |_| |     /___\.-,_| |_| | | |  | |  | |_| |  
  \____|   |_| \_\  <<\___/  |_|    \_)-\___/     |__"__|\_)-\___/  |_|  |_|   \____|  
  _)(|_    //   \\_(__) )(   ||>>_       \\                   \\   <<,-,,-.    _)(|_   
 (__)__)  (__)  (__)   (__) (__)__)     (__)                 (__)   (./  \.)  (__)__)  


# Sonido Vivo – Backend

Backend de la tienda de instrumentos **Sonido Vivo** (proyecto Duoc UC). Está hecho con **microservicios**: en vez de una sola aplicación grande, hay varias aplicaciones Spring Boot pequeñas, cada una responsable de una parte del negocio (usuarios, catálogo, carrito, pedidos, pagos, etc.), y un **API Gateway** que las junta detrás de una sola dirección.

> El frontend (React) vive en otro repositorio y solo habla con el gateway: `http://localhost:8080`.

---

## 1. Mapa rápido

```
Navegador (React :5173)
        │
        ▼
 API Gateway :8080  ──── pregunta a ────►  Eureka :8761 (agenda de servicios)
        │
        ├─ /api/auth/**                       → ms-auth        :8081  (login y registro, firma el JWT)
        ├─ /api/usuarios/**                   → ms-usuarios    :8082  (admin de usuarios)
        ├─ /api/productos/**, /api/categorias → ms-catalogo    :8083  (productos y categorías)
        ├─ /api/productos/*/stock, /api/inventario/** → ms-inventario :8084 (stock y reservas)
        ├─ /api/carrito/**                    → ms-carrito     :8085
        ├─ /api/pedidos/**, /api/reportes/**  → ms-pedidos     :8086
        ├─ /api/puntos-retiro/**              → ms-ubicaciones :8087  (tienda y puntos para el mapa)
        └─ /api/pagos/**                      → ms-pagos       :8088  (Webpay Plus, ambiente de pruebas)

 MySQL :3306 → una base de datos por microservicio (ms-auth y el gateway no tienen)
```

| Servicio | Puerto | Base de datos | Para qué sirve |
|---|---|---|---|
| api-gateway | 8080 | – | Puerta de entrada única, CORS y reparto de rutas |
| ms-auth | 8081 | – | Valida credenciales y **firma** el token JWT |
| ms-usuarios | 8082 | `usuarios_db` | Guarda usuarios (clave con BCrypt) y roles |
| ms-catalogo | 8083 | `catalogo_db` | Productos y categorías (pide el stock a inventario) |
| ms-inventario | 8084 | `inventario_db` | Stock, reservas y movimientos de stock |
| ms-carrito | 8085 | `carrito_db` | Carrito de cada cliente (solo ids y cantidades) |
| ms-pedidos | 8086 | `pedidos_db` | Pedidos, estados y reporte de ventas |
| ms-ubicaciones | 8087 | `ubicaciones_db` | Puntos de retiro (latitud/longitud) |
| ms-pagos | 8088 | `pagos_db` | Pago con tarjeta vía Webpay Plus |
| eureka-server | 8761 | – | Registro donde cada servicio se anota para ser encontrado |

**Tecnologías:** Java 17, Spring Boot 3.5, Spring Security + JWT (jjwt), Spring Data JPA (Hibernate), MySQL 8.4, Spring Cloud (Gateway, Eureka, OpenFeign), springdoc (Swagger), Lombok, JUnit 5 + Mockito, Docker Compose.

---

## 2. Cómo funciona (en simple)

**Login**
1. El front manda `POST /api/auth/login` con correo y clave.
2. `ms-auth` le pregunta a `ms-usuarios` por ese correo (llamada interna).
3. `ms-auth` compara la clave con el hash BCrypt y, si coincide, **crea un token JWT** (8 horas) con `id`, `nombre`, `rol` y el correo.
4. El front guarda el token y lo manda en cada petición: `Authorization: Bearer <token>`.

**Cada petición protegida**
- Todos los microservicios comparten la misma `JWT_SECRET`. Cada uno tiene un filtro (`JwtAuthenticationFilter`) que lee el token, verifica la firma y deja pasar o rechaza según el rol. Solo `ms-auth` *firma* tokens; los demás solo los *leen*.

**Compra**
1. El cliente llena su carrito (`ms-carrito`) y confirma el pedido (`POST /api/pedidos`).
2. `ms-pedidos` pide los **precios reales** a `ms-catalogo` (nunca confía en el precio que manda el front) y **reserva el stock** en `ms-inventario`.
3. Si paga con **transferencia o efectivo**, el pedido queda `EN_PREPARACION` y el stock se descuenta altiro.
4. Si paga con **tarjeta**, queda `PENDIENTE_PAGO`; el front llama a `ms-pagos`, que crea la transacción en Webpay. Cuando Webpay devuelve al cliente, `ms-pagos` confirma el pago y avisa a `ms-pedidos`: si fue aprobado pasa a `EN_PREPARACION` (stock descontado), si no, a `RECHAZADO` (stock devuelto).
5. Vendedor/admin avanzan el estado: `EN_PREPARACION → DESPACHADO → ENTREGADO` (no se puede saltar pasos).

Las rutas `/internal/**` son solo para que los servicios se hablen entre ellos. El gateway **no** las publica.

---

## 3. Requisitos

- **Docker Desktop** (forma recomendada), o bien:
- **JDK 17**, **Maven 3.9+**, **MySQL 8** y un servidor **Eureka** (si lo corres sin Docker).
- Git.

---

## 4. Cómo ejecutarlo

### 4.1 Con Docker (recomendado, levanta todo)

```bash
# 1) Crear el archivo de variables
cp .env.example .env        # en Windows: copy .env.example .env

# 2) Editar .env y completar:
#    DB_PASSWORD=<una clave para MySQL>
#    JWT_SECRET=<clave en Base64 de 32 bytes o más>
```

Para generar `JWT_SECRET`:

```powershell
# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```
```bash
# Linux / Mac
openssl rand -base64 32
```

```bash
# 3) Construir y levantar todo (la primera vez demora varios minutos)
docker compose up --build

# Para apagar
docker compose down            # conserva los datos
docker compose down -v         # borra también la base de datos (empieza de cero)
```

Cuando termine, revisa `http://localhost:8761`: deben aparecer los servicios registrados (`API-GATEWAY`, `MS-AUTH`, `MS-USUARIOS`, etc.). Si falta alguno, espera medio minuto: tardan en anotarse.

> El archivo `mysql/init.sql` crea las 7 bases de datos solo la **primera vez** que se crea el contenedor de MySQL. Las tablas las crea Hibernate solas (`ddl-auto=update`).

### 4.2 Sin Docker (desde el IDE o con Maven)

1. Ten MySQL corriendo y crea las bases ejecutando `mysql/init.sql`.
2. Levanta Eureka (lo más fácil es un solo contenedor):
   `docker run -p 8761:8761 steeltoeoss/eureka-server`
3. Define las variables de entorno **antes** de iniciar cada servicio:
   - `DB_PASSWORD` (la de tu MySQL) y `JWT_SECRET` (la misma para todos).
   - `DB_URL` y `DB_USER` ya traen valor por defecto (`localhost:3306`, `root`).
   - `ms-pagos` además usa `WEBPAY_RETURN_URL` y `FRONT_URL` (traen valor por defecto).
4. Inicia cada servicio (uno por terminal):
   ```bash
   cd ms-usuarios
   mvn spring-boot:run
   ```
   Orden sugerido: `ms-usuarios`, `ms-auth`, `ms-catalogo`, `ms-inventario`, `ms-carrito`, `ms-ubicaciones`, `ms-pedidos`, `ms-pagos` y al final `api-gateway`.

> Nota: las carpetas con `mvnw` no traen la carpeta `.mvn`, así que `./mvnw` no funciona. Usa el `mvn` que tienes instalado.

### 4.3 Documentación interactiva (Swagger)

Cada microservicio publica su Swagger. Ejemplo: `http://localhost:8086/swagger-ui/index.html` para pedidos. Para probar rutas protegidas, haz login primero y pega el token en el botón *Authorize* (o usa Postman).

---

## 5. Usuarios de prueba

`ms-usuarios` los crea automáticamente la primera vez, si la tabla está vacía:

| Rol | Correo | Clave |
|---|---|---|
| ADMIN | admin@sonidovivo.cl | admin123 |
| VENDEDOR | vendedor@sonidovivo.cl | vendedor123 |
| CLIENTE | cliente@correo.cl | cliente123 |
| VENDEDOR (desactivado) | inactivo@sonidovivo.cl | inactivo123 |

También se cargan automáticamente (`data.sql`): 51 productos en 10 categorías, su stock inicial y 2 puntos de retiro (Viña del Mar y Valparaíso).

---

## 6. Endpoints principales (siempre vía `http://localhost:8080`)

| Método y ruta | Quién puede | Qué hace |
|---|---|---|
| `POST /api/auth/login` | Público | Devuelve `{ token, usuario }` |
| `POST /api/auth/registro` | Público | Crea un CLIENTE y devuelve `{ token, usuario }` |
| `GET /api/usuarios` · `POST` · `PUT /{id}` · `PATCH /{id}/estado` | ADMIN | Administración de usuarios |
| `GET /api/productos?categoria=&busqueda=` · `GET /{id}` · `GET /api/categorias` | Público | Catálogo con stock |
| `POST/PUT /api/productos`, `PATCH /api/productos/{id}/desactivar`, `POST /api/categorias` | ADMIN | Mantener catálogo |
| `PUT /api/productos/{id}/stock` | ADMIN, VENDEDOR | Ajusta el stock |
| `GET /api/inventario/movimientos?productoId=` | ADMIN, VENDEDOR | Historial de stock |
| `GET /api/carrito` · `PUT /api/carrito/items` · `DELETE /api/carrito/items/{productoId}` · `DELETE /api/carrito` | CLIENTE | Carrito |
| `POST /api/pedidos` | CLIENTE | Crea el pedido |
| `GET /api/pedidos/mis-pedidos` | Con sesión | Pedidos del cliente |
| `GET /api/pedidos` | VENDEDOR, ADMIN | Todos los pedidos |
| `PATCH /api/pedidos/{id}/estado` | VENDEDOR, ADMIN | Avanza el estado |
| `GET /api/reportes/ventas?desde=&hasta=` | ADMIN | Reporte de ventas (fechas `AAAA-MM-DD`) |
| `GET /api/puntos-retiro` · `/{id}` | Público | Puntos para el mapa |
| `POST/PUT /api/puntos-retiro`, `PATCH /{id}/desactivar` | ADMIN | Mantener puntos |
| `POST /api/pagos/iniciar` | CLIENTE | Crea la transacción Webpay → `{ token, url }` |
| `GET/POST /api/pagos/retorno` | Lo llama Webpay | Confirma el pago y redirige al front |

Los errores siempre vuelven como JSON con un campo `mensaje` (ej: `{"mensaje":"Ese correo ya está registrado","status":409,...}`), que es lo que muestra el front.

---

## 7. Pruebas

### 7.1 Pruebas unitarias (por separado, cada microservicio)

Son pruebas de la capa *service* con **Mockito**: no necesitan MySQL, Eureka ni Docker. Se ejecutan dentro de la carpeta del servicio:

```bash
cd ms-auth
mvn test                          # todas las pruebas del servicio
mvn test -Dtest=AuthServiceTest   # solo una clase
```

| Servicio | Clase de prueba | Qué comprueba |
|---|---|---|
| ms-auth | `AuthServiceTest` | Login correcto, clave incorrecta (mismo mensaje que el mock) y cuenta desactivada |
| ms-usuarios | `UsuarioServiceTest` | Clave guardada hasheada, correo repetido, admin no se desactiva a sí mismo |
| ms-catalogo | `ProductoServiceTest` | Stock agregado desde inventario, catálogo responde aunque inventario falle, código repetido |
| ms-inventario | `ReservaServiceTest` | Reservar descuenta, sin stock no guarda nada, liberar devuelve unidades |
| ms-pagos | `PagoServiceTest` | Pago aprobado, rechazado y anulado |
| ms-pedidos | `PedidoServiceTest` | ⚠️ **Archivo incompleto**: termina a mitad de la declaración de los mocks, así que `mvn test` en este servicio no compila hasta completarlo |

`ms-carrito`, `ms-ubicaciones` y `api-gateway` todavía no tienen pruebas.

Las carpetas `ms-pedidos`, `ms-usuarios` e `ms-inventario` incluyen `GenerarTokenDePrueba.java` (en `src/test`). No es una prueba: es un `main` que imprime un token JWT para Postman. Ejecútalo desde el IDE con las variables `JWT_SECRET=<la de tu .env>` y `ROL=ADMIN` (o `VENDEDOR` / `CLIENTE`).

### 7.2 Pruebas en conjunto (todo levantado)

Con `docker compose up` corriendo, prueba el flujo real con PowerShell:

```powershell
# 1) Login
$r = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login `
  -ContentType "application/json" `
  -Body '{"email":"cliente@correo.cl","password":"cliente123"}'
$h = @{ Authorization = "Bearer $($r.token)" }

# 2) Ver catálogo (público)
Invoke-RestMethod http://localhost:8080/api/productos

# 3) Agregar al carrito y verlo
Invoke-RestMethod -Method Put -Uri http://localhost:8080/api/carrito/items -Headers $h `
  -ContentType "application/json" -Body '{"productoId":1,"cantidad":2}'
Invoke-RestMethod http://localhost:8080/api/carrito -Headers $h

# 4) Crear un pedido pagado con transferencia
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/pedidos -Headers $h `
  -ContentType "application/json" `
  -Body '{"productos":[{"id":1,"cantidad":2}],"metodoPago":"TRANSFERENCIA","tipoEntrega":"RETIRO_TIENDA","puntoRetiro":1}'

# 5) Verificar que el stock bajó
Invoke-RestMethod http://localhost:8080/api/productos/1
```

(En Linux/Mac sirve el mismo flujo con `curl`; o usa Postman / el Swagger de cada servicio.)

Qué deberías ver: el pedido sale `EN_PREPARACION` con el total calculado por el servidor, y el stock del producto 1 baja en 2. Prueba también casos de error: un token de CLIENTE en `GET /api/usuarios` debe dar **403**, y sin token en `GET /api/carrito` debe dar **401**.

Con **tarjeta**, el flujo completo necesita el frontend (el navegador es quien viaja a Webpay). Pasos en el README del frontend.

### 7.3 Pruebas con el frontend

Levanta este backend y el frontend con `VITE_USE_MOCKS=false` y `VITE_API_URL=http://localhost:8080`. Ver el README del frontend.

---

## 8. Variables de entorno

| Variable | Dónde se usa | Por defecto |
|---|---|---|
| `DB_PASSWORD` | MySQL y servicios con base | *(obligatoria)* |
| `JWT_SECRET` | **Todos** los servicios con seguridad (incl. ms-auth) | *(obligatoria)*, Base64, ≥ 32 bytes |
| `DB_URL`, `DB_USER` | Servicios con base | `localhost:3306/<nombre>_db`, `root` |
| `EUREKA_URL` | Todos | `http://localhost:8761/eureka/` |
| `FRONT_URL` | Gateway (CORS) y ms-pagos (redirección) | `http://localhost:5173` |
| `WEBPAY_RETURN_URL` | ms-pagos | `http://localhost:8080/api/pagos/retorno` |
| `WEBPAY_URL`, `WEBPAY_CODIGO_COMERCIO`, `WEBPAY_API_KEY` | ms-pagos | Credenciales **públicas** de integración de Transbank (solo pruebas, sin cobros reales) |

El `.env` real nunca se sube a Git (está en `.gitignore`).

---

## 9. Problemas frecuentes

| Síntoma | Causa probable |
|---|---|
| Todo responde **401** | La `JWT_SECRET` no es la misma en todos los servicios, o el token venció (8 h) |
| El gateway responde **503** o no encuentra el servicio | Ese microservicio aún no aparece en Eureka (espera) o no arrancó |
| Error de conexión a MySQL | `DB_PASSWORD` mal puesta, o MySQL aún no está "healthy" |
| Cambiaste `init.sql` y no pasa nada | Solo corre al crear el volumen: `docker compose down -v` y vuelve a levantar |
| El navegador bloquea por **CORS** | El front debe llamar al **gateway (8080)**, no a un microservicio directo. Revisa `FRONT_URL` |
| Productos con stock 0 en el catálogo | `ms-inventario` no está disponible: el catálogo responde igual, pero con stock 0 (es intencional) |

**Cosas a tener en cuenta (limitaciones del diseño):**
- Si un cliente inicia pago con tarjeta y cierra Webpay sin volver, el pedido queda `PENDIENTE_PAGO` con el stock reservado; no hay un proceso que lo libere por tiempo.
- Las rutas `/internal/**` no piden token; están protegidas porque el gateway no las expone y en Docker los puertos quedan en `127.0.0.1`.
- Los textos de los `README.md` / `README-zip.md` que vienen dentro del zip son notas antiguas de la guía del curso. Puedes reemplazarlos por este archivo.

---

## 10. Estructura de un microservicio

```
ms-pedidos/
├─ pom.xml                      # dependencias (Maven)
├─ Dockerfile                   # cómo se empaqueta en imagen
└─ src/
   ├─ main/java/cl/sonidovivo/pedidos/
   │  ├─ PedidosApplication.java   # punto de entrada (main)
   │  ├─ controller/               # recibe las peticiones HTTP (API)
   │  ├─ service/                  # lógica de negocio
   │  ├─ repository/               # acceso a la base de datos (JPA)
   │  ├─ model/                    # entidades = tablas
   │  ├─ dto/                      # objetos que entran/salen por la API
   │  ├─ client/                   # clientes Feign (llaman a otros servicios)
   │  ├─ security/                 # filtro JWT y reglas por rol
   │  ├─ exception/                # errores y respuesta de error en JSON
   │  └─ enums/                    # valores fijos (EstadoPedido, etc.)
   ├─ main/resources/application.properties   # configuración
   └─ test/java/...                           # pruebas
```

Para entender el proyecto paso a paso, revisa la **guía de estudio del backend**.
