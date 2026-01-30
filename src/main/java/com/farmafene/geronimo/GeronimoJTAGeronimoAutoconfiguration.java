package com.farmafene.geronimo;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnClass(value = GeronimoTransactionManager.class)
@ConditionalOnMissingBean(value = PlatformTransactionManager.class)
@ConditionalOnProperty(prefix = "jta.geronimo", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GeronimoJTAGeronimoAutoconfigurationProperties.class)
@Import(value = { GeronimoTXBeans.class })
public class GeronimoJTAGeronimoAutoconfiguration {

}
