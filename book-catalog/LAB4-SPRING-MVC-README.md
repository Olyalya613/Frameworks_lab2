# Laboratory work 4 — Spring MVC

The project preserves the Maven modules `core / persistence / web` and continues the previous Spring Boot laboratory.

## Spring MVC elements
- `WebConfig`: `@Configuration` + `@EnableWebMvc`.
- `BookController`: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@DeleteMapping`.
- Spring Boot automatically registers `DispatcherServlet`.
- `ApiExceptionHandler` returns JSON errors with HTTP statuses.
- `core` contains models and business rules; `persistence` contains JDBC/H2 repositories.

## Run
From project root:

    mvn clean install
    cd web
    mvn spring-boot:run

Server: http://localhost:7070

## Postman checks
GET http://localhost:7070/books

GET http://localhost:7070/books/1

POST http://localhost:7070/comments
Content-Type: application/json

    {
      "bookId": 1,
      "author": "Olha",
      "text": "Spring MVC works correctly"
    }

Expected: 201 Created.

Validation: send empty author/text to POST /comments -> 400 JSON.
Not found: GET /books/999999 -> 404 JSON.
Delete: DELETE /comments/{id} -> 204 No Content when allowed.
