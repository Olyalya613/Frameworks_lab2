# Лабораторна робота №3 — Javalin

Проєкт створений на основі Лаб 2. Модулі `core` і `persistence` не змінені. Зміни виконані тільки у `web`.

# Збірка

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


