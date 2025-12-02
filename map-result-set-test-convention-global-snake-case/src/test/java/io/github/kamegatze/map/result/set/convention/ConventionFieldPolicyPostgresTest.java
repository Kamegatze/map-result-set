package io.github.kamegatze.map.result.set.convention;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.kamegatze.map.result.set.MapResultSetUtils;
import io.github.kamegatze.map.result.set.convention.university.mapper.StudentMapper;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
class ConventionFieldPolicyPostgresTest {

  @Container static PostgreSQLContainer container = new PostgreSQLContainer("postgres:16-alpine");

  static JdbcTemplate jdbcTemplate;

  static DataSource dataSource;

  @BeforeAll
  static void setUp() {
    var datasource = new PGSimpleDataSource();
    datasource.setUrl(container.getJdbcUrl());
    datasource.setUser(container.getUsername());
    datasource.setPassword(container.getPassword());

    dataSource = datasource;

    Flyway.configure()
        .locations("classpath:db/migration/postgres")
        .dataSource(datasource)
        .load()
        .migrate();

    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Test
  void givenStudentViaGlobalCamelCase_whenSelectStudentById_thenGetCorrectStudent() {
    var mapper = MapResultSetUtils.getMapper(StudentMapper.class);

    var student =
        jdbcTemplate.query(
            "select id, firstName, lastName, patronymic, birthdate from student_camel_case where id = 1",
            mapper::getStudentCamelCase);

    assertNotNull(student);
    assertEquals(1, student.getId());
    assertEquals("Al", student.getFirstName());
    assertEquals("Sh", student.getLastName());
    assertEquals("Pv", student.getPatronymic());
    assertEquals(LocalDate.of(1999, 12, 24), student.getBirthdate());
  }

  @Test
  void givenStudentAsConventionPascalCase_whenSelectStudentById_thenGetCorrectStudent() {
    var mapper = MapResultSetUtils.getMapper(StudentMapper.class);

    var student =
        jdbcTemplate.query(
            "select Id, FirstName, LastName, Patronymic, Birthdate from student_pascal_case where id = 1",
            mapper::getStudentPascalCase);

    assertNotNull(student);
    assertEquals(1, student.getId());
    assertEquals("Al", student.getFirstName());
    assertEquals("Sh", student.getLastName());
    assertEquals("Pv", student.getPatronymic());
    assertEquals(LocalDate.of(1999, 12, 24), student.getBirthdate());
  }

  @Test
  void givenStudentAsConventionSnakeCase_whenSelectStudentById_thenGetCorrectStudent() {
    var mapper = MapResultSetUtils.getMapper(StudentMapper.class);

    var student =
        jdbcTemplate.query(
            "select id, first_name, last_name, patronymic, birthdate from student_snake_case where id = 1",
            mapper::getStudentSnakeCase);

    assertNotNull(student);
    assertEquals(1, student.getId());
    assertEquals("Al", student.getFirstName());
    assertEquals("Sh", student.getLastName());
    assertEquals("Pv", student.getPatronymic());
    assertEquals(LocalDate.of(1999, 12, 24), student.getBirthdate());
  }
}
