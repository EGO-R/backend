--liquibase formatted sql

--changeset EGO-R:1
create table if not exists clients (
    id bigserial primary key ,
    email varchar(65) not null unique ,
    name varchar(65) not null
);

--changeset EGO-R:2
create table if not exists video (
    id bigserial primary key ,
    name varchar(256) not null ,
    video_url varchar(256) not null ,
    user_id bigint not null references clients(id)
)