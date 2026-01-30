package com.farmafene.geronimo.jms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.geronimo.connector.GeronimoBootstrapContext;
import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;

import com.farmafene.jms.outbound.XAConnectionFactoryFromConnectionFactory;
import com.farmafene.jms.ra.JMSActivationSpec;
import com.farmafene.jms.ra.JMSMessageEndpointFactory;
import com.farmafene.jms.ra.JMSResourceAdapter;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.ResourceAdapterInternalException;
import jakarta.transaction.TransactionManager;

public class JmsMessageEndpointManagerBuilder {

	private static final Logger log = LoggerFactory.getLogger(JmsMessageEndpointManagerBuilder.class);

	private JmsMessageEndpointManagerBuilder() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private int numConsumers = 2;
		private String queue = null;
		private ConnectionFactory physicalCF;
		private MessageListener messageListener;
		private TransactionManager transactionManager;
		private JMSResourceAdapter resourceAdapter;
		private boolean autoStart = false;

		private Builder() {
		}

		public Builder autoStart(boolean autoStart) {
			this.autoStart = autoStart;
			return this;
		}

		public Builder numConsumers(int numConsumers) {
			this.numConsumers = numConsumers;
			return this;
		}

		public Builder queue(String queue) {
			this.queue = queue;
			return this;
		}

		public Builder resourceAdapter(JMSResourceAdapter resourceAdapter) {
			this.resourceAdapter = resourceAdapter;
			return this;
		}

		public Builder messageListener(MessageListener messageListener) {
			this.messageListener = messageListener;
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

		public JmsMessageEndpointManager build() {
			JMSResourceAdapter ra = resourceAdapter;
			if (null == ra) {
				ra = new JMSResourceAdapter();
			}
			JMSActivationSpec spec = new JMSActivationSpec();
			spec.setLocalTransactions(true);
			spec.setNumSessions(numConsumers);
			if (null != queue) {
				spec.setQueue(queue);
			}
			spec.setXAConnectionFactory(new XAConnectionFactoryFromConnectionFactory(physicalCF));
			JmsMessageEndpointManager endpointManager = null;
			if (null == resourceAdapter) {
				ra = new JMSResourceAdapter();
				endpointManager = new JmsMessageEndpointManagerDisposable(numConsumers);
			} else {
				endpointManager = new JmsMessageEndpointManager();
			}

			endpointManager.setAutoStartup(autoStart);
			endpointManager.setResourceAdapter(ra);
			endpointManager.setActivationSpec(spec);
			endpointManager.setMessageEndpointFactory(//
					new JMSMessageEndpointFactory( //
							transactionManager, //
							messageListener //
					) //
			);
			return endpointManager;
		}

		public static class JmsMessageEndpointManagerDisposable extends JmsMessageEndpointManager
				implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {
			ExecutorService exec = null;

			public JmsMessageEndpointManagerDisposable(int numConsumers) {
				exec = Executors.newFixedThreadPool(numConsumers);
			}

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				log.info("Recibido evento: {}", event);
				if (!this.isAutoStartup()) {
					try {
						GeronimoWorkManager gwm = new GeronimoWorkManager(null, null, exec, null);
						BootstrapContext bsc = new GeronimoBootstrapContext(gwm, null, null);
						this.getResourceAdapter().start(bsc);
						this.start();
					} catch (ResourceAdapterInternalException e) {
						log.error("Error en el arranque del sistema!!!");
					}
				}
			}

			/**
			 * @see org.springframework.jca.endpoint.GenericMessageEndpointManager#destroy()
			 */
			@Override
			public void destroy() {
				log.info("Recibido evento: {}", "destroy()");
				this.getResourceAdapter().stop();
				this.exec.shutdown();
				super.destroy();
			}
		}
	}
}
