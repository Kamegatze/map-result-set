# 🚀 MapResultSet (Compile-Time Data Mapper)

map-result-set - это ультролегкий, специализированный на базе данных annotation processing tools (APT) для Java 8+. Он генерирует
чистый, null-safe и типобезопасный код маппинга `java.sql.ResultSet` во время компиляции приложения.

## 🏛️ Архитектурная концепция: Синтез MapStruct и Hibernate
Проект объединяет в себе два самых популярных и привычных паттерна Java-экосистемы, полностью перенося их работу на этап компиляции (Ahead-of-Time):
1. **Контракты интерфейсов в стиле MapStruct:** Вы не пишете императивный код маппинга руками. Как в MapStruct, вы лишь объявляете интерфейс и описываете
**только сигнатуру метода:** что передается на вход (`java.sql.ResultSet` или пустой аргумент) и что нужно получить на выходе (объект, список или ленивый callback).
На основе этой декларации компилятор генерирует готовую реализацию.
2. **Инкапсуляция правил маппинга в стиле Hibernate:** В отличии классического MapStruct, где правила маппинга описываются над
методами интерфейса, здесь **вся логика маппинга инкапсулируется внутри самих моделей данных.** Вы размечаете аннотациями (`@Column`, `@GroupBy`, `@Cursor`, `@Id`)
поля ваших рекордов или POJO-классов. Маппинг считывает метаданные сущностей и генерирует оптимальный код "под капотом", оставляя код интерфейсов-мапперов кристально чистыми.

## 🔥 Главные преимущества
- **Compile-Time генерация:** Никакой рефлексии в рантайме (в отличие от Hibernate, Spring Data JPA или классического `BeanPropertyRowMapper`. Рефлексия только при создании объектов мапперов `io.github.kamegatze.map.result.set.MapResultSetUtils.getMapper(your.package.path.interfacies.YourMapper.class)`,
но их можно создавать и просто через `new your.package.path.interfacies.YourMapperImpl()`)
- **Полная поддержка классических POJOs(Java 8+) и records(Java 17+):** Полная поддержка иммутабельных рекордов. Генератор автоматически сопоставляет имена колонок
`java.sql.ResultSet` с полями record и вызывает канонический конструктор напрямую.
- **Поддержка вложенных курсоров Oracle (`REF CURSOR`):** Автоматический маппинг вложенных `java.sql.ResultSet` из хранимых процедур
и функций без ручной рутины.
- **Агностик к фреймворкам:** Библиотека не имеет внешних зависимостей, кроме стандартного JDBC API и JavaPoet для генерации кода при APT.
Она одинаково встраивается в чистый JDBC, Spring `JdbcTemplate`, Javalin, Helidon, Quarkus или Ktor.
- **Предсказуемый дебаг:** Сгенерированный код мапперов физически находится в вашей папке `target/generated-sources` или `build/generated`.
Вы всегда можете туда поставить брейкпоинт и пройтись дебаггером.

### 🍏 Совместимость с GraalVM Native Image и `MapResultSetUtils`

Распространённое опасение, связанное с Ahead-of-Time (AOT) компиляцией и GraalVM Native Image, — это использование рефлексии в рантайме. **MapResultSet на 100% дружелюбен к GraalVM «из коробки».**

Когда вы используете фабричный хелпер:
```java
DepartmentMapper mapper = MapResultSetUtils.getMapper(DepartmentMapper.class);
```

#### Как это работает под капотом:
Фабрика `MapResultSetUtils` **не** сканирует поля, методы или аннотации в рантайме. Она просто использует лёгкую рефлексию, чтобы найти сгенерированный на этапе компиляции класс, добавляя суффикс `Impl` (например, `Class.forName("...DepartmentMapperImpl")`), и создаёт его экземпляр через конструктор по умолчанию.

#### Альтернатива без рефлексии:
Если вы хотите полностью обойтись без рефлексии или вообще не настраивать `reflect-config.json` для GraalVM для ваших мапперов, вы можете создавать сгенерированные реализации **напрямую с помощью стандартного ключевого слова `new`**:

```java
// 100% Compile-time, zero reflection, pure Java performance
DepartmentMapper mapper = new DepartmentMapperImpl();
```
Это гарантирует, что ваши нативные бинарники, скомпилированные с помощью GraalVM, Quarkus или Micronaut, будут запускаться мгновенно и иметь минимальный объём памяти, без каких-либо сюрпризов в рантайме.

## 📦 Быстрый старт (Java 8+)

**1. Описание ваших данных и правил маппинга внутри сущности**

Все правила извлечения данных из `java.sql.ResultSet` инкапсулированы в модели (это могут быть как records, так и просто POJO).
Интерфейсы мапперов ничего не знают о колонках базы данных:

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

**2. Объявите интерфейс маппера**

Вы описываете только то, что хотите получить, и то, как планируете это вызывать. На выбор доступно дво контракта:

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
    // возвращает callback с описанием как нужно маппить одну конкретную строку из БД
    // то есть это по сути тот же RowMapper<Department>
    ResultSetMapper<Department> resultSetMapper();
}
```

**3. Примеры использования в кодовой базе**

Во время компиляции сгенерируется класс `DepartmentMapperImpl`, который можно получить через утилиту `io.github.kamegatze.map.result.set.MapResultSetUtils`.

```java
DepartmentMapper mapper = MapResultSetUtils.getMapper(DepartmentMapper.class);

Department department = jdbcTemplate.queryForObject("select id, name from department where id = ?", (rs, rowNum) -> mapper.mapDepartment(rs), 42L);

List<Department> departmentList = jdbcTemplate.query("select id, name from department", mapper.resultSetMapper()::mapRow);
```

## 🛡️ Гарантия порядка аргументов (Compile-Time Order Guarantee)
Одна из главных болей при маппинге данных в `record` - это строгий порядок аргументов в его каноническом конструкторе.
Рантайм-фреймворки часто спотыкаются здесь, пытаясь сопоставлять индексы колонок БД с позициями в конструкторе через рефлексию.

### MapResultSet решает это на этапе компиляции:
1. Процессор аннотаций анализирует структуру рекорда и точный порядок параметров его конструктора.
2. Для каждого компонента генерируется независимый вызов по имени колонки, результат которого сохраняется в локальную переменную.
3. Локальные переменные передаются в конструктор рекорда в том порядке, который зафиксирован в Java-классе компилятором.

Даже если ваш SQL-запрос возвращает колонки в хаотичном порядке (`SELECT email, id, username...`), сгенерированный код всегда будет монолитно стабилен. Если проект скомпилировался — он **гарантированно и безошибочно** отработает в рантайме.

## 🧠 Философия типов и null-safe подход
Библиотека придерживается строгой философии: **данные в базе всегда могут быть `NULL`.** Попытка смаппить `NULL` из базы данных в примитивный тип Java (`long`, `int`, `boolean`) приводит к скрытым багам, когда отсутствие значения превращается в дефолтный `0` или `false`.

За кулисами генератор использует безопасное извлечение типов через современное JDBC API:
```java
// Сгенерированный код автоматически использует безопасный контракт getObject
Long id = rs.getObject("id", Long.class); 
String username = rs.getString("username");
Boolean isActive = rs.getObject("is_active", Boolean.class);
```
Метод `rs.getObject(columnLabel, Class<T>)` идеально возвращает чистый Java `null`, если в базе данных колонка была пустой. Никакой магии, никаких дефолтных примитивных значений - только честная работа с объектами-обертками.

## 💎 Killer-Feature: Вложенные курсоры Oracle (REF CURSOR)
Если вы работаете с Oracle DB и вызываете хранимые процедуры, возвращающие структуру с вложенными курсорами, ручной маппинг превращается в написание сотен строк спагетти-кода. **MapResultSet** решает это декларативно, используя Hibernate-style разметку связей прямо на полях сущностей.

### Что сгенерирует компонент во время компиляции:

Никакой магии и скрытых прокси — только прямолинейная производительность голого JDBC-драйвера. Но если внимательно присмотреться то можно заметить, что переменные имеют в название уникальные идентификаторы
это сделано для того, что бы решить проблему с коллизиями названий так как имя поля и имя сущности могут совпадать.
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

## ⚙️ Настройка соглашений об именовании

По умолчанию, если поля объявлены без аннотации `@Column`, маппер сопоставляет их по политике **`SNAKE_CASE`** (например, `firstName` автоматически сопоставляется с колонкой `first_name`). Вы можете настроить глобальный стиль соглашения об именовании для всего проекта, а также переопределить его локально для конкретных классов сущностей или рекордов.

Поддерживаемые политики (enum `Policy`):
* `SNAKE_CASE` (по умолчанию) — `firstName` сопоставляется с колонкой `first_name`
* `CAMEL_CASE` — `firstName` сопоставляется с колонкой `firstName`
* `PASCAL_CASE` — `firstName` сопоставляется с колонкой `FirstName`

### 1. Глобальная конфигурация
Чтобы изменить политику соглашения для всего проекта, создайте класс конфигурации в любом месте вашего исходного кода и отметьте его аннотацией `@MapResultSetConfiguration`:

```java
import io.github.kamegatze.map.result.set.processor.MapResultSetConfiguration;
import io.github.kamegatze.map.result.set.processor.Policy;

@MapResultSetConfiguration(conventionFieldPolicy = Policy.PASCAL_CASE) // Глобально переопределяет SNAKE_CASE по умолчанию на PASCAL_CASE
public class MapResultSetGlobalConfig {
    // Этот пустой класс служит единой точкой конфигурации для компилятора APT.
    // В будущих версиях здесь будет управляться больше свойств конфигурации.
}
```

### 2. Локальная конфигурация (переопределение для сущности)
Если конкретная таблица базы данных или выход хранимой процедуры отклоняется от вашей глобальной политики проекта, вы можете переопределить её непосредственно на целевой сущности или рекорде с помощью аннотации `@ConventionFieldPolicy`:

```java
import io.github.kamegatze.map.result.set.processor.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.processor.Policy;

@ConventionFieldPolicy(Policy.CAMEL_CASE) // Переопределяет глобальную политику на CAMEL_CASE для этой конкретной модели
public record Employee(
    @Id Long id,
    String firstName, // Сопоставляется с "firstName" (camelCase) вместо "FirstName"
    String lastName   // Сопоставляется с "lastName" (camelCase) вместо "LastName"
) {}
```

## 🛠️ Подключение библиотеки

### Gradle

```kotlin
implementation("io.github.kamegatze:map-result-set-java8:0.1.1")
annotationProcessor("io.github.kamegatze:map-result-set-java8:0.1.1")
```
**Если вы хотите использовать `record` как сущности из БД нужно тогда поменять с `map-result-set-java8` на `map-result-set-java17`**

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
                    <!-- ВАЖНО: Если вы используете Lombok, он ДОЛЖЕН быть объявлен здесь, ПЕРЕД map-result-set -->
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
**Если вы хотите использовать `record` как сущности из БД нужно тогда поменять с `map-result-set-java8` на `map-result-set-java17`**

## 🗺️ Вектор развития проекта (Roadmap v0.2.0+)
Текущая версия `0.1.1` уже доступна в Maven Central и стабильно решает задачи маппинга плоских структур и вложенных курсоров Oracle. В следующих релизах фокус направлен на автоматизацию следующих Enterprise-задач:
- Ядро компилятора (APT Engine).
- Полная поддержка Java 8+ классических POJO и Java 17+ Records.
- Маппинг вложенных курсоров Oracle (`REF CURSOR`) через `@Cursor`.
- **В разработке:** Декларативный маппинг классических SQL-связей таблиц (`@OneToMany` / Автоматическая агрегация плоского `java.sql.ResultSet` на основе `JOIN` по ключу `@Id` с поддержкой любой вложенности, заданной пользователем).
- **В разработке:** Строгая compile-time валидация против примитивов. Блокировка сборки проекта при попытке использовать типы `int`/`long`/`boolean` для гарантированной защиты от потери `NULL` значений.
- **В разработке:** Перенос аннотаций и метаданных из интерфейса-маппера в сгенерированный класс-реализацию для бесшовного подхватывания мапперов DI-фреймворками (Spring, Micronaut) в качестве валидных бинов.
- **В разработке:** Кастомные конвертеры типов данных (Аналог JPA `@Convert` для маппинга JSON-колонок или специфичных перечислений). 
- **В разработке:** Оптимизация читаемости сгенерированного кода.