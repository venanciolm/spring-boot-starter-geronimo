package com.farmafene.geronimo.jms;

public class EndTestMsg implements ITestMessage {

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" [");
		sb.append("]");
		return sb.toString();
	}
}
