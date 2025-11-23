package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.university.model.StudentRecordNestedOne;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentRecordNestedOneMapper {

  default RowMapper<StudentRecordNestedOne> getRowMapper() {
    return (rs, rowNum) -> getResultSetMapper().mapRow(rs, rowNum);
  }

  ResultSetMapper<StudentRecordNestedOne> getResultSetMapper();

  StudentRecordNestedOne getStudentRecordNestedOne(ResultSet resultSet);

  List<StudentRecordNestedOne> getStudentRecordNestedOneList(ResultSet resultSet);
}
