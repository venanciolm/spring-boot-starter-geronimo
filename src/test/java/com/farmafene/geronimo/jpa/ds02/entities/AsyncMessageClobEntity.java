package com.farmafene.geronimo.jpa.ds02.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "ASYNCMSGCLOB_AMC")
public class AsyncMessageClobEntity implements Serializable {

//	CREATE TABLE ASYNCMSGCLOB_AMC(
//	AM_ID CHAR(18),
//	AMC_MSG  CLOB
//	);
//	ALTER TABLE ASYNCMSGCLOB_AMC ADD CONSTRAINT "PK_ASYNCMSGCLOB_AMC" PRIMARY KEY (AM_ID);
	@Id
	@Column(name = "AM_ID")
	private String id;

	@Column(name = "AMC_MSG")
	private String data;
}
