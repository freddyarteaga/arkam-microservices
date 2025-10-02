# Arquitectura Hexagonal - Microservicio de Usuario

## Estructura del Proyecto

Este microservicio ha sido refactorizado para implementar la arquitectura hexagonal (Ports and Adapters), separando claramente las responsabilidades y mejorando la mantenibilidad del código.

### Capas de la Arquitectura

#### 1. Capa de Dominio (`domain/`)
- **`model/`**: Entidades de dominio puras
  - `User.java`: Entidad principal del usuario
  - `Address.java`: Entidad de dirección
  - `UserRole.java`: Enum de roles de usuario

#### 2. Capa de Aplicación (`application/`)
- **`dto/`**: Objetos de transferencia de datos
  - `request/`: DTOs para requests
  - `response/`: DTOs para responses
  - `AddressDto.java`: DTO para direcciones
- **`mapper/`**: Mappers para conversión entre DTOs y entidades
- **`port/in/`**: Puertos de entrada (Use Cases)
  - `CreateUserUseCase.java`
  - `GetUserUseCase.java`
  - `UpdateUserUseCase.java`
- **`port/out/`**: Puertos de salida (Interfaces)
  - `UserRepositoryPort.java`
  - `KeycloakPort.java`
- **`service/`**: Servicios de aplicación
  - `UserService.java`: Implementación de los casos de uso

#### 3. Capa de Infraestructura (`infrastructure/`)
- **`adapter/persistence/`**: Adaptadores de persistencia
  - `entity/`: Entidades JPA/MongoDB
  - `mapper/`: Mappers de persistencia
  - `repository/`: Repositorios JPA
  - `UserRepositoryAdapter.java`: Adaptador del puerto de repositorio
- **`adapter/rest/`**: Adaptadores REST
  - `UserController.java`: Controlador REST
  - `KeycloakAdapter.java`: Adaptador para Keycloak
- **`config/`**: Configuraciones
  - `ApplicationConfig.java`: Configuración de la aplicación
- **`exception/`**: Manejo de excepciones
  - `UserNotFoundException.java`
  - `ErrorResponse.java`
  - `ValidationErrorResponse.java`
  - `GlobalExceptionHandler.java`

## Beneficios de la Arquitectura Hexagonal

1. **Separación de Responsabilidades**: Cada capa tiene una responsabilidad específica
2. **Testabilidad**: Fácil testing con mocks de los puertos
3. **Mantenibilidad**: Código más organizado y fácil de mantener
4. **Flexibilidad**: Fácil cambio de tecnologías sin afectar el dominio
5. **Independencia**: El dominio no depende de frameworks externos

## Flujo de Datos

1. **Request** → `UserController` (Infraestructura)
2. **Controller** → `UserService` (Aplicación)
3. **Service** → `UserRepositoryPort` / `KeycloakPort` (Puertos)
4. **Puertos** → `UserRepositoryAdapter` / `KeycloakAdapter` (Infraestructura)
5. **Response** ← `UserResponseDto` (Aplicación)

## Casos de Uso Implementados

- **Crear Usuario**: Integración con Keycloak + persistencia en MongoDB
- **Obtener Usuario**: Por ID o todos los usuarios
- **Actualizar Usuario**: Actualización de datos del usuario
- **Manejo de Excepciones**: Excepciones específicas y globales
- **Validaciones**: Validaciones de entrada con mensajes personalizados
