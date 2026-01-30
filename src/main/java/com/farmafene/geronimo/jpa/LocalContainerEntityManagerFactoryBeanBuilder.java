package com.farmafene.geronimo.jpa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

import com.farmafene.commons.tx.hibernate.SpringJTAHBLocator;

import jakarta.persistence.spi.PersistenceProvider;
import jakarta.resource.ResourceException;

public class LocalContainerEntityManagerFactoryBeanBuilder {
	private static final Logger log = LoggerFactory.getLogger(LocalContainerEntityManagerFactoryBeanBuilder.class);

	private LocalContainerEntityManagerFactoryBeanBuilder() {
	}

	public static Builder builder() {
		return new LocalContainerEntityManagerFactoryBeanBuilder.Builder();
	}

	public static class Builder {
		private static final String DDL_AUTO_VALUE_Validate = "validate";
		private static final String DDL_AUTO_VALUE_Create = "create";
		private static final String DDL_AUTO_VALUE_Update = "update";
		private static final String DDL_AUTO_VALUE_CreateDrop = "create-drop";
		private static final String DDL_AUTO_PROP = "hibernate.hbm2ddl.auto";
		private DefaultPersistenceUnitManager persistenceUnitManager;
		private String jpaUnitName;
		private String dialect;
		private Boolean showSQL = false;
		private Boolean formatSQL = false;
		private Integer fetchSize = 100;
		private Map<String, Object> hibernateProps = new LinkedHashMap<String, Object>();
		private PersistenceProvider persistenceProvider;
		private String ddlAutoStr;

		private Builder() {
		}

		public Builder persistenceProvider(PersistenceProvider persistenceProvider) {
			this.persistenceProvider = persistenceProvider;
			return this;
		}

		public Builder persistenceUnitManager(DefaultPersistenceUnitManager persistenceUnitManager) {
			this.persistenceUnitManager = persistenceUnitManager;
			return this;
		}

		public Builder jpaUnitName(String jpaUnitName) {
			this.jpaUnitName = jpaUnitName;
			return this;
		}

		public Builder showSQL(Boolean showSQL) {
			this.showSQL = showSQL;
			return this;
		}

		public Builder formatSQL(boolean formatSQL) {
			this.formatSQL = formatSQL;
			return this;
		}

		public Builder fetchSize(Integer fechSize) {
			this.fetchSize = fechSize;
			return this;
		}

		public Builder dialect(String dialect) {
			this.dialect = dialect;
			return this;
		}

		public Builder ddlCreate() {
			ddlAutoStr = DDL_AUTO_VALUE_Create;
			return this;
		}

		public Builder ddlUpdate() {
			ddlAutoStr = DDL_AUTO_VALUE_Update;
			return this;
		}

		public Builder ddlValidate() {
			ddlAutoStr = DDL_AUTO_VALUE_Validate;
			return this;
		}

		public Builder ddlCreateDrop() {
			ddlAutoStr = DDL_AUTO_VALUE_CreateDrop;
			return this;
		}

		public Builder ddlClear() {
			ddlAutoStr = null;
			return this;
		}

		public Builder putHibernateProp(String option, Object value) {
			if (null != value) {
				hibernateProps.put(option, value);
			} else {
				hibernateProps.remove(option);
			}
			return this;
		}

		public LocalContainerEntityManagerFactoryBean build() throws ResourceException {
			AbstractJtaPlatform locator = new SpringJTAHBLocator();
			Map<String, Object> hibernateInitProps = new LinkedHashMap<String, Object>();
			if (null != ddlAutoStr) {
				hibernateInitProps.put(DDL_AUTO_PROP, ddlAutoStr);
			}
			// hibernateInitProps.put("hibernate.archive.autodetection", "class,hbm");
			hibernateInitProps.put("hibernate.flushMode", "ALWAYS");
			hibernateInitProps.put("hibernate.generate_statistics", false);
			hibernateInitProps.put("hibernate.id.new_generator_mappings", false);
			// hibernateProps.put("hibernate.query.sql.jdbc_style_params_base", true); //
			// FIX!!!
			hibernateInitProps.put("hibernate.current_session_context_class", "jta");
			hibernateInitProps.put("hibernate.cache.use_query_cache", true);
			hibernateInitProps.put("hibernate.cache.use_second_level_cache", true);
			hibernateInitProps.put("hibernate.transaction.jta.platform", locator);
			hibernateInitProps.put("hibernate.physical_naming_strategy",
					CamelCaseToUnderscoresNamingStrategy.class.getCanonicalName());
			hibernateInitProps.put("hibernate.cache.region.factory_class", NoCachingRegionFactory.class);
			hibernateInitProps.put("hibernate.temp.use_jdbc_metadata_defaults", false);
			// hibernateProps.put("hibernate.cache.region.factory_class",
			// EhCacheRegionFactory.class);
//			hibernateProps.put("hibernate.cache.region_prefix", "");
//			hibernateProps.put("net.sf.ehcache.configurationResourceName", "/hb-ehcache-AUTH.xml");

			LocalContainerEntityManagerFactoryBean fb = new LocalContainerEntityManagerFactoryBean();
			fb.setJtaDataSource(persistenceUnitManager.getDefaultJtaDataSource());
			fb.setPersistenceUnitManager(persistenceUnitManager);
			fb.setPersistenceUnitName(jpaUnitName);
			fb.setPersistenceProvider(persistenceProvider);
			fb.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
			fb.setJpaPropertyMap(new HashMap<>());
			fb.getJpaPropertyMap().putAll(hibernateInitProps);
			if (null != dialect) {
				hibernateProps.put("hibernate.dialect", dialect);
			}
			if (null != fetchSize) {
				hibernateProps.put("hibernate.default_batch_fetch_size", fetchSize);
			}
			if (null != showSQL) {
				hibernateProps.put("hibernate.show_sql", showSQL);
			}
			if (null != formatSQL) {
				hibernateProps.put("hibernate.format_sql", formatSQL);
			}
			fb.getJpaPropertyMap().putAll(hibernateProps);
			log.info("Instanciado {} con las propiedades: {}{}", fb, System.lineSeparator(), fb.getJpaPropertyMap());
			return fb;
		}
	}
}
