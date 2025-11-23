package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.university.model.StudentRecordNestedTwo;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentRecordNestedTwoMapper {

  default RowMapper<StudentRecordNestedTwo> getRowMapper() {
    return (rs, rowNum) -> getResultSetMapper().mapRow(rs, rowNum);
  }

  ResultSetMapper<StudentRecordNestedTwo> getResultSetMapper();

  List<StudentRecordNestedTwo> getStudentRecordNestedTwoAll(ResultSet resultSet);

  StudentRecordNestedTwo getStudentRecordNestedTwo(ResultSet resultSet);
}
