package com.farmafene.geronimo.cfg;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.farmafene.geronimo.jms.ProducerConnectionFactoryBuilder;

import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;

@org.springframework.context.annotation.Configuration
@ActiveProfiles("test")
public class ProducerCfg {

	@Bean("VM_MQConnectionFactory")
	public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
		return new ActiveMQConnectionFactory(TestConstants.BROKER_URL);
	}

	@Bean("ProducerConnectionFactory")
	public ConnectionFactory getEmbeddedProducerConnectionFactory(//
			@Autowired @Qualifier("VM_MQConnectionFactory") ActiveMQConnectionFactory activeMQConnectionFactory, //
			@Autowired @Qualifier("jtaTransactionManager") TransactionManager transactionManager //
	) {
		return ProducerConnectionFactoryBuilder //
				.builder() //
				.physicalCF(activeMQConnectionFactory) //
				.transactionManager(transactionManager) //
				.build();
	}

	@Bean("queueTestTemplate")
	JmsTemplate getJMSTemplate( //
			@Autowired @Qualifier("ProducerConnectionFactory") ConnectionFactory cf //
	) {
		JmsTemplate template = new JmsTemplate(cf);
		template.setDefaultDestinationName(TestConstants.TEST_QUEUE);
		template.setMessageTimestampEnabled(true);
		return template;
	}

}
