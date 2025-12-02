package io.github.kamegatze.map.result.set.processor.university.model;

import io.github.kamegatze.map.result.set.Cursor;
import java.time.LocalDate;
import java.util.List;

public record StudentRecordNestedOne(
    Long id,
    String firstName,
    String lastName,
    String patronymic,
    LocalDate birthdate,
    @Cursor List<SubjectRecord> subject) {}
