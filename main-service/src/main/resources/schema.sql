DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_to_event CASCADE;
DROP TABLE If EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    name  VARCHAR(250)        NOT NULL,
    email VARCHAR(254) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    name VARCHAR(50) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS location
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    lat                NUMERIC,
    lon                NUMERIC
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    annotation         VARCHAR(2000)               NOT NULL,
    category_id        BIGINT                      NOT NULL,
    confirmed_Requests BIGINT,
    create_date        TIMESTAMP WITHOUT TIME ZONE,
    description        VARCHAR(7000),
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT                      NOT NULL,
    location_id        BIGINT,
    paid               BOOLEAN,
    participant_limit  INTEGER DEFAULT 0,
    published_date     TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT true,
    status             VARCHAR(200),
    title              VARCHAR(120)                NOT NULL,
    CONSTRAINT fk_event_to_user FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_event_to_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES location (id)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    create_date  TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(20),
    CONSTRAINT fk_requests_to_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requests_to_user FOREIGN KEY (requester_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    pinned BOOLEAN      NOT NULL,
    title  VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS compilations_to_event
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT fk_event_compilation_to_event FOREIGN KEY (event_id) REFERENCES events (id) ON UPDATE CASCADE,
    CONSTRAINT fk_event_compilation_to_compilation FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON UPDATE CASCADE
    );