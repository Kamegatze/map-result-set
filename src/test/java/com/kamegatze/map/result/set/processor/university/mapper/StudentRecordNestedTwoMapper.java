package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentRecordNestedTwo;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentRecordNestedTwoMapper {
    RowMapper<StudentRecordNestedTwo> getRowMapper();

    List<StudentRecordNestedTwo> getStudentRecordNestedTwoAll(ResultSet resultSet);

    StudentRecordNestedTwo getStudentRecordNestedTwo(ResultSet resultSet);
}
