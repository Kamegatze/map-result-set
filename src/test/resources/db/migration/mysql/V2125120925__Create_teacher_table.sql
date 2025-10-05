create table if not exists teacher (
    id bigint primary key not null auto_increment,
    last_name text not null,
    first_name text not null,
    patronymic text,
    birthdate date not null
);