package io.github.kamegatze.map.result.set.processor.university.model;

import io.github.kamegatze.map.result.set.Cursor;
import java.time.LocalDate;
import java.util.List;

public class StudentClassNestedTwo {
  private Long id;

  private String firstName;

  private String lastName;

  private String patronymic;
  private LocalDate birthdate;

  @Cursor List<SubjectClassNestedOne> subject;

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
