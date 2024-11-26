CREATE TABLE security.t_tokens
(
    id            BIGSERIAL     PRIMARY KEY,
    c_token       TEXT          NOT NULL,
    c_revoked     BOOLEAN       NOT NULL,
    c_user_id     BIGINT        NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (c_user_id) REFERENCES security.t_users (id)
);