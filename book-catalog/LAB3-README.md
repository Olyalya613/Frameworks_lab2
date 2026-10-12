# Лабораторна робота №3 — Javalin

Проєкт створений на основі Лаб 2. Модулі `core` і `persistence` не змінені. Зміни виконані тільки у `web`.

## 1. Окрема Git-гілка

Після розпакування/копіювання проєкту у ваш Git-репозиторій:

```powershell
git checkout main
git pull
git checkout -b lab3-javalin
git add .
git commit -m "Лабораторна 3: версія на Javalin"
git push origin lab3-javalin
```

## 2. Збірка

У корені `book-catalog`:

```powershell
mvn clean install
```

Очікувано: `BUILD SUCCESS`.

## 3. Запуск

```powershell
cd web
mvn exec:java
```

API: `http://localhost:7070`

Зупинка: `Ctrl+C`.

## 4. Перевірка маршрутів у Postman / REST Client

### Список книг
`GET http://localhost:7070/books?page=0&size=10&sort=title`

Пошук:
`GET http://localhost:7070/books?q=Orwell&field=author&page=0&size=10&sort=title`

### Одна книга
`GET http://localhost:7070/books/1`

### Додати коментар
`POST http://localhost:7070/books/1/comments`

Header: `Content-Type: application/json`

Body → raw → JSON:
```json
{
  "author": "Olha",
  "text": "Цікавий відгук для лабораторної роботи"
}
```

Очікуваний статус: `201 Created`.

### Видалити коментар
`DELETE http://localhost:7070/comments/1`

Для щойно створеного коментаря очікуваний статус: `204 No Content`.

### Перевірка 400
`GET http://localhost:7070/books?page=abc`

### Перевірка 404
`GET http://localhost:7070/books/999999`

## 5. Що сфотографувати для звіту

1. `mvn clean install` з `BUILD SUCCESS`.
2. GET `/books` у Postman.
3. GET `/books/1`.
4. POST `/books/1/comments` зі статусом 201.
5. DELETE `/comments/{id}` зі статусом 204.
6. Консоль, де видно middleware-лог `HTTP GET /books` та INFO створення/видалення коментаря.

## Коротка теорія

**Javalin** — легкий Java/Kotlin веб-фреймворк, у якому маршрути задаються без окремих Servlet-класів. Основний клас `Javalin` створює і запускає застосунок.

**Context (`ctx`)** представляє поточний HTTP-запит і відповідь. Через нього читаються `queryParam`, `pathParam`, тіло запиту, задається статус та повертається JSON.

**Маршрути** задаються методами `app.get()`, `app.post()`, `app.delete()` тощо.

**Middleware** через `app.before()` дозволяє виконати спільну дію перед маршрутами, наприклад записати кожний HTTP-запит у лог.

**Exception handling** через `app.exception()` дозволяє централізовано перетворювати винятки у потрібні HTTP-коди та JSON-помилки.

## Servlet API vs Javalin

| Критерій | Servlet API | Javalin |
|---|---|---|
| Маршрут | `web.xml`, mapping, перевірка URI | `app.get("/books", ...)` |
| GET | `doGet()` | `app.get()` |
| POST | `doPost()` | `app.post()` |
| Query-параметр | `request.getParameter("q")` | `ctx.queryParam("q")` |
| Path-параметр | часто треба розбирати URI | `ctx.pathParam("id")` |
| JSON-відповідь | налаштування response + ObjectMapper | `ctx.json(object)` |
| HTTP-статус | `response.setStatus(...)` | `ctx.status(...)` |
| Middleware | Filter | `app.before()` |
| Помилки | try/catch або filter/error mapping | `app.exception()` |

## Відповіді на питання

**1. Як у Javalin задаються маршрути для різних HTTP-методів?**  
Маршрути задаються методами `app.get()`, `app.post()`, `app.delete()` та іншими. Наприклад: `app.get("/books", ctx -> {...})`.

**2. Для чого використовується Context?**  
`Context` містить дані поточного запиту і дозволяє сформувати відповідь. Наприклад, `ctx.queryParam("q")` отримує query-параметр, а `ctx.pathParam("id")` — параметр із маршруту `/books/{id}`.

**3. Чим app.get()/app.post() відрізняються від doGet()/doPost()?**  
У Javalin маршрут і код його обробки задаються прямо в одному місці. У Servlet API потрібно створювати Servlet, перевизначати `doGet()`/`doPost()` і додатково налаштовувати mapping. Тому в Javalin HTTP-рівень виходить коротшим і зрозумілішим.

**4. Для чого app.before() та app.exception()?**  
`app.before()` виконує спільний код перед запитами, у цій роботі він використаний для логування. `app.exception()` централізовано обробляє винятки та формує потрібний HTTP-код і JSON-відповідь.

## Висновок

У цій роботі HTTP-рівень було реалізовано через Javalin замість Servlet API. Маршрути стали коротшими та наочнішими, параметри зручно отримуються через `Context`, а middleware і централізована обробка винятків дозволяють не дублювати однаковий код у кожному маршруті.
