# README - Despliegue en Kubernetes para Microservicios ARKAM

## Introducción

Este documento proporciona una guía completa para desplegar la arquitectura de microservicios ARKAM en un clúster de Kubernetes. El proyecto implementa una arquitectura de microservicios utilizando Spring Boot, Spring Cloud y principios de Arquitectura Hexagonal, desplegada en Kubernetes con Minikube para desarrollo local.

### Arquitectura General

La arquitectura incluye los siguientes componentes:

- **Microservicios principales**: Gateway, User Service, Product Service, Order Service, Notification Service
- **Infraestructura**: Eureka (descubrimiento de servicios), Config Server (configuración centralizada), Keycloak (autenticación), RabbitMQ (mensajería), PostgreSQL y MongoDB (bases de datos)
- **Monitoreo**: Prometheus, Grafana, Loki y Alloy para observabilidad
- **Orquestación**: Kubernetes con Deployments, StatefulSets, Services, ConfigMaps, Secrets e Ingress

### Tecnologías Utilizadas

- **Kubernetes**: Orquestación de contenedores
- **Minikube**: Clúster local para desarrollo
- **Spring Boot 3.4.3**: Framework de microservicios
- **Spring Cloud**: Suite de herramientas para microservicios
- **PostgreSQL**: Base de datos relacional
- **MongoDB**: Base de datos NoSQL
- **RabbitMQ**: Sistema de mensajería
- **Keycloak**: Gestión de identidad y acceso
- **Prometheus/Grafana**: Monitoreo y visualización
- **Loki/Alloy**: Agregación y análisis de logs

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalados los siguientes componentes:

- **Minikube**: `choco install minikube` (Windows) o equivalente para tu SO
- **kubectl**: Herramienta de línea de comandos para Kubernetes
- **Docker**: Para construir y gestionar imágenes de contenedores
- **Git**: Para clonar el repositorio
- **Java 21+**: Para desarrollo local (opcional)
- **Maven 3.8+**: Para construcción de proyectos (opcional)

### Verificación de Instalación

```bash
# Verificar Minikube
minikube version

# Verificar kubectl
kubectl version --client

# Verificar Docker
docker --version
```

## Instalación Paso a Paso

### Paso 1: Preparar el Entorno

1. **Iniciar Minikube** con recursos suficientes:
   ```bash
   minikube start --memory=4096 --cpus=2
   ```

2. **Habilitar el addon de Ingress**:
   ```bash
   minikube addons enable ingress
   ```

3. **Configurar Docker para usar el daemon de Minikube**:
   ```bash
   eval $(minikube docker-env)
   ```

### Paso 2: Construir y Publicar Imágenes Docker

Si necesitas reconstruir las imágenes (opcional si ya están en Docker Hub):

```bash
# Navegar al directorio del proyecto
cd d:/arkam-devops/arkam-microservices

# Construir imágenes usando los scripts proporcionados
./deploy/docker/build-images-jib.sh
# o
./deploy/docker/build-projects.sh && ./deploy/docker/build-images-buildpacks.sh

# Verificar imágenes construidas
docker images | grep freddyarte
```

### Paso 3: Desplegar Configuraciones Base

1. **Aplicar ConfigMaps**:
   ```bash
   kubectl apply -f configmaps/app-config.yaml
   ```

2. **Aplicar Secrets**:
   ```bash
   kubectl apply -f secrets/app-secrets.yaml
   ```

3. **Crear PersistentVolumeClaims**:
   ```bash
   kubectl apply -f pvcs/database-pvcs.yaml
   ```

### Paso 4: Desplegar Bases de Datos

```bash
kubectl apply -f statefulsets/
```

Verificar que los StatefulSets estén corriendo:
```bash
kubectl get statefulsets
kubectl get pods
```

### Paso 5: Desplegar Servicios de Infraestructura

```bash
# Aplicar deployments de infraestructura
kubectl apply -f deployments/infrastructure/

# Aplicar servicios de infraestructura
kubectl apply -f services/infrastructure-services.yaml
```

### Paso 6: Desplegar Microservicios

```bash
# Aplicar deployments de microservices
kubectl apply -f deployments/microservices/

# Aplicar servicios de microservices
kubectl apply -f services/microservices-services.yaml
```

### Paso 7: Configurar Monitoreo

```bash
kubectl apply -f monitoring/
```

### Paso 8: Configurar Ingress

```bash
kubectl apply -f ingress/ingress.yaml
```

## Configuración

### Variables de Entorno

Las configuraciones principales se gestionan a través de ConfigMaps y Secrets:

- **ConfigMap `app-config`**: Contiene URLs de servicios, perfiles Spring y configuraciones generales
- **Secret `app-secrets`**: Almacena contraseñas y claves sensibles (codificadas en base64)

### Configuración de Bases de Datos

- **PostgreSQL**: Puerto 5432, usuario configurable via ConfigMap
- **MongoDB**: Puerto 27017, base de datos `arkam_user`

### Configuración de Mensajería

- **RabbitMQ**: Puerto 5672 (AMQP), 15672 (Management UI)
- **Credenciales**: Configuradas via ConfigMap y Secret

### Configuración de Monitoreo

- **Prometheus**: Puerto 9090
- **Grafana**: Puerto 3000 (acceso anónimo habilitado)
- **Loki**: Puerto 3100 para agregación de logs
- **Alloy**: Puerto 12345 para recolección de métricas

## Uso Básico

### Acceder a los Servicios

Una vez desplegado, puedes acceder a los servicios a través del Ingress:

```bash
# Obtener la URL del clúster
minikube service gateway-service --url
# Ejemplo: http://192.168.49.2:30000

# Acceder a servicios específicos
# Gateway: http://arkam.local (si configurado en /etc/hosts)
# Keycloak: http://arkam.local/keycloak
# Grafana: http://arkam.local/grafana
# Prometheus: http://arkam.local/prometheus
```

### Endpoints Principales

- **API Gateway**: `GET /` - Punto de entrada principal
- **Eureka Dashboard**: `GET /eureka/apps` - Lista de servicios registrados
- **Health Checks**: `GET /actuator/health` en cada microservicio

### Gestión de Usuarios (Keycloak)

1. Accede a Keycloak: `http://arkam.local/keycloak`
2. Credenciales por defecto: `admin` / `admin`
3. Crea realms, clientes y usuarios según necesites

### Monitoreo y Logs

- **Grafana**: Visualiza métricas y dashboards
- **Prometheus**: Consulta métricas directamente
- **Loki**: Busca y analiza logs centralizados

## Uso Avanzado

### Escalado de Servicios

```bash
# Escalar un deployment
kubectl scale deployment user-service --replicas=3

# Verificar escalado
kubectl get pods
```

### Actualización de Imágenes

```bash
# Actualizar imagen de un servicio
kubectl set image deployment/gateway-service gateway=freddyarte/gateway-service:latest

# Verificar rollout
kubectl rollout status deployment/gateway-service
```

### Gestión de Configuración

Para cambiar configuraciones sin reconstruir imágenes:

1. Edita el ConfigMap:
   ```bash
   kubectl edit configmap app-config
   ```

2. Reinicia los pods afectados:
   ```bash
   kubectl rollout restart deployment/gateway-service
   ```

### Backup y Restauración

```bash
# Backup de datos (ejemplo para PostgreSQL)
kubectl exec -it postgres-0 -- pg_dump -U postgres arkam_order > order_backup.sql

# Para MongoDB
kubectl exec -it mongo-0 -- mongodump --db arkam_user --out /backup
```

## Ejemplos de Código

### Cliente REST Básico

```bash
# Crear un usuario
curl -X POST http://arkam.local/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'

# Obtener productos
curl http://arkam.local/api/products

# Crear una orden
curl -X POST http://arkam.local/api/orders \
  -H "X-User-ID: testuser" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "prod123",
        "quantity": 2
      }
    ]
  }'
```

### Configuración Programática

```java
// Configuración de cliente Kubernetes (ejemplo)
@Configuration
public class KubernetesConfig {

    @Bean
    public KubernetesClient kubernetesClient() {
        return new DefaultKubernetesClient();
    }
}
```

### Métricas Personalizadas

```yaml
# Ejemplo de configuración Prometheus
scrape_configs:
  - job_name: 'microservices'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        regex: '.*-service'
        action: keep
```

## Solución de Problemas

### Verificación de Estado

```bash
# Ver estado general
kubectl get all

# Ver logs de un pod específico
kubectl logs -f pod/gateway-service-12345-abcde

# Ver eventos del clúster
kubectl get events --sort-by=.metadata.creationTimestamp

# Ver uso de recursos
kubectl top pods
kubectl top nodes
```

### Problemas Comunes

1. **Pods en estado Pending**:
   - Verificar recursos disponibles: `kubectl describe pod <pod-name>`
   - Revisar límites de recursos en los deployments

2. **Errores de conectividad**:
   - Verificar servicios: `kubectl get services`
   - Comprobar DNS: `kubectl exec -it <pod> -- nslookup <service-name>`

3. **Errores de base de datos**:
   - Verificar PVCs: `kubectl get pvc`
   - Revisar logs de base de datos: `kubectl logs -f statefulset/postgres`

4. **Problemas de Ingress**:
   - Verificar addon: `minikube addons list | grep ingress`
   - Comprobar configuración: `kubectl describe ingress arkam-ingress`

### Comandos Útiles

```bash
# Limpiar todo el despliegue
kubectl delete all --all --all-namespaces

# Reiniciar Minikube
minikube stop && minikube start

# Acceder a un pod
kubectl exec -it <pod-name> -- /bin/bash

# Port forwarding para debugging
kubectl port-forward svc/gateway-service 8080:8080
```

## Mejores Prácticas

### Escalabilidad

- **Horizontal Pod Autoscaling**: Implementa HPA para microservicios basado en uso de CPU/memoria
- **Límites de Recursos**: Establece requests y limits apropiados
- **Autoescalado de Clúster**: Usa CA para escalado de nodos

### Seguridad

- **Políticas de Red**: Restringe el tráfico entre pods
- **RBAC**: Implementa control de acceso basado en roles
- **Certificados TLS**: Usa cert-manager para TLS en ingress
- **Gestión de Secretos**: Usa gestión externa de secretos (Vault, AWS Secrets Manager)

### Monitoreo

- **Recolección de Métricas**: Usa Prometheus para métricas
- **Trazado Distribuido**: Zipkin para trazado de requests
- **Agregación de Logs**: Loki para logging centralizado
- **Alertas**: Configura alertas en Prometheus para issues críticos

## Contribución

### Desarrollo Local

1. Clona el repositorio:
   ```bash
   git clone <repository-url>
   cd arkam-microservices
   ```

2. Ejecuta localmente con Docker Compose (alternativa a K8s):
   ```bash
   cd deploy/docker
   docker-compose up -d
   ```

3. Para desarrollo con K8s:
   - Modifica los archivos YAML en `K8S/`
   - Reconstruye imágenes si es necesario
   - Aplica cambios: `kubectl apply -f <modified-file>`

### Mejores Prácticas de Contribución

- **Versionado**: Usa tags semánticos para imágenes Docker
- **CI/CD**: Implementa pipelines automatizados para construcción y despliegue
- **Testing**: Incluye tests de integración para K8s
- **Documentación**: Mantén actualizada esta guía

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Enlaces Útiles

- [Documentación de Kubernetes](https://kubernetes.io/docs/)
- [Guía de Minikube](https://minikube.sigs.k8s.io/docs/)
- [Spring Cloud Kubernetes](https://spring.io/projects/spring-cloud-kubernetes)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)

## Soporte

Para soporte técnico o preguntas:
- Crea un issue en el repositorio del proyecto
- Revisa los logs de los pods para diagnóstico
- Consulta la documentación de troubleshooting arriba

---

**Nota**: Esta guía asume un entorno de desarrollo local con Minikube. Para producción, considera usar un clúster gestionado como EKS, GKE o AKS con configuraciones de seguridad adicionales.