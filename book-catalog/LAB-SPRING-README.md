# Laboratory work — Spring Core & Boot

This version continues the Book Catalog project and replaces the Javalin web layer with Spring Boot.

## What is demonstrated
- `@SpringBootApplication` — application entry point.
- `@Service` — application services.
- `@Repository` — repository adapters.
- `@Component` — `BeanInspector`.
- `@Configuration` + `@Bean` — custom `Clock` bean.
- Constructor DI — controllers/services.
- Field DI — `BeanInspector` (shown intentionally for the assignment).
- `application.properties` — application name, port and custom settings.
- Spring Boot auto-configuration via `spring-boot-starter-web`.

## Run
From the project root:

```powershell
mvn clean install
cd web
mvn spring-boot:run
```

Open:
- http://localhost:7070/info
- http://localhost:7070/books
- http://localhost:7070/books/1

POST comment in Postman:
`POST http://localhost:7070/books/1/comments`

```json
{
  "author": "Olha",
  "text": "Spring Boot laboratory comment"
}
```

Delete using the returned comment id:
`DELETE http://localhost:7070/comments/{id}`

## Recommended screenshots for the report
1. Terminal: Spring Boot startup and `Started SpringBookApplication`.
2. Terminal: `FIELD DI works...` and `Spring beans loaded...`.
3. Browser/Postman: `GET /info` showing properties and bean flags.
4. Postman: `GET /books` — 200.
5. Postman: `POST /books/1/comments` — 201.
6. Postman: `DELETE /comments/{id}` — 204.
