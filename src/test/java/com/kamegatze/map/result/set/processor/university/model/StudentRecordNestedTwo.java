package com.kamegatze.map.result.set.processor.university.model;

import com.kamegatze.map.result.set.Column;
import com.kamegatze.map.result.set.Cursor;
import java.time.LocalDate;
import java.util.List;

public record StudentRecordNestedTwo(
        Long id,
        @Column("first_name") String firstName,
        @Column("last_name") String lastName,
        String patronymic,
        LocalDate birthdate,
        @Cursor("subject") List<SubjectRecordNestedOne> subject) {}
