package io.github.kamegatze.map.result.set.convention.university.mapper;

import io.github.kamegatze.map.result.set.MapResultSet;
import io.github.kamegatze.map.result.set.convention.university.model.StudentCamelCase;
import io.github.kamegatze.map.result.set.convention.university.model.StudentPascalCase;
import io.github.kamegatze.map.result.set.convention.university.model.StudentSnakeCase;
import java.sql.ResultSet;

@MapResultSet
public interface StudentMapper {

  StudentCamelCase getStudentCamelCase(ResultSet rs);

  StudentPascalCase getStudentPascalCase(ResultSet rs);

  StudentSnakeCase getStudentSnakeCase(ResultSet resultSet);
}
