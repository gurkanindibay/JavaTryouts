# Sample Kubernetes Project — Cloud Tryout

This sample project demonstrates core cloud and Kubernetes concepts to help you learn: containerization, manifests, deployment, service exposure, observability metrics, and a CI/CD pipeline template.

Contents
- app/: simple Python Flask app that exposes a `/` endpoint and `/metrics` for Prometheus
- Dockerfile: builds the app image
- k8s/: Kubernetes manifests (Deployment, Service, Ingress sample)
- .github/workflows/ci-cd.yml: GitHub Actions template to build and deploy the image to a Kubernetes cluster

Prerequisites (local)
- Docker Desktop or Docker Engine
- kubectl (https://kubernetes.io/docs/tasks/tools/)
- kind (https://kind.sigs.k8s.io/) or minikube
- bash (you already have `bash.exe` available)
- (optional) GitHub account and container registry (Docker Hub / GHCR)

Quick local run (using kind)

1) Build the image locally:

```bash
# from repository root
cd cloud-tryout/sample-k8s-project
docker build -t sample-k8s:latest .

# create a kind cluster if you don't have one
kind create cluster --name sample
# load image into kind
kind load docker-image sample-k8s:latest --name sample

# apply k8s manifests
kubectl apply -f k8s/

# port-forward to access service from localhost
kubectl port-forward svc/sample-service 8080:80
# in another terminal:
curl http://localhost:8080/
curl http://localhost:8080/metrics
```

Notes
- The `k8s/deployment.yaml` uses placeholder image name `YOUR_REGISTRY/sample-k8s:tag`. For local testing using kind, the image `sample-k8s:latest` (built above) will be used.
- The GitHub Actions workflow contains placeholders for registry credentials and kubeconfig. Fill secrets in your GitHub repo to enable CI/CD.

What's next
- If you want, I can add a Helm chart, a manifest to enable Prometheus scraping (ServiceMonitor), or a small GitHub Actions secret setup helper.

Enjoy exploring Kubernetes!

Cloud features demonstrated
---------------------------
This sample project is small but intentionally shows common cloud architecture building blocks and platform features you should learn. Each item below lists where it appears in the repo and a short exercise you can try to learn it deeper.

- Containerization (build & run): `Dockerfile`, `app/`
	- What it shows: creating a reproducible container image for your service.
	- Try: push the image to Docker Hub or GitHub Container Registry and update the Deployment image to use the registry path.

- Container orchestration (Kubernetes manifests): `k8s/deployment.yaml`, `k8s/service.yaml`, `k8s/ingress.yaml`
	- What it shows: Deployments, Services (ClusterIP) and example Ingress configuration.
	- Try: convert the manifests to a Helm chart or Kustomize overlay and deploy with a single command.

- Service discovery & networking: `k8s/service.yaml`, Ingress example
	- What it shows: how Services expose pods inside the cluster and how Ingress provides HTTP routing into the cluster.
	- Try: change the Service to `NodePort` and observe how to access the app without port-forward.

- Health checks & resource control: probes and resource requests/limits in `k8s/deployment.yaml`
	- What it shows: readiness/liveness probes and CPU/memory requests and limits to help the scheduler and platform manage resources.
	- Try: trigger a failing liveness/readiness to see how Kubernetes restarts or removes pods.

- Observability (application metrics): `app/` exposes `/metrics` and Deployment annotations for Prometheus scraping
	- What it shows: instrumenting an app with Prometheus metrics and how Prometheus discovers scrape targets via annotations or ServiceMonitor.
	- Try: install a local Prometheus + Grafana (or use Docker Compose) and scrape this endpoint; build a simple dashboard.

- CI/CD template: `.github/workflows/ci-cd.yml`
	- What it shows: a minimal pipeline to build and push container images and deploy manifests to a cluster (placeholders for registry and kubeconfig secrets).
	- Try: wire the workflow up to a real container registry and a test cluster (or use GitHub Actions self-hosted runner) to run the full pipeline.

- Deployment strategies and rolling updates: `k8s/deployment.yaml`
	- What it shows: how Kubernetes handles rolling updates for Deployments.
	- Try: update the image tag and observe the rollout, then experiment with `kubectl rollout undo`.

- Local cluster vs managed cloud note
	- What it shows: this repo uses local Kubernetes manifests which are portable to managed Kubernetes services (EKS/AKS/GKE) with little change.
	- Try: deploy the same manifests to a small managed cluster (or cloud free-tier) and compare differences (load balancers, Ingress controllers, storage classes).

Suggested extensions (learning exercises)
- Add Horizontal Pod Autoscaler (HPA) to learn autoscaling behaviour under load.
- Add Secrets and ConfigMaps usage for configuration and sensitive values; try using `Secret` mounted as env or file.
- Add a `ServiceMonitor` and run Prometheus Operator to practice cluster observability patterns.
- Add a small sample load test (hey or vegeta) and observe autoscaling and resource behaviour.
