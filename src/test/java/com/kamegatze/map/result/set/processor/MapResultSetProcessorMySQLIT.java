package com.kamegatze.map.result.set.processor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kamegatze.map.result.set.MapResultSetUtils;
import com.kamegatze.map.result.set.processor.university.mapper.CollectionMapper;
import com.kamegatze.map.result.set.processor.university.mapper.StudentClassMapper;
import com.kamegatze.map.result.set.processor.university.mapper.StudentRecordMapper;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers
class MapResultSetProcessorMySQLIT {

  @Container static MySQLContainer mysql = new MySQLContainer("mysql:8.0.36");

  static JdbcTemplate jdbcTemplate;
  static DataSource dataSource;

  @BeforeAll
  static void setUp() {
    var datasource = new MysqlDataSource();

    datasource.setUser(mysql.getUsername());
    datasource.setPassword(mysql.getPassword());
    datasource.setUrl(mysql.getJdbcUrl());

    dataSource = datasource;

    Flyway.configure()
        .locations("classpath:db/migration/mysql")
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
  void givenStudentRecordFromCollectionMapper_whenQueryAllStudent_thenIterableStudent()
      throws SQLException {
    var mapper = MapResultSetUtils.getMapper(CollectionMapper.class);

    try (var connection = dataSource.getConnection();
        var statement = connection.prepareStatement("select * from student")) {
      statement.execute();

      var students = mapper.toIterable(statement.getResultSet());

      assertNotNull(students);
      assertTrue(students.iterator().hasNext());
      assertInstanceOf(Iterable.class, students);
    }
  }

  @Test
  void givenStudentRecordFromCollectionMapper_whenQueryAllStudent_thenCollectionStudent()
      throws SQLException {
    var mapper = MapResultSetUtils.getMapper(CollectionMapper.class);

    try (var connection = dataSource.getConnection();
        var statement = connection.prepareStatement("select * from student")) {
      statement.execute();

      var students = mapper.toCollection(statement.getResultSet());

      assertNotNull(students);
      assertFalse(students.isEmpty());
      assertInstanceOf(Collection.class, students);
    }
  }

  @Test
  void givenStudentRecordFromCollectionMapper_whenQueryAllStudent_thenSetStudent()
      throws SQLException {
    var mapper = MapResultSetUtils.getMapper(CollectionMapper.class);

    try (var connection = dataSource.getConnection();
        var statement = connection.prepareStatement("select * from student")) {
      statement.execute();

      var students = mapper.toSet(statement.getResultSet());

      assertNotNull(students);
      assertFalse(students.isEmpty());
      assertInstanceOf(Set.class, students);
    }
  }
}
