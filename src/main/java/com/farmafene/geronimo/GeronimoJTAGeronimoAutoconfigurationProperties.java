package com.farmafene.geronimo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jta.geronimo")
@ConditionalOnProperty(prefix = "jta.geronimo", name = "enable", havingValue = "true", matchIfMissing = true)
@Validated
public class GeronimoJTAGeronimoAutoconfigurationProperties {
	private boolean enabled = true;
	private int transactionTimeoutSeconds = 60;

	/**
	 * @return the enable
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enable the enable to set
	 */
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	/**
	 * @return the transactionTimeoutSeconds
	 */
	public int getTransactionTimeoutSeconds() {
		return transactionTimeoutSeconds;
	}

	/**
	 * @param transactionTimeoutSeconds the transactionTimeoutSeconds to set
	 */
	public void setTransactionTimeoutSeconds(int transactionTimeoutSeconds) {
		this.transactionTimeoutSeconds = transactionTimeoutSeconds;
	}
}
