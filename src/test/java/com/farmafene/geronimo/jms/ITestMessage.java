package com.farmafene.geronimo.jms;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes(//
		value = { //
				@JsonSubTypes.Type(value = MsgRequest01.class, name = "MsgRequest01Msg"), //
				@JsonSubTypes.Type(value = MsgRequest02.class, name = "MsgRequest02Msg"), //
				@JsonSubTypes.Type(value = EndTestMsg.class, name = "EndTestMsg") //
		}//
)

public interface ITestMessage {
}
