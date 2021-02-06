package olizarovich.probation.rest.test.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.test.FlywayHelperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource({"classpath:application.properties"})
public class FlywayPostgresqlTestConfig {
    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("flyway.driver", "org.postgresql.Driver"));
        dataSource.setUrl(env.getProperty("flyway.url"));
        dataSource.setUsername(env.getProperty("flyway.user"));
        dataSource.setPassword(env.getProperty("flyway.password"));

        return dataSource;
    }

    @Bean
    @DependsOn("flywayFactory")
    public Flyway flyway() {
        return flywayFactory().createFlyway();
    }

    @Bean
    @DependsOn("flywayConfiguration")
    public FlywayHelperFactory flywayFactory() {
        FlywayHelperFactory flywayHelperFactory = new FlywayHelperFactory();

        flywayHelperFactory.setFlywayConfiguration(flywayConfiguration());
        return flywayHelperFactory;
    }

    @Bean
    @DependsOn("dataSource")
    public ClassicConfiguration flywayConfiguration() {
        ClassicConfiguration classicConfiguration = new ClassicConfiguration();

        classicConfiguration.setDataSource(dataSource());
        return classicConfiguration;
    }
}
