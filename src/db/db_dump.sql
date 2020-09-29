--Таблица имён и кодов стран
create table table_country
(
    id bigserial not null,
    country_code varchar not null,
    country_name varchar not null,
    country2phone bigint
);

create unique index table_country_id_uindex
    on table_country (id);

alter table table_country
    add constraint table_country_pk
        primary key (id);

create index table_country_code_index on table_country (country_code);
create index table_country_country2phone_index on table_country (country2phone);

--Таблица телефонных кодов
create table table_phone_code
(
    id bigserial not null,
    country_code varchar not null,
    phone_code varchar
);

create unique index table_phone_code_id_uindex
    on table_phone_code (id);

alter table table_phone_code
    add constraint table_phone_code_pk
        primary key (id);

create index table_phone_code_country_code_index on table_phone_code (country_code);
