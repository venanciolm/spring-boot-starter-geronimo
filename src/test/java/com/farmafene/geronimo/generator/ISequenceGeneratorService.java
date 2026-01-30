package com.farmafene.geronimo.generator;

import java.math.BigDecimal;
import java.util.concurrent.Future;

public interface ISequenceGeneratorService {

	/**
	 * Genera un identificador
	 * 
	 * @param seqName no puede ser null
	 * @param prefix  no puede ser null
	 * @return
	 */
	Future<BigDecimal> generateNextValue(String seqName, String prefix);
}
