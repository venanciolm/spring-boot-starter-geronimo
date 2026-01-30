package com.farmafene.geronimo.jpa;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.geronimo.connector.outbound.GenericConnectionManager;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.LocalTransactions;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.NoPool;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTrackingCoordinator;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.tranql.connector.jdbc.XADataSourceWrapper;

import com.farmafene.commons.tx.hibernate.SpringJTAHBLocator;
import com.farmafene.commons.tx.jdbc.XADatasourceFromContainerDataSource;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.resource.ResourceException;

public class PersistenceUnitManagerBuilder {
	private static final Logger log = LoggerFactory.getLogger(PersistenceUnitManagerBuilder.class);

	private PersistenceUnitManagerBuilder() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private DataSource datasource;
		private String jpaUnitName;
		private Set<String> packagesToScan = new LinkedHashSet<String>();

		private Builder() {
		}

		public Builder datasource(DataSource datasource) {
			this.datasource = datasource;
			return this;
		}

		public Builder jpaUnitName(String jpaUnitName) {
			this.jpaUnitName = jpaUnitName;
			return this;
		}

		public Builder addPackageToScan(String packageToScan) {
			packagesToScan.add(packageToScan);
			return this;
		}

		public DefaultPersistenceUnitManager build() {
			AbstractJtaPlatform locator = new SpringJTAHBLocator();
			XADataSourceWrapper mcf = //
					new XADataSourceWrapper(//
							new XADatasourceFromContainerDataSource(datasource) //
					);
			if (datasource instanceof HikariDataSource) {
				mcf.setUserName(((HikariDataSource) datasource).getUsername());
				mcf.setPassword(((HikariDataSource) datasource).getPassword());
			}
			GenericConnectionManager gcm = //
					new GenericConnectionManager( //
							LocalTransactions.INSTANCE, //
							new NoPool(), //
							null, //
							new ConnectionTrackingCoordinator(true), //
							(RecoverableTransactionManager) locator.getTransactionManager(),
							//
							mcf, //
							jpaUnitName + "_CM", //
							/* getClass().getClassLoader() */null //
					);
			DefaultPersistenceUnitManager pum = new DefaultPersistenceUnitManager();
			String[] aPackages = packagesToScan.toArray(new String[0]);
			log.info("Los paquetes son: {}", Arrays.toString(aPackages));
			pum.setPackagesToScan(aPackages);
			pum.setDefaultPersistenceUnitName(jpaUnitName);
			try {
				pum.setDefaultJtaDataSource((DataSource) mcf.createConnectionFactory(gcm));
			} catch (ResourceException e) {
				throw new IllegalStateException(e);
			}
			return pum;
		}
	}
}
