# ARKAM Microservices Kubernetes Deployment Guide

This guide provides comprehensive instructions for deploying the ARKAM microservices architecture to a Kubernetes cluster using Minikube.

## Prerequisites

- Minikube installed
- kubectl installed
- Docker images pushed to Docker Hub (freddyarte/*)
- At least 8GB RAM and 4 CPU cores available

## Step-by-Step Deployment

### 1. Start Minikube

```bash
minikube start --memory=8192 --cpus=4
minikube addons enable ingress
minikube addons enable metrics-server
```

### 2. Enable Minikube Docker Environment

```bash
eval $(minikube docker-env)
```

### 3. Apply ConfigMaps and Secrets

```bash
kubectl apply -f configmaps/
kubectl apply -f secrets/
```

### 4. Apply PersistentVolumeClaims

```bash
kubectl apply -f pvcs/
```

### 5. Deploy Databases

```bash
kubectl apply -f statefulsets/
```

### 6. Deploy Infrastructure Services

```bash
kubectl apply -f deployments/infrastructure/
kubectl apply -f services/infrastructure-services.yaml
```

Wait for services to be ready:

```bash
kubectl get pods
```

### 7. Deploy Microservices

```bash
kubectl apply -f deployments/microservices/
kubectl apply -f services/microservices-services.yaml
```

### 8. Deploy Monitoring (Optional)

```bash
kubectl apply -f monitoring/
```

### 9. Apply Ingress

```bash
kubectl apply -f ingress/
```

## Validations

### Check Pod Status

```bash
kubectl get pods
```

All pods should be in `Running` state.

### Check Services

```bash
kubectl get services
```

### Health Checks

#### Gateway Service
```bash
curl $(minikube service gateway-service --url)
```

#### Eureka Server
```bash
curl $(minikube service eureka --url)/eureka/apps
```

#### Individual Services
```bash
# User Service
curl $(minikube service user-service --url)/actuator/health

# Product Service
curl $(minikube service product-service --url)/actuator/health

# Order Service
curl $(minikube service order-service --url)/actuator/health
```

### Logs

```bash
# View logs for a specific service
kubectl logs -f deployment/gateway-service

# View logs for all services
kubectl logs -f -l app.kubernetes.io/name
```

## Accessing Services

### Via LoadBalancer Services

```bash
# Gateway
minikube service gateway-service

# Keycloak
minikube service keycloak

# Grafana
minikube service grafana

# Prometheus
minikube service prometheus
```

### Via Ingress

Add to `/etc/hosts`:
```
192.168.49.2 arkam.local
```

Then access:
- Gateway: http://arkam.local
- Keycloak: http://arkam.local/keycloak
- Grafana: http://arkam.local/grafana
- Prometheus: http://arkam.local/prometheus

## Troubleshooting

### Common Issues

1. **Pods not starting**
   - Check resource limits: `kubectl describe pod <pod-name>`
   - Verify image availability: `docker images | grep freddyarte`
   - Check PVC binding: `kubectl get pvc`

2. **Service communication issues**
   - Verify service names: `kubectl get services`
   - Check DNS resolution: `kubectl exec -it <pod> -- nslookup <service-name>`
   - Review environment variables in deployments

3. **Database connection failures**
   - Check StatefulSet status: `kubectl get statefulsets`
   - Verify PVC creation: `kubectl get pvc`
   - Check database logs: `kubectl logs <postgres-pod>`

4. **Ingress not working**
   - Ensure ingress addon is enabled: `minikube addons list | grep ingress`
   - Check ingress resource: `kubectl get ingress`
   - Verify host entry in /etc/hosts

### Debugging Commands

```bash
# Get detailed pod information
kubectl describe pod <pod-name>

# Check events
kubectl get events --sort-by=.metadata.creationTimestamp

# Port forward for debugging
kubectl port-forward deployment/gateway-service 8080:8080

# Execute into a pod
kubectl exec -it <pod-name> -- /bin/bash
```

## Best Practices

### Scalability

- **Horizontal Pod Autoscaling**: Implement HPA for microservices based on CPU/memory usage
  ```yaml
  apiVersion: autoscaling/v2
  kind: HorizontalPodAutoscaler
  metadata:
    name: gateway-hpa
  spec:
    scaleTargetRef:
      apiVersion: apps/v1
      kind: Deployment
      name: gateway-service
    minReplicas: 1
    maxReplicas: 10
    metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
  ```

- **Resource Limits**: Set appropriate requests and limits
- **Cluster Autoscaling**: Use CA for node scaling

### Security

- **Network Policies**: Restrict traffic between pods
  ```yaml
  apiVersion: networking.k8s.io/v1
  kind: NetworkPolicy
  metadata:
    name: deny-all
  spec:
    podSelector: {}
    policyTypes:
    - Ingress
    - Egress
  ```

- **RBAC**: Implement Role-Based Access Control
- **TLS Certificates**: Use cert-manager for ingress TLS
- **Secrets Management**: Use external secret management (Vault, AWS Secrets Manager)

### Monitoring

- **Metrics Collection**: Use Prometheus for metrics
- **Distributed Tracing**: Zipkin for request tracing
- **Log Aggregation**: Loki for centralized logging
- **Alerting**: Set up alerts in Prometheus for critical issues

### Backup and Recovery

- **Database Backups**: Regular backups of PostgreSQL and MongoDB
- **Persistent Volumes**: Use reliable storage classes
- **Disaster Recovery**: Multi-zone deployments for high availability

## Architecture Overview

```
Internet
    |
  Ingress
    |
LoadBalancer Services (Gateway, Keycloak, Monitoring)
    |
  Microservices (User, Product, Order, Notification)
    |
Infrastructure (Eureka, Config Server, RabbitMQ, Kafka)
    |
  Databases (PostgreSQL, MongoDB)
```

## Cleanup

```bash
# Delete all resources
kubectl delete -f . --recursive

# Stop Minikube
minikube stop

# Delete Minikube cluster
minikube delete
```

## Next Steps

- Implement CI/CD pipelines for automated deployments
- Set up monitoring dashboards in Grafana
- Configure log aggregation and alerting
- Implement security scanning and compliance checks
- Set up backup and disaster recovery procedures