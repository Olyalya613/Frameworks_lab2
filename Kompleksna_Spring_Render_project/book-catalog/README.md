# Комплексна лабораторна робота · Spring Boot на Render

Багатомодульний Maven-проєкт каталогу книг із MVC, Spring Security, JPA, REST, AOP та тестами з ЛР10. Для production використовується PostgreSQL. Локальний профіль `lab9` з H2 збережено для попередніх сценаріїв. Для перевірки саме PostgreSQL використовуйте Compose або профіль `prod`.

## 1. Локальна збірка у Windows

У PowerShell відкрийте папку з головним `pom.xml`. Потрібні JDK 21 і Maven.

```powershell
mvn clean package -pl web -am
```

Результат: `web/target/web-1.0-SNAPSHOT.jar`. Усі модулі можна також перевірити командою `mvn test`.

Якщо є Docker Desktop, створіть у цій самій папці файл `.env` із двома власними паролями. Для паролів у локальному `.env` зручно взяти довгі випадкові буквено-цифрові рядки. Пароль адміністратора має бути щонайменше 16 символів і до 72 байтів UTF-8.

```dotenv
POSTGRES_PASSWORD=REPLACE_WITH_RANDOM_DB_PASSWORD
APP_ADMIN_PASSWORD=REPLACE_WITH_DIFFERENT_RANDOM_ADMIN_PASSWORD
```

```powershell
docker compose up --build -d
docker compose logs -f web
```

Перевірте [каталог](http://localhost:8080/books) та [Actuator](http://localhost:8080/actuator/health). Вхід адміністратора: `admin` і ваш `APP_ADMIN_PASSWORD`. Для HTTP локально Compose задає `APP_SECURE_COOKIE=false`. На Render залиште безпечне значення за замовчуванням. `docker compose down` зупиняє сервіси зі збереженням тому БД.

Dockerfile розташований у `web/Dockerfile`, а контекстом збірки є папка з батьківським `pom.xml`:

```powershell
docker build -f web/Dockerfile -t mpf-labs-web .
```

## 2. GitHub

Створіть гілку `deploy` у `Frameworks_lab2`. Архів містить дві папки верхнього рівня: `book-catalog` і `.github`. Скопіюйте обидві у корінь репозиторію. Робочий процес має бути доступний за шляхом `.github/workflows/ci.yml` у корені, а батьківський POM — за шляхом `book-catalog/pom.xml`. На GitHub перевірте вкладку **Actions**.

Якщо завантажуєте файли через браузер, переносіть модулі окремими порціями, зберігаючи шляхи. Файл workflow можна створити через **Add file → Create new file**, указавши повний шлях `.github/workflows/ci.yml`, і вставити його вміст із ZIP. Не додавайте `.env`, `target` та локальні файли БД.

## 3. PostgreSQL на Render

1. Створіть **Postgres** у Render. Виберіть той самий регіон, у якому створите Web Service. Запишіть внутрішній **Hostname**, порт, назву БД, користувача та пароль.
2. Для Spring сформуйте JDBC URL `jdbc:postgresql://<internal-host>:5432/<database>`. Якщо Render показує адресу `postgresql://user:password@host/database`, візьміть з неї лише внутрішній хост і назву БД. Логін і пароль задайте окремо.
3. Для DBeaver або psql із вашого комп’ютера використовуйте зовнішні параметри Render та TLS. Якщо зовнішній доступ обмежено, перевірте налаштування доступу для своєї IP-адреси.

## 4. Web Service на Render

Створіть **Web Service**, підключіть GitHub і виберіть гілку `deploy` та середовище **Docker**.

| Налаштування | Значення, якщо проєкт у підпапці репозиторію |
|---|---|
| Root Directory | `book-catalog` |
| Dockerfile Path | `./web/Dockerfile` |
| Docker Build Context Directory | `.` |
| Health Check Path | `/actuator/health` |

Якщо `pom.xml` лежить у корені репозиторію, Root Directory залиште порожнім. Docker Command залиште порожнім, щоб застосувався ENTRYPOINT.

У **Environment** додайте:

| Змінна | Значення |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | JDBC URL із внутрішнім хостом |
| `SPRING_DATASOURCE_USERNAME` | користувач PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | пароль PostgreSQL |
| `APP_ADMIN_PASSWORD` | ваш окремий пароль для першого створення `admin` |

Render передає `PORT` самостійно, а застосунок слухає `0.0.0.0`. SMTP необов’язковий. Якщо потрібне надсилання пошти, конфігурація читає `SMTP_HOST`, `SMTP_PORT`, `GMAIL_USERNAME`, `GMAIL_APP_PASSWORD`, `BOOKAPP_MAIL_ENABLED` та `BOOKAPP_MAIL_TO`. У тестовому розгортанні залиште SMTP вимкненим.

Під час першого запуску Flyway створює схему й демонстраційні дані. Hibernate перевіряє схему. Акаунт `admin` створюється один раз і зберігається у БД. Заміна `APP_ADMIN_PASSWORD` після створення акаунта не змінює його наявний пароль.

## 5. Перевірка і знімки для звіту

1. **Environment**. Покажіть імена змінних, приховавши значення секретів.
2. **Logs**. Покажіть застосування Flyway і рядок `Started SpringBookApplication`.
3. **Публічний URL**. Відкрийте адресу `https://<your-service>.onrender.com`, увійдіть і покажіть каталог разом з адресним рядком.
4. **Робочий сценарій**. Зареєструйте USER, увійдіть і перевірте, що `/books/add` повертає 403. Увійдіть як ADMIN, додайте книгу, відкрийте її і додайте коментар. Зробіть знімок нового коментаря. Також перевірте редагування та видалення свіжого коментаря.
5. **Health**. Відкрийте `https://<your-service>.onrender.com/actuator/health` без входу. Очікується HTTP 200 і `{"status":"UP"}`.
6. **GitHub Actions**. Покажіть успішний workflow і артефакт `book-catalog-web`.
7. **PostgreSQL**. У DBeaver покажіть створені записи, наприклад результат `SELECT id, book_id, text FROM comments ORDER BY id DESC LIMIT 5;`. Для користувачів вибирайте `id, username, role` без колонки `password`.
