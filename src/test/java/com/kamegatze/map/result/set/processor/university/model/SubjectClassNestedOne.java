package com.kamegatze.map.result.set.processor.university.model;

import com.kamegatze.map.result.set.Cursor;
import java.util.List;

public class SubjectClassNestedOne {
  private Long id;
  private String name;

  @Cursor("teachers")
  private List<TeacherClass> teachers;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TeacherClass> getTeachers() {
    return teachers;
  }

  public void setTeachers(List<TeacherClass> teachers) {
    this.teachers = teachers;
  }
}
