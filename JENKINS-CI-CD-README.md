# CI/CD con Jenkins para Microservicios ARKAM

Este documento proporciona una guía completa para implementar Integración Continua y Despliegue Continuo (CI/CD) utilizando Jenkins en el proyecto de microservicios ARKAM. El proyecto utiliza Spring Boot, Spring Cloud, Arquitectura Hexagonal, y está configurado con Docker Jib para construcción de imágenes.

## Arquitectura del Proyecto

### Microservicios
- **Order**: Gestión de órdenes y carritos (JPA con PostgreSQL)
- **Eureka**: Servicio de descubrimiento
- **Config Server**: Configuración centralizada
- **Gateway**: Puerta de enlace API
- **Product**: Gestión de productos (referenciado)
- **User**: Gestión de usuarios (referenciado)

### Tecnologías Clave
- **Spring Boot 3.4.3** con **Spring Cloud**
- **Arquitectura Hexagonal** (dominio, aplicación, infraestructura)
- **WebFlux** y **Reactor** para reactividad
- **JPA/Hibernate** para persistencia
- **Docker Jib** para construcción de imágenes
- **AWS** para despliegues en la nube

## Prerrequisitos

### Infraestructura
- **Jenkins** (versión 2.387+)
- **Docker** (para construcción de imágenes)
- **AWS CLI** configurado
- **Git** para control de versiones
- **Maven 3.8+** y **Java 21+**

### Servicios AWS
- **ECR** (Elastic Container Registry)
- **ECS/EKS** o **EC2** para despliegues
- **RDS** para bases de datos
- **ELB** para balanceo de carga

### Credenciales y Configuración
```bash
# Configurar AWS CLI
aws configure

# Verificar configuración
aws sts get-caller-identity

# Configurar Docker para ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
```

## Instalación y Configuración de Jenkins

### 1. Instalación de Jenkins

#### Opción A: Docker (Recomendado)
```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

#### Opción B: Instalación Nativa (Ubuntu/Debian)
```bash
# Agregar clave GPG
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null

# Agregar repositorio
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

# Instalar
sudo apt update
sudo apt install jenkins

# Iniciar servicio
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

### 2. Configuración Inicial

1. **Acceder a Jenkins**: `http://localhost:8080`
2. **Obtener contraseña inicial**:
   ```bash
   sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   ```
3. **Instalar plugins recomendados**:
   - Git
   - Pipeline
   - Docker
   - AWS Steps
   - Maven Integration
   - JUnit

### 3. Configuración Global

#### Configurar JDK y Maven
- **Manage Jenkins** > **Global Tool Configuration**
- **JDK**: Agregar JDK 21
- **Maven**: Agregar Maven 3.8+

#### Configurar Credenciales AWS
- **Manage Jenkins** > **Manage Credentials**
- **Add Credentials** > **AWS Credentials**
- ID: `aws-credentials`
- Access Key ID y Secret Access Key

#### Configurar Docker
```bash
# En el servidor Jenkins
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

## Pipelines de Jenkins

### Pipeline Declarativo para Microservicios

Crear un nuevo **Pipeline** job con el siguiente script:

```groovy
pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REGISTRY = '<account-id>.dkr.ecr.${AWS_REGION}.amazonaws.com'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    parameters {
        choice(name: 'SERVICE', choices: ['order', 'eureka', 'configserver', 'gateway'], description: 'Microservicio a desplegar')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Entorno de despliegue')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/your-org/arkam-microservices.git'
            }
        }

        stage('Compile and Test') {
            steps {
                dir("${params.SERVICE}") {
                    sh 'mvn clean compile test'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dir("${params.SERVICE}") {
                        sh "mvn compile jib:build -Djib.to.image=${ECR_REGISTRY}/arkam-${params.SERVICE}:${IMAGE_TAG} -Djib.to.auth.username=AWS -Djib.to.auth.password=\$(aws ecr get-login-password --region ${AWS_REGION})"
                    }
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    sh "aws ecr describe-repositories --repository-names arkam-${params.SERVICE} --region ${AWS_REGION} || aws ecr create-repository --repository-name arkam-${params.SERVICE} --region ${AWS_REGION}"
                    sh "docker push ${ECR_REGISTRY}/arkam-${params.SERVICE}:${IMAGE_TAG}"
                }
            }
        }

        stage('Deploy to AWS') {
            steps {
                script {
                    if (params.ENVIRONMENT == 'dev') {
                        deployToECS('dev')
                    } else if (params.ENVIRONMENT == 'staging') {
                        deployToECS('staging')
                    } else {
                        deployToEKS('prod')
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline ejecutado exitosamente!'
            slackSend channel: '#ci-cd', message: "Despliegue exitoso de ${params.SERVICE} en ${params.ENVIRONMENT}"
        }
        failure {
            echo 'Pipeline falló!'
            slackSend channel: '#ci-cd', message: "Error en despliegue de ${params.SERVICE} en ${params.ENVIRONMENT}"
        }
    }
}

def deployToECS(String environment) {
    sh """
        aws ecs update-service \
            --cluster arkam-${environment} \
            --service arkam-${params.SERVICE} \
            --force-new-deployment \
            --region ${AWS_REGION}
    """
}

def deployToEKS(String environment) {
    sh """
        kubectl set image deployment/arkam-${params.SERVICE} \
            arkam-${params.SERVICE}=${ECR_REGISTRY}/arkam-${params.SERVICE}:${IMAGE_TAG} \
            --namespace=${environment}
        kubectl rollout status deployment/arkam-${params.SERVICE} --namespace=${environment}
    """
}
```

### Pipeline Específico para Order (con Base de Datos)

```groovy
pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REGISTRY = '<account-id>.dkr.ecr.${AWS_REGION}.amazonaws.com'
        DB_HOST = credentials('db-host')
        DB_PASSWORD = credentials('db-password')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/your-org/arkam-microservices.git'
            }
        }

        stage('Test with Database') {
            steps {
                script {
                    // Iniciar PostgreSQL para tests de integración
                    sh 'docker run -d --name postgres-test -e POSTGRES_PASSWORD=test -p 5432:5432 postgres:15'

                    dir('order') {
                        sh 'mvn test -Dspring.profiles.active=test'
                    }
                }
            }
            post {
                always {
                    sh 'docker stop postgres-test && docker rm postgres-test'
                }
            }
        }

        stage('Build and Push') {
            steps {
                dir('order') {
                    sh """
                        mvn clean package -DskipTests
                        docker build -t ${ECR_REGISTRY}/arkam-order:${BUILD_NUMBER} .
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                        docker push ${ECR_REGISTRY}/arkam-order:${BUILD_NUMBER}
                    """
                }
            }
        }

        stage('Database Migration') {
            steps {
                script {
                    // Ejecutar Flyway o Liquibase para migraciones
                    sh """
                        docker run --rm \
                            -e DB_HOST=${DB_HOST} \
                            -e DB_PASSWORD=${DB_PASSWORD} \
                            ${ECR_REGISTRY}/arkam-order:${BUILD_NUMBER} \
                            --spring.profiles.active=migration
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        aws ecs update-service \
                            --cluster arkam-prod \
                            --service arkam-order \
                            --force-new-deployment \
                            --region ${AWS_REGION}
                    """
                }
            }
        }
    }
}
```

## Configuración de AWS

### 1. Crear Repositorios ECR

```bash
# Para cada microservicio
aws ecr create-repository --repository-name arkam-order --region us-east-1
aws ecr create-repository --repository-name arkam-eureka --region us-east-1
aws ecr create-repository --repository-name arkam-configserver --region us-east-1
aws ecr create-repository --repository-name arkam-gateway --region us-east-1
```

### 2. Configurar ECS Cluster

```bash
# Crear cluster
aws ecs create-cluster --cluster-name arkam-prod

# Crear definición de tarea (ejemplo para Order)
aws ecs register-task-definition --cli-input-json file://task-definition-order.json
```

**task-definition-order.json**:
```json
{
    "family": "arkam-order",
    "containerDefinitions": [
        {
            "name": "order-service",
            "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/arkam-order:latest",
            "essential": true,
            "portMappings": [
                {
                    "containerPort": 8080,
                    "hostPort": 8080
                }
            ],
            "environment": [
                {
                    "name": "SPRING_PROFILES_ACTIVE",
                    "value": "prod"
                },
                {
                    "name": "DB_HOST",
                    "value": "arkam-db.cluster-xxxx.us-east-1.rds.amazonaws.com"
                }
            ],
            "secrets": [
                {
                    "name": "DB_PASSWORD",
                    "valueFrom": "arn:aws:secretsmanager:us-east-1:xxxx:secret:db-password"
                }
            ]
        }
    ]
}
```

### 3. Configurar Servicio ECS

```bash
aws ecs create-service \
    --cluster arkam-prod \
    --service-name arkam-order \
    --task-definition arkam-order \
    --desired-count 2 \
    --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:...,containerName=order-service,containerPort=8080"
```

## Configuración de Docker Jib

Los microservicios ya están configurados con Jib en `pom.xml`:

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.4.6</version>
    <configuration>
        <from>
            <image>gcr.io/distroless/java21</image>
        </from>
        <to>
            <image>freddyarte/order-service</image>
        </to>
    </configuration>
</plugin>
```

### Construcción con Jib

```bash
# Construir y subir a Docker Hub
mvn compile jib:build

# Construir y subir a ECR
mvn compile jib:build \
    -Djib.to.image=<account-id>.dkr.ecr.us-east-1.amazonaws.com/arkam-order:latest \
    -Djib.to.auth.username=AWS \
    -Djib.to.auth.password=$(aws ecr get-login-password --region us-east-1)
```

## Testing y Calidad de Código

### Configurar SonarQube

1. **Instalar SonarQube**:
   ```bash
   docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
   ```

2. **Configurar en Jenkins**:
   - Instalar plugin SonarQube Scanner
   - Configurar servidor SonarQube
   - Agregar análisis en pipeline

3. **Pipeline con SonarQube**:
   ```groovy
   stage('SonarQube Analysis') {
       steps {
           withSonarQubeEnv('SonarQube') {
               sh 'mvn sonar:sonar'
           }
       }
   }
   ```

### Tests de Integración

```groovy
stage('Integration Tests') {
    steps {
        script {
            // Iniciar servicios dependientes
            sh 'docker-compose -f docker-compose.test.yml up -d'

            // Ejecutar tests
            sh 'mvn verify -P integration-test'

            // Detener servicios
            sh 'docker-compose -f docker-compose.test.yml down'
        }
    }
}
```

## Monitoreo y Alertas

### Configurar Slack Notifications

1. **Crear Webhook en Slack**
2. **Configurar en Jenkins**:
   - Instalar plugin Slack Notification
   - Configurar webhook URL
   - Agregar notificaciones en pipeline

### Health Checks

```groovy
stage('Health Check') {
    steps {
        script {
            def response = httpRequest(
                url: "http://load-balancer/health",
                timeout: 30
            )
            if (response.status != 200) {
                error("Health check failed")
            }
        }
    }
}
```

## Troubleshooting

### Problemas Comunes

#### 1. Error de Autenticación ECR
```bash
# Solución: Renovar token
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
```

#### 2. Tests Fallan por Base de Datos
```bash
# Usar Testcontainers
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

#### 3. Despliegue Lento
- Optimizar imágenes Docker (usar Jib layers)
- Implementar blue-green deployments
- Usar AWS CodeDeploy

#### 4. Problemas de Memoria en Jenkins
```bash
# Aumentar memoria JVM
JAVA_OPTS="-Xmx2048m -Xms512m"
```

### Logs y Debugging

#### Ver Logs de Jenkins
```bash
# Logs del contenedor
docker logs jenkins

# Logs del servicio
sudo journalctl -u jenkins -f
```

#### Debug Pipeline
```groovy
stage('Debug') {
    steps {
        sh 'printenv'
        sh 'docker version'
        sh 'aws --version'
    }
}
```

## Mejores Prácticas

1. **Seguridad**:
   - Usar secrets management (AWS Secrets Manager)
   - Rotar credenciales regularmente
   - Implementar RBAC en Jenkins

2. **Performance**:
   - Paralelizar stages cuando sea posible
   - Usar agentes distribuidos
   - Cache de dependencias Maven

3. **Reliability**:
   - Implementar retries en operaciones fallidas
   - Configurar timeouts apropiados
   - Monitorear métricas de pipeline

4. **Escalabilidad**:
   - Usar Jenkins Kubernetes plugin para auto-scaling
   - Implementar pipelines compartidos
   - Automatizar creación de jobs

## Conclusión

Esta configuración de CI/CD proporciona un flujo completo desde el commit hasta el despliegue en producción, aprovechando las fortalezas de Jenkins, Docker Jib, y AWS. La Arquitectura Hexagonal del proyecto facilita el testing y el despliegue independiente de cada microservicio.

Para soporte adicional, consultar la documentación oficial de Jenkins y AWS, o revisar los logs detallados en caso de errores.