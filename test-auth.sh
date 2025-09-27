#!/bin/bash

# Script de prueba para Autenticación de Microservicios ARKAM
# Asegurarse de que los servicios estén ejecutándose: docker-compose up

echo "=== Prueba de Autenticación ARKAM ==="
echo

# Colores para la salida
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # Sin Color

# Configuración
KEYCLOAK_URL="http://localhost:8443"
REALM="arkam-app"
CLIENT_ID="oauth2-pkce"
GATEWAY_URL="http://localhost:8080"
TEST_USER="testuser"
TEST_PASSWORD="password123"

echo -e "${YELLOW}Paso 1: Probando conectividad de Keycloak${NC}"
if curl -s "${KEYCLOAK_URL}/realms/${REALM}/.well-known/openid-connect-configuration" > /dev/null; then
    echo -e "${GREEN}✓ Keycloak es accesible${NC}"
else
    echo -e "${RED}✗ Keycloak no es accesible en ${KEYCLOAK_URL}${NC}"
    exit 1
fi

echo
echo -e "${YELLOW}Paso 2: Obteniendo access token${NC}"
TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=${CLIENT_ID}" \
    -d "username=${TEST_USER}" \
    -d "password=${TEST_PASSWORD}")

ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')

if [ "$ACCESS_TOKEN" != "null" ] && [ -n "$ACCESS_TOKEN" ]; then
    echo -e "${GREEN}✓ Access token obtenido exitosamente${NC}"
else
    echo -e "${RED}✗ Falló al obtener access token${NC}"
    echo "Respuesta: $TOKEN_RESPONSE"
    exit 1
fi

echo
echo -e "${YELLOW}Paso 3: Decodificando token JWT${NC}"
# Decodificar payload JWT (segunda parte)
JWT_PAYLOAD=$(echo $ACCESS_TOKEN | cut -d'.' -f2 | base64 -d 2>/dev/null || echo $ACCESS_TOKEN | cut -d'.' -f2 | base64 -D)
echo "Payload JWT:"
echo $JWT_PAYLOAD | jq . 2>/dev/null || echo $JWT_PAYLOAD

echo
echo -e "${YELLOW}Paso 4: Probando Gateway sin token${NC}"
NO_AUTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "${GATEWAY_URL}/api/users")
if [ "$NO_AUTH_RESPONSE" = "401" ]; then
    echo -e "${GREEN}✓ Gateway requiere correctamente autenticación${NC}"
else
    echo -e "${RED}✗ Respuesta inesperada: ${NO_AUTH_RESPONSE}${NC}"
fi

echo
echo -e "${YELLOW}Paso 5: Probando Gateway con token${NC}"
AUTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer ${ACCESS_TOKEN}" \
    "${GATEWAY_URL}/api/users")

if [ "$AUTH_RESPONSE" = "200" ]; then
    echo -e "${GREEN}✓ ¡Autenticación exitosa!${NC}"
elif [ "$AUTH_RESPONSE" = "401" ]; then
    echo -e "${RED}✗ Falló la autenticación - verificar token o roles${NC}"
else
    echo -e "${YELLOW}⚠ Respuesta inesperada: ${AUTH_RESPONSE}${NC}"
fi

echo
echo -e "${YELLOW}Paso 6: Probando llamada API completa${NC}"
FULL_RESPONSE=$(curl -s -H "Authorization: Bearer ${ACCESS_TOKEN}" "${GATEWAY_URL}/api/users")
echo "Respuesta API:"
echo $FULL_RESPONSE | jq . 2>/dev/null || echo $FULL_RESPONSE

echo
echo -e "${GREEN}=== Prueba completada ===${NC}"