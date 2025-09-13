create table if not exists student (
    id bigint primary key not null auto_increment,
    first_name text not null,
    last_name text not null,
    patronymic text,
    birthdate date not null
);