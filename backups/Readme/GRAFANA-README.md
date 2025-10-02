# Configuración Completa de Monitoreo y Calidad: Grafana, Prometheus, Zipkin y SonarQube

## Resumen

Se ha implementado un sistema completo de monitoreo y análisis de calidad para tu proyecto de microservicios Spring Boot, incluyendo:

- **Grafana**: Para visualización de métricas y dashboards.
- **Prometheus**: Para recolección y almacenamiento de métricas.
- **Zipkin**: Para rastreo distribuido (distributed tracing).
- **SonarQube**: Para análisis estático de código y calidad.

Todos los servicios han sido configurados para exponer métricas Prometheus, enviar trazas a Zipkin y analizar código con SonarQube.

## Ejecutando el Sistema de Monitoreo

1. Asegúrate de tener Docker y Docker Compose instalados en tu sistema.

2. Navega al directorio raíz de tu proyecto donde se encuentra el archivo `docker-compose.yml`.

3. Ejecuta el siguiente comando para iniciar todos los servicios de monitoreo:

   ```bash
   docker-compose up -d grafana prometheus zipkin
   ```

   O para iniciar todo el stack de infraestructura:

   ```bash
   docker-compose up -d
   ```

   El flag `-d` ejecuta los contenedores en segundo plano.

4. **URLs de Acceso:**
   - **Grafana**: http://localhost:3000
   - **Prometheus**: http://localhost:9090
   - **Zipkin**: http://localhost:9411
   - **SonarQube**: http://localhost:9000

5. Inicia tus microservicios Spring Boot (configserver, eureka, gateway, user-service, product-service, order-service, notification-service) para que comiencen a enviar métricas y trazas.

## Accediendo a los Servicios de Monitoreo

### Grafana
- **URL**: http://localhost:3000
- **Usuario por defecto**: admin
- **Contraseña por defecto**: admin

   *Nota: La contraseña se establece a través de la variable de entorno `GF_SECURITY_ADMIN_PASSWORD` en el archivo `docker-compose.yml`. Para producción, se recomienda cambiar esta contraseña por seguridad.*

### Prometheus
- **URL**: http://localhost:9090
- **Interfaz**: Consulta métricas recolectadas y estado de los targets.

### Zipkin
- **URL**: http://localhost:9411
- **Interfaz**: Visualiza trazas distribuidas de tus microservicios.

### SonarQube
- **URL**: http://localhost:9000
- **Usuario por defecto**: admin
- **Contraseña por defecto**: admin
- **Interfaz**: Análisis de calidad de código, cobertura y vulnerabilidades.

## Configurando y Revisando el Monitoreo

### Configuración Inicial de Grafana

1. **Inicio de Sesión**: Usa las credenciales mencionadas arriba para acceder a la interfaz de Grafana.

2. **Agregar Fuente de Datos Prometheus**:
   - Ve a **Configuración** (icono de engranaje) > **Data Sources**.
   - Haz clic en **Add data source**.
   - Selecciona **Prometheus**.
   - Configura la URL: `http://prometheus:9090` (desde dentro de Docker) o `http://localhost:9090` (desde host).
   - Haz clic en **Save & Test**.

3. **Crear o Importar Dashboards**:
   - Ve a **Crear** (icono de +) > **Dashboard**.
   - Puedes crear paneles personalizados o importar dashboards predefinidos desde la comunidad de Grafana.
   - Para importar, ve a **Dashboard** > **Import** y pega el ID o JSON del dashboard.
   - Dashboards útiles para Spring Boot: ID 4701 (JVM Metrics), ID 6756 (Spring Boot Statistics).

4. **Explorar Métricas en Grafana**:
   - Una vez configuradas las fuentes de datos, puedes crear paneles para visualizar métricas como:
     - Uso de CPU y memoria (JVM)
     - Latencia de servicios
     - Tasa de errores
     - Número de requests
   - Usa PromQL para consultas avanzadas.

### Revisando Métricas en Prometheus

1. Accede a http://localhost:9090
2. Ve a **Status** > **Targets** para verificar que todos los microservicios estén siendo scrapeados correctamente.
3. Usa la pestaña **Graph** para consultar métricas específicas, por ejemplo:
   - `jvm_memory_used_bytes`: Memoria usada por JVM
   - `http_server_requests_seconds_count`: Número de requests HTTP

### Revisando Trazas en Zipkin

1. Accede a http://localhost:9411
2. En la interfaz de Zipkin, puedes:
   - Buscar trazas por servicio, operación o tags
   - Visualizar el flujo de llamadas entre microservicios
   - Identificar cuellos de botella en el rendimiento
   - Depurar errores distribuidos

### Analizando Código con SonarQube

1. Accede a http://localhost:9000
2. Inicia sesión con admin/admin (cambia la contraseña en el primer acceso)
3. Crea un nuevo proyecto o importa uno existente
4. Ejecuta análisis de código desde tu terminal:

   ```bash
   mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=<token>
   ```

   Para obtener un token: Ve a **My Account** > **Security** > **Generate Token**

5. Revisa los resultados: cobertura de código, bugs, vulnerabilidades, code smells y deuda técnica.

### Endpoints de Métricas en Microservicios

Cada microservicio expone métricas en:
- **Actuator Health**: `http://localhost:{puerto}/actuator/health`
- **Métricas Prometheus**: `http://localhost:{puerto}/actuator/prometheus`

Puertos de los servicios:
- Config Server: 8888
- Eureka: 8761
- Gateway: 8080
- Product Service: 8081
- User Service: 8082
- Order Service: 8083
- Notification Service: 8084

## Deteniendo el Sistema de Monitoreo y Calidad

Para detener los servicios de monitoreo y calidad:

```bash
docker-compose down grafana prometheus zipkin sonarqube
```

Para detener todo el stack:

```bash
docker-compose down
```

Esto detendrá y removerá los contenedores, pero los volúmenes de datos se conservarán.

## Notas Adicionales

- **Persistencia de Datos**:
  - Grafana: Volumen `grafana_data`
  - Prometheus: Volumen `prometheus_data`
  - SonarQube: Volúmenes `sonarqube_data`, `sonarqube_extensions`, `sonarqube_logs`

- **Métricas Incluidas**: Los microservicios exponen automáticamente métricas JVM, HTTP, base de datos y personalizadas a través de Micrometer.

- **Trazas Distribuidas**: Con Zipkin, puedes rastrear requests a través de múltiples servicios, ayudando a identificar problemas de rendimiento.

- **Dashboards Recomendados**:
  - JVM (Micrometer): ID 4701
  - Spring Boot Statistics: ID 6756
  - Spring Boot Actuator: ID 12464

- **Seguridad**:
  - Cambia la contraseña de admin de Grafana.
  - Considera configurar autenticación en Prometheus y Zipkin para entornos de producción.

- **Documentación Oficial**:
  - [Grafana](https://grafana.com/docs/grafana/latest/)
  - [Prometheus](https://prometheus.io/docs/)
  - [Zipkin](https://zipkin.io/)
  - [SonarQube](https://docs.sonarsource.com/sonarqube/)
  - [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

- **Troubleshooting**:
  - Verifica logs: `docker-compose logs [servicio]`
  - Asegúrate de que los microservicios estén ejecutándose y accesibles.
  - Confirma que las dependencias de monitoreo se hayan compilado correctamente.

Si encuentras problemas, revisa que todos los servicios estén en la misma red Docker y que las URLs de configuración sean correctas.