package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.university.model.StudentClassNestedOne;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentClassNestedOneMapper {

    default RowMapper<StudentClassNestedOne> getRowMapper() {
        return (rs, rowNum) -> getResultSetMapper().mapRow(rs, rowNum);
    }

    ResultSetMapper<StudentClassNestedOne> getResultSetMapper();

    StudentClassNestedOne getStudentClassNestedOne(ResultSet resultSet);

    List<StudentClassNestedOne> getStudentClassNestedOneList(ResultSet resultSet);
}
