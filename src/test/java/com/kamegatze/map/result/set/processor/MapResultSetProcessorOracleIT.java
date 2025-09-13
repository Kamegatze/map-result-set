package com.kamegatze.map.result.set.processor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kamegatze.map.result.set.MapResultSetUtils;
import com.kamegatze.map.result.set.processor.university.mapper.*;
import java.sql.SQLException;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

@Testcontainers
class MapResultSetProcessorOracleIT {

    @Container
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart");

    static JdbcTemplate jdbcTemplate;
    static DataSource dataSource;

    @BeforeAll
    static void setUp() throws SQLException {
        var datasource = new OracleDataSource();
        datasource.setUser(oracle.getUsername());
        datasource.setPassword(oracle.getPassword());
        datasource.setURL(oracle.getJdbcUrl());
        dataSource = datasource;

        Flyway.configure()
                .locations("classpath:db/migration/oracle")
                .dataSource(datasource)
                .load()
                .migrate();

        jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Test
    void givenMapperClassWithoutNestedObject_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentClassMapper.class);

        var studentClassList = jdbcTemplate.query("select * from student", mapper.getRowMapper());

        assertNotNull(studentClassList);
        assertFalse(studentClassList.isEmpty());
    }

    @Test
    void givenMapperRecordWithoutNestedObject_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentRecordMapper.class);

        var studentRecordList = jdbcTemplate.query("select * from student", mapper.getRowMapper());

        assertNotNull(studentRecordList);
        assertFalse(studentRecordList.isEmpty());
    }

    @Test
    void givenMapperClassWithNestedOne_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedOneMapper.class);

        var studentClassNestedOneList =
                jdbcTemplate.query(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(select * from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
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
                cursor(select * from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
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

    @Test
    void givenStudentClassListWithoutNestedViaDatasource_whenQueryAllStudent_thenGetListStudent()
            throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentClassMapper.class);

        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from student")) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentClassList = mapper.getStudentsClass(resultSet);

            assertFalse(studentClassList.isEmpty());
        }
    }

    @Test
    void givenStudentRecordListWithoutNestedViaDatasource_whenQueryAllStudent_thenGetListStudent()
            throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentRecordMapper.class);

        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from student")) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentRecordList = mapper.getStudentRecordList(resultSet);

            assertFalse(studentRecordList.isEmpty());
        }
    }

    @Test
    void givenStudentClassWithoutNested_whenQueryAllStudent_thenGetListStudent() throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentClassMapper.class);

        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from student where id = 1")) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentClass = mapper.getStudentClass(resultSet);

            assertNotNull(studentClass);
        }
    }

    @Test
    void givenStudentRecordWithoutNested_whenQueryAllStudent_thenGetListStudent() throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentRecordMapper.class);

        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from student where id = 1")) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentRecord = mapper.getStudentRecord(resultSet);

            assertNotNull(studentRecord);
        }
    }

    @Test
    void givenStudentClassNestedOneViaDatasource_whenQueryAllStudent_thenGetListStudent()
            throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedOneMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select * from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentClassNestedOneList = mapper.getStudentClassNestedOneList(resultSet);

            assertFalse(studentClassNestedOneList.isEmpty());
        }
    }

    @Test
    void givenStudentRecordNestedOneViaDatasource_whenQueryAllStudent_thenGetListStudent()
            throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedOneMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select * from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentRecordNestedOneList = mapper.getStudentRecordNestedOneList(resultSet);

            assertFalse(studentRecordNestedOneList.isEmpty());
        }
    }

    @Test
    void givenStudentClassNestedOneViaDatasource_whenQueryAllStudent_thenOne() throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedOneMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select * from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             where id = 1
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentClassNestedOne = mapper.getStudentClassNestedOne(resultSet);

            assertNotNull(studentClassNestedOne);
        }
    }

    @Test
    void givenStudentRecordNestedOneViaDatasource_whenQueryAllStudent_thenOne() throws Exception {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedOneMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select * from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             where id = 1
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentRecordNestedOne = mapper.getStudentRecordNestedOne(resultSet);

            assertNotNull(studentRecordNestedOne);
        }
    }

    @Test
    void givenStudentClassNestedTwo_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedTwoMapper.class);

        var studentList =
                jdbcTemplate.query(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(select subject.id, subject.name
                ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
                from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
                from student s
                """,
                        mapper.getRowMapper());

        assertNotNull(studentList);
        assertFalse(studentList.isEmpty());
        assertNotNull(studentList.get(0).getSubject());
        assertFalse(studentList.get(0).getSubject().isEmpty());
        assertNotNull(studentList.get(0).getSubject().get(0).getTeachers());
        assertFalse(studentList.get(0).getSubject().get(0).getTeachers().isEmpty());
    }

    @Test
    void givenStudentClassNestedTwo_whenQueryAllStudent_thenGetOneStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedTwoMapper.class);

        var student =
                jdbcTemplate.queryForObject(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(select subject.id, subject.name
                ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
                from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
                from student s
                where s.id = 1
                """,
                        mapper.getRowMapper());

        assertNotNull(student);
        assertNotNull(student.getSubject());
        assertFalse(student.getSubject().isEmpty());
        assertNotNull(student.getSubject().get(0).getTeachers());
        assertFalse(student.getSubject().get(0).getTeachers().isEmpty());
    }

    @Test
    void givenStudentClassNestedTwoViaDatasource_whenQueryAllStudent_thenGetListStudent()
            throws SQLException {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedTwoMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
            select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
            cursor(select subject.id, subject.name
            ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
            from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
            from student s
            """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentList = mapper.getStudentClassNestedTwoAll(resultSet);

            assertNotNull(studentList);
            assertFalse(studentList.isEmpty());
            assertNotNull(studentList.get(0).getSubject());
            assertFalse(studentList.get(0).getSubject().isEmpty());
            assertNotNull(studentList.get(0).getSubject().get(0).getTeachers());
            assertFalse(studentList.get(0).getSubject().get(0).getTeachers().isEmpty());
        }
    }

    @Test
    void givenStudentClassNestedTwoViaDatasourceById_whenQueryAllStudent_thenOneStudent()
            throws SQLException {
        var mapper = MapResultSetUtils.getMapper(StudentClassNestedTwoMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select subject.id, subject.name
             ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
             from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             where s.id = 1
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var student = mapper.getStudentClassNestedTwo(resultSet);

            assertNotNull(student);
            assertNotNull(student.getSubject());
            assertFalse(student.getSubject().isEmpty());
            assertNotNull(student.getSubject().get(0).getTeachers());
            assertFalse(student.getSubject().get(0).getTeachers().isEmpty());
        }
    }

    @Test
    void givenStudentRecordNestedTwo_whenQueryAllStudent_thenGetListStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedTwoMapper.class);

        var studentList =
                jdbcTemplate.query(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(select subject.id, subject.name
                ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
                from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
                from student s
                """,
                        mapper.getRowMapper());

        assertNotNull(studentList);
        assertFalse(studentList.isEmpty());
        assertNotNull(studentList.get(0).subject());
        assertFalse(studentList.get(0).subject().isEmpty());
        assertNotNull(studentList.get(0).subject().get(0).teachers());
        assertFalse(studentList.get(0).subject().get(0).teachers().isEmpty());
    }

    @Test
    void givenStudentRecordNestedTwo_whenQueryAllStudent_thenGetOneStudent() {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedTwoMapper.class);

        var student =
                jdbcTemplate.queryForObject(
                        """
                select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
                cursor(select subject.id, subject.name
                ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
                from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
                from student s
                where s.id = 1
                """,
                        mapper.getRowMapper());

        assertNotNull(student);
        assertNotNull(student.subject());
        assertFalse(student.subject().isEmpty());
        assertNotNull(student.subject().get(0).teachers());
        assertFalse(student.subject().get(0).teachers().isEmpty());
    }

    @Test
    void givenStudentRecordNestedTwoViaDatasource_whenQueryAllStudent_thenGetListStudent()
            throws SQLException {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedTwoMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select subject.id, subject.name
             ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
             from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var studentList = mapper.getStudentRecordNestedTwoAll(resultSet);

            assertNotNull(studentList);
            assertFalse(studentList.isEmpty());
            assertNotNull(studentList.get(0).subject());
            assertFalse(studentList.get(0).subject().isEmpty());
            assertNotNull(studentList.get(0).subject().get(0).teachers());
            assertFalse(studentList.get(0).subject().get(0).teachers().isEmpty());
        }
    }

    @Test
    void givenStudentRecordNestedTwoViaDatasourceById_whenQueryAllStudent_thenOneStudent()
            throws SQLException {
        var mapper = MapResultSetUtils.getMapper(StudentRecordNestedTwoMapper.class);

        try (var connection = dataSource.getConnection();
                var statement =
                        connection.prepareStatement(
                                """
             select s.id, s.first_name, s.last_name, s.patronymic, s.birthdate,
             cursor(select subject.id, subject.name
             ,cursor(select * from teacher t left join teacher_subject ts on t.id = ts.teacher_id where ts.subject_id = subject.id) as teachers
             from subject left join student_subject ss on subject.id = ss.subject_id where ss.student_id = s.id) as subject
             from student s
             where s.id = 1
             """)) {
            statement.execute();
            var resultSet = statement.getResultSet();

            var student = mapper.getStudentRecordNestedTwo(resultSet);

            assertNotNull(student);
            assertNotNull(student.subject());
            assertFalse(student.subject().isEmpty());
            assertNotNull(student.subject().get(0).teachers());
            assertFalse(student.subject().get(0).teachers().isEmpty());
        }
    }
}
