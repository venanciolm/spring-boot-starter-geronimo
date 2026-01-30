package com.farmafene.geronimo.jpa.ds01.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

//CREATE TABLE SERIALGEN_SG (
//		  SER_SEQ	VARCHAR(255),
//		  SER_PREFIX VARCHAR(255),
//		  SER_VALOR	DECIMAL(9,0),
//        SG_TIMESTAMP TIMESTAMP(6) DEFAULT NOW()
//		);
@SuppressWarnings("serial")
@Entity
@Table(name = "SERIALGEN_SG")
@IdClass(SerialGenEntityPK.class)
public class SerialGenEntity implements Serializable {

	@Id
	@Column(name = "SER_SEQ", length = 255)
	private String secuencia;
	@Id
	@Column(name = "SER_PREFIX", length = 255)
	private String prefix;
	@Id
	@Column(name = "SER_VALOR", length = 9, precision = 0)
	private BigDecimal valor;

	@Column(name = "SG_TIMESTAMP")
	private Timestamp timestamp;

	public SerialGenEntity() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("={");
		sb.append("seq=").append(secuencia);
		sb.append(", prefix=").append(prefix);
		sb.append(", valor=").append(valor);
		sb.append(", timestamp=").append(timestamp);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @return the secuencia
	 */
	public String getSecuencia() {
		return secuencia;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return the valor
	 */
	public BigDecimal getValor() {
		return valor;
	}

	/**
	 * @param secuencia the secuencia to set
	 */
	public void setSecuencia(String secuencia) {
		this.secuencia = secuencia;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
