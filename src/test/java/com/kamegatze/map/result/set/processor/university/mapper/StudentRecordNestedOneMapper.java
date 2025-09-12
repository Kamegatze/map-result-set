package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentRecordNestedOne;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentRecordNestedOneMapper {

    RowMapper<StudentRecordNestedOne> getRowMapper();

    StudentRecordNestedOne getStudentRecordNestedOne(ResultSet resultSet);

    List<StudentRecordNestedOne> getStudentRecordNestedOneList(ResultSet resultSet);
}
