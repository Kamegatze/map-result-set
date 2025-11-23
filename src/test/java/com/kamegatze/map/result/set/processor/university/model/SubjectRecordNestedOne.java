package com.kamegatze.map.result.set.processor.university.model;

import com.kamegatze.map.result.set.Cursor;
import java.util.List;

public record SubjectRecordNestedOne(
    Long id, String name, @Cursor("teachers") List<TeacherRecord> teachers) {}
