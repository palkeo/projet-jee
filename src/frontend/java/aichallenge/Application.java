package aichallenge;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.support.ResourceBundleMessageSource;

@EnableAutoConfiguration
@Configuration
@ComponentScan
@EnableJpaRepositories
public class Application
{
    @Bean
    public ResourceBundleMessageSource messageSource()
    {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("aichallenge/messages.properties");
        return ms;
    }

    @Bean
    public DataSource datasource()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        // Hack me ! Please !
        dataSource.setUrl("jdbc:postgresql://viod.eu:5432/aichallenge");
        dataSource.setUsername("aichallenge");
        dataSource.setPassword("dudule"); // that's a cool password :)
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
	DataSource dataSource,
	JpaVendorAdapter jpaVendorAdapter)
    {
	LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
	lef.setDataSource(dataSource);
	lef.setJpaVendorAdapter(jpaVendorAdapter);
	lef.setPackagesToScan("aichallenge");

	return lef;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter()
    {
	HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
	hibernateJpaVendorAdapter.setShowSql(true);
	hibernateJpaVendorAdapter.setGenerateDdl(true);
	hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
	return hibernateJpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
	return new JpaTransactionManager();
    }

    public static void main(String[] args)
    {
	SpringApplication.run(Application.class, args);
    }
}
