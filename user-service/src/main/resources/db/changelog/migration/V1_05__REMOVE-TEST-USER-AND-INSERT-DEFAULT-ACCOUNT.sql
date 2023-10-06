DELETE FROM users_roles WHERE username = 'test';

DELETE FROM users WHERE username = 'test';

INSERT INTO users (username, password)
VALUES ('webGLsoft2023', '$2a$10$olU3OrQgvpF7thNqszlHFOuoQz7Uh9jEqx/eMihONtxGqG9DlFcTa');
-- Password: WebGlsoft@2023

INSERT INTO users_roles (username, role_id)
VALUES ('webGLsoft2023', (SELECT id FROM role WHERE name = 'ROLE_USER'));