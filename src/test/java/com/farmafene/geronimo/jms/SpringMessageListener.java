package com.farmafene.geronimo.jms;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;

import com.farmafene.commons.tx.XAHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.transaction.Transactional;

public class SpringMessageListener implements MessageListener {

	private static final Logger LOG = LoggerFactory.getLogger(SpringMessageListener.class);
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	/**
	 * 
	 * @see jakarta.jms.MessageListener#onMessage(jakarta.jms.Message)
	 */
	@Override
	@Transactional(rollbackOn = Throwable.class)
	public void onMessage(Message message) {
		org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(//
				new TransactionSynchronization() {

					/**
					 * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
					 */
					@Override
					public void beforeCommit(boolean readOnly) {
						dolog("beforeCommit", readOnly ? "RO" : "RW");
					}

					/**
					 * @see org.springframework.transaction.support.TransactionSynchronization#beforeCompletion()
					 */
					@Override
					public void beforeCompletion() {
						dolog("beforeCompletion", null);
					}

					/**
					 * @see org.springframework.transaction.support.TransactionSynchronization#afterCommit()
					 */
					@Override
					public void afterCommit() {
						dolog("afterCommit", null);
					}

					/**
					 * @see org.springframework.transaction.support.TransactionSynchronization#afterCompletion(int)
					 */
					@Override
					public void afterCompletion(int status) {
						dolog("afterCompletion", XAHelper.getStringFromStatus(status));
					}

					private void dolog(String method, String value) {
						StringBuilder sb = new StringBuilder();
						String lf = System.lineSeparator();
						sb.append(lf).append("/+---------------------------------------+");
						sb.append(lf).append(" | ").append(SpringMessageListener.this);
						sb.append(lf).append(" | .").append(method).append("(").append(value == null ? "" : value)
								.append(")");
						sb.append(lf).append(" +---------------------------------------+");
						LOG.info("{}", sb);
					}
				} //
		);
		if (TextMessage.class.isAssignableFrom(message.getClass())) {
			TextMessage msg = (TextMessage) message;
			try {
				StringBuilder sb = new StringBuilder();
				String lf = System.lineSeparator();
				sb.append(lf).append("/+---------------------------------------+");
				sb.append(lf).append(" | Consumer:");
				sb.append(lf).append(" +---------------------------------------+");
				sb.append(lf).append(" | Msg:   ").append(msg.getText());
				sb.append(lf).append(" +---------------------------------------+");
				LOG.info("{}", sb);
				ITestMessage oMsg = new ObjectMapper().reader().readValue(msg.getText(), ITestMessage.class);
				if (MsgRequest01.class.isAssignableFrom(oMsg.getClass())) {
					process((MsgRequest01) oMsg);
				}
				if (MsgRequest02.class.isAssignableFrom(oMsg.getClass())) {
					process((MsgRequest02) oMsg);
				}
				if (EndTestMsg.class.isAssignableFrom(oMsg.getClass())) {
					process((EndTestMsg) oMsg);
				}
			} catch (JMSException e) {
				throw new UnsupportedOperationException("Mensaje invalido", e);
			} catch (Exception e) {
				throw new UnsupportedOperationException("Mensaje invalido", e);
			}
		} else {
			throw new UnsupportedOperationException("Mensaje invalido");
		}
	}

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

	private void process(EndTestMsg oMsg) {
		countDownLatch.countDown();
	}

	private void process(MsgRequest02 oMsg) {
		// TODO Auto-generated method stub

	}

	private void process(MsgRequest01 oMsg) {
		// TODO Auto-generated method stub

	}
}
