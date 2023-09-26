CREATE TABLE users (
                       username VARCHAR PRIMARY KEY,
                       password VARCHAR NOT NULL,
                       birthday DATE,
                       create_time TIMESTAMP DEFAULT current_timestamp,
                       last_login TIMESTAMP
);
