package se.plushogskolan.casemanagement.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static se.plushogskolan.casemanagement.properties.PropertyReader.readProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import se.plushogskolan.casemanagement.auditing.CustomAuditorAware;

@Configuration
@EnableJpaRepositories("se.plushogskolan.casemanagement.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class InfrastructureConfig {

	@Bean
	public DataSource dataSource() {

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(readProperty("driver"));
		config.setJdbcUrl(readProperty("url"));
		config.setUsername(readProperty("username"));
		config.setPassword(readProperty("password"));
		
		return new HikariDataSource(config); 
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setGenerateDdl(true);

		return adapter;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan("se.plushogskolan.casemanagement.model");

		return factory;
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new CustomAuditorAware();
	}

}
