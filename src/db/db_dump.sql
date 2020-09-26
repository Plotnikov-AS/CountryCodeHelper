--Таблица пользователей
create table usr
(
    id       bigserial not null
        constraint usr_pk
            primary key,
    active   boolean   not null,
    name     varchar(255) default NULL::character varying,
    password varchar(255) default NULL::character varying,
    username varchar(255) default NULL::character varying
);

alter table usr
    owner to postgres;

--Таблица ролей пользователей
create table user_role
(
    user_id bigint
        constraint fkfpm8swft53ulq2hl11yplpr5
            references usr,
    roles   varchar(255)
);

alter table user_role
    owner to postgres;

--Добавление админ-пользователя
INSERT INTO usr (active, name, password, username) VALUES (true, 'Admin', 'admin', 'admin');
INSERT INTO user_role (user_id, roles) VALUES ((select id from usr where username = 'admin'), 'ADMIN');