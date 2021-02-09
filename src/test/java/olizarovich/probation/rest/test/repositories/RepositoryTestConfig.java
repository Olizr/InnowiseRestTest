package olizarovich.probation.rest.test.repositories;

import olizarovich.probation.rest.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootConfiguration
@EnableJpaRepositories({"olizarovich.probation.rest.repositories"})
@EntityScan("olizarovich.probation.rest.models")
@EnableTransactionManagement
@AutoConfigurationPackage
public class RepositoryTestConfig {

}
