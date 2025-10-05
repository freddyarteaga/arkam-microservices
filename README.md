# Microservicios ARKAM - Spring Boot

Este proyecto implementa una arquitectura de microservicios utilizando Spring Boot, Spring Cloud, y principios de Arquitectura Hexagonal. Los microservicios incluidos son: **Order**, **Eureka**, **Config Server** y **Gateway**.

## Arquitectura General

La arquitectura sigue el patrón de microservicios con las siguientes características:
- **Descubrimiento de Servicios**: Eureka Server para registro y descubrimiento dinámico de servicios.
- **Configuración Centralizada**: Config Server para gestionar configuraciones externas.
- **Enrutamiento y Puerta de Enlace**: Gateway para enrutar solicitudes, autenticación y balanceo de carga.
- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura, con puertos y adaptadores para interacciones externas.
- **Programación Reactiva**: Uso de WebFlux y Reactor para operaciones no bloqueantes y eficientes.

### Tecnologías Utilizadas
- **Spring Boot 3.4.3**: Framework principal para desarrollo de aplicaciones.
- **Spring Cloud**: Suite de herramientas para microservicios.
  - **Eureka**: Servicio de descubrimiento.
  - **Config Server**: Configuración centralizada.
  - **Gateway**: Enrutamiento inteligente.
- **Spring WebFlux**: Programación reactiva para APIs REST.
- **Reactor**: Librería para programación reactiva (Mono/Flux).
- **Spring Data JPA**: Persistencia de datos con Hibernate.
- **PostgreSQL**: Base de datos relacional.
- **Lombok**: Reducción de código boilerplate.
- **Maven**: Gestión de dependencias y build.

## Estructura del Proyecto

```
arkam-microservices/
├── order/                 # Microservicio de Órdenes
├── eureka/                # Servicio de Descubrimiento
├── configserver/          # Configuración Centralizada
├── gateway/               # Puerta de Enlace API
├── product/               # Microservicio de Productos (referenciado)
├── user/                  # Microservicio de Usuarios (referenciado)
└── README.md
```

Cada microservicio sigue la Arquitectura Hexagonal:
- `domain/`: Lógica de negocio pura.
- `application/`: Casos de uso, DTOs, puertos.
- `infrastructure/`: Adaptadores para persistencia, REST, configuración.

## Cómo Ejecutar los Microservicios

### Prerrequisitos
- Java 21+
- Maven 3.8+
- PostgreSQL (para Order)
- Docker (opcional para contenedores)

### Orden de Inicio
1. **Config Server**: `cd configserver && mvn spring-boot:run`
2. **Eureka**: `cd eureka && mvn spring-boot:run`
3. **Gateway**: `cd gateway && mvn spring-boot:run`
4. **Order**: `cd order && mvn spring-boot:run`

### Comandos de Build y Ejecución

Para cada microservicio:
```bash
# Compilar
cd <microservicio> && mvn compile

# Ejecutar tests
cd <microservicio> && mvn test

# Ejecutar aplicación
cd <microservicio> && mvn spring-boot:run

# Crear JAR
cd <microservicio> && mvn package
```

### Configuración de Base de Datos
Para Order, configurar PostgreSQL en `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orderdb
    username: postgres
    password: password
```

## Microservicio Order - Detalles Técnicos

El microservicio Order maneja carritos de compra y órdenes, implementando Arquitectura Hexagonal con programación reactiva.

### Archivos Clave y Lógica

#### OrderRepositoryAdapter.java
Adaptador de infraestructura que implementa `OrderRepositoryPort`. Maneja la persistencia de órdenes usando JPA de forma reactiva.

```java
@Override
public Mono<Order> save(Order order) {
    return Mono.fromCallable(() -> {
        OrderEntity entity = orderPersistenceMapper.toEntity(order);
        OrderEntity savedEntity = orderJpaRepository.save(entity);
        return orderPersistenceMapper.toDomain(savedEntity);
    });
}
```
- **Funcionamiento**: Convierte entidades de dominio a JPA y viceversa. Usa `Mono.fromCallable` para ejecutar operaciones bloqueantes de JPA en un hilo separado, manteniendo la reactividad.
- **Mono/Flux**: Retorna `Mono<Order>` para operaciones de guardado único.

#### OrderPersistenceMapper.java
Mapea entre entidades de dominio y entidades JPA.

```java
public OrderEntity toEntity(Order order) {
    OrderEntity entity = new OrderEntity();
    // ... mapeo de campos
    entity.setItems(order.getItems().stream()
            .map(item -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                // ... mapeo con setOrder(entity) para relación bidireccional
                return itemEntity;
            })
            .collect(Collectors.toList()));
    return entity;
}
```
- **Funcionamiento**: Convierte objetos de dominio puro a entidades JPA con anotaciones. Maneja relaciones @OneToMany con cascade para items de orden.
- **Importancia**: Separa la lógica de dominio de detalles de persistencia.

#### OrderEntity.java
Entidad JPA que representa una orden en la base de datos.

```java
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();
    // ... timestamps con @CreationTimestamp y @UpdateTimestamp
}
```
- **Funcionamiento**: Define la estructura de la tabla `orders`. Usa `@OneToMany` con cascade para manejar items automáticamente.
- **Auditoría**: Timestamps automáticos con Hibernate.

#### OrderItemEntity.java
Entidad JPA para items de orden.

```java
@Entity
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
}
```
- **Funcionamiento**: Representa cada item en una orden. Relación @ManyToOne con OrderEntity.

#### OrderJpaRepository.java
Interfaz de repositorio JPA.

```java
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
```
- **Funcionamiento**: Proporciona operaciones CRUD básicas. Extiende JpaRepository para métodos como save(), findById().

#### Order.java (Dominio)
Modelo de dominio puro para órdenes.

```java
public class Order {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status = OrderStatus.PENDING;
    private List<OrderItem> items = new ArrayList<>();

    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```
- **Funcionamiento**: Contiene lógica de negocio pura. `calculateTotalAmount()` suma precios de items usando streams.
- **Arquitectura Hexagonal**: Sin dependencias de infraestructura.

#### CartItemJpaRepository.java
Repositorio para items de carrito.

```java
@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByUserIdAndProductId(String userId, String productId);
    List<CartItemEntity> findByUserId(String userId);

    @Modifying
    @Query("DELETE FROM CartItemEntity c WHERE c.userId = :userId")
    void deleteByUserId(String userId);
}
```
- **Funcionamiento**: Métodos para buscar y eliminar items por usuario. `deleteByUserId` es una consulta JPQL modificadora.

#### CartItemRepositoryAdapter.java
Adaptador reactivo para operaciones de carrito.

```java
@Override
public Flux<CartItem> findByUserId(String userId) {
    return Mono.fromCallable(() -> cartItemJpaRepository.findByUserId(userId).stream()
            .map(cartItemPersistenceMapper::toDomain)
            .collect(Collectors.toList()))
            .flatMapMany(Flux::fromIterable);
}

@Override
public Mono<Void> deleteByUserId(String userId) {
    return findByUserId(userId)
            .flatMap(this::delete)
            .then();
}
```
- **Funcionamiento**: `findByUserId` ejecuta consulta bloqueante y convierte a `Flux` reactivo. `deleteByUserId` encuentra todos los items y los elimina uno por uno de forma reactiva.
- **Mono/Flux**: `Flux` para múltiples items, `Mono<Void>` para operaciones sin retorno.
- **Reactividad**: Usa `flatMapMany` para convertir listas a streams reactivos, permitiendo procesamiento no bloqueante.

### Operaciones Importantes
- **findByUserId**: Busca items de carrito por usuario, convirtiendo resultados JPA a dominio reactivo.
- **deleteByUserId**: Elimina todos los items de un usuario, implementado reactivamente para evitar problemas de transacción en contextos reactivos.
- **Mapeos**: Convierten entre entidades JPA (con anotaciones) y modelos de dominio puros.
- **Persistencia JPA**: Usa Hibernate con operaciones bloqueantes envueltas en Mono/Flux para integración reactiva.

## Adaptability to Infrastructure Changes

La Arquitectura Hexagonal permite cambiar la infraestructura sin afectar la lógica de dominio. A continuación, se demuestra cómo cambiar la fuente de datos del microservicio Order de una base de datos JPA a una API externa de terceros, manteniendo intacta la funcionalidad del dominio.

### Ejemplo: Cambiando de JPA a API Externa

#### Contexto Actual (JPA)
El `OrderRepositoryAdapter` actual usa JPA para persistir órdenes:

```java
// OrderRepositoryAdapter.java (implementación JPA)
@Override
public Mono<Order> save(Order order) {
    return Mono.fromCallable(() -> {
        OrderEntity entity = orderPersistenceMapper.toEntity(order);
        OrderEntity savedEntity = orderJpaRepository.save(entity);
        return orderPersistenceMapper.toDomain(savedEntity);
    });
}
```

Las entidades JPA contienen anotaciones específicas de infraestructura:

```java
// OrderEntity.java
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();
    // ... timestamps
}
```

```java
// OrderItemEntity.java
@Entity
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
}
```

El mapper convierte entre dominio y JPA:

```java
// OrderPersistenceMapper.java
public OrderEntity toEntity(Order order) {
    OrderEntity entity = new OrderEntity();
    entity.setUserId(order.getUserId());
    entity.setTotalAmount(order.getTotalAmount());
    entity.setStatus(order.getStatus());
    entity.setItems(order.getItems().stream()
            .map(item -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setProductId(item.getProductId());
                itemEntity.setQuantity(item.getQuantity());
                itemEntity.setPrice(item.getPrice());
                return itemEntity;
            })
            .collect(Collectors.toList()));
    return entity;
}
```

El modelo de dominio permanece puro:

```java
// Order.java (Dominio - SIN CAMBIOS)
public class Order {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status = OrderStatus.PENDING;
    private List<OrderItem> items = new ArrayList<>();

    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

#### Implementación con API Externa

**Paso 1: Crear Nuevo Adaptador de API**

Crear `OrderApiAdapter` que implemente `OrderRepositoryPort`:

```java
// OrderApiAdapter.java (nuevo adaptador)
@Component
@RequiredArgsConstructor
public class OrderApiAdapter implements OrderRepositoryPort {

    private final WebClient webClient; // o RestClient

    @Override
    public Mono<Order> save(Order order) {
        // Convertir a DTO para API
        OrderApiDto apiDto = mapToApiDto(order);

        return webClient.post()
                .uri("/external-orders")
                .bodyValue(apiDto)
                .retrieve()
                .bodyToMono(OrderApiDto.class)
                .map(this::mapFromApiDto);
    }

    @Override
    public Mono<Optional<Order>> findById(Long id) {
        return webClient.get()
                .uri("/external-orders/{id}", id)
                .retrieve()
                .bodyToMono(OrderApiDto.class)
                .map(dto -> Optional.of(mapFromApiDto(dto)))
                .defaultIfEmpty(Optional.empty());
    }

    // ... otros métodos implementados con llamadas HTTP

    private OrderApiDto mapToApiDto(Order order) {
        // Conversión a formato API (sin anotaciones JPA)
        return new OrderApiDto(order.getId(), order.getUserId(), /* ... */);
    }

    private Order mapFromApiDto(OrderApiDto dto) {
        // Conversión desde formato API
        Order order = new Order();
        order.setId(dto.getId());
        // ... mapear campos
        return order;
    }
}
```

**Paso 2: Crear DTOs para API**

```java
// OrderApiDto.java
public class OrderApiDto {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemApiDto> items;
    // getters/setters
}

// OrderItemApiDto.java
public class OrderItemApiDto {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    // getters/setters
}
```

**Paso 3: Actualizar Configuración**

En `application.yml`, cambiar configuración:

```yaml
# De:
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orderdb

# A:
order:
  api:
    base-url: https://external-order-service.com
    api-key: ${EXTERNAL_API_KEY}
```

**Paso 4: Configurar Bean Condicional**

Usar `@ConditionalOnProperty` para elegir implementación:

```java
@Configuration
public class OrderRepositoryConfig {

    @Bean
    @ConditionalOnProperty(name = "order.storage", havingValue = "jpa", matchIfMissing = true)
    public OrderRepositoryPort jpaOrderRepository(OrderJpaRepository jpaRepo, OrderPersistenceMapper mapper) {
        return new OrderRepositoryAdapter(jpaRepo, mapper);
    }

    @Bean
    @ConditionalOnProperty(name = "order.storage", havingValue = "api")
    public OrderRepositoryPort apiOrderRepository(WebClient.Builder webClientBuilder) {
        return new OrderApiAdapter(webClientBuilder.build());
    }
}
```

**Paso 5: Cambiar Propiedad**

En configuración externa (Config Server):

```properties
order.storage=api
```

### Beneficios de la Arquitectura Hexagonal

1. **Aislamiento del Dominio**: El modelo `Order.java` no cambia, manteniendo la lógica de negocio intacta.

2. **Facilidad de Testing**: Los casos de uso se prueban con mocks del puerto, independiente de la infraestructura.

3. **Escalabilidad**: Cambios de infraestructura no requieren modificar servicios de aplicación o dominio.

4. **Flexibilidad**: Soporta múltiples implementaciones (JPA, API, memoria, etc.) simultáneamente.

5. **Mantenibilidad**: Separación clara permite equipos trabajar en diferentes capas sin conflictos.

Este ejemplo demuestra cómo la Arquitectura Hexagonal en microservicios facilita adaptaciones rápidas a cambios de infraestructura, manteniendo la estabilidad del sistema.

## Ejemplos de Uso

### Crear Orden
```bash
POST /api/orders
Header: X-User-ID: user123
```
Respuesta: Detalles de la orden creada, carrito limpiado automáticamente.

### Agregar al Carrito
```bash
POST /api/cart
Header: X-User-ID: user123
Body: {"productId": "prod1", "quantity": 2}
```

### Ver Carrito
```bash
GET /api/cart
Header: X-User-ID: user123
```

## Notas Adicionales
- Todos los servicios usan configuración centralizada desde Config Server.
- Eureka maneja el registro automático de servicios.
- Gateway proporciona un punto de entrada único con enrutamiento inteligente.
- Las operaciones de base de datos son transaccionales para mantener consistencia.
- La programación reactiva mejora el rendimiento bajo carga alta.