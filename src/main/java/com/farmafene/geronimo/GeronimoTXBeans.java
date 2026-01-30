package com.farmafene.geronimo;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.farmafene.commons.tx.hibernate.SpringJTAHBLocator;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;

@Configuration
@ConditionalOnProperty(prefix = "jta.geronimo", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GeronimoTXBeans {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeronimoTXBeans.class);

	@Bean( //
			name = { //
					"jtaTransactionManager" //
			}//
	)
	public TransactionManager getTransactionManager(//
			@Autowired //
			@Qualifier("j2eeTrasactionManager") //
			TransactionManager gtm //
	) {
		return gtm;
	}

	@Bean( //
			name = { //
					"recoverableTransactionManager" //
			}//
	)
	public RecoverableTransactionManager getRecoverableTransactionManager(//
			@Autowired //
			@Qualifier("j2eeTrasactionManager") //
			RecoverableTransactionManager gtm //
	) {
		return gtm;
	}

	@Bean( //
			name = { //
					"transRegistry", //
			}//
	)
	public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry(//
			@Autowired //
			@Qualifier("j2eeTrasactionManager") //
			TransactionManager gtm //
	) {
		return (TransactionSynchronizationRegistry) gtm;
	}

	@Bean( //
			name = { //
					"userTransaction" //
			}//
	)
	public UserTransaction getUserTransaction(//
			@Autowired //
			@Qualifier("j2eeTrasactionManager") //
			TransactionManager gtm //
	) {
		return (UserTransaction) gtm;
	}

	@Bean( //
			name = { //
					"j2eeTrasactionManager", //
			}//
	)
	public GeronimoTransactionManager getGeronimoTransactionManager(//
			GeronimoJTAGeronimoAutoconfigurationProperties conf //
	) throws Exception {
		GeronimoTransactionManager gtm = new GeronimoTransactionManager(conf.getTransactionTimeoutSeconds());
		SpringJTAHBLocator l = new SpringJTAHBLocator();
		l.setTransactionManager(gtm);
		l.setUserTransaction(gtm);
		l.afterPropertiesSet();
		LOGGER.info("Instanciando '{}' con transactionTimeoutSeconds {} s.", gtm.getClass().getSimpleName(),
				conf.getTransactionTimeoutSeconds());
		return gtm;
	}

	@Bean("transactionManager")
	@Primary
	public PlatformTransactionManager getJtaPlatformTransactionManager(//
			@Autowired //
			@Qualifier("j2eeTrasactionManager") //
			TransactionManager txm, //
			@Autowired //
			@Qualifier("userTransaction") //
			UserTransaction utx, //
			@Autowired //
			@Qualifier("transRegistry") //
			TransactionSynchronizationRegistry tsr //
	) {
		JtaTransactionManager m = new JtaTransactionManager();
		m.setTransactionManager(txm);
		m.setUserTransaction(utx);
		m.setTransactionSynchronizationRegistry(tsr);
		m.setAllowCustomIsolationLevels(true);
		return m;
	}
}
