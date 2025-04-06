--liquibase formatted sql

--changeset EGO-R:1
alter table video
add column preview varchar(256) not null default '';
