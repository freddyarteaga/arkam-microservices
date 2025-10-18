# Kubernetes Configuration Plan for ARKAM Microservices

## Overview
This document outlines the complete Kubernetes configuration for deploying the ARKAM microservices architecture in Minikube. Based on the existing Docker Compose setup, we've created equivalent Kubernetes manifests including Deployments, StatefulSets, Services, ConfigMaps, Secrets, Ingress, and monitoring components.

## Folder Structure
```
K8S/
├── configmaps/
│   └── app-config.yaml
├── secrets/
│   └── app-secrets.yaml
├── pvcs/
│   └── database-pvcs.yaml
├── statefulsets/
│   ├── postgres-statefulset.yaml
│   └── mongo-statefulset.yaml
├── deployments/
│   ├── infrastructure/
│   │   ├── zookeeper-deployment.yaml
│   │   ├── kafka-deployment.yaml
│   │   ├── rabbitmq-deployment.yaml
│   │   ├── keycloak-deployment.yaml
│   │   ├── zipkin-deployment.yaml
│   │   ├── config-server-deployment.yaml
│   │   └── eureka-deployment.yaml
│   └── microservices/
│       ├── gateway-deployment.yaml
│       ├── user-service-deployment.yaml
│       ├── product-service-deployment.yaml
│       ├── order-service-deployment.yaml
│       └── notification-service-deployment.yaml
├── services/
│   ├── infrastructure-services.yaml
│   ├── microservices-services.yaml
│   └── database-services.yaml
├── ingress/
│   └── ingress.yaml
├── monitoring/
│   ├── prometheus-deployment.yaml
│   ├── grafana-deployment.yaml
│   ├── loki-deployment.yaml
│   └── alloy-deployment.yaml
└── README.md (deployment guide)
```

## ConfigMaps

### app-config.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  MONGO_URI: "mongodb://mongo:27017/arkam_user"
  DB_USER: "postgres"
  RABBITMQ_HOST: "rabbitmq"
  RABBITMQ_PORT: "5672"
  RABBITMQ_USERNAME: "guest"
  RABBITMQ_PASSWORD: "guest"
  RABBITMQ_VHOST: "/"
  ZIPKIN_URL: "http://zipkin:9411"
  SPRING_PROFILES_ACTIVE: "docker"
```

## Secrets

### app-secrets.yaml
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  DB_PASSWORD: c2FzYQ==  # base64 encoded 'sasa'
  RABBITMQ_PASSWORD: Z3Vlc3Q=  # base64 encoded 'guest'
  KC_BOOTSTRAP_ADMIN_USERNAME: YWRtaW4=  # 'admin'
  KC_BOOTSTRAP_ADMIN_PASSWORD: YWRtaW4=  # 'admin'
  PGADMIN_DEFAULT_EMAIL: cGdhZG1pbjRAcGdhZG1pbi5vcmc=  # 'pgadmin4@pgadmin.org'
  PGADMIN_DEFAULT_PASSWORD: YWRtaW4=  # 'admin'
```

## PersistentVolumeClaims

### database-pvcs.yaml
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: postgres-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mongo-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pgadmin-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

## StatefulSets

### postgres-statefulset.yaml
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:14
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_USER
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DB_USER
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: DB_PASSWORD
        - name: PGDATA
          value: /data/postgres
        volumeMounts:
        - name: postgres-storage
          mountPath: /data/postgres
        resources:
          limits:
            memory: 700Mi
  volumeClaimTemplates:
  - metadata:
      name: postgres-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 5Gi
```

### mongo-statefulset.yaml
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mongo
spec:
  serviceName: mongo
  replicas: 1
  selector:
    matchLabels:
      app: mongo
  template:
    metadata:
      labels:
        app: mongo
    spec:
      containers:
      - name: mongo
        image: mongodb/mongodb-community-server:latest
        ports:
        - containerPort: 27017
        volumeMounts:
        - name: mongo-storage
          mountPath: /data/db
        resources:
          limits:
            memory: 700Mi
  volumeClaimTemplates:
  - metadata:
      name: mongo-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 5Gi
```

## Deployments

### Infrastructure Deployments

#### zookeeper-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zookeeper
spec:
  replicas: 1
  selector:
    matchLabels:
      app: zookeeper
  template:
    metadata:
      labels:
        app: zookeeper
    spec:
      containers:
      - name: zookeeper
        image: confluentinc/cp-zookeeper:7.5.0
        ports:
        - containerPort: 2181
        env:
        - name: ZOOKEEPER_CLIENT_PORT
          value: "2181"
        - name: ZOOKEEPER_TICK_TIME
          value: "2000"
```

#### kafka-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: confluentinc/cp-kafka:7.5.0
        ports:
        - containerPort: 9092
        env:
        - name: KAFKA_BROKER_ID
          value: "1"
        - name: KAFKA_ZOOKEEPER_CONNECT
          value: "zookeeper:2181"
        - name: KAFKA_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka:9092"
        - name: KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR
          value: "1"
```

#### rabbitmq-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rabbitmq
spec:
  replicas: 1
  selector:
    matchLabels:
      app: rabbitmq
  template:
    metadata:
      labels:
        app: rabbitmq
    spec:
      containers:
      - name: rabbitmq
        image: rabbitmq:3-management
        ports:
        - containerPort: 5672
        - containerPort: 15672
        env:
        - name: RABBITMQ_DEFAULT_USER
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_DEFAULT_PASS
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: RABBITMQ_PASSWORD
```

#### keycloak-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: keycloak
spec:
  replicas: 1
  selector:
    matchLabels:
      app: keycloak
  template:
    metadata:
      labels:
        app: keycloak
    spec:
      containers:
      - name: keycloak
        image: quay.io/keycloak/keycloak:26.2.5
        ports:
        - containerPort: 8080
        env:
        - name: KC_BOOTSTRAP_ADMIN_USERNAME
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: KC_BOOTSTRAP_ADMIN_USERNAME
        - name: KC_BOOTSTRAP_ADMIN_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: KC_BOOTSTRAP_ADMIN_PASSWORD
        command: ["start-dev"]
        resources:
          limits:
            memory: 700Mi
```

#### zipkin-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zipkin
spec:
  replicas: 1
  selector:
    matchLabels:
      app: zipkin
  template:
    metadata:
      labels:
        app: zipkin
    spec:
      containers:
      - name: zipkin
        image: openzipkin/zipkin
        ports:
        - containerPort: 9411
```

#### config-server-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-server
spec:
  replicas: 1
  selector:
    matchLabels:
      app: config-server
  template:
    metadata:
      labels:
        app: config-server
    spec:
      containers:
      - name: config-server
        image: freddyarte/config-server
        ports:
        - containerPort: 8888
        env:
        - name: SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS
          value: "/config"
        - name: RABBITMQ_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_HOST
        - name: RABBITMQ_PORT
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_PORT
        - name: RABBITMQ_USERNAME
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: RABBITMQ_PASSWORD
        - name: RABBITMQ_VHOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_VHOST
        volumeMounts:
        - name: config-volume
          mountPath: /config
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8888
          initialDelaySeconds: 30
          periodSeconds: 10
      volumes:
      - name: config-volume
        configMap:
          name: config-files
```

#### eureka-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eureka
spec:
  replicas: 1
  selector:
    matchLabels:
      app: eureka
  template:
    metadata:
      labels:
        app: eureka
    spec:
      containers:
      - name: eureka
        image: freddyarte/eureka-server
        ports:
        - containerPort: 8761
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 30
          periodSeconds: 10
```

### Microservices Deployments

#### gateway-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gateway-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gateway-service
  template:
    metadata:
      labels:
        app: gateway-service
    spec:
      containers:
      - name: gateway
        image: freddyarte/gateway-service
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: ZIPKIN_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: ZIPKIN_URL
```

#### user-service-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: freddyarte/user-service
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: MONGO_URI
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: MONGO_URI
        - name: RABBITMQ_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_HOST
        - name: RABBITMQ_PORT
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_PORT
        - name: RABBITMQ_USERNAME
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: RABBITMQ_PASSWORD
        - name: RABBITMQ_VHOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_VHOST
```

#### product-service-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: freddyarte/product-service
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: DB_USER
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DB_USER
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: DB_PASSWORD
        - name: RABBITMQ_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_HOST
        - name: RABBITMQ_PORT
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_PORT
        - name: RABBITMQ_USERNAME
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: RABBITMQ_PASSWORD
        - name: RABBITMQ_VHOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_VHOST
```

#### order-service-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
      - name: order-service
        image: freddyarte/order-service
        ports:
        - containerPort: 8083
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: DB_USER
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DB_USER
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: DB_PASSWORD
        - name: RABBITMQ_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_HOST
        - name: RABBITMQ_PORT
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_PORT
        - name: RABBITMQ_USERNAME
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: RABBITMQ_PASSWORD
        - name: RABBITMQ_VHOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: RABBITMQ_VHOST
```

#### notification-service-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: notification-service
  template:
    metadata:
      labels:
        app: notification-service
    spec:
      containers:
      - name: notification-service
        image: freddyarte/notification-service
        ports:
        - containerPort: 8084  # Assuming a port, adjust if needed
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
```

## Services

### infrastructure-services.yaml
```yaml
apiVersion: v1
kind: Service
metadata:
  name: zookeeper
spec:
  selector:
    app: zookeeper
  ports:
  - port: 2181
    targetPort: 2181
---
apiVersion: v1
kind: Service
metadata:
  name: kafka
spec:
  selector:
    app: kafka
  ports:
  - port: 9092
    targetPort: 9092
---
apiVersion: v1
kind: Service
metadata:
  name: rabbitmq
spec:
  selector:
    app: rabbitmq
  ports:
  - port: 5672
    targetPort: 5672
  - port: 15672
    targetPort: 15672
---
apiVersion: v1
kind: Service
metadata:
  name: keycloak
spec:
  selector:
    app: keycloak
  ports:
  - port: 8080
    targetPort: 8080
  type: LoadBalancer
---
apiVersion: v1
kind: Service
metadata:
  name: zipkin
spec:
  selector:
    app: zipkin
  ports:
  - port: 9411
    targetPort: 9411
---
apiVersion: v1
kind: Service
metadata:
  name: config-server
spec:
  selector:
    app: config-server
  ports:
  - port: 8888
    targetPort: 8888
---
apiVersion: v1
kind: Service
metadata:
  name: eureka
spec:
  selector:
    app: eureka
  ports:
  - port: 8761
    targetPort: 8761
```

### microservices-services.yaml
```yaml
apiVersion: v1
kind: Service
metadata:
  name: gateway-service
spec:
  selector:
    app: gateway-service
  ports:
  - port: 8080
    targetPort: 8080
  type: LoadBalancer
---
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
  - port: 8082
    targetPort: 8082
---
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  selector:
    app: product-service
  ports:
  - port: 8081
    targetPort: 8081
---
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order-service
  ports:
  - port: 8083
    targetPort: 8083
---
apiVersion: v1
kind: Service
metadata:
  name: notification-service
spec:
  selector:
    app: notification-service
  ports:
  - port: 8084
    targetPort: 8084
```

### database-services.yaml
```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
---
apiVersion: v1
kind: Service
metadata:
  name: mongo
spec:
  selector:
    app: mongo
  ports:
  - port: 27017
    targetPort: 27017
```

## Ingress

### ingress.yaml
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: arkam-ingress
spec:
  rules:
  - host: arkam.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: gateway-service
            port:
              number: 8080
      - path: /keycloak
        pathType: Prefix
        backend:
          service:
            name: keycloak
            port:
              number: 8080
      - path: /grafana
        pathType: Prefix
        backend:
          service:
            name: grafana
            port:
              number: 3000
      - path: /prometheus
        pathType: Prefix
        backend:
          service:
            name: prometheus
            port:
              number: 9090
```

## Monitoring

### prometheus-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus
spec:
  replicas: 1
  selector:
    matchLabels:
      app: prometheus
  template:
    metadata:
      labels:
        app: prometheus
    spec:
      containers:
      - name: prometheus
        image: prom/prometheus:v2.44.0
        ports:
        - containerPort: 9090
        volumeMounts:
        - name: prometheus-config
          mountPath: /etc/prometheus
      volumes:
      - name: prometheus-config
        configMap:
          name: prometheus-config
---
apiVersion: v1
kind: Service
metadata:
  name: prometheus
spec:
  selector:
    app: prometheus
  ports:
  - port: 9090
    targetPort: 9090
  type: LoadBalancer
```

### grafana-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      containers:
      - name: grafana
        image: grafana/grafana:latest
        ports:
        - containerPort: 3000
        env:
        - name: GF_PATHS_PROVISIONING
          value: /etc/grafana/provisioning
        - name: GF_AUTH_ANONYMOUS_ENABLED
          value: "true"
        - name: GF_AUTH_ANONYMOUS_ORG_ROLE
          value: Admin
        volumeMounts:
        - name: grafana-datasources
          mountPath: /etc/grafana/provisioning/datasources
      volumes:
      - name: grafana-datasources
        configMap:
          name: grafana-datasources
---
apiVersion: v1
kind: Service
metadata:
  name: grafana
spec:
  selector:
    app: grafana
  ports:
  - port: 3000
    targetPort: 3000
  type: LoadBalancer
```

### loki-deployment.yaml (Simplified - combine read, write, backend, gateway)
```yaml
# Note: Full Loki setup is complex; this is a simplified version
apiVersion: apps/v1
kind: Deployment
metadata:
  name: loki
spec:
  replicas: 1
  selector:
    matchLabels:
      app: loki
  template:
    metadata:
      labels:
        app: loki
    spec:
      containers:
      - name: loki
        image: grafana/loki:latest
        ports:
        - containerPort: 3100
        volumeMounts:
        - name: loki-config
          mountPath: /etc/loki
      volumes:
      - name: loki-config
        configMap:
          name: loki-config
---
apiVersion: v1
kind: Service
metadata:
  name: loki
spec:
  selector:
    app: loki
  ports:
  - port: 3100
    targetPort: 3100
```

### alloy-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: alloy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: alloy
  template:
    metadata:
      labels:
        app: alloy
    spec:
      containers:
      - name: alloy
        image: grafana/alloy:latest
        ports:
        - containerPort: 12345
        volumeMounts:
        - name: alloy-config
          mountPath: /etc/alloy
        - name: logs
          mountPath: /logs-parent
      volumes:
      - name: alloy-config
        configMap:
          name: alloy-config
      - name: logs
        hostPath:
          path: /logs-parent
---
apiVersion: v1
kind: Service
metadata:
  name: alloy
spec:
  selector:
    app: alloy
  ports:
  - port: 12345
    targetPort: 12345
```

## Deployment Guide

### Prerequisites
- Minikube installed
- kubectl installed
- Docker images pushed to Docker Hub

### Step-by-Step Deployment

1. **Start Minikube**
   ```bash
   minikube start --memory=4096 --cpus=2
   minikube addons enable ingress
   ```

2. **Enable Minikube Docker Environment**
   ```bash
   eval $(minikube docker-env)
   ```

3. **Apply ConfigMaps and Secrets**
   ```bash
   kubectl apply -f K8S/configmaps/
   kubectl apply -f K8S/secrets/
   ```

4. **Apply PersistentVolumeClaims**
   ```bash
   kubectl apply -f K8S/pvcs/
   ```

5. **Deploy Databases**
   ```bash
   kubectl apply -f K8S/statefulsets/
   ```

6. **Deploy Infrastructure Services**
   ```bash
   kubectl apply -f K8S/deployments/infrastructure/
   kubectl apply -f K8S/services/infrastructure-services.yaml
   ```

7. **Deploy Microservices**
   ```bash
   kubectl apply -f K8S/deployments/microservices/
   kubectl apply -f K8S/services/microservices-services.yaml
   ```

8. **Deploy Monitoring**
   ```bash
   kubectl apply -f K8S/monitoring/
   ```

9. **Apply Ingress**
   ```bash
   kubectl apply -f K8S/ingress/
   ```

### Validations

1. **Check Pod Status**
   ```bash
   kubectl get pods
   ```

2. **Check Services**
   ```bash
   kubectl get services
   ```

3. **Health Checks**
   ```bash
   # Gateway
   curl $(minikube service gateway-service --url)
   
   # Eureka
   curl $(minikube service eureka --url)/eureka/apps
   ```

4. **Logs**
   ```bash
   kubectl logs -f deployment/gateway-service
   ```

### Troubleshooting

- **Pods not starting**: Check resource limits and image availability
- **Service communication**: Verify service names and ports
- **Database connections**: Check PVC binding and database initialization
- **Ingress not working**: Ensure ingress addon is enabled

### Best Practices

#### Scalability
- Use HorizontalPodAutoscaler for microservices
- Implement resource requests and limits
- Use Cluster Autoscaler for nodes

#### Security
- Use NetworkPolicies to restrict traffic
- Implement RBAC for access control
- Use TLS certificates for ingress
- Store secrets in external secret management

#### Monitoring
- Set up alerts in Prometheus
- Use Grafana dashboards for visualization
- Implement distributed tracing with Zipkin
- Monitor logs with Loki and Alloy

## Next Steps
Switch to Code mode to create the actual YAML files based on this plan.