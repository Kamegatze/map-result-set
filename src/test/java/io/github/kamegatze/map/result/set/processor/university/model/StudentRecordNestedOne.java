package io.github.kamegatze.map.result.set.processor.university.model;

import io.github.kamegatze.map.result.set.Column;
import io.github.kamegatze.map.result.set.Cursor;
import java.time.LocalDate;
import java.util.List;

public record StudentRecordNestedOne(
    Long id,
    @Column("first_name") String firstName,
    @Column("last_name") String lastName,
    String patronymic,
    LocalDate birthdate,
    @Cursor("subject") List<SubjectRecord> subject) {}
