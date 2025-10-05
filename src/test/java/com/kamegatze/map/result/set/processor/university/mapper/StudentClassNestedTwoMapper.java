package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.university.model.StudentClassNestedTwo;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentClassNestedTwoMapper {

    default RowMapper<StudentClassNestedTwo> getRowMapper() {
        return (rs, rowNum) -> getResultSetMapper().mapRow(rs, rowNum);
    }

    ResultSetMapper<StudentClassNestedTwo> getResultSetMapper();

    List<StudentClassNestedTwo> getStudentClassNestedTwoAll(ResultSet resultSet);

    StudentClassNestedTwo getStudentClassNestedTwo(ResultSet resultSet);
}
