package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentRecordNestedOne;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

@MapResultSet
public interface StudentRecordNestedOneMapper {

    RowMapper<StudentRecordNestedOne> getRowMapper();

    StudentRecordNestedOne getStudentRecordNestedOne(ResultSet resultSet);

    List<StudentRecordNestedOne> getStudentRecordNestedOneList(ResultSet resultSet);
}
