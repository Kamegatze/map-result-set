package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.university.model.StudentRecord;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentRecordMapper {

    default RowMapper<StudentRecord> getRowMapper() {
        return (rs, rowNum) -> getResultSetMapper().mapRow(rs, rowNum);
    }

    ResultSetMapper<StudentRecord> getResultSetMapper();

    StudentRecord getStudentRecord(ResultSet resultSet);

    List<StudentRecord> getStudentRecordList(ResultSet resultSet);
}
