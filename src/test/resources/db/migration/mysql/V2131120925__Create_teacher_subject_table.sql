create table if not exists teacher_subject (
    id bigint primary key not null auto_increment,
    teacher_id bigint not null references teacher(id),
    subject_id bigint not null references subject(id)
);