package com.farmafene.geronimo.jms;

class MsgRequest01 implements ITestMessage {
	private String dato01;
	private long dato02;

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" [");
		sb.append("dato01=").append(dato01);
		sb.append(", dato02=").append(dato02);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return the dato01
	 */
	public String getDato01() {
		return dato01;
	}

	/**
	 * @param dato01 the dato01 to set
	 */
	public void setDato01(String dato01) {
		this.dato01 = dato01;
	}

	/**
	 * @return the dato02
	 */
	public long getDato02() {
		return dato02;
	}

	/**
	 * @param dato02 the dato02 to set
	 */
	public void setDato02(long dato02) {
		this.dato02 = dato02;
	}
}
