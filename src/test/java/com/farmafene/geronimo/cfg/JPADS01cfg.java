package com.farmafene.geronimo.cfg;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

import com.farmafene.geronimo.jpa.LocalContainerEntityManagerFactoryBeanBuilder;
import com.farmafene.geronimo.jpa.PersistenceUnitManagerBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.spi.PersistenceProvider;
import jakarta.resource.ResourceException;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableJpaRepositories(//
		basePackages = TestConstants.JPA_UNIT_DS01_PACKAGES, //
		entityManagerFactoryRef = TestConstants.JPA_UNIT_DS01 //
)
public class JPADS01cfg {

	@Bean(TestConstants.DS01)
	public HikariDataSource hDatasource() {
		log.info("Configurando HikariDataSource ...");
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(TestConstants.TESTDB_DS01_URL);
		config.setUsername("sa");
		config.setPassword("");
		config.setDriverClassName(TestConstants.TEST_JDBC_DRIVER);
		config.setMinimumIdle(1);
		config.setMaximumPoolSize(5);
		config.setPoolName(TestConstants.DS01);
		HikariDataSource hDs = new HikariDataSource(config);
		log.info("Retornando el DataSource: '{}'", hDs);
		return hDs;
	}

	@Bean(TestConstants.JPA_UNIT_DS01_PUM)
	@DependsOn({ "transactionManager" })
	DefaultPersistenceUnitManager getDefaultPersistenceUnitManager( //
			@Autowired @Qualifier(TestConstants.DS01) DataSource ds //
	) {
		return PersistenceUnitManagerBuilder //
				.builder() //
				.datasource(ds) //
				.jpaUnitName(TestConstants.JPA_UNIT_DS01) //
				.addPackageToScan(TestConstants.JPA_UNIT_DS01_PACKAGES) //
				.build();
	}

	@Bean(TestConstants.JPA_UNIT_DS01)
	LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean(//
			@Autowired @Qualifier(TestConstants.JPA_UNIT_DS01_PUM) DefaultPersistenceUnitManager pum, //
			@Autowired PersistenceProvider pp //
	) throws ResourceException {
		return LocalContainerEntityManagerFactoryBeanBuilder //
				.builder() //
				.jpaUnitName(TestConstants.JPA_UNIT_DS01) //
				.persistenceUnitManager(pum) //
				.persistenceProvider(pp) //
				.dialect(TestConstants.TESTDB_DIALECT) //
				.ddlCreate() //
				.formatSQL(true) //
				.showSQL(true) //
				.build();
	}
}
