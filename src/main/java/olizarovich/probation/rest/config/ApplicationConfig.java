package olizarovich.probation.rest.config;

import olizarovich.probation.rest.interceptors.LogInterceptor;
import olizarovich.probation.rest.repositories.DocumentRepository;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.repositories.RoleRepository;
import olizarovich.probation.rest.services.DocumentService;
import olizarovich.probation.rest.services.PersonService;
import olizarovich.probation.rest.services.implementation.DocumentServiceImplementation;
import olizarovich.probation.rest.services.implementation.PersonServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableTransactionManagement
@EnableJpaRepositories({"olizarovich.probation.rest.repositories"})
@EntityScan("olizarovich.probation.rest.models")
@ComponentScan({"olizarovich.probation.rest.controllers", "olizarovich.probation.rest.config"})
@Configuration
@SpringBootApplication
@Import({SpringDataSourceConfig.class, SecurityConfiguration.class})
public class ApplicationConfig implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }

    @Bean
    public PersonService personService(@Autowired PersonRepository personRepository,
                                       @Autowired RoleRepository mockRoleRepository,
                                       @Autowired PasswordEncoder bCryptPasswordEncoder) {
        return new PersonServiceImplementation(bCryptPasswordEncoder, mockRoleRepository, personRepository);
    }

    @Bean
    public DocumentService documentService(@Autowired DocumentRepository documentRepository) {
        return new DocumentServiceImplementation(documentRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor());
    }
}
