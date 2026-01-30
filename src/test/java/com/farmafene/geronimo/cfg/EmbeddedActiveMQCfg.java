package com.farmafene.geronimo.cfg;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.farmafene.geronimo.jms.EmbeddedActiveMQBuilder;

@org.springframework.context.annotation.Configuration
@ActiveProfiles("test")
public class EmbeddedActiveMQCfg {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedActiveMQCfg.class);

	@Bean
	public EmbeddedActiveMQ EmbeddedActiveMQ() {
		LOGGER.info("Arrancando el \"EmbeddedActiveMQ\"");
		EmbeddedActiveMQ broker = EmbeddedActiveMQBuilder //
				.builder() //
				// .autoStart(false) //
				.build();
		LOGGER.info("Arrancado el \"EmbeddedActiveMQ\" con instancia: {}", broker);
		return broker;
	}

	@Bean
	DisposableBean DisposableBeanEmbeddedActiveMQ(//
			@Autowired EmbeddedActiveMQ broker) {
		return new DisposableBean() {

			/**
			 * 
			 * @see org.springframework.beans.factory.DisposableBean#destroy()
			 */
			@Override
			public void destroy() throws Exception {
				LOGGER.info("Parando el \"EmbeddedActiveMQ\" con instancia: {}", broker);
				broker.stop();
				LOGGER.info("Parado el \"EmbeddedActiveMQ\" con instancia: {}", broker);
			}
		};
	}
}
