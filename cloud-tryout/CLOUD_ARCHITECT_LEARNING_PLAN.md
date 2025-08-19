# Cloud Architect Learning Plan

This document is a practical learning plan to become a Cloud Architect. It was generated for your workspace and placed in `cloud-tryout/CLOUD_ARCHITECT_LEARNING_PLAN.md`.

## Overview
Goal: Acquire the skills to design, build, secure, and operate production-grade cloud architectures and pass at least one professional cloud certification.

Who this is for: Developers, sysadmins, or engineers with basic programming or systems knowledge who want to move into cloud architecture.

Time estimates (example tracks):
- Intensive: 6 months (10–15 hrs/week)
- Moderate: 12 months (6–8 hrs/week)

---

## Checklist (explicit requirements covered)
- [x] Foundations (networking, Linux, scripting)
- [x] Core cloud platform services (compute, storage, networking, IAM)
- [x] Infrastructure as Code (Terraform) and provider IaC
- [x] Containers & Kubernetes
- [x] CI/CD pipelines and DevOps practices
- [x] Security, governance, and cost control
- [x] Observability: metrics, logs, tracing and SLOs
- [x] Data, streaming, and analytics basics
- [x] Hands-on capstone projects and portfolio guidance
- [x] Certification roadmap and interview prep

---

## 1) Foundations (2–6 weeks)
Focus areas:
- TCP/IP, DNS, HTTP/S, load balancing, routing, VPN basics
- Linux fundamentals (shell, file permissions, package management)
- Virtualization vs containers vs serverless
- Scripting: Python or Bash for automation
- Basic system design concepts (consistency, partitioning, tradeoffs)

Quick wins:
- Run a Linux VM, create users, practice SSH and basic networking commands
- Write simple automation scripts (backup a folder to cloud storage)

Resources:
- FreeCodeCamp or Linux Foundation quickstarts; Codecademy/Exercism for Python; networking tutorials

---

## 2) Core Cloud Platform (8–12 weeks)
Pick a primary provider (AWS recommended for market share; Azure or GCP ok).

Core topics:
- Compute (VMs, autoscaling)
- Storage (object, block, file)
- Networking (VPC/VNet, subnets, routing, security groups)
- Identity & Access Management (IAM) and least privilege
- Managed databases (RDS/Cloud SQL) and NoSQL (DynamoDB/Firestore)
- Managed services: CDN, message queues, caching
- Serverless: Functions, API gateway, event buses

Practice:
- Create resources through console; then re-create with IaC
- Small app: deploy a web app using managed DB and load balancing

Resources:
- Official provider docs, provider fundamentals courses (Cloud Practitioner / AZ-900)

---

## 3) Infrastructure as Code (2–4 weeks)
Skills:
- Terraform (provider-agnostic) — modules, remote state, locking
- CloudFormation or Bicep/ARM (provider native)

Exercise:
- Terraform: provision VPC, autoscaling group, RDS instance; store state remotely

Resources:
- HashiCorp Learn, example GitHub repos

---

## 4) Containers & Orchestration (3–6 weeks)
Topics:
- Docker: images, containers, registries
- Kubernetes: pods, deployments, services, ingress, ConfigMaps, Secrets
- Managed k8s: EKS/AKS/GKE and local tools (kind, minikube)
- Helm and kustomize for deployments

Exercise:
- Containerize a sample app and deploy to a k8s cluster with Helm

---

## 5) CI/CD & DevOps Practices (2–4 weeks)
Topics:
- Git workflows, GitHub Actions/GitLab CI/Jenkins
- Pipeline for build → test → image → deploy
- Blue/green & canary deployments, automated rollbacks

Exercise:
- Implement end-to-end pipeline deploying to k8s or serverless

---

## 6) Security, Governance & Cost (3–4 weeks)
Topics:
- IAM best practices, role separation, cross-account strategies
- Network security: private subnets, bastions, WAF
- Encryption and KMS, logging & audit trails
- Cost controls: tagging, budgets, reserved vs spot instances

Exercise:
- Implement tagging, budgets and a least-privilege IAM example

---

## 7) Observability & Reliability (2–4 weeks)
Topics:
- Metrics (Prometheus), logs (ELK/EFK), tracing (OpenTelemetry)
- SLO/SLI/SLA definition and alerts
- Resilience patterns: retries, circuit breakers, health checks

Exercise:
- Add Prometheus + Grafana to an app; create dashboards and alerts

---

## 8) Data, Analytics & Messaging (3–4 weeks)
Topics:
- OLTP vs OLAP, data warehouses, streaming (Kafka / Kinesis / PubSub)
- ETL/ELT pipelines and batch vs streaming distinction

Exercise:
- Build a simple streaming pipeline: producer → topic → consumer → sink

---

## 9) Architecture Patterns & Migration Strategies (ongoing)
Topics:
- Event-driven architectures, CQRS, microservices vs modular monolith
- Caching strategies, database sharding, replication
- Migration: lift-and-shift, replatform, refactor

Resources:
- AWS Well-Architected, Azure Architecture Center, GCP Architecture Framework

---

## 10) Capstone Projects (pick 2–3)
Examples:
1) Scalable web app: React frontend, Spring Boot backend, PostgreSQL RDS, ALB, autoscaling, Terraform, GitHub Actions
2) Event-driven microservices with Kafka: producer, consumers, k8s deployment, monitoring
3) Serverless data pipeline: S3, Lambda, Step Functions, DynamoDB
4) Multi-region DR setup: cross-region DB replication and DNS failover

Deliverables per project:
- Architecture diagram (diagrams.net)
- IaC repo (Terraform/CloudFormation)
- CI/CD pipeline, monitoring dashboards, README with cost estimate

---

## Certification Roadmap
- Foundational: AWS Cloud Practitioner / AZ-900 / Google Cloud Digital Leader
- Associate: AWS Solutions Architect – Associate, AZ-104 (admin) equivalents
- Professional: AWS Solutions Architect – Professional, AZ-305, Google Professional Cloud Architect
- Specialties later: Security, Networking, DevOps, Data

---

## Interview & Career Prep
- Build a portfolio repo with 2–3 full projects and clear READMEs
- Practice presenting architecture decisions and tradeoffs
- Mock system design interviews focusing on cloud constraints

---

## Immediate Next Steps (first week)
1. Create free-tier account for your chosen cloud provider
2. Initialize a GitHub repo named `cloud-portfolio`
3. Complete a "deploy a web app + managed DB" quick lab and commit the steps

---

## Notes & Personalization
If you share:
- Your background (developer/sysadmin/manager)
- Preferred cloud provider (AWS/Azure/GCP)
- Weekly time available

I will generate a personalized 12-week plan with exact labs, links, and a first-week checklist.

---

_Last updated: Aug 18, 2025_
