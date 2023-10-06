ALTER TABLE role ALTER COLUMN name TYPE VARCHAR;
INSERT INTO role (name, description) VALUES ('ROLE_ADMIN', 'Admin'), ('ROLE_USER', 'User');
