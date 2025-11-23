package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.university.model.StudentClass;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentClassMapper {

  default RowMapper<StudentClass> getRowMapper() {
    return (rs, rowNum) -> getResultSetMapper().mapRow(rs, rowNum);
  }

  ResultSetMapper<StudentClass> getResultSetMapper();

  StudentClass getStudentClass(ResultSet resultSet);

  List<StudentClass> getStudentsClass(ResultSet resultSet);
}
