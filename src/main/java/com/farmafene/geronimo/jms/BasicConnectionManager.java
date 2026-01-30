package com.farmafene.geronimo.jms;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEvent;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.LocalTransactionException;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;

@SuppressWarnings("serial")
public class BasicConnectionManager implements ConnectionManager, ConnectionEventListener {

	private static final Logger log = LoggerFactory.getLogger(BasicConnectionManager.class);
	Set<?> connectionSet = new HashSet<>();
	private TransactionManager txManager;

	public BasicConnectionManager(TransactionManager txManager) {
		this.txManager = txManager;
	}

	/**
	 * 
	 * @param event
	 */
	@Override
	public void connectionErrorOccurred(ConnectionEvent event) {
		log.trace("connectionErrorOccurred(ConnectionEvent {}", event);
	}

	/**
	 * 
	 * @see javax.resource.spi.ConnectionManager#allocateConnection(javax.resource.spi.ManagedConnectionFactory,
	 *      javax.resource.spi.ConnectionRequestInfo)
	 */
	@Override
	public Object allocateConnection(ManagedConnectionFactory mcf, ConnectionRequestInfo cxRequestInfo)
			throws ResourceException {
		log.trace("allocateConnection(ManagedConnectionFactory {}, ConnectionRequestInfo {})", mcf, cxRequestInfo);
		ManagedConnection object = (ManagedConnection) mcf.createManagedConnection(null, cxRequestInfo);
		log.trace("La conexión es: {}", object);
		LocalTransaction localTransaction = object.getLocalTransaction();
		log.trace("La Local tx es: {}", localTransaction);
		XAResource xares = new XAResource() {
			private Xid xid;
			private int transactionTimeout;

			// Implementation of javax.transaction.xa.XAResource

			public void commit(Xid xid, boolean flag) throws XAException {
				if (this.xid == null || !this.xid.equals(xid)) {
					throw new XAException("Invalid Xid");
				}
				try {
					localTransaction.commit();
				} catch (ResourceException e) {
					throw (XAException) new XAException().initCause(e);
				} finally {
					this.xid = null;
				}

			}

			public void forget(Xid xid) throws XAException {
				this.xid = null;
			}

			public int getTransactionTimeout() throws XAException {
				return transactionTimeout;
			}

			public boolean isSameRM(XAResource xares) throws XAException {
				return this == xares;
			}

			public Xid[] recover(int n) throws XAException {
				return new Xid[0];
			}

			public void rollback(Xid xid) throws XAException {
				if (this.xid == null || !this.xid.equals(xid)) {
					throw new XAException("Invalid Xid");
				}
				try {
					localTransaction.rollback();
				} catch (ResourceException e) {
					throw (XAException) new XAException().initCause(e);
				} finally {
					this.xid = null;
				}
			}

			public boolean setTransactionTimeout(int txTimeout) throws XAException {
				this.transactionTimeout = txTimeout;
				return true;
			}

			public void start(Xid xid, int flag) throws XAException {
				if (flag == XAResource.TMNOFLAGS) {
					// first time in this transaction
					if (this.xid != null) {
						throw new XAException("already enlisted");
					}
					this.xid = xid;
					try {
						localTransaction.begin();
					} catch (ResourceException e) {
						throw (XAException) new XAException("could not start local tx").initCause(e);
					}
				} else if (flag == XAResource.TMRESUME) {
					if (xid != this.xid) {
						throw new XAException("attempting to resume in different transaction");
					}
				} else {
					throw new XAException("unknown state");
				}
			}

			public void end(Xid xid, int flag) throws XAException {
				if (xid != this.xid) {
					throw new XAException("Invalid Xid");
				}
				// we could keep track of if the flag is TMSUCCESS...
			}

			public int prepare(Xid xid) throws XAException {
				// log warning that semantics are incorrect...
				return XAResource.XA_OK;
			}
		};
		log.trace("La XAres es: {}", localTransaction);
		try {
			txManager.getTransaction().enlistResource(xares);
		} catch (IllegalStateException e) {
			log.trace("", e);
			throw new jakarta.resource.spi.IllegalStateException(e);
		} catch (RollbackException e) {
			log.trace("", e);
			throw new LocalTransactionException(e);
		} catch (SystemException e) {
			log.trace("", e);
			throw new LocalTransactionException(e);
		}
		return object.getConnection(null, cxRequestInfo);
	}

	/**
	 * 
	 * @see javax.resource.spi.ConnectionEventListener#connectionClosed(javax.resource.spi.ConnectionEvent)
	 */
	@Override
	public void connectionClosed(ConnectionEvent event) {
		log.trace("connectionClosed(ConnectionEvent  {})", event);
		try {
			((ManagedConnection) event.getConnectionHandle()).cleanup();
		} catch (ResourceException e) {
			log.warn("connectionClosed(ConnectionEvent  {})", event, e);
		}
		try {
			((ManagedConnection) event.getConnectionHandle()).destroy();
		} catch (ResourceException e) {
			log.warn("connectionClosed(ConnectionEvent  {})", event, e);
		}
	}

	/**
	 * 
	 * @see javax.resource.spi.ConnectionEventListener#localTransactionStarted(javax.resource.spi.ConnectionEvent)
	 */
	@Override
	public void localTransactionStarted(ConnectionEvent event) {
		log.trace("localTransactionStarted(ConnectionEvent {})", event);
	}

	/**
	 * 
	 * @see javax.resource.spi.ConnectionEventListener#localTransactionCommitted(javax.resource.spi.ConnectionEvent)
	 */
	@Override
	public void localTransactionCommitted(ConnectionEvent event) {
		log.trace("localTransactionCommitted(ConnectionEvent {})", event);
	}

	/**
	 * 
	 * @see javax.resource.spi.ConnectionEventListener#localTransactionRolledback(javax.resource.spi.ConnectionEvent)
	 */
	@Override
	public void localTransactionRolledback(ConnectionEvent event) {
		log.trace("localTransactionRolledback(ConnectionEvent {})", event);
	}

	/**
	 * @return the txManager
	 */
	public TransactionManager getTxManager() {
		return txManager;
	}
}
