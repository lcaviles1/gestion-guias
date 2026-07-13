# Sistema de Gestión de Guías de Despacho

Proyecto desarrollado para la asignatura Desarrollo Cloud Native — CDY2204.

Esta rama corresponde a la Semana 8 e incorpora mensajería asíncrona con RabbitMQ, persistencia en Amazon RDS MySQL y seguridad mediante Spring Security.

## Funcionalidades

- Creación de guías y generación de archivos en EFS.
- Publicación de mensajes en la cola `guias.procesamiento`.
- Consumo de mensajes y persistencia en RDS MySQL.
- Envío de mensajes fallidos a `guias.errores`.
- Subida y descarga de archivos en Amazon S3.
- Control de acceso por roles.
- Publicación de endpoints mediante AWS API Gateway.

## Tecnologías

- Java 17 y Spring Boot
- Spring Security y JWT
- RabbitMQ y Spring AMQP
- Amazon EC2, EFS, S3 y RDS MySQL
- AWS API Gateway
- Azure AD B2C
- Docker, Maven y Postman

## Colas RabbitMQ

- `guias.procesamiento`: flujo principal de mensajes.
- `guias.errores`: mensajes que no pudieron persistirse.

## Roles

- `GESTOR_GUIAS`: crear, consumir, consultar, subir, actualizar y eliminar guías.
- `DESCARGA_GUIAS`: descargar guías autorizadas.

## Endpoints principales

| Método | Endpoint | Permiso |
|---|---|---|
| GET | `/health` | Público |
| POST | `/api/guias/crear` | GESTOR_GUIAS |
| POST | `/api/guias/consumir` | GESTOR_GUIAS |
| GET | `/api/guias/procesadas/count` | GESTOR_GUIAS |
| POST | `/api/guias/subir` | GESTOR_GUIAS |
| GET | `/api/guias/descargar` | DESCARGA_GUIAS |
| PUT | `/api/guias/actualizar` | GESTOR_GUIAS |
| DELETE | `/api/guias/eliminar` | GESTOR_GUIAS |
| GET | `/api/guias/historial` | GESTOR_GUIAS |

## Ejecución

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Las credenciales y conexiones se configuran mediante variables de entorno.

## Rama de entrega

`semana8-colas-rabbitmq`

## Autor

Lucas Avilés
