
# Spring Boot Redbook Deployment on Kubernetes

## 1. Docker Image Build

The Spring Boot project was successfully packaged using Maven.

```bash
mvn clean package
```

Build result:

```bash
BUILD SUCCESS
```

Then the Docker image was built:

```bash
docker build -t redbook-app:latest -f dockerfile .
```

Image successfully created:

```bash
docker images | grep redbook-app
```

<img width="421" height="44" alt="截图1" src="https://github.com/user-attachments/assets/5fb50a31-0953-4da5-98ec-833f495eff25" />


---

## 2. Kubernetes Cluster Status

Verify Kubernetes cluster is running:

```bash
kubectl get nodes
```

Result shows:

- docker-desktop   Ready   control-plane   v1.34.1

<img width="837" height="613" alt="截图2" src="https://github.com/user-attachments/assets/070c9caa-7962-4390-88db-ada01ab1c65b" />


---

## 3. Pods Status

Check deployed pods inside namespace `redbook`:

```bash
kubectl get pods -n redbook -o wide
```

Result:

- mysql pod → Running
- redbook-app pod → Running

Both are READY 1/1.

<img width="754" height="449" alt="截图3" src="https://github.com/user-attachments/assets/9091a47e-d32e-4463-ab40-abf1a6a75a33" />


---

## 4. Services Configuration

Check services:

```bash
kubectl get svc -n redbook
```

Result:

- mysql → ClusterIP (3306)
- redbook-service → NodePort (80:30080)

<img width="563" height="89" alt="截图4" src="https://github.com/user-attachments/assets/3a10847f-0685-45e4-91bc-a295b0f1dbc2" />


---

## 5. Port Forward & API Test

Forward service port:

```bash
kubectl port-forward svc/redbook-service -n redbook 8080:80
```

Test API:

```bash
curl http://localhost:8080/api/v1/posts
```

Response:

```json
{
  "content": [],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

<img width="768" height="156" alt="image" src="https://github.com/user-attachments/assets/1c951272-8faf-48de-a44b-b8a0652c4624" />


---

The Spring Boot Redbook application was:

- Packaged with Maven  
- Containerized using Docker  
- Deployed to Kubernetes  
- Connected to MySQL  
- Successfully accessed via port forwarding  

Deployment completed successfully.
