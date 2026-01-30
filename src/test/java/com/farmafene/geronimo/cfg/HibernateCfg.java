package com.farmafene.geronimo.cfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateCfg {

	@Bean( //
			name = { //
					"HibernateProvider" //
			}//
	)
	public org.hibernate.jpa.HibernatePersistenceProvider getHibernatePersistenceProvider() {
		return new org.hibernate.jpa.HibernatePersistenceProvider();
	}

}
