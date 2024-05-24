CREATE TABLE IF NOT EXISTS app_user_entity (
    id SERIAL PRIMARY KEY,
    fName VARCHAR(30),
    lName VARCHAR(30),
    username VARCHAR(30),
    phone VARCHAR(15),
    email VARCHAR(40),
    address VARCHAR(50),
    city VARCHAR(30),
    postal_code VARCHAR(5),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);


CREATE TABLE IF NOT EXISTS nail_service_entity (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50),
    duration INTEGER,
    price DECIMAL(10, 2),
    admin_service BOOLEAN
);

CREATE TABLE IF NOT EXISTS reservation_entity (
    id SERIAL PRIMARY KEY,
    fName VARCHAR(30),
    lName VARCHAR(30),
    email VARCHAR(40),
    phone VARCHAR(15),
    address VARCHAR(50),
    city VARCHAR(30),
    postal_code VARCHAR(5),
    price DECIMAL(10, 2),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    nail_service_id BIGINT,
    status VARCHAR(50),
    FOREIGN KEY (nail_service_id) REFERENCES nail_service_entity(id)
);

CREATE TABLE IF NOT EXISTS reservation_settings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30),
    start_time TIME,
    end_time TIME,
    is_active BOOLEAN
);
