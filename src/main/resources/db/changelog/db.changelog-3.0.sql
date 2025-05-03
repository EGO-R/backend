--liquibase formatted sql

--changeset EGO-R:1
create table if not exists auth_provider
(
    id               bigserial primary key,
    user_id          bigint       not null references clients (id),
    provider_type    int          not null,
    provider_user_id bigint,
    credentials      varchar(256) not null
);

--changeset EGO-R:2
alter table clients
add column role smallint not null default 0;
