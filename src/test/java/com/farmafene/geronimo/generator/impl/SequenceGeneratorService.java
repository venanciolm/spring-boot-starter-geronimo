package com.farmafene.geronimo.generator.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.farmafene.geronimo.cfg.TestConstants;
import com.farmafene.geronimo.generator.ISequenceGeneratorService;
import com.farmafene.geronimo.jpa.ds01.entities.SerialEntity;
import com.farmafene.geronimo.jpa.ds01.entities.SerialEntityPK;
import com.farmafene.geronimo.jpa.ds01.entities.SerialGenEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.TransactionRequiredException;

@Service
public class SequenceGeneratorService implements ISequenceGeneratorService, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(ISequenceGeneratorService.class);
	private static final int MAX_LOCKS = 1;

	@Autowired
	private PlatformTransactionManager ptm;
	@PersistenceContext(unitName = TestConstants.JPA_UNIT_DS01) /* (unitName = "JPA_UNIT_...") */
	private EntityManager entityManager;

	private String[] locks;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		locks = new String[MAX_LOCKS];
		for (int i = 0; i < locks.length; i++) {
			locks[i] = Integer.toString(i);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 *
	 * @see com.farmafene.tomcat9.api.services.ISequenceGeneratorService#generateNextValue(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	@Transactional(noRollbackFor = RuntimeException.class)
	public Future<BigDecimal> generateNextValue(String seqName, String prefix) {
		int index = String.format("{%1$s}{%2$s}", seqName, prefix).hashCode() % MAX_LOCKS;
		if (index < 0) {
			index = -1 * index;
		}
		LOGGER.debug("El valor del lock {}:{} es {}", seqName, prefix, index);
		synchronized (locks[index]) {
			ExecutorService s = Executors.newSingleThreadExecutor();
			BigDecimal[] returnDecimal = new BigDecimal[1];
			Future<BigDecimal> returnValue = null;
			CountDownLatch l = new CountDownLatch(1);
			try {
				returnValue = s.submit( //
						() -> {
							try {
								for (int i = 0; i < 4; i++) {
									SequenceGeneratorService.this.doNextValueWithLock(seqName, prefix, returnDecimal);
									if (null != returnDecimal[0]) {
										break;
									}
								}
								return returnDecimal[0];
							} finally {
								l.countDown();
							}
						} //
				);
			} finally {
				s.shutdownNow();
			}
			try {
				l.await();
			} catch (InterruptedException e) {
				// do nothing
			}
			SerialGenEntity sge = new SerialGenEntity();
			sge.setPrefix(prefix);
			sge.setSecuencia(seqName);
			sge.setValor(returnDecimal[0]);
			sge.setTimestamp(new Timestamp(System.currentTimeMillis()));
			sge = entityManager.merge(sge);
			entityManager.remove(sge);
			return returnValue;
		}
	}

	public void doNextValueWithLock(String seqName, String prefix, BigDecimal[] returnDecimal) {
		TransactionTemplate tt = //
				new TransactionTemplate(ptm);
		tt.setName("ISequenceGeneratorService.doNextValueWithLock");
		tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		tt.executeWithoutResult(//
				(status) -> {
					Exception findException = null;
					BigDecimal returnValue = null;
					try {
						LOGGER.debug("Esperando a la obtención del lock");
						SerialEntity ent = entityManager.find(SerialEntity.class, new SerialEntityPK(seqName, prefix),
								LockModeType.PESSIMISTIC_WRITE);
						LOGGER.debug("Hemos obtenido el lock!!!");
						if (null == ent) {
							ent = new SerialEntity();
							ent.setPrefix(prefix);
							ent.setSecuencia(seqName);
							ent.setValor(BigDecimal.ZERO);
							LOGGER.debug("El Generado es:  {}", ent);
							entityManager.persist(ent);
							entityManager.flush();
						} else {
							LOGGER.debug("Obtenido es:     {}", ent);
							ent.setValor(ent.getValor().add(BigDecimal.ONE));
							LOGGER.debug("Incrementado es: {}", ent);
							SerialGenEntity sge = new SerialGenEntity();
							sge.setPrefix(ent.getPrefix());
							sge.setSecuencia(ent.getSecuencia());
							sge.setValor(ent.getValor());
							sge.setTimestamp(new Timestamp(System.currentTimeMillis()));
							LOGGER.debug("El Generado es:  {}", sge);
							entityManager.persist(ent);
							entityManager.persist(sge);
							entityManager.flush();
							returnValue = ent.getValor();
						}
						returnDecimal[0] = returnValue;
					} catch (IllegalArgumentException e) {
						findException = e;
					} catch (TransactionRequiredException e) {
						findException = e;
					} catch (OptimisticLockException e) {
						findException = e;
					} catch (PessimisticLockException e) {
						findException = e;
					} catch (LockTimeoutException e) {
						findException = e;
					} catch (PersistenceException e) {
						findException = e;
					} catch (Exception e) {
						findException = e;
					}
					if (null != findException) {
						LOGGER.error("Error en el proceso !({},{})", seqName, prefix, findException);
					}
				} //
		);
	}
}
