INSERT INTO books(title,author,pub_year) VALUES ('1984','George Orwell',1949),('Clean Code','Robert C. Martin',2008),('Effective Java','Joshua Bloch',2018),('Refactoring','Martin Fowler',2018),('Design Patterns','Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides',1994),('The Pragmatic Programmer','Andrew Hunt, David Thomas',1999);
-- Demo users only; password values are deliberately unusable placeholders (no authentication implemented).
INSERT INTO users(username,password,role) VALUES ('Olha','!demo-no-login!','USER'),('Anna','!demo-no-login!','USER'),('Max','!demo-no-login!','USER');
INSERT INTO comments(book_id, user_id, text, created_at)
SELECT b.id, u.id, seed.comment_text, CURRENT_TIMESTAMP
FROM (VALUES
    ('1984', 'Olha', 'Цікава класика!'),
    ('Clean Code', 'Olha', 'Корисна книга для розробників.'),
    ('1984', 'Anna', 'Рекомендую прочитати.'),
    ('Effective Java', 'Max', 'Чудові приклади Java.')
) AS seed(book_title, username, comment_text)
JOIN books b ON b.title = seed.book_title
JOIN users u ON u.username = seed.username;
