
## Question 1: Containers vs Virtual Machines

Containers and virtual machines are both used to isolate applications, but they work in different ways.

From the architecture point of view, virtual machines run on a hypervisor and each VM includes a full guest operating system with its own kernel. Containers do not have a separate OS. They run directly on the host operating system and share the same kernel.

Because of this, resource sharing is different. Virtual machines allocate CPU, memory, and storage to each guest OS, which causes more overhead. Containers share the host kernel and only isolate processes, so they use fewer resources.

Startup time is also very different. A virtual machine needs to boot an operating system, so it usually takes seconds or even minutes. A container only starts application processes, so it can start in milliseconds.

In practice, virtual machines are often used when strong isolation or different operating systems are required. Containers are more suitable for microservices, cloud-native applications, and environments where fast startup and efficient resource usage are important.

## Question 2: Linux Kernel Features for Containers

Linux containers mainly depend on namespaces and cgroups.

Namespaces are used for isolation. They give each container its own view of system resources. For example, the PID namespace isolates process IDs so processes inside a container cannot see processes from other containers. The NET namespace isolates network resources such as IP addresses and network interfaces. The MNT (mount) namespace isolates filesystem mount points so each container can have its own filesystem structure.

Cgroups are used for resource control. They limit and manage how much CPU, memory, and other resources a group of processes can use. For example, cgroups can restrict the maximum memory a container can consume.

Namespaces and cgroups work together to make containers possible. Namespaces provide isolation, while cgroups make sure containers do not use more resources than allowed.

## Question 3: Container Image Layering

A container image is made of multiple layers. Each layer represents a filesystem change, such as installing software or adding files. These layers are read-only and can be shared by different images.

When a container runs, a writable layer is added on top of the read-only layers. Any changes made by the container are stored in this writable layer.

The copy-on-write mechanism means that when a container tries to modify a file from a read-only layer, the file is first copied into the writable layer. The modification only affects the copy, not the original layer.

This design is important because it saves storage space and improves performance. Multiple containers can share the same image layers, and containers can start quickly without copying the entire filesystem.

## Question 4: OCI Standards

The Open Container Initiative (OCI) is an organization that defines open standards for containers. Its goal is to make containers portable and compatible across different tools and platforms.

The OCI Runtime Specification defines how a container should be created and run. It describes the container lifecycle and how the runtime interacts with the operating system.

The OCI Image Specification defines the format of container images, including layers and metadata. This allows images built by one tool to be used by another.

The OCI Distribution Specification defines how container images are pushed to and pulled from registries.

Standardization is important because it avoids vendor lock-in and allows different container tools to work together in the same ecosystem.

## Question 5: Container Runtime Architecture

In a Kubernetes environment, container execution has multiple layers.

At the top level is Kubernetes, which is the orchestration layer. Kubernetes is responsible for scheduling containers, managing scaling, and maintaining the desired state of applications.

Below Kubernetes is the high-level container runtime, such as containerd or CRI-O. This layer manages container images and handles container lifecycle operations requested by Kubernetes.

At the lowest level is the low-level runtime, usually runc. This runtime directly interacts with the Linux kernel to create containers using namespaces and cgroups.

These layers work together step by step: Kubernetes sends requests, the high-level runtime processes them, and the low-level runtime executes them on the operating system.

## Question 6: Container Runtime Interface (CRI)

The Container Runtime Interface (CRI) is an API used by Kubernetes to communicate with container runtimes. Its main purpose is to allow Kubernetes to support different runtimes without being tightly coupled to one implementation.

CRI provides two main services. The RuntimeService is responsible for container and pod lifecycle operations, such as starting and stopping containers. The ImageService handles image-related operations, such as pulling and removing images.

By using CRI, Kubernetes becomes more flexible and easier to extend with new container runtimes.

## Question 7: Java Container Awareness

Before JDK 8u191 and JDK 10, Java did not work well in containers. The JVM read system resources from the host machine instead of the container limits. This often caused Java applications to use too much memory and crash in containers.

Modern JDK versions added support for container environments by reading cgroup information. The JVM can now correctly detect memory and CPU limits set for containers.

For memory configuration, it is recommended to use options like -XX:MaxRAMPercentage instead of fixed heap sizes. This allows the JVM to adjust memory usage based on the container limits.

This is important in production because it makes Java applications more stable and predictable when running in containers.

## Question 8: Multi-stage Docker Builds

Multi-stage Docker builds allow a Dockerfile to have multiple build stages. Each stage can use a different base image, and only necessary files are copied to the final image.

The main benefit is reducing image size and improving security. Build tools and source code are not included in the final image.

A common example is building a Java application with Maven. One stage uses a Maven image to compile the code and generate a JAR file. The final stage uses a small JRE image and copies only the JAR file.

This greatly reduces image size and makes deployment faster and safer.

## Question 9: Container Networking

Docker supports several network modes.

Bridge mode is the default mode. Containers are connected through a virtual bridge and can communicate with each other on the same host.

Host mode allows a container to share the host’s network stack. This improves performance but reduces isolation.

None mode disables networking completely and is useful for containers that do not need network access.

Overlay mode allows containers to communicate across multiple hosts and is commonly used in clustered environments.

In Kubernetes, networking is handled by CNI plugins such as Calico or Flannel. These plugins manage IP addresses and network connectivity between pods.

## Question 10: Container Storage

Containers support different types of storage.

Named volumes are managed by the container runtime and persist even after a container is deleted. They are commonly used for databases and other stateful applications.

Bind mounts map a host directory directly into a container. They provide good performance but depend on the host filesystem structure. They are often used in development.

tmpfs stores data in memory only. It is very fast but not persistent, so data is lost when the container stops. It is useful for temporary or sensitive data.

Each storage type should be chosen based on performance needs and data persistence requirements.
