# Docker — Key Concepts
Docker is a platform for building, packaging, and running applications inside containers. A container is a lightweight, isolated runtime environment bundling an application together with all its dependencies (libraries, system tools, runtime). Unlike virtual machines, containers share the host OS kernel rather than virtualizing an entire OS, making them significantly faster to start and more resource-efficient.

A Docker Image is a read-only template used to create containers. It packages application code, runtime, libraries, and configuration together. Images are built using a Dockerfile — a script of step-by-step instructions (e.g., declaring a base image, copying source files, installing dependencies, setting environment variables). Images are layered: each instruction in the Dockerfile produces a new layer, and unchanged layers are cached and reused, improving both build speed and storage efficiency.

A Docker Container is a running instance of an image. Containers achieve isolation through two core Linux primitives: namespaces (providing process, network, and filesystem isolation) and cgroups (enforcing resource limits such as CPU and memory caps).

A Docker Registry (e.g., Docker Hub, AWS ECR) is a centralized repository for storing and distributing images. Teams build an image once and deploy it consistently across environments (dev, staging, prod), eliminating the classic "works on my machine" problem.

# Kubernetes — Key Concepts
While Docker runs containers on a single machine, Kubernetes (K8s) orchestrates containers across a cluster of machines — handling scheduling, scaling, self-healing, and rollouts automatically.

A Cluster consists of two parts: the Control Plane, which makes global decisions (scheduling, scaling, health management), and Worker Nodes, which are the machines that actually run application workloads. Key control plane components include the API Server (the central interface for all operations), etcd (a distributed key-value store holding the entire cluster state), the Scheduler (assigns Pods to nodes), and the Controller Manager (runs background loops that reconcile actual vs. desired state).

The smallest deployable unit is a Pod. A Pod groups one or more tightly coupled containers that share the same network namespace (same IP address) and storage volumes. Pods are ephemeral — they are not rescheduled in place if they fail; Kubernetes simply replaces them with new ones.

A Deployment declares the desired state for a set of identical Pods. It manages a ReplicaSet under the hood to ensure the specified number of Pod replicas is always running, and supports rolling updates and rollbacks with zero downtime.

A Service provides stable networking and load balancing on top of ephemeral Pods. Because Pod IPs change whenever they are replaced, a Service exposes a consistent, stable endpoint (DNS name + IP) for both internal cluster communication and external traffic. Common types include ClusterIP (internal only), NodePort, and LoadBalancer.

Underpinning everything is the declarative model: you describe the desired state in YAML configuration files and submit them to the cluster. Kubernetes then continuously reconciles the actual state of the cluster toward that desired state — automatically restarting crashed containers, rescheduling Pods from failed nodes, and scaling replicas up or down as configured.
