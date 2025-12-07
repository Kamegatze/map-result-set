package io.github.kamegatze.map.result.set.processor.university.model;

import io.github.kamegatze.map.result.set.Column;
import java.time.LocalDate;

public record StudentRecord(
    Long id,
    @Column("first_name") String firstName,
    @Column("last_name") String lastName,
    String patronymic,
    LocalDate birthdate) {}
