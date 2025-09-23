# ARKAM Microservices - Arquitectura y Patrones de Diseño

## 📋 Preguntas Técnicas Respondidas

Este documento responde específicamente a las preguntas técnicas sobre la arquitectura, patrones de diseño y validación implementados en los microservicios de ARKAM.

---

## 🏗️ 1. ¿Por qué se eligió Arquitectura Hexagonal?

### Razones Técnicas

#### **1.1 Separación de Responsabilidades**
La arquitectura hexagonal permite una clara separación entre:
- **Lógica de Negocio** (Domain + Application)
- **Infraestructura** (Persistence, REST, External Services)
- **Interfaces** (Ports)

```java
// Ejemplo en Product Service
// Domain Layer - Lógica pura de negocio
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    // Lógica de negocio pura, sin dependencias externas
}

// Application Layer - Casos de uso
public interface CreateProductUseCase {
    ProductResponseDto createProduct(CreateProductRequestDto requestDto);
}

// Infrastructure Layer - Implementación concreta
@RestController
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    // Implementación REST
}
```

#### **1.2 Testabilidad Mejorada**
```java
// Fácil mocking gracias a la inversión de dependencias
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepositoryPort productRepository; // Puerto mockeado
    
    @InjectMocks
    private ProductService productService; // Servicio real con mocks
}
```

#### **1.3 Independencia de Tecnologías**
- **Base de datos**: Puede cambiar de PostgreSQL a MongoDB sin afectar la lógica de negocio
- **Framework**: Puede cambiar de Spring Boot a Quarkus
- **Protocolos**: Puede agregar GraphQL además de REST

#### **1.4 Mantenibilidad**
- **Cambios aislados**: Modificar la persistencia no afecta la lógica de negocio
- **Evolución independiente**: Cada capa evoluciona por separado
- **Debugging simplificado**: Errores localizados por capa

---

## 🔒 2. ¿Cómo garantizamos la Separación de Responsabilidades?

### **2.1 Estructura de Capas**

```
src/main/java/com/arkam/{service}/
├── domain/                    # Capa de Dominio
│   └── model/                # Entidades de negocio
├── application/              # Capa de Aplicación
│   ├── port/in/             # Puertos de entrada (Use Cases)
│   ├── port/out/            # Puertos de salida (Repositories)
│   ├── service/             # Servicios de aplicación
│   └── dto/                 # DTOs de transferencia
└── infrastructure/          # Capa de Infraestructura
    ├── adapter/rest/        # Adaptadores REST
    ├── adapter/persistence/ # Adaptadores de persistencia
    └── config/              # Configuración
```

### **2.2 Reglas de Dependencia**

#### **Regla 1: Domain NO depende de nada**
```java
// ✅ CORRECTO - Domain puro
public class Product {
    private String name;
    private BigDecimal price;
    
    public boolean isExpensive() {
        return price.compareTo(new BigDecimal("100")) > 0;
    }
}

// ❌ INCORRECTO - Domain con dependencias externas
public class Product {
    @Autowired
    private ProductRepository repository; // NO debe estar aquí
}
```

#### **Regla 2: Application depende solo de Domain**
```java
// ✅ CORRECTO - Application usa Domain
@Service
public class ProductService implements CreateProductUseCase {
    private final ProductRepositoryPort productRepository; // Puerto, no implementación
    
    public ProductResponseDto createProduct(CreateProductRequestDto requestDto) {
        Product product = productMapper.toProduct(requestDto); // Domain
        Product savedProduct = productRepository.save(product); // Puerto
        return productMapper.toResponseDto(savedProduct);
    }
}
```

#### **Regla 3: Infrastructure implementa puertos**
```java
// ✅ CORRECTO - Infrastructure implementa puerto
@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository jpaRepository;
    
    @Override
    public Product save(Product product) {
        // Implementación concreta
    }
}
```

### **2.3 Inyección de Dependencias**

```java
// Application Layer define el contrato
public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
}

// Infrastructure Layer implementa el contrato
@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    // Implementación con JPA
}

// Application Layer usa el puerto, no la implementación
@Service
public class ProductService {
    private final ProductRepositoryPort productRepository; // Puerto, no implementación
}
```

---

## 🎨 3. ¿Qué Patrones de Diseño se Aplicaron y en qué Parte del Código?

### **3.1 Repository Pattern**

#### **Ubicación**: `infrastructure/adapter/persistence/`

```java
// Puerto (contrato) - Application Layer
public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findByActiveTrue();
}

// Implementación - Infrastructure Layer
@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository jpaRepository;
    
    @Override
    public Product save(Product product) {
        ProductEntity entity = persistenceMapper.toEntity(product);
        ProductEntity savedEntity = jpaRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }
}
```

**Beneficios**:
- Abstrae el acceso a datos
- Fácil cambio de implementación (JPA → MongoDB)
- Testeable con mocks

### **3.2 Service Layer Pattern**

#### **Ubicación**: `application/service/`

```java
@Service
public class ProductService implements CreateProductUseCase, GetProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ProductMapper productMapper;
    
    @Override
    public ProductResponseDto createProduct(CreateProductRequestDto requestDto) {
        // Lógica de negocio
        Product product = productMapper.toProduct(requestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }
}
```

**Beneficios**:
- Centraliza la lógica de negocio
- Reutilizable entre controladores
- Fácil testing

### **3.3 DTO Pattern**

#### **Ubicación**: `application/dto/`

```java
// Request DTO
public class CreateProductRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
}

// Response DTO
public class ProductResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean active;
}
```

**Beneficios**:
- Control de datos expuestos
- Validación de entrada
- Versionado de API

### **3.4 Mapper Pattern**

#### **Ubicación**: `application/mapper/`

```java
@Component
public class ProductMapper {
    public Product toProduct(CreateProductRequestDto dto) {
        return Product.builder()
            .name(dto.getName())
            .price(dto.getPrice())
            .stockQuantity(dto.getStockQuantity())
            .active(true)
            .build();
    }
    
    public ProductResponseDto toResponseDto(Product product) {
        return ProductResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .stockQuantity(product.getStockQuantity())
            .active(product.isActive())
            .build();
    }
}
```

**Beneficios**:
- Separación entre entidades y DTOs
- Transformación centralizada
- Fácil mantenimiento

### **3.5 Adapter Pattern**

#### **Ubicación**: `infrastructure/adapter/`

```java
// REST Adapter
@RestController
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductRequestDto requestDto) {
        ProductResponseDto response = createProductUseCase.createProduct(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

// Persistence Adapter
@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository jpaRepository;
    // Implementación específica de JPA
}
```

**Beneficios**:
- Adapta interfaces externas a puertos internos
- Aisla cambios de infraestructura
- Fácil intercambio de implementaciones

### **3.6 Use Case Pattern (CQRS Simplificado)**

#### **Ubicación**: `application/port/in/`

```java
// Comandos (Write Operations)
public interface CreateProductUseCase {
    ProductResponseDto createProduct(CreateProductRequestDto requestDto);
}

public interface UpdateProductUseCase {
    ProductResponseDto updateProduct(Long id, UpdateProductRequestDto requestDto);
}

// Consultas (Read Operations)
public interface GetProductUseCase {
    ProductResponseDto findProductById(Long id);
    List<ProductResponseDto> findAllActiveProducts();
    List<ProductResponseDto> searchProducts(String keyword);
}
```

**Beneficios**:
- Separación clara de responsabilidades
- Fácil testing individual
- Escalabilidad independiente

### **3.7 Reactive Streams Pattern**

#### **Ubicación**: Controllers y Services

```java
// Reactive Controller
@RestController
public class ProductController {
    @GetMapping("/reactive")
    public Flux<ResponseEntity<ProductResponseDto>> getAllProductsReactive() {
        return getProductUseCase.findAllActiveProductsReactive()
                .map(ResponseEntity::ok);
    }
}

// Reactive Service
public Flux<ProductResponseDto> findAllActiveProductsReactive() {
    return Flux.fromIterable(productRepository.findByActiveTrue())
            .map(productMapper::toResponseDto);
}
```

**Beneficios**:
- Programación asíncrona
- Mejor escalabilidad
- Backpressure automático

---

## ✅ 4. ¿Cómo se Validaron los Datos?

### **4.1 Validación en DTOs (Bean Validation)**

#### **Request DTOs con Validaciones**

```java
// CreateProductRequestDto
public class CreateProductRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede exceder 999,999.99")
    private BigDecimal price;
    
    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad en stock no puede ser negativa")
    @Max(value = 999999, message = "La cantidad en stock no puede exceder 999,999")
    private Integer stockQuantity;
}

// CreateUserRequestDto
public class CreateUserRequestDto {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
    private String username;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", 
             message = "La contraseña debe contener al menos una letra minúscula, una mayúscula y un número")
    private String password;
}
```

### **4.2 Activación de Validación en Controllers**

```java
@RestController
public class ProductController {
    
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody CreateProductRequestDto requestDto) {
        // @Valid activa la validación automática
        ProductResponseDto response = createProductUseCase.createProduct(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDto requestDto) {
        // Validación automática en request body
        ProductResponseDto updatedProduct = updateProductUseCase.updateProduct(id, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }
}
```

### **4.3 Manejo de Errores de Validación**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Error de validación", 
                errors
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
```

### **4.4 Validación en Servicios (Lógica de Negocio)**

```java
@Service
public class ProductService {
    
    public ProductResponseDto createProduct(CreateProductRequestDto requestDto) {
        // Validación de negocio
        if (productRepository.existsByName(requestDto.getName())) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }
        
        Product product = productMapper.toProduct(requestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }
    
    public ProductResponseDto updateProduct(Long id, UpdateProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));
        
        // Validación de estado
        if (!product.isActive()) {
            throw new BusinessException("No se puede actualizar un producto inactivo");
        }
        
        productMapper.updateProductFromDto(product, requestDto);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDto(updatedProduct);
    }
}
```

### **4.5 Validación en Tests**

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Test
    void createProduct_WithValidRequest_ShouldReturnProductResponse() {
        // Given
        CreateProductRequestDto validRequest = new CreateProductRequestDto();
        validRequest.setName("Producto Válido");
        validRequest.setPrice(new BigDecimal("99.99"));
        validRequest.setStockQuantity(100);
        
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // When
        ProductResponseDto result = productService.createProduct(validRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("Producto Válido", result.getName());
    }
    
    @Test
    void createProduct_WithDuplicateName_ShouldThrowException() {
        // Given
        CreateProductRequestDto request = new CreateProductRequestDto();
        request.setName("Producto Duplicado");
        
        when(productRepository.existsByName("Producto Duplicado")).thenReturn(true);
        
        // When & Then
        assertThrows(BusinessException.class, () -> 
            productService.createProduct(request));
    }
}
```

### **4.6 Niveles de Validación Implementados**

| Nivel | Ubicación | Herramienta | Propósito |
|-------|-----------|-------------|-----------|
| **DTO** | `application/dto/` | Bean Validation | Validación de formato y estructura |
| **Controller** | `infrastructure/adapter/rest/` | `@Valid` | Activación automática de validación |
| **Service** | `application/service/` | Lógica de negocio | Validaciones de reglas de negocio |
| **Repository** | `infrastructure/adapter/persistence/` | Constraints DB | Validación a nivel de base de datos |
| **Tests** | `src/test/` | JUnit + Mockito | Validación de comportamiento |

---

## 📊 Resumen de Patrones y Validaciones

### **Patrones de Diseño Aplicados**

| Patrón | Ubicación | Beneficio |
|--------|-----------|-----------|
| **Repository** | `infrastructure/adapter/persistence/` | Abstracción de datos |
| **Service Layer** | `application/service/` | Lógica de negocio centralizada |
| **DTO** | `application/dto/` | Transferencia de datos controlada |
| **Mapper** | `application/mapper/` | Transformación de objetos |
| **Adapter** | `infrastructure/adapter/` | Adaptación de interfaces |
| **Use Case** | `application/port/in/` | Separación de responsabilidades |
| **Reactive Streams** | Controllers/Services | Programación asíncrona |

### **Validaciones Implementadas**

| Tipo | Nivel | Herramienta | Ejemplo |
|------|-------|-------------|---------|
| **Formato** | DTO | Bean Validation | `@Email`, `@Size` |
| **Negocio** | Service | Lógica propia | Duplicados, estados |
| **Integridad** | Repository | Constraints DB | Claves únicas |
| **Comportamiento** | Tests | JUnit | Casos de prueba |

---

**Versión**: 2.0.0  
**Fecha**: $(date)  
**Autor**: Equipo de Desarrollo ARKAM  
**Tipo**: Documentación Técnica de Arquitectura
