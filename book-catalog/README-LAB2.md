# Book Catalog — 3-tier Maven project

## Модулі
- `core` — моделі, порти, бізнес-правила. Не залежить від JDBC/Servlet.
- `persistence` — JDBC/H2 адаптери.
- `web` — Servlet/JSON/HTTP, Jetty 11.

## Вимоги
- JDK 21
- Maven 3.9+

## Перший запуск
Відкрити термінал саме у папці `book-catalog`:

```powershell
mvn clean install
cd web
mvn jetty:run
```

Відкрити: http://localhost:8080/

Зупинка Jetty: `Ctrl+C`.

## HTTP
- `GET /books?q=&page=0&size=10&sort=title`
- `GET /books/{id}`
- `POST /books/{id}/comments` (`author`, `text`, form-urlencoded)
- `DELETE /comments/{id}`

Додатково UI передає `field=all|title|author|year`, щоб можна було явно шукати за назвою, автором або роком. Без `field` контракт `GET /books` працює: `q` шукає одразу в назві, авторі та році.

## Коди помилок
400 — неправильні параметри/валідація; 404 — ресурс/endpoint не знайдено; 409 — відгук старший за 24 години; 500 — внутрішня помилка.

Формат:
```json
{"error":{"status":400,"code":"BAD_REQUEST","message":"..."}}
```

## Архітектура
`web` має `persistence` тільки як Maven runtime dependency. Створення persistence adapter виконується у composition root через reflection, тому bytecode web не залежить від persistence і ArchUnit правило це перевіряє.

## Для звіту
1. `core` не залежить від JDBC/Servlet, щоб бізнес-логіка не була прив'язана до БД чи HTTP та могла тестуватися окремо.
2. Правило 24 годин знаходиться у `CommentService`, бо це бізнес-правило, а не правило БД чи контролера.
3. INFO — успішне створення/видалення відгуку; WARN — 4xx; ERROR — 5xx.
4. Приклад правила для public-конструкторів core:
```java
constructors().that().areDeclaredInClassesThat().resideInAPackage("..core..")
    .should().bePublic();
```

Для скриншотів: `mvn clean install`, консоль Jetty після створення/видалення відгуку, сторінка каталогу та сторінка книги.


## UI/API mapping
Browser UI uses `/api/...` internally so static `index.html` cannot conflict with the API servlet.
The assignment endpoints are exposed under the API servlet as:
- `/api/books`
- `/api/books/{id}`
- `/api/books/{id}/comments`
- `/api/comments/{id}`

The search form uses a real HTML `submit` event, so both the button and Enter trigger search.
