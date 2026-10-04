import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.testcontainers.mysql.MySQLContainer

abstract class MySqlBuilderService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    private val mySql = MySQLContainer("mysql:8.0.36")

    init {
        mySql.start()
    }

    val jdbcUrl: String get() = mySql.jdbcUrl
    val username: String get() = mySql.username
    val password: String get() = mySql.password

    override fun close() {
        mySql.stop()
    }
}