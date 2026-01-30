package com.farmafene.geronimo.jms;

public class MsgRequest02 implements ITestMessage {

	private String dato04;
	private long dato03;

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" [");
		sb.append("dato03=").append(dato03);
		sb.append(", dato04=").append(dato04);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return the dato04
	 */
	public String getDato04() {
		return dato04;
	}

	/**
	 * @param dato04 the dato04 to set
	 */
	public void setDato04(String dato04) {
		this.dato04 = dato04;
	}

	/**
	 * @return the dato03
	 */
	public long getDato03() {
		return dato03;
	}

	/**
	 * @param dato03 the dato03 to set
	 */
	public void setDato03(long dato03) {
		this.dato03 = dato03;
	}

}
