import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// ============================================================
// @TestConfiguration reutilizável com withReuse(true)
// Usar em: src/test/java/.../TestcontainersConfig.java
// ============================================================

@TestConfiguration
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:17")
            .withReuse(true);   // container persiste entre reinicializações da JVM
    }

    @Bean
    @ServiceConnection
    GenericContainer<?> redis() {
        return new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .withReuse(true);
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafka() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.0"))
            .withReuse(true);
    }
}

// ============================================================
// TestApplication — boot local com todos os containers
// Usar em: src/test/java/.../TestApplication.java
// ============================================================

class TestApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication
            .from(Application::main)
            .with(TestcontainersConfig.class)
            .run(args);
    }
}
