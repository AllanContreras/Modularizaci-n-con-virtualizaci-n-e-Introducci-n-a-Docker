# Modularización con virtualización e Introducción a Docker

Este repositorio contiene el desarrollo del taller y la tarea:

- Una aplicación mínima con **Spring Boot** empaquetada en Docker.
- Una aplicación web usando un **framework HTTP propio** (sin Spring), concurrente y con apagado elegante, también empaquetada en Docker y lista para desplegar en AWS EC2.

## Estructura del repositorio

- `spring-docker-demo/`
  - Proyecto Maven con Spring Boot.
  - Controlador REST simple (`/greeting`).
  - `Dockerfile` y `docker-compose.yml` para el taller.
- `custom-framework-webapp/`
  - Proyecto Maven con un framework HTTP propio basado en `ServerSocket`.
  - Servidor concurrente con pool de hilos y ruta de apagado (`/shutdown`).
  - `Dockerfile` y `docker-compose.yml`.
  - README específico con instrucciones detalladas.

## Cómo correr rápidamente cada parte

### 1. Demo con Spring Boot

```bash
cd spring-docker-demo
mvn clean package

docker build -t springdockerdemo .
docker run -d -p 34000:5000 --name springdemo springdockerdemo
```

Luego abre en el navegador:

- `http://localhost:5000/greeting`

*(O ajusta el puerto según el `Dockerfile` / variables de entorno que uses.)*

### 2. WebApp con framework propio (Tarea)

```bash
cd custom-framework-webapp
mvn clean package

docker build -t customframeworkwebapp .
docker run -d -p 34000:6000 --name cfweb customframeworkwebapp
```

Probar en el navegador:

- `http://localhost:34000/hello`
- `http://localhost:34000/hello?name=Allan`

Apagado elegante del servidor dentro del contenedor:

- `http://localhost:34000/shutdown`

Más detalles (arquitectura, comandos de Docker Hub y despliegue en AWS EC2) están en:

- `custom-framework-webapp/README.md`

Ahí se describe también qué evidencias capturar (pantallas, comandos, video) para la entrega de la tarea.
