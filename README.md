# 🚀 MapResultSet (Compile-Time Data Mapper)

🌐 **[Russian Documentation / Документация на русском языке](README_RU.md)**

---

map-result-set is an ultralight annotation processing tool (APT) specialized for databases, designed for Java 8+. It generates clean, null-safe, and type-safe code for mapping `java.sql.ResultSet` during application compilation.

## 🏛️ Architectural Concept: A Synthesis of MapStruct and Hibernate
The project combines the two most popular and familiar patterns of the Java ecosystem, fully shifting their work to the compilation stage (Ahead-of-Time):
1. **MapStruct-style interface contracts:** You don't write imperative mapping code by hand. Just like in MapStruct, you only declare an interface and describe **only the method signature:** what is passed as input (`java.sql.ResultSet` or an empty argument) and what should be produced as output (an object, a list, or a lazy callback). Based on this declaration, the compiler generates a ready-to-use implementation.
2. **Hibernate-style encapsulation of mapping rules:** Unlike classic MapStruct, where mapping rules are described above the interface methods, here **all mapping logic is encapsulated inside the data models themselves.** You annotate (`@Column`, `@GroupBy`, `@Cursor`, `@Id`) the fields of your records or POJO classes. The mapping reads the entity metadata and generates optimal code "under the hood," leaving the mapper interface code crystal clear.

## 🔥 Key Advantages
- **Compile-Time generation:** No runtime reflection (unlike Hibernate, Spring Data JPA, or the classic `BeanPropertyRowMapper`). Reflection is used only when creating mapper objects via `io.github.kamegatze.map.result.set.MapResultSetUtils.getMapper(your.package.path.interfacies.YourMapper.class)`, but they can also be created simply via `new your.package.path.interfacies.YourMapperImpl()`.
- **Full support for classic POJOs (Java 8+) and records (Java 17+):** Full support for immutable records. The generator automatically matches `java.sql.ResultSet` column names with record fields and invokes the canonical constructor directly.
- **Support for nested Oracle cursors (`REF CURSOR`):** Automatic mapping of nested `java.sql.ResultSet` from stored procedures and functions without manual boilerplate.
- **Framework-agnostic:** The library has no external dependencies other than the standard JDBC API and JavaPoet for code generation during APT. It integrates equally well into plain JDBC, Spring `JdbcTemplate`, Javalin, Helidon, Quarkus, or Ktor.
- **Predictable debugging:** The generated mapper code physically resides in your `target/generated-sources` or `build/generated` folder. You can always set a breakpoint there and step through it with the debugger.

### 🍏 GraalVM Native Image Compatibility & `MapResultSetUtils`

A common concern with Ahead-of-Time (AOT) compilation and GraalVM Native Image is the use of runtime reflection. **MapResultSet is 100% GraalVM friendly out of the box.**

When you use the factory helper:
```java
DepartmentMapper mapper = MapResultSetUtils.getMapper(DepartmentMapper.class);
```

#### How it works under the hood:
The `MapResultSetUtils` factory **does not** scan fields, methods, or annotations at runtime. It simply uses light reflection to locate the compile-time generated class by appending the `Impl` suffix (e.g., `Class.forName("...DepartmentMapperImpl")`) and instantiates it via a default constructor.

#### Zero-Reflection Alternative:
If you want to completely bypass reflection or avoid configuring GraalVM's `reflect-config.json` for your mappers altogether, you can instantiate the generated implementations **directly using the standard `new` keyword**:

```java
// 100% Compile-time, zero reflection, pure Java performance
DepartmentMapper mapper = new DepartmentMapperImpl();
```
This ensures that your native binaries compiled with GraalVM, Quarkus, or Micronaut will boot instantly and have a minimal memory footprint, with no runtime surprises.

## 📦 Quick Start (Java 8+)

**1. Describe your data and mapping rules inside the entity**

All rules for extracting data from `java.sql.ResultSet` are encapsulated in the models (these can be either records or plain POJOs). The mapper interfaces know nothing about database columns:

```java
import io.github.kamegatze.map.result.set.Cursor;
import io.github.kamegatze.map.result.set.Column;

public record Department (Long id, String name, @Cursor List<Employee> employeeList) {}

public class Employee {
    @Column("emp_id")
    private Long id;
    private String firstName;
    private String lastName;
    // get/set are required, or generated via lombok, but then lombok must run before map-result-set
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

**2. Declare the mapper interface**

You describe only what you want to get and how you plan to call it. Two contracts are available:

```java
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import io.github.kamegatze.map.result.set.processor.ResultSetMapper;
import io.github.kamegatze.map.result.set.MapResultSet;

@MapResultSet
public interface DepartmentMapper {
    Department mapDepartment(ResultSet resultSet);
    Optional<Department> mapDepartmentOptional(ResultSet resultSet);
    List<Department> mapDepartmentList(ResultSet resultSet);
    Set<Department> mapDepartmentSet(ResultSet resultSet);
    Collection<Department> mapDepartmentCollection(ResultSet resultSet);
    // returns a callback describing how to map a single specific row from the DB
    // i.e., this is essentially the same as RowMapper<Department>
    ResultSetMapper<Department> resultSetMapper();
}
```

**3. Usage examples in the codebase**

During compilation, the `DepartmentMapperImpl` class will be generated, which can be obtained via the `io.github.kamegatze.map.result.set.MapResultSetUtils` utility.

```java
DepartmentMapper mapper = MapResultSetUtils.getMapper(DepartmentMapper.class);

Department department = jdbcTemplate.queryForObject("select id, name, from department where id = ?", (rs, rowNum) -> mapper.mapDepartment(rs), 42L);

List<Department> departmentList = jdbcTemplate.query("select id, name, from department", mapper.resultSetMapper()::mapRow);
```

## 🛡️ Compile-Time Order Guarantee
One of the biggest pains when mapping data into a `record` is the strict order of arguments in its canonical constructor. Runtime frameworks often stumble here, trying to match DB column indices with constructor positions via reflection.

### MapResultSet solves this at the compilation stage:
1. The annotation processor analyzes the record structure and the exact order of its constructor parameters.
2. For each component, an independent call by column name is generated, and the result is stored in a local variable.
3. The local variables are passed to the record constructor in the order fixed in the Java class by the compiler.

Even if your SQL query returns columns in a chaotic order (`SELECT email, id, username...`), the generated code will always be monolithically stable. If the project compiled, it will **guaranteed and flawlessly** work at runtime.

## 🧠 Type Philosophy and Null-Safe Approach
The library adheres to a strict philosophy: **data in the database can always be `NULL`.** Attempting to map `NULL` from the database into a primitive Java type (`long`, `int`, `boolean`) leads to hidden bugs where a missing value turns into a default `0` or `false`.

Behind the scenes, the generator uses safe type extraction via the modern JDBC API:
```java
// The generated code automatically uses the safe getObject contract
Long id = rs.getObject("id", Long.class); 
String username = rs.getString("username");
Boolean isActive = rs.getObject("is_active", Boolean.class);
```
The `rs.getObject(columnLabel, Class<T>)` method perfectly returns a clean Java `null` if the database column was empty. No magic, no default primitive values — only honest work with wrapper objects.

## 💎 Killer Feature: Nested Oracle Cursors (REF CURSOR)
If you work with Oracle DB and call stored procedures that return a structure with nested cursors, manual mapping turns into writing hundreds of lines of spaghetti code. **MapResultSet** solves this declaratively, using Hibernate-style relationship annotations directly on entity fields.

### What the component generates during compilation:

No magic and no hidden proxies — just the straightforward performance of the raw JDBC driver. But if you look closely, you'll notice that the variables have unique identifiers in their names. This is done to solve the problem of name collisions, since the field name and the entity name may coincide.
```java
  @Override
  public ResultSetMapper<StudentClassNestedTwo> getResultSetMapper() {
    ResultSetMapper<StudentClassNestedTwo> root = (rs911b6218dea24746a399ceafc2d2697b , rowNum911b6218dea24746a399ceafc2d2697b) -> {
      Long id911b6218dea24746a399ceafc2d2697b = rs911b6218dea24746a399ceafc2d2697b.getObject("id", Long.class);
      String firstName911b6218dea24746a399ceafc2d2697b = rs911b6218dea24746a399ceafc2d2697b.getObject("first_name", String.class);
      String lastName911b6218dea24746a399ceafc2d2697b = rs911b6218dea24746a399ceafc2d2697b.getObject("last_name", String.class);
      String patronymic911b6218dea24746a399ceafc2d2697b = rs911b6218dea24746a399ceafc2d2697b.getObject("patronymic", String.class);
      LocalDate birthdate911b6218dea24746a399ceafc2d2697b = rs911b6218dea24746a399ceafc2d2697b.getObject("birthdate", LocalDate.class);
      ResultSetMapper<TeacherClass> teachers8ada5e85dc7a402a9d4e1bec4107fedd = (rs1b5f8cce7e49645f1ac22f5d7948aed4f , rowNum1b5f8cce7e49645f1ac22f5d7948aed4f) -> {
        Long idb5f8cce7e49645f1ac22f5d7948aed4f = rs1b5f8cce7e49645f1ac22f5d7948aed4f.getObject("id", Long.class);
        String firstNameb5f8cce7e49645f1ac22f5d7948aed4f = rs1b5f8cce7e49645f1ac22f5d7948aed4f.getObject("first_name", String.class);
        String lastNameb5f8cce7e49645f1ac22f5d7948aed4f = rs1b5f8cce7e49645f1ac22f5d7948aed4f.getObject("last_name", String.class);
        String patronymicb5f8cce7e49645f1ac22f5d7948aed4f = rs1b5f8cce7e49645f1ac22f5d7948aed4f.getObject("patronymic", String.class);
        LocalDate birthdateb5f8cce7e49645f1ac22f5d7948aed4f = rs1b5f8cce7e49645f1ac22f5d7948aed4f.getObject("birthdate", LocalDate.class);
        TeacherClass teachersb5f8cce7e49645f1ac22f5d7948aed4f = new TeacherClass();
        teachersb5f8cce7e49645f1ac22f5d7948aed4f.setId(idb5f8cce7e49645f1ac22f5d7948aed4f);
        teachersb5f8cce7e49645f1ac22f5d7948aed4f.setFirstName(firstNameb5f8cce7e49645f1ac22f5d7948aed4f);
        teachersb5f8cce7e49645f1ac22f5d7948aed4f.setLastName(lastNameb5f8cce7e49645f1ac22f5d7948aed4f);
        teachersb5f8cce7e49645f1ac22f5d7948aed4f.setPatronymic(patronymicb5f8cce7e49645f1ac22f5d7948aed4f);
        teachersb5f8cce7e49645f1ac22f5d7948aed4f.setBirthdate(birthdateb5f8cce7e49645f1ac22f5d7948aed4f);
        return teachersb5f8cce7e49645f1ac22f5d7948aed4f;
      };
      ResultSetMapper<SubjectClassNestedOne> subject911b6218dea24746a399ceafc2d2697b = (rs18ada5e85dc7a402a9d4e1bec4107fedd , rowNum18ada5e85dc7a402a9d4e1bec4107fedd) -> {
        Long id8ada5e85dc7a402a9d4e1bec4107fedd = rs18ada5e85dc7a402a9d4e1bec4107fedd.getObject("id", Long.class);
        String name8ada5e85dc7a402a9d4e1bec4107fedd = rs18ada5e85dc7a402a9d4e1bec4107fedd.getObject("name", String.class);
        SubjectClassNestedOne subject8ada5e85dc7a402a9d4e1bec4107fedd = new SubjectClassNestedOne();
        subject8ada5e85dc7a402a9d4e1bec4107fedd.setId(id8ada5e85dc7a402a9d4e1bec4107fedd);
        subject8ada5e85dc7a402a9d4e1bec4107fedd.setName(name8ada5e85dc7a402a9d4e1bec4107fedd);
        subject8ada5e85dc7a402a9d4e1bec4107fedd.setTeachers(extractResultSetMapper(teachers8ada5e85dc7a402a9d4e1bec4107fedd,(ResultSet) rs18ada5e85dc7a402a9d4e1bec4107fedd.getObject("teachers"),true));
        return subject8ada5e85dc7a402a9d4e1bec4107fedd;
      };
      StudentClassNestedTwo root911b6218dea24746a399ceafc2d2697b = new StudentClassNestedTwo();
      root911b6218dea24746a399ceafc2d2697b.setId(id911b6218dea24746a399ceafc2d2697b);
      root911b6218dea24746a399ceafc2d2697b.setFirstName(firstName911b6218dea24746a399ceafc2d2697b);
      root911b6218dea24746a399ceafc2d2697b.setLastName(lastName911b6218dea24746a399ceafc2d2697b);
      root911b6218dea24746a399ceafc2d2697b.setPatronymic(patronymic911b6218dea24746a399ceafc2d2697b);
      root911b6218dea24746a399ceafc2d2697b.setBirthdate(birthdate911b6218dea24746a399ceafc2d2697b);
      root911b6218dea24746a399ceafc2d2697b.setSubject(extractResultSetMapper(subject911b6218dea24746a399ceafc2d2697b,(ResultSet) rs911b6218dea24746a399ceafc2d2697b.getObject("subject"),true));
      return root911b6218dea24746a399ceafc2d2697b;
    };
    return root;
  }

  private <T> List<T> extractResultSetMapper(ResultSetMapper<T> resultSetMapper, ResultSet rs,
      boolean isCloseResultSet) throws SQLException {
    try {
      List<T> list = new ArrayList<T>();
      while(rs.next()) {
        list.add(resultSetMapper.mapRow(rs, rs.getRow()));
      }
      return list;
    } finally {
      if(isCloseResultSet) {
        JdbcUtility.close(rs);
      }
    }
  }
```

## ⚙️ Naming Conventions Configuration

By default, if fields are declared without the `@Column` annotation, the mapper matches them using the **`SNAKE_CASE`** policy (e.g., `firstName` automatically maps to the `first_name` column). You can customize the global naming convention style for the entire project, as well as override it locally for specific entity classes or records.

Supported policies (`Policy` enum):
* `SNAKE_CASE` (Default) — `firstName` maps to column `first_name`
* `CAMEL_CASE` — `firstName` maps to column `firstName`
* `PASCAL_CASE` — `firstName` maps to column `FirstName`

### 1. Global Configuration
To change the project-wide convention policy, create a configuration class anywhere in your source code and mark it with the `@MapResultSetConfiguration` annotation:

```java
import io.github.kamegatze.map.result.set.processor.MapResultSetConfiguration;
import io.github.kamegatze.map.result.set.processor.Policy;

@MapResultSetConfiguration(conventionFieldPolicy = Policy.PASCAL_CASE) // Overrides default SNAKE_CASE to PASCAL_CASE globally
public class MapResultSetGlobalConfig {
    // This empty class acts as a single point of configuration for the APT compiler.
    // In future versions, more configuration properties will be managed here.
}
```

### 2. Local Configuration (Per Entity Override)
If a specific database table or stored procedure output deviates from your global project policy, you can override it directly on the target entity or record using the `@ConventionFieldPolicy` annotation:

```java
import io.github.kamegatze.map.result.set.processor.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.processor.Policy;

@ConventionFieldPolicy(Policy.CAMEL_CASE) // Overrides the global policy to CAMEL_CASE for this specific model
public record Employee(
    @Id Long id,
    String firstName, // Maps to "firstName" (camelCase) instead of "FirstName"
    String lastName   // Maps to "lastName" (camelCase) instead of "LastName"
) {}
```

## 🛠️ Library Setup

### Gradle

```kotlin
implementation("io.github.kamegatze:map-result-set-java8:0.1.1")
annotationProcessor("io.github.kamegatze:map-result-set-java8:0.1.1")
```
**If you want to use `record` as DB entities, you need to switch from `map-result-set-java8` to `map-result-set-java17`**

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.github.kamegatze</groupId>
        <artifactId>map-result-set-java8</artifactId>
        <version>0.1.1</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <annotationProcessorPaths>
                    <!-- IMPORTANT: If you use Lombok, it MUST be declared here, BEFORE map-result-set -->
                    <!--
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    --> 
                    <path>
                        <groupId>io.github.kamegatze</groupId>
                        <artifactId>map-result-set-java8</artifactId>
                        <version>0.1.1</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```
**If you want to use `record` as DB entities, you need to switch from `map-result-set-java8` to `map-result-set-java17`**

## 🗺️ Project Roadmap (v0.2.0+)
The current version `0.1.1` is already available on Maven Central and reliably handles the mapping of flat structures and nested Oracle cursors. In upcoming releases, the focus is on automating the following Enterprise tasks:
- Compiler core (APT Engine).
- Full support for Java 8+ classic POJOs and Java 17+ Records.
- Mapping of nested Oracle cursors (`REF CURSOR`) via `@Cursor`.
- **In development:** Declarative mapping of classic SQL table relationships (`@OneToMany` / Automatic aggregation of a flat `java.sql.ResultSet` based on a `JOIN` by `@Id` key with support for any user-defined nesting).
- **In development:** Strict compile-time validation against primitives. Blocking the project build when attempting to use `int`/`long`/`boolean` types to guarantee protection against loss of `NULL` values.
- **In development:** Moving annotations and metadata from the mapper interface into the generated implementation class for seamless pickup of mappers by DI frameworks (Spring, Micronaut) as valid beans.
- **In development:** Custom data type converters (Analogous to JPA `@Convert` for mapping JSON columns or specific enums).
- **In development:** Optimization of generated code readability.