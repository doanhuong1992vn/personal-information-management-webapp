CREATE TABLE users_roles
(
    username             VARCHAR,
    role_id              INT,
    PRIMARY KEY (username, role_id),
    FOREIGN KEY (username) REFERENCES users (username),
    FOREIGN KEY (role_id) REFERENCES role (id)
);