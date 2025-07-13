package com.kamegatze.map.result.set.processor;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.kamegatze.map.result.set.MapResultSetUtils;
import com.kamegatze.map.result.set.processor.university.mapper.StudentClassMapper;
import com.kamegatze.map.result.set.processor.university.mapper.StudentClassNestedOneMapper;
import com.kamegatze.map.result.set.processor.university.mapper.StudentRecordMapper;
import com.kamegatze.map.result.set.processor.university.mapper.StudentRecordNestedOneMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class MapResultSetProcessorPostgresIT {

    @Container
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16-alpine");

    static JdbcTemplate jdbcTemplate;
    static DataSource dataSource;

    static class CustomPGDataSource extends PGSimpleDataSource {

        private boolean isAutoCommit = true;

        @Override
        public Connection getConnection() throws SQLException {
            var connection = super.getConnection();
            connection.setAutoCommit(isAutoCommit);
            return connection;
        }

        public boolean isAutoCommit() {
            return isAutoCommit;
        }

        public void setAutoCommit(boolean autoCommit) {
            isAutoCommit = autoCommit;
        }
    }

    @BeforeAll
    static void setUp() {
        var datasource = new CustomPGDataSource();
        datasource.setUrl(container.getJdbcUrl());
        datasource.setUser(container.getUsername());
        datasource.setPassword(container.getPassword());
        datasource.setAutoCommit(false);

        dataSource = datasource;

        var flyway = Flyway.configure().dataSource(datasource).load();

        flyway.migrate();

        jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Test
    void givenMapperClassWithoutNestedObject_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentClassMapper.class);

        var studentClassList = jdbcTemplate.query("select * from student", mapper.getRowMapper());

        assertFalse(studentClassList.isEmpty());
    }

    @Test
    void givenMapperRecordWithoutNestedObject_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentRecordMapper.class);

        var studentRecordList = jdbcTemplate.query("select * from student", mapper.getRowMapper());

        assertFalse(studentRecordList.isEmpty());
    }

    @Test
    void givenMapperClassWithNestedOne_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedOneMapper.class);

        var studentClassNestedOneList =
                jdbcTemplate.query(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(format('select * from subject s left join student_subject ss on s.id = ss.subject_id where ss.student_id = %s',
                s.id)) as subject
                from student s
                """,
                        mapper.getRowMapper());

        assertFalse(studentClassNestedOneList.isEmpty());
        var subjectsOne = studentClassNestedOneList.get(0).getSubject();
        assertFalse(subjectsOne.isEmpty());
        var subjectsTwo = studentClassNestedOneList.get(1).getSubject();
        assertFalse(subjectsTwo.isEmpty());
        var subjectsThree = studentClassNestedOneList.get(2).getSubject();
        assertFalse(subjectsThree.isEmpty());
    }

    @Test
    void givenMapperRecordWithNestedOne_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedOneMapper.class);

        var studentClassNestedOneList =
                jdbcTemplate.query(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(format('select * from subject s left join student_subject ss on s.id = ss.subject_id where ss.student_id = %s',
                s.id)) as subject
                from student s
                """,
                        mapper.getRowMapper());

        assertFalse(studentClassNestedOneList.isEmpty());
        var subjectsOne = studentClassNestedOneList.get(0).subject();
        assertFalse(subjectsOne.isEmpty());
        var subjectsTwo = studentClassNestedOneList.get(1).subject();
        assertFalse(subjectsTwo.isEmpty());
        var subjectsThree = studentClassNestedOneList.get(2).subject();
        assertFalse(subjectsThree.isEmpty());
    }
}
