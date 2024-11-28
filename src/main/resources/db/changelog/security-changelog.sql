--liquibase formatted sql

--changeset alexandergarifullin:1
CREATE SCHEMA IF NOT EXISTS security;

--changeset alexandergarifullin:2
CREATE TABLE security.t_users
(
    id                  BIGSERIAL   PRIMARY KEY,
    c_name              TEXT        NOT NULL,
    c_login             TEXT        UNIQUE NOT NULL,
    c_hashed_password   TEXT        NOT NULL,
    c_role              TEXT        NOT NULL
);

--changeset alexandergarifullin:3
CREATE INDEX IF NOT EXISTS idx_users_login ON security.t_users (c_login);