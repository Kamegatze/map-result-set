package io.github.kamegatze.map.result.set.processor.university.model;

import io.github.kamegatze.map.result.set.Cursor;
import java.util.List;

public record SubjectRecordNestedOne(Long id, String name, @Cursor List<TeacherRecord> teachers) {}
