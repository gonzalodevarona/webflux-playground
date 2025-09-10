# WebFlux Playground - Section 1 ONLY

A small Spring Boot WebFlux playground that demonstrates both reactive (WebClient) and traditional (blocking RestClient) HTTP clients interacting with an external service.

The application expects an external helper service (provided as external-services.jar) to be running. It exposes simple product endpoints and an SSE stream.

## 1) Run the external services (required first)

How To Run

- Default port (7070):
  java -jar external-services.jar

- It uses port 7070 by default.

- To change the port (example: 8080):
  java -jar external-services.jar --server.port=8080

Notes
- The app’s controllers are currently configured to call http://localhost:7070. If you run the external service on any other port, update the baseUrl in the controllers or set an application property and wire it in code.

## 2) Run the Spring Boot application

From the project root:

- Using Maven (dev run):
  ./mvnw spring-boot:run

- Or build the jar:
  ./mvnw clean package
  java -jar target/webflux-playground-0.0.1-SNAPSHOT.jar

The Spring Boot app runs (by default) on http://localhost:8080.

## Available Endpoints

All endpoints return or involve a Product with fields:
- id (Integer)
- description (String)
- price (Integer)

Endpoints are grouped into two controllers: /reactive (WebFlux WebClient) and /traditional (blocking RestClient).

Reactive controller (/reactive)
- GET /reactive/products
  Description: Calls the external service /demo01/products/notorious and returns the resulting list as a Flux<Product>. Errors are swallowed (onErrorComplete) and would result in an empty stream if the external service is unavailable.
  Example:
    curl http://localhost:8080/reactive/products

- GET /reactive/products/stream (Server-Sent Events)
  Produces: text/event-stream
  Description: Connects to the external service /demo01/products and streams Product items as SSE. Keep the connection open to receive ongoing items.
  Example (watch stream):
    curl -N http://localhost:8080/reactive/products/stream

Traditional controller (/traditional)
- GET /traditional/products
  Description: Calls external /demo01/products/notorious using a blocking RestClient and returns a JSON array List<Product>.
  Example:
    curl http://localhost:8080/traditional/products

- GET /traditional/products/non-reactive
  Description: Calls external /demo01/products using a blocking RestClient, then adapts the List<Product> to a Flux<Product> (still retrieved in a blocking fashion).
  Example:
    curl http://localhost:8080/traditional/products/non-reactive

## Configuration Notes

- The controllers use a hard-coded base URL of http://localhost:7070 for the external service. If you changed the external service port, update:
  - ReactiveWebController: WebClient.builder().baseUrl("http://localhost:7070")
  - TraditionalWebController: RestClient.builder().baseUrl("http://localhost:7070")

Alternatively, you can refactor these to read from an application property (e.g., external.service.base-url) and configure it in src/main/resources/application.properties.

## Troubleshooting

- If you get connection errors or empty responses, ensure external-services.jar is running and reachable at the configured port.
- Port already in use: change Spring Boot server.port or the external service port.
- For SSE testing, use curl -N to prevent buffering and keep the stream open.
