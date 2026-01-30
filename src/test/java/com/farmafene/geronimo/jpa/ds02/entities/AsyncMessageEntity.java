package com.farmafene.geronimo.jpa.ds02.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import com.farmafene.geronimo.jpa.SequenceMsg;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@Entity
@Table(name = "ASYNCMSG_AM")
public class AsyncMessageEntity implements Serializable {

//	CREATE TABLE ASYNCMSG_AM(
//	AM_ID CHAR(18),
//	AM_CLASS VARCHAR2(128),
//	AM_FECHA TIMESTAMP(6),
//	AM_FACK  TIMESTAMP(6)
//	);
//	ALTER TABLE ASYNCMSG_AM ADD CONSTRAINT "PK_ASYNCMSG_AM" PRIMARY KEY (AM_ID);

	
	@Id
	@SequenceMsg
	@Column(name = "AM_ID")
	private String id;
	@Column(name = "AM_CLASS")
	private String clase;
	@Column(name = "AM_FECHA")
	private Timestamp fechaCreacion;
	@Column(name = "AM_FACK")
	private Timestamp fechaACK;
	@OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumns(value = { @JoinColumn(name = "AM_ID", referencedColumnName = "AM_ID") })
	private AsyncMessageClobEntity data;
}