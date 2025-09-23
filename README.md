# ARKAM Microservices

Este es un proyecto de microservicios desarrollado con Spring Boot y Spring Cloud.

## Arquitectura

El proyecto sigue una arquitectura de microservicios, compuesta por los siguientes servicios:

- **Config Server:** Servidor de configuración centralizado para todos los microservicios.
- **Eureka Server:** Registro y descubrimiento de servicios.
- **API Gateway:** Punto de entrada único para todas las solicitudes de los clientes. Enruta las solicitudes a los microservicios apropiados.
- **User Service:** Gestiona los usuarios y la autenticación.
- **Product Service:** Gestiona el catálogo de productos.
- **Order Service:** Gestiona los carritos de compras y los pedidos de los clientes.
- **Notification Service:** Envía notificaciones (por ejemplo, confirmaciones de pedidos).

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Cloud 2025.0.0**
- **Spring Cloud Netflix Eureka:** Service discovery.
- **Spring Cloud Config:** Centralized configuration.
- **Spring Cloud Gateway:** API Gateway.
- **Spring Data JPA:** Persistencia de datos.
- **PostgreSQL:** Base de datos relacional.
- **Spring Cloud Stream with Kafka:** Mensajería asíncrona para la comunicación entre servicios.
- **Resilience4j:** Circuit Breaker para la tolerancia a fallos.
- **Lombok:** Para reducir el código boilerplate.
- **Maven:** Gestión de dependencias y construcción del proyecto.
- **Docker:** Para la contenerización de los servicios.

## Cómo Empezar

### Prerrequisitos

- Java 21
- Maven
- Docker

### Construcción

Para construir todos los microservicios, ejecute el siguiente comando en la raíz del proyecto:

```bash
mvn clean install
```

### Ejecución

Puede ejecutar los servicios individualmente usando su IDE o la línea de comandos. Alternativamente, puede usar el archivo `docker-compose.yml` para iniciar todos los servicios a la vez.

```bash
docker-compose up -d
```

## API Endpoints (Order Service)

A continuación se muestran los endpoints para el servicio de pedidos:

### Carrito (`/api/cart`)

- `POST /`: Agrega un artículo al carrito.
  - **Header:** `X-User-ID` (ID del usuario)
  - **Body:** `CartItemRequest`
- `DELETE /items/{productId}`: Elimina un artículo del carrito.
  - **Header:** `X-User-ID` (ID del usuario)
  - **Path Variable:** `productId`
- `GET /`: Obtiene el contenido del carrito, incluyendo la cantidad total y el precio total.
  - **Header:** `X-User-ID` (ID del usuario)

### Pedidos (`/api/orders`)

- `POST /`: Crea un nuevo pedido a partir del carrito del usuario.
  - **Header:** `X-User-ID` (ID del usuario)

### Mensajes (`/message`)

- `GET /`: Endpoint de prueba para obtener un mensaje desde el servidor de configuración.

## Revisión del Código (Order Service)

- **Separación de responsabilidades:** El código está bien estructurado en capas (controlador, servicio, dominio, infraestructura), lo que facilita su mantenimiento.
- **Uso de DTOs:** Se utilizan DTOs (Data Transfer Objects) para la comunicación entre las capas, lo cual es una buena práctica.
- **Programación reactiva:** Se utiliza Project Reactor (Mono) en el `OrderService` para la creación de pedidos de forma asíncrona.
- **Tolerancia a fallos:** Se utiliza Resilience4j (`@Retry`) en el `CartService` para reintentar las llamadas a otros servicios en caso de fallo.
- **Configuración centralizada:** El uso de `@RefreshScope` en `MessageController` indica que el servicio puede recargar su configuración desde el Config Server sin necesidad de reiniciarse.

### Puntos de Mejora

- **Manejo de excepciones:** El manejo de excepciones podría mejorarse. Por ejemplo, en lugar de devolver `ResponseEntity.badRequest().body("No se pudo completar la solicitud")`, se podrían lanzar excepciones más específicas y manejarlas con un `@ControllerAdvice` global.
- **Validación:** Se podría añadir validación a los DTOs de entrada (por ejemplo, usando `jakarta.validation.constraints`).
- **Pruebas unitarias:** Sería beneficioso añadir más pruebas unitarias para asegurar el correcto funcionamiento de la lógica de negocio.

Espero que esta revisión y el archivo `README.md` te sean de gran ayuda. ¡Déjame saber si tienes alguna otra pregunta!