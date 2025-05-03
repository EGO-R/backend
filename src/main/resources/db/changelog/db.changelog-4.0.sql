--liquibase formatted sql

--changeset EGO-R:1
create table if not exists refresh_token
(
    id         bigserial primary key,
    user_id    bigint       not null references clients (id),
    token_hash varchar(256) not null,
    expires_at timestamp    not null,
    revoked    bool         not null default false
);
