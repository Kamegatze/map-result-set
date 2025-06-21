# MapResultSet
Library for mapping ResultSet to java objects or create object RowMapper. Library must create mapping via annotation processing with the help generate classes implementation interface.

# API

To use this library, you need to declare an interface with the `@MapResultSet` annotation and declare instance via static method `ResultSetMap.getMapper(Class<?> clazz)`, for example:
```java
import com.kamegatze.map.result.set.MapResultSet;
import java.sql.ResultSet;
import com.example.Student;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentMapResultSet {

    StudentMapResultSet INSTANCE = ResultSetMap.getMapper(StudentMapResultSet.class);

    Student map(ResultSet resultSet);

    RowMapper<Student> createRowMapper();
}
```

API like MapStruct API. API must support create RowMapper as in the example.