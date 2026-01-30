package com.farmafene.geronimo.generator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecuenceGeneratorServiceLocator implements InitializingBean {

	private static ISequenceGeneratorService SGSERVICE;

	@Autowired
	private ISequenceGeneratorService sequenceGeneratorService;

	public static ISequenceGeneratorService getISequenceGeneratorService() {
		return SGSERVICE;
	}

	/**
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		SGSERVICE = sequenceGeneratorService;
	}
}
