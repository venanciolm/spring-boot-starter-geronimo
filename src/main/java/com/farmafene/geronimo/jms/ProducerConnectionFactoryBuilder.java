package com.farmafene.geronimo.jms;

import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmafene.jms.outbound.JMSLocalManagedConnectionFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.resource.ResourceException;
import jakarta.transaction.TransactionManager;

public class ProducerConnectionFactoryBuilder {

	private static final Logger log = LoggerFactory.getLogger(ProducerConnectionFactoryBuilder.class);

	private ProducerConnectionFactoryBuilder() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private ConnectionFactory physicalCF;
		private TransactionManager transactionManager;
		private int maxConnections = 2;
		private int idleTimeoutMinutes = 1;

		private Builder() {
		}

		public Builder idleTimeoutMinutes(int idleTimeoutMinutes) {
			this.idleTimeoutMinutes = idleTimeoutMinutes;
			return this;
		}

		public Builder maxConnections(int maxConnections) {
			this.maxConnections = maxConnections;
			return this;
		}

		public Builder transactionManager(TransactionManager transactionManager) {
			this.transactionManager = transactionManager;
			return this;
		}

		public Builder physicalCF(ConnectionFactory physicalCF) {
			this.physicalCF = physicalCF;
			return this;
		}

		public ConnectionFactory build() {
			JmsPoolConnectionFactory pcf = new JmsPoolConnectionFactory();
			pcf.setConnectionFactory(physicalCF);
			pcf.setMaxConnections(maxConnections);
			pcf.setConnectionIdleTimeout(idleTimeoutMinutes * 60000);
			pcf.setUseProviderJMSContext(false);
			BasicConnectionManager gcm = new BasicConnectionManager(transactionManager);
			JMSLocalManagedConnectionFactory mcf = new JMSLocalManagedConnectionFactory(pcf);
			ConnectionFactory wcf = null;
			try {
				wcf = (ConnectionFactory) mcf.createConnectionFactory(gcm);
			} catch (ResourceException e) {
				throw new IllegalStateException(e);
			}
			log.info("Devolviendo la ConnectionFactory: {}", wcf);
			return wcf;
		}
	}
}
