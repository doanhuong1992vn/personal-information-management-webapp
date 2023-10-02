INSERT INTO users (username, password, birthday)
VALUES ('test', '$2a$10$iPWV4Fastic0HpveOOnL0utmpcAu4ffC3Vrxjo0BFyDdWbtr9XmDu', '1992-02-18');
-- Password: 123

INSERT INTO users_roles (username, role_id)
VALUES ('test', (SELECT id FROM role WHERE name = 'ROLE_USER'));