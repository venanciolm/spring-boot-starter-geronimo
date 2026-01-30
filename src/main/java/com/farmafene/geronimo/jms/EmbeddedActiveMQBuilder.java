package com.farmafene.geronimo.jms;

import java.io.File;

import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.JournalType;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressFullMessagePolicy;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.utils.critical.CriticalAnalyzerPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.context.annotation.Configuration
public class EmbeddedActiveMQBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedActiveMQBuilder.class);

	private EmbeddedActiveMQBuilder() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private boolean autoStart = true;
		private File instanceBrokerStorage = new File(new File("target"), "amq-prog");
		private String acceptorUrl = "vm://0";

		private Builder() {
		}

		Builder autoStart(boolean autoStart) {
			this.autoStart = autoStart;
			return this;
		}

		Builder instanceBrokerStorage(File instanceBrokerStorage) {
			this.instanceBrokerStorage = instanceBrokerStorage;
			return this;
		}

		Builder acceptorUrl(String acceptorUrl) {
			this.acceptorUrl = acceptorUrl;
			return this;
		}

		public EmbeddedActiveMQ build() {
			EmbeddedActiveMQ broker = new EmbeddedActiveMQ();
			Configuration cfg = new ConfigurationImpl();
			broker.setConfiguration(cfg);
			cfg.setName("EmbeddedActiveMQ");
			cfg.setMaxRedeliveryRecords(1);
			cfg.setBrokerInstance(instanceBrokerStorage);
			cfg.setSecurityEnabled(false);
			cfg.setPersistenceEnabled(true);
			cfg.setJournalType(JournalType.ASYNCIO);
			cfg.setJournalDatasync(true);
			cfg.setJournalMinFiles(2);
			cfg.setJournalPoolFiles(10);
			cfg.setJournalDeviceBlockSize(4096);
			cfg.setJournalFileSize(10 * 1024 * 1024);
			cfg.setJournalBufferTimeout_AIO(660000);
			cfg.setJournalBufferTimeout_NIO(660000);
			cfg.setJournalMaxIO_AIO(4096);
			cfg.setJournalMaxIO_NIO(4096);
			cfg.setDiskScanPeriod(5000);
//<paging-directory>target/amq/data/paging</paging-directory>
//<bindings-directory>target/amq/bindings</bindings-directory>
//<journal-directory>target/amq/data/journal</journal-directory>
//<large-messages-directory>target/amq/data/large-messages</large-messages-directory>
//<max-redelivery-records>1</max-redelivery-records>
//<security-enabled>false</security-enabled>
//<persistence-enabled>true</persistence-enabled>
//<journal-type>ASYNCIO</journal-type>
//<journal-datasync>true</journal-datasync>
//<journal-min-files>2</journal-min-files>
//<journal-pool-files>10</journal-pool-files>
//<journal-device-block-size>4096</journal-device-block-size>
//<journal-file-size>10M</journal-file-size>
//<journal-buffer-timeout>660000</journal-buffer-timeout>
//<journal-max-io>4096</journal-max-io>
//<disk-scan-period>5000</disk-scan-period>
//<max-disk-usage>90</max-disk-usage>
//<critical-analyzer>true</critical-analyzer>
//<critical-analyzer-timeout>120000</critical-analyzer-timeout>
//<critical-analyzer-check-period>60000</critical-analyzer-check-period>
//<critical-analyzer-policy>HALT</critical-analyzer-policy>
//<page-sync-timeout>2544000</page-sync-timeout>
//<global-max-messages>-1</global-max-messages>
//<persist-delivery-count-before-delivery>true</persist-delivery-count-before-delivery>
//<address-setting match="activemq.management#">
//	<dead-letter-address>DLQ</dead-letter-address>
//	<expiry-address>ExpiryQueue</expiry-address>
//	<redelivery-delay>10000</redelivery-delay>
//	<!-- with -1 only the global-max-size is in use for limiting -->
//	<max-size-bytes>-1</max-size-bytes>
//	<message-counter-history-day-limit>10</message-counter-history-day-limit>
//	<address-full-policy>PAGE</address-full-policy>
//	<auto-create-queues>true</auto-create-queues>
//	<auto-create-addresses>true</auto-create-addresses>
//</address-setting>
//<address-setting match="#">
//	<dead-letter-address>DLQ</dead-letter-address>
//	<expiry-address>ExpiryQueue</expiry-address>
//	<!-- DLQ -->
//	<redelivery-delay>10000</redelivery-delay>
//	<max-delivery-attempts>6</max-delivery-attempts>
//	<auto-create-dead-letter-resources>true</auto-create-dead-letter-resources>
//	<dead-letter-queue-prefix/>
//	<!-- override the default -->
//	<dead-letter-queue-suffix>.dlq</dead-letter-queue-suffix>
//	<!-- DLQ -->
//	<message-counter-history-day-limit>10</message-counter-history-day-limit>
//	<address-full-policy>PAGE</address-full-policy>
//	<auto-create-queues>true</auto-create-queues>
//	<auto-create-addresses>true</auto-create-addresses>
//	<auto-delete-queues>false</auto-delete-queues>
//	<auto-delete-addresses>false</auto-delete-addresses>
//	<!-- The size of each page file -->
//	<page-size-bytes>10M</page-size-bytes>
//	<!-- When we start applying the address-full-policy, e.g paging -->
//	<!-- Both are disabled by default, which means we will use the global-max-size/global-max-messages -->
//	<max-size-bytes>-1</max-size-bytes>
//	<max-size-messages>-1</max-size-messages>
//	<!-- When we read from paging into queues (memory) -->
//	<max-read-page-messages>-1</max-read-page-messages>
//	<max-read-page-bytes>20M</max-read-page-bytes>
//	<!-- Limit on paging capacity before starting to throw errors -->
//	<page-limit-bytes>-1</page-limit-bytes>
//	<page-limit-messages>-1</page-limit-messages>
//</address-setting>
			cfg.setMaxDiskUsage(90);
			cfg.setCriticalAnalyzer(true);
			cfg.setCriticalAnalyzerTimeout(120000);
			cfg.setCriticalAnalyzerCheckPeriod(60000);
			cfg.setCriticalAnalyzerPolicy(CriticalAnalyzerPolicy.HALT);
			cfg.setPageSyncTimeout(2544000);
			cfg.setGlobalMaxMessages(-1L);
			cfg.setPersistDeliveryCountBeforeDelivery(true);
			try {
				cfg.addAcceptorConfiguration("in-vm", acceptorUrl);
				CoreAddressConfiguration addrCfg = new CoreAddressConfiguration();
				cfg.addAddressConfiguration(addrCfg);
				addrCfg.setName("Default");

				@SuppressWarnings("removal")
				QueueConfiguration dlqQueue = new QueueConfiguration();
				addrCfg.addQueueConfig(dlqQueue);
				dlqQueue.setRoutingType(RoutingType.ANYCAST);
				dlqQueue.setName("DeadLetterQueue");
				dlqQueue.setAddress("DLQ");
				dlqQueue.setInternal(true);
				dlqQueue.setDurable(true);
				dlqQueue.setEnabled(true);
				dlqQueue.setAutoCreateAddress(true);
				dlqQueue.setTemporary(false);

				@SuppressWarnings("removal")
				QueueConfiguration eqQueue = new QueueConfiguration();
				addrCfg.addQueueConfig(eqQueue);
				eqQueue.setRoutingType(RoutingType.ANYCAST);
				eqQueue.setName("ExpiryQueue");
				eqQueue.setAddress("ExpiryQueue");
				eqQueue.setInternal(true);
				eqQueue.setDurable(true);
				eqQueue.setEnabled(true);
				eqQueue.setAutoCreateAddress(true);
				eqQueue.setTemporary(false);

				AddressSettings mSetting = new AddressSettings();
				mSetting.setDefaultAddressRoutingType(RoutingType.ANYCAST);
				cfg.addAddressSetting("activemq.management#", mSetting);
				@SuppressWarnings("removal")
				SimpleString deadLetterAddress = new SimpleString("DLQ");
				mSetting.setDeadLetterAddress(deadLetterAddress);
				@SuppressWarnings("removal")
				SimpleString expiryAddress = new SimpleString("ExpiryQueue");
				mSetting.setExpiryAddress(expiryAddress);
				mSetting.setRedeliveryDelay(10000L);
				mSetting.setMaxSizeBytes(-1);
				mSetting.setMessageCounterHistoryDayLimit(10);
				mSetting.setAddressFullMessagePolicy(AddressFullMessagePolicy.PAGE);
				mSetting.setAutoCreateQueues(true);
				mSetting.setAutoCreateAddresses(true);

				AddressSettings gSetting = new AddressSettings();
				gSetting.setDefaultAddressRoutingType(RoutingType.ANYCAST);
				cfg.addAddressSetting("#", gSetting);
				gSetting.setDeadLetterAddress(deadLetterAddress);
				gSetting.setExpiryAddress(expiryAddress);
				gSetting.setMaxRedeliveryDelay(10000);
				gSetting.setMaxDeliveryAttempts(6);
				gSetting.setAutoCreateDeadLetterResources(true);
				@SuppressWarnings("removal")
				SimpleString deadLetterQueuePrefix = new SimpleString("");
				gSetting.setDeadLetterQueuePrefix(deadLetterQueuePrefix);
				@SuppressWarnings("removal")
				SimpleString deadLetterQueueSuffix = new SimpleString(".dlq");
				gSetting.setDeadLetterQueueSuffix(deadLetterQueueSuffix);
				gSetting.setMessageCounterHistoryDayLimit(10);
				gSetting.setAddressFullMessagePolicy(AddressFullMessagePolicy.PAGE);
				gSetting.setAutoCreateQueues(true);
				gSetting.setAutoCreateAddresses(true);
				gSetting.setAutoDeleteQueues(false);
				gSetting.setAutoDeleteAddresses(false);
				gSetting.setPageSizeBytes(10 * 1024 * 1024);
				gSetting.setMaxSizeBytes(-1);
				gSetting.setMaxSizeMessages(-1);
				gSetting.setMaxReadPageBytes(-1);
				gSetting.setMaxReadPageBytes(20 * 1024 * 1024);
				gSetting.setPageLimitBytes(-1L);
				gSetting.setPageLimitMessages(-1L);

			} catch (Exception e) {
				LOGGER.error("Error en la configuracion del Acceptor", e);
				throw new UnsupportedOperationException(e);
			}
			if (autoStart) {
				try {
					broker.start();
				} catch (Exception e) {
					LOGGER.error("Error en arranque del broker", e);
					throw new UnsupportedOperationException(e);
				}
			}
			return broker;
		}
	}
}
