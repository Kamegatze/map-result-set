package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentClassNestedOne;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentClassNestedOneMapper {

    RowMapper<StudentClassNestedOne> getRowMapper();

    StudentClassNestedOne getStudentClassNestedOne(ResultSet resultSet);

    List<StudentClassNestedOne> getStudentClassNestedOneList(ResultSet resultSet);
}
