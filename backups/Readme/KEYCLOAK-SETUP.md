# Configuración de Keycloak para Microservicios ARKAM

## Prerrequisitos
- Docker y Docker Compose instalados
- Servicios ejecutándose con `docker-compose up`

## Acceder a la Consola de Administración de Keycloak
1. Abrir navegador en `http://localhost:8443`
2. Iniciar sesión con:
   - Usuario: `admin`
   - Contraseña: `admin`

## Crear Realm
1. Hacer clic en el menú desplegable arriba a la izquierda (muestra "master")
2. Hacer clic en "Create realm"
3. Establecer Nombre: `arkam-app`
4. Hacer clic en "Create"

## Configurar Ajustes del Realm
1. Ir a "Realm settings" → pestaña "Login"
2. Habilitar:
   - User registration: ON
   - Forgot password: ON
   - Remember me: ON
3. Guardar

### Importante: Configurar Frontend URL
1. En "Realm settings" → pestaña "General"
2. En "Frontend URL": `http://keycloak:8080`
3. Guardar

**Esto es crucial** para que Keycloak genere tokens con el issuer URI correcto (`http://keycloak:8080/realms/arkam-app`) que coincida con la configuración del gateway.

## Crear Cliente para PKCE
1. Ir a "Clients" → "Create client"
2. Configurar:
   - Client ID: `oauth2-pkce`
   - Client type: `OpenID Connect`
   - Client authentication: `Off` (cliente público)
3. Next
4. Configurar:
   - Authentication flow:
     - ✅ Standard flow
     - ✅ Direct access grants
5. Next
6. Configurar:
   - Login theme: `keycloak`
7. Guardar

## Configurar Client Scopes
1. Ir al cliente `oauth2-pkce` → pestaña "Client scopes"
2. Agregar scopes asignados por defecto:
   - `profile`
   - `email`
   - `roles` (si no está presente)

## Crear Roles
1. Ir a "Realm roles" → "Create role"
2. Crear roles:
   - `USER` (para usuarios regulares)
   - `ADMIN` (para administradores)

## Crear Usuario de Prueba
1. Ir a "Users" → "Create new user"
2. Configurar:
   - Username: `testuser`
   - Email: `test@example.com`
   - First name: `Test`
   - Last name: `User`
   - Email verified: ON
3. Crear
4. Ir a pestaña "Credentials" → Establecer contraseña: `password123`
   - Temporary: OFF
5. Ir a pestaña "Role mapping" → Asignar rol `USER`

## Probar Flujo de Autenticación

### Obtener Access Token (para pruebas)
```bash
curl -X POST http://localhost:8443/realms/arkam-app/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=oauth2-pkce" \
  -d "username=testuser" \
  -d "password=password123"
```

### Usar Token en Llamadas API
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer TU_ACCESS_TOKEN"
```

## Solución de Problemas

### 401 Unauthorized
- Verificar que el token sea válido y no haya expirado
- Verificar que el client ID coincida con `oauth2-pkce`
- Asegurarse de que el usuario tenga roles asignados

### Token Inválido
- Verificar nombre del realm `arkam-app`
- Verificar que la URI del issuer en la configuración del gateway coincida con la URL de Keycloak

### Problemas de Extracción de Roles
- Asegurarse de que los roles estén asignados al usuario
- Verificar que el payload JWT contenga `resource_access.oauth2-pkce.roles`

## Flujo PKCE para Frontend
Para aplicaciones web usando PKCE:

1. Generar code verifier y challenge
2. Redirigir a: `http://localhost:8443/realms/arkam-app/protocol/openid-connect/auth`
   - `client_id=oauth2-pkce`
   - `response_type=code`
   - `scope=openid profile email`
   - `redirect_uri=TU_REDIRECT_URI`
   - `code_challenge=TU_CHALLENGE`
   - `code_challenge_method=S256`
3. Intercambiar código por token en el endpoint de token