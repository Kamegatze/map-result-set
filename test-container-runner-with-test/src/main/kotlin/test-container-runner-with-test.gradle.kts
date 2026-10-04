import java.util.concurrent.atomic.AtomicReference

val postgresqlProvider =
    gradle.sharedServices.registerIfAbsent(
        "postgresqlService",
        PostgresqlBuilderService::class.java,
    )
val oracleProvider =
    gradle.sharedServices.registerIfAbsent("oracleService", OracleBuilderService::class.java)
val mySqlProvider =
    gradle.sharedServices.registerIfAbsent("mySqlService", MySqlBuilderService::class.java)

val previousProjectAtomic = AtomicReference<Project>()

allprojects.forEach { it ->
    if (previousProjectAtomic.get() == null) {
        previousProjectAtomic.getAndSet(it)
    } else {
        it.tasks.withType<Test>().configureEach {
            mustRunAfter(previousProjectAtomic.getAndSet(it).tasks.withType<Test>())
        }
    }
    it.tasks.withType<Test>().configureEach {
        usesService(postgresqlProvider)
        usesService(oracleProvider)
        usesService(mySqlProvider)
        val postgresqlJdbcUrl = postgresqlProvider.map { it.jdbcUrl }
        val postgresqlUsername = postgresqlProvider.map { it.username }
        val postgresqlPassword = postgresqlProvider.map { it.password }
        val oracleJdbcUrl = oracleProvider.map { it.jdbcUrl }
        val oracleUsername = oracleProvider.map { it.username }
        val oraclePassword = oracleProvider.map { it.password }
        val mysqlJdbcUrl = mySqlProvider.map { it.jdbcUrl }
        val mysqlUsername = mySqlProvider.map { it.username }
        val mysqlPassword = mySqlProvider.map { it.password }
        doFirst {
            environment("POSTGRESQL_JDBC_URL", postgresqlJdbcUrl.get())
            environment("POSTGRESQL_USERNAME", postgresqlUsername.get())
            environment("POSTGRESQL_PASSWORD", postgresqlPassword.get())
            environment("ORACLE_JDBC_URL", oracleJdbcUrl.get())
            environment("ORACLE_USERNAME", oracleUsername.get())
            environment("ORACLE_PASSWORD", oraclePassword.get())
            environment("MYSQL_JDBC_URL", mysqlJdbcUrl.get())
            environment("MYSQL_USERNAME", mysqlUsername.get())
            environment("MYSQL_PASSWORD", mysqlPassword.get())
        }
    }
}
