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

@EnableAutoConfiguration
@Configuration
@ComponentScan
@EnableJpaRepositories
public class Application
{

    @Bean
    public DataSource datasource()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/aichallenge");
        dataSource.setUsername("aichallenge");
        dataSource.setPassword("dudule");
        return dataSource;

        // return new EmbeddedDatabaseBuilder().setType(H2).build();
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
