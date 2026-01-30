package com.farmafene.geronimo.cfg;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;

import com.farmafene.geronimo.jms.JmsMessageEndpointManagerBuilder;
import com.farmafene.geronimo.jms.SpringMessageListener;

import jakarta.jms.MessageListener;
import jakarta.transaction.TransactionManager;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ConsumerRAcfg {

	@Bean("Consumer")
	public JmsMessageEndpointManager getEmbeddedProducerConnectionFactory(//
			@Autowired @Qualifier("SpringMessageListener") MessageListener messageListener, //
			@Autowired @Qualifier("VM_MQConnectionFactory") ActiveMQConnectionFactory activeMQConnectionFactory, //
			@Autowired @Qualifier("jtaTransactionManager") TransactionManager transactionManager //
	) {
		return JmsMessageEndpointManagerBuilder //
				.builder() //
				.messageListener(messageListener) //
				.numConsumers(4) //
				.queue(TestConstants.TEST_QUEUE) //
				.physicalCF(activeMQConnectionFactory) //
				.transactionManager(transactionManager) //
				.build();
	}

	@Bean("SpringMessageListener")
	SpringMessageListener getTestSpringMessageListener() {
		return new SpringMessageListener();
	}

}
