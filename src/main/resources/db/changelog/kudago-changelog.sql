--liquibase formatted sql

--changeset alexandergarifullin:1
CREATE SCHEMA IF NOT EXISTS kudago;

--changeset alexandergarifullin:2
CREATE TABLE kudago.t_locations
(
    id     BIGSERIAL  PRIMARY KEY,
    c_slug TEXT       NOT NULL UNIQUE,
    c_name TEXT       NOT NULL
);

--changeset alexandergarifullin:3
CREATE TABLE kudago.t_events
(
    id            BIGSERIAL PRIMARY KEY,
    c_name        TEXT      NOT NULL,
    c_start_date  TIMESTAMP NOT NULL,
    c_price       TEXT,
    c_free        BOOLEAN   NOT NULL,
    c_location_id BIGINT    NOT NULL,
    CONSTRAINT fk_event_location FOREIGN KEY (c_location_id) REFERENCES kudago.t_locations (id)
);

--changeset alexandergarifullin:4
CREATE INDEX IF NOT EXISTS idx_event_name ON kudago.t_events (c_name);
CREATE INDEX IF NOT EXISTS idx_event_start_date ON kudago.t_events (c_start_date);
CREATE INDEX IF NOT EXISTS idx_event_location ON kudago.t_events (c_location_id);
CREATE INDEX IF NOT EXISTS idx_location_slug ON kudago.t_locations (c_slug);