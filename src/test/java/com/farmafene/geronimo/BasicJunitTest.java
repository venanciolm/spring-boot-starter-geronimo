package com.farmafene.geronimo;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.farmafene.geronimo.cfg.ConsumerRAcfg;
import com.farmafene.geronimo.cfg.EmbeddedActiveMQCfg;
import com.farmafene.geronimo.cfg.HibernateCfg;
import com.farmafene.geronimo.cfg.JPADS01cfg;
import com.farmafene.geronimo.cfg.JPADS02cfg;
import com.farmafene.geronimo.cfg.ProducerCfg;
import com.farmafene.geronimo.cfg.TestConstants;
import com.farmafene.geronimo.generator.ExecutorInTransaction;
import com.farmafene.geronimo.generator.ISequenceGeneratorService;
import com.farmafene.geronimo.jms.EndTestMsg;
import com.farmafene.geronimo.jms.SpringMessageListener;
import com.farmafene.geronimo.jpa.ds01.entities.SerialEntity;
import com.farmafene.geronimo.jpa.ds01.entities.SerialEntityPK;
import com.farmafene.geronimo.jpa.ds01.repositories.JpaSerialEntityRepository;
import com.farmafene.geronimo.jpa.ds02.entities.AsyncMessageEntity;
import com.farmafene.geronimo.jpa.ds02.repositories.JpaAsyncMsgEntityRepository;
import com.farmafene.geronimo.test.GeronimoStackTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringJUnitConfig(//
		classes = { //
				EmbeddedActiveMQCfg.class, //
				ProducerCfg.class, //
				ConsumerRAcfg.class, //
				HibernateCfg.class, //
				JPADS01cfg.class, //
				JPADS02cfg.class, //
		}//
)
@EnableAutoConfiguration( //
		exclude = { //
				JmxAutoConfiguration.class //
				, MongoAutoConfiguration.class //
				// , SecurityAutoConfiguration.class //
				, DataSourceAutoConfiguration.class //
				, DataSourceTransactionManagerAutoConfiguration.class //
				, HibernateJpaAutoConfiguration.class //
				, JdbcRepositoriesAutoConfiguration.class //
				, JpaRepositoriesAutoConfiguration.class //

		}//
)
@GeronimoStackTest
@PropertySource("classpath:com/farmafene/geronimo/jtaGeronimo.properties")
@TestMethodOrder(OrderAnnotation.class)
@Sql( //
		scripts = "classpath:initDataDS01.sql", //
		config = @SqlConfig( //
				dataSource = TestConstants.DS01 //
		), //
		executionPhase = ExecutionPhase.BEFORE_TEST_CLASS //
)
@Sql( //
		scripts = "classpath:initDataDS02.sql", //
		config = @SqlConfig( //
				dataSource = TestConstants.DS02 //
		), //
		executionPhase = ExecutionPhase.BEFORE_TEST_CLASS //
)
@ComponentScan(basePackages = "com.farmafene.geronimo.generator")
public class BasicJunitTest {

	@Autowired
	ExecutorInTransaction runner;
	@PersistenceContext(unitName = "JPA_UNIT_DS01")
	private EntityManager emds01;

	@PersistenceContext(unitName = "JPA_UNIT_DS02")
	private EntityManager emds02;

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicJunitTest.class);
	@Value("${jta.geronimo.transactionTimeoutSeconds:-1}")
	private int transactionTimeOut;
	@Value("${jta.geronimo.enabled:false}")
	private boolean enabled;

	@Autowired
	@Qualifier(TestConstants.DS01)
	private DataSource ds01;
	@Autowired
	@Qualifier(TestConstants.DS02)
	private DataSource ds02;

	@Autowired
	@Qualifier("ProducerConnectionFactory")
	private ConnectionFactory pcf;

	@Autowired
	@Qualifier("SpringMessageListener")
	private SpringMessageListener listener;

	@Autowired
	@Qualifier("Consumer")
	JmsMessageEndpointManager consumer;

	@Autowired
	private JpaSerialEntityRepository repo01;

	@Autowired
	private JpaAsyncMsgEntityRepository repo02;

	@Autowired(required = false)
	private GeronimoJTAGeronimoAutoconfigurationProperties conf;

	@Autowired(required = false)
	private ISequenceGeneratorService service;
	@Autowired(required = false)
	@Qualifier("queueTestTemplate")
	private JmsTemplate jmsSender;

	@Test
	@Order(1)
	public void lecturaEnvironment() {
		LOGGER.info("El valor environment 'jta.geronimo.enabled' {}", enabled);
		LOGGER.info("El valor environment 'jta.geronimo.transactionTimeoutSeconds' {}", transactionTimeOut);
	}

	@Test
	@Order(2)
	public void initLecturaProperties() {
		LOGGER.info("El valor properties  'jta.geronimo.enabled' {}", conf == null ? "<null>" : "" + conf.isEnabled());
		LOGGER.info("El valor properties  'jta.geronimo.transactionTimeoutSeconds' {}",
				conf == null ? "<null>" : "" + conf.getTransactionTimeoutSeconds());
	}

	@Test
	@Order(3)
	public void existeProductor() {
		LOGGER.info("El productor es: {}", pcf);
	}

	@Test
	@Order(4)
	public void existeConsumidor() {
		LOGGER.info("El consumidor es: {}", consumer);
	}

	@Test
	@Order(5)
	public void existeListener() {
		LOGGER.info("El el listener es: {}", listener);
	}

	@Test
	@Order(6)
	public void accesoDS01() {
		LOGGER.error("{}{}{}{}{}{}", //
				System.lineSeparator(), //
				"======================================================", //
				System.lineSeparator(), //
				" accesoDS01", //
				System.lineSeparator(), //
				"======================================================");
		Optional<SerialEntity> item = repo01.findById(new SerialEntityPK("dummy", "------"));
		LOGGER.info("El valor recuperado es: {}", item);
		try {
			for (int i = 0; i < 3; i++) {
				LOGGER.info("Generando: {}", service.generateNextValue("dummy", "------").get());
			}
		} catch (InterruptedException e) {
			LOGGER.error("{}", e.getMessage(), e);
		} catch (ExecutionException e) {
			LOGGER.error("{}", e.getMessage(), e);
		}
		repo01.findAll()//
				.forEach(//
						(i) -> {//
							LOGGER.info("Tenemos: {}", i);
						}//
				);
	}

	@Test
	@Order(7)
	public void accesoDS02() {
		runner.execute(//
				() -> {
					LOGGER.error("{}{}{}{}{}{}", //
							System.lineSeparator(), //
							"======================================================", //
							System.lineSeparator(), //
							" accesoDS02", //
							System.lineSeparator(), //
							"======================================================");
					repo02.findAll().forEach(//
							(i) -> {
								LOGGER.info("{}", i);
							} //
					);
					try {
						AsyncMessageEntity ame1 = new AsyncMessageEntity();
						ame1.setClase("Clase");
						ame1.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
						LOGGER.error("Procedemos a salvar: {}", ame1);
						repo02.save(ame1);
						LOGGER.error("Salvado: {}", ame1);
					} catch (Throwable th) {
						LOGGER.error("Error en el test: {}", th.getMessage(), th);
					}
				});
		runner.execute(//
				() -> {
					repo02.findAll().forEach(//
							(i) -> {
								LOGGER.info("{}", i);
							} //
					);//
				}//
		);
	}

	@Test
	@Order(8)
	public void accesoEstadoDS() {
		LOGGER.info("{}", ds01);
		LOGGER.info("{}", ds02);
	}

	@Test
	@Order(9)
	public void waitForLatch() {
		LOGGER.error("{}{}", System.lineSeparator(), //
				"======================================================");
		runner.execute(//
				() -> {//
					jmsSender.send(//
							new MessageCreator() {
								/**
								 * 
								 */
								@Override
								public Message createMessage(Session session) throws JMSException {
									TextMessage msg = session.createTextMessage();
									try {
										msg.setText(new ObjectMapper().writerWithDefaultPrettyPrinter()
												.writeValueAsString(new EndTestMsg()));
									} catch (JsonProcessingException e) {
										LOGGER.error("Error en el proceso", e);
									} catch (JMSException e) {
										LOGGER.error("Error en el proceso", e);
									}
									return msg;
								}
							}//
					);
				}//
		);
		try {
			listener.getCountDownLatch().await(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.error("Error en el listener", e);
		}
	}
}
