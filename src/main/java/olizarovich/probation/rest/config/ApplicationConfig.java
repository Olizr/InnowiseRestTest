package olizarovich.probation.rest.config;

import olizarovich.probation.rest.repositories.DocumentRepository;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.services.DocumentService;
import olizarovich.probation.rest.services.PersonService;
import olizarovich.probation.rest.services.implementation.DocumentServiceImplementation;
import olizarovich.probation.rest.services.implementation.PersonServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableJpaRepositories({"olizarovich.probation.rest.repositories"})
@EntityScan("olizarovich.probation.rest.models")
@Configuration
public class ApplicationConfig {
    @Bean
    public PersonService personService(@Autowired PersonRepository personRepository) {
        return new PersonServiceImplementation(personRepository);
    }

    @Bean
    public DocumentService documentService(@Autowired DocumentRepository documentRepository) {
        return new DocumentServiceImplementation(documentRepository);
    }
}
