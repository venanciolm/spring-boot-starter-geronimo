package com.farmafene.geronimo.generator;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Id;

@SuppressWarnings("serial")
public class SequenceUUIDGenerator implements BeforeExecutionGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SequenceMsgGenerator.class);

	/**
	 * 
	 * @see org.hibernate.generator.Generator#getEventTypes()
	 */
	@Override
	public EnumSet<EventType> getEventTypes() {
		return EventTypeSets.INSERT_ONLY;
	}

	/**
	 * 
	 * @see org.hibernate.generator.BeforeExecutionGenerator#generate(org.hibernate.engine.spi.SharedSessionContractImplementor,
	 *      java.lang.Object, java.lang.Object, org.hibernate.generator.EventType)
	 */
	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue,
			EventType eventType) {
		Object out = getValueForIdAnnotation(owner);
		if (null == out) {
			out = UUID.randomUUID().toString();
			LOGGER.info("El valor generado es: {}", out);
		} else {
			LOGGER.info("El valor introducido es: {}", out);
		}
		return out;
	}

	/**
	 * Este método, a priori, no debería ser necesario. pero ..., obtenemos y se ha
	 * establecido el valor en Identificador Entonces, no lo generamos.
	 * 
	 * @param owner
	 * @return
	 */
	private Object getValueForIdAnnotation(Object owner) {
		Object out = null;
		for (Field f : owner.getClass().getDeclaredFields()) {
			try {
				if (null != f.getDeclaredAnnotation(Id.class)) {
					if (!f.canAccess(owner)) {
						f.setAccessible(true);
					}
					out = f.get(owner);
					break;
				}
			} catch (IllegalArgumentException e) {
				IllegalStateException ise = new IllegalStateException(e);
				LOGGER.error("Error en la generacion", e);
				throw ise;
			} catch (IllegalAccessException e) {
				IllegalStateException ise = new IllegalStateException(e);
				LOGGER.error("Error en la generacion", e);
				throw ise;
			}
		}
		return out;
	}
}
