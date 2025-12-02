package io.github.kamegatze.map.result.set.processor.university.model;

import java.time.LocalDate;

public record StudentRecord(
    Long id, String firstName, String lastName, String patronymic, LocalDate birthdate) {}
