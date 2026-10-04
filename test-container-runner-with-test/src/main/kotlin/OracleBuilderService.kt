import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.testcontainers.oracle.OracleContainer

abstract class OracleBuilderService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    private val oracle = OracleContainer("gvenzl/oracle-free:slim-faststart")

    init {
        oracle.start()
    }

    val jdbcUrl: String get() = oracle.jdbcUrl
    val username: String get() = oracle.username
    val password: String get() = oracle.password

    override fun close() {
        oracle.stop()
    }
}