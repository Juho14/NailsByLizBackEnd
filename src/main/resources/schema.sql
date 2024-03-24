CREATE TABLE IF NOT EXISTS app_user_entity (
    id SERIAL PRIMARY KEY,
    fName VARCHAR(255),
    lName VARCHAR(255),
    username VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS nail_service_entity (
    id SERIAL PRIMARY KEY,
    type VARCHAR(255),
    duration INTEGER,
    price DOUBLE PRECISION,
    admin_service BOOLEAN
);

CREATE TABLE IF NOT EXISTS reservation_entity (
    id SERIAL PRIMARY KEY,
    fName VARCHAR(255),
    lName VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    nail_service_id BIGINT,
    status VARCHAR(255),
    FOREIGN KEY (nail_service_id) REFERENCES nail_service_entity(id)
);

CREATE TABLE IF NOT EXISTS reservation_settings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    start_time TIME,
    end_time TIME,
    is_active BOOLEAN
);
