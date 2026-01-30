package com.farmafene.geronimo.generator;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class ExecutorInTransaction {
	@Transactional(rollbackOn = Throwable.class)
	public void execute(IProcess i) {
		i.execute();
	}
}
