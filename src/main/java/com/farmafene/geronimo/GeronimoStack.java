package com.farmafene.geronimo;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;

import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Documented
@Inherited
@EnableTransactionManagement
@Import(value = GeronimoTXBeans.class)
public @interface GeronimoStack {
}
