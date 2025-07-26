package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

@MapResultSet
public interface StudentRecordMapper {

    RowMapper<StudentRecord> getRowMapper();

    StudentRecord getStudentRecord(ResultSet resultSet);

    List<StudentRecord> getStudentRecordList(ResultSet resultSet);
}
