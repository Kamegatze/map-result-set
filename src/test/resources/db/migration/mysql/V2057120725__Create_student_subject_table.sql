create table if not exists student_subject(
    id bigint primary key not null auto_increment,
    student_id bigint not null references student(id),
    subject_id bigint not null references subject(id)
);