package com.kamegatze.map.result.set.processor.university.model;

import com.kamegatze.map.result.set.Column;
import com.kamegatze.map.result.set.Cursor;
import java.time.LocalDate;
import java.util.List;

public class StudentClassNestedTwo {
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String patronymic;
    private LocalDate birthdate;

    @Cursor("subject")
    List<SubjectClassNestedOne> subject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public List<SubjectClassNestedOne> getSubject() {
        return subject;
    }

    public void setSubject(List<SubjectClassNestedOne> subject) {
        this.subject = subject;
    }
}
