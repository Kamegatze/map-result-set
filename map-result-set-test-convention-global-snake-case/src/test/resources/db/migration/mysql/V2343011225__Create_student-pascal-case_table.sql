create table if not exists student_pascal_case (
  Id bigint primary key not null auto_increment,
  FirstName text not null,
  LastName text not null,
  Patronymic text,
  Birthdate date not null
);