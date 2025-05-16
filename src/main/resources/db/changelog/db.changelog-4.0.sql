--liquibase formatted sql

--changeset EGO-R:1
create table if not exists refresh_token
(
    user_id    bigint       primary key references clients (id),
    token_hash varchar(256) not null ,
    expires_at timestamp    not null
);
