# Custom Framework WebApp

Pequeña aplicación web usando un framework HTTP propio (sin Spring), empaquetada en Docker y lista para desplegarse en AWS EC2.

## Arquitectura

- **Módulo de framework** (`com.example.framework`):
  - `SimpleHttpServer`: servidor HTTP basado en `ServerSocket`.
    - Maneja múltiples conexiones concurrentes usando un `ExecutorService` (pool de hilos).
    - Expone métodos `start()` y `stop()` para control explícito del ciclo de vida.
    - Soporta rutas simples tipo `GET /hello`.
  - `HttpRequest`: representación sencilla de la petición (método, path, query params).
  - `RouteHandler`: interfaz funcional para definir handlers.
- **Módulo de aplicación** (`com.example.app`):
  - `MainApplication`: registra rutas y arranca el servidor.
    - `GET /hello?name=Allan` -> `Hello from custom framework, Allan!`
    - `GET /shutdown` -> responde y luego apaga el servidor de forma elegante.

## Requisitos

- Java 17+
- Maven 3+
- Docker y Docker Compose
- Cuenta en Docker Hub
- Cuenta AWS con acceso a EC2

## Compilar la aplicación

Desde la carpeta del proyecto `custom-framework-webapp`:

```bash
mvn clean install
```

Esto genera:
- `target/custom-framework-webapp-1.0-SNAPSHOT.jar`
- Dependencias copiadas en `target/dependency` (por el plugin `maven-dependency-plugin`).

## Ejecutar localmente (sin Docker)

```bash
# Windows PowerShell, estando en custom-framework-webapp
$env:PORT=6000
mvn exec:java -Dexec.mainClass="com.example.app.MainApplication"
```

Luego abre en el navegador:

- `http://localhost:6000/hello`
- `http://localhost:6000/hello?name=Allan`

Para apagar de forma elegante:

- `http://localhost:6000/shutdown`

## Imagen Docker

Dockerfile: `Dockerfile` en la raíz del proyecto.

Construir la imagen:

```bash
mvn clean package
docker build -t customframeworkwebapp .
```

Verifica que exista:

```bash
docker images
```

### Correr contenedores individuales

```bash
docker run -d -p 34000:6000 --name cfweb1 customframeworkwebapp
# Opcionales
# docker run -d -p 34001:6000 --name cfweb2 customframeworkwebapp
# docker run -d -p 34002:6000 --name cfweb3 customframeworkwebapp
```

Probar en el navegador:

- `http://localhost:34000/hello`

Para apagar un contenedor, puedes llamar:

- `http://localhost:34000/shutdown`

o detener el contenedor:

```bash
docker stop cfweb1
```

## Docker Compose

Archivo: `docker-compose.yml`.

Levantar el servicio:

```bash
docker-compose up -d
```

Ver servicios:

```bash
docker ps
```

Probar:

- `http://localhost:8088/hello`

Parar y eliminar servicios:

```bash
docker-compose down
```

## Subir la imagen a Docker Hub

1. Crea un repositorio en Docker Hub, por ejemplo: `TUUSUARIO/customframeworkwebapp`.
2. En tu máquina local, etiqueta la imagen:

```bash
docker tag customframeworkwebapp TUUSUARIO/customframeworkwebapp:latest
```

3. Inicia sesión en Docker Hub:

```bash
docker login
```

4. Empuja la imagen:

```bash
docker push TUUSUARIO/customframeworkwebapp:latest
```

En Docker Hub deberías ver el tag `latest` en tu repositorio.

## Despliegue en AWS EC2

1. Crea una instancia EC2 (por ejemplo Amazon Linux 2) y conéctate por SSH.
2. Instala Docker:

```bash
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
# Cierra sesión y vuelve a entrar para aplicar el cambio de grupo
```

3. Inicia sesión en Docker Hub desde la instancia:

```bash
docker login
```

4. Descarga la imagen y crea un contenedor:

```bash
docker pull TUUSUARIO/customframeworkwebapp:latest

docker run -d -p 42000:6000 --name cfwebaws TUUSUARIO/customframeworkwebapp:latest
```

5. En el Security Group de la instancia EC2, abre el puerto `42000/TCP` desde Internet (0.0.0.0/0 o según tu política).

6. Prueba desde tu navegador local con la DNS pública de la instancia, por ejemplo:

- `http://ec2-XX-YY-ZZ-A.compute-1.amazonaws.com:42000/hello`

## Evidencias para la entrega

En tu repositorio de GitHub incluye:

- Código fuente completo del proyecto.
- Este `README.md` actualizado con:
  - Capturas de pantalla de:
    - Contenedores corriendo en tu máquina local (Docker Desktop / `docker ps`).
    - Imagen publicada en Docker Hub.
    - Servicio respondiendo en EC2 (`/hello`).
  - Descripción corta de la arquitectura y clases principales.
  - Comandos usados para compilar, construir imágenes y desplegar.

Para el video sugerido:
- Muestra cómo levantas la app localmente.
- Cómo construyes la imagen y corres contenedores.
- Cómo accedes a la app en AWS EC2.
