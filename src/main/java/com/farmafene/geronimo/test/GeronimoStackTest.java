package com.farmafene.geronimo.test;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.farmafene.geronimo.GeronimoTXBeans;

@Documented
@Inherited
@EnableTransactionManagement
@Import(value = GeronimoTXBeans.class)
@ActiveProfiles("test")
public @interface GeronimoStackTest {
}
