DROP TABLE persons IF EXISTS;

CREATE TABLE persons  (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50)
);