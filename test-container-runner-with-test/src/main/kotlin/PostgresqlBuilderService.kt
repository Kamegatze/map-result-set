import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.testcontainers.postgresql.PostgreSQLContainer

abstract class PostgresqlBuilderService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    private val postgresql = PostgreSQLContainer("postgres:16-alpine")

    init {
        postgresql.start()
    }

    val jdbcUrl: String get() = postgresql.jdbcUrl
    val username: String get() = postgresql.username
    val password: String get() = postgresql.password

    override fun close() {
        postgresql.stop()
    }
}