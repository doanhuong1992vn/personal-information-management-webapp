CREATE TYPE role_name AS ENUM ('ROLE_ADMIN', 'ROLE_USER');
CREATE TABLE role (
                      id                  SERIAL PRIMARY KEY,
                      name                role_name NOT NULL,
                      description         VARCHAR
);