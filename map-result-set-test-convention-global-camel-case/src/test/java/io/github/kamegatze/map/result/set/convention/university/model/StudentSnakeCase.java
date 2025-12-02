package io.github.kamegatze.map.result.set.convention.university.model;

import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy.Policy;
import java.time.LocalDate;

@ConventionFieldPolicy(Policy.SNAKE_CASE)
public final class StudentSnakeCase {

  private Long id;

  private String firstName;

  private String lastName;

  private String patronymic;
  private LocalDate birthdate;

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
}
