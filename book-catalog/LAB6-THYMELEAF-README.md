# Lab 6 — Thymeleaf

## Run
```powershell
mvn clean install
cd web
mvn spring-boot:run
```

Open: http://localhost:7070/books

Add form: http://localhost:7070/books/add

Language: http://localhost:7070/books?lang=uk and http://localhost:7070/books?lang=en

The form uses Thymeleaf `th:object` / `th:field`, the list uses `th:each`, empty state uses `th:if`/`th:unless`, and the common header is included with `th:replace`.
