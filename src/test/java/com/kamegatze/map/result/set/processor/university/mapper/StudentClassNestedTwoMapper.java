package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentClassNestedTwo;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentClassNestedTwoMapper {
    RowMapper<StudentClassNestedTwo> getRowMapper();

    List<StudentClassNestedTwo> getStudentClassNestedTwoAll(ResultSet resultSet);

    StudentClassNestedTwo getStudentClassNestedTwo(ResultSet resultSet);
}
