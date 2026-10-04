create table if not exists student_camel_case (
  id bigint primary key not null auto_increment,
  firstName text not null,
  lastName text not null,
  patronymic text,
  birthdate date not null
);