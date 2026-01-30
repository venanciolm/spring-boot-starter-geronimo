package com.farmafene.geronimo.cfg;

public class TestConstants {

	public static final String TEST_QUEUE = "test.queue";
	public static final String BROKER_URL = "vm://0";

	public static final String TEST_JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
	public static final String TESTDB_DIALECT = "org.hibernate.dialect.HSQLDialect";

	public static final String DS01 = "DS01";
	public static final String TESTDB_DS01_URL = "jdbc:hsqldb:mem:target/testdb_01";
	public static final String JPA_UNIT_DS01 = "JPA_UNIT_" + DS01;
	public static final String JPA_UNIT_DS01_PUM = JPA_UNIT_DS01 + "_PUM";
	public static final String JPA_UNIT_DS01_PACKAGES = "com.farmafene.geronimo.jpa.ds01";

	public static final String DS02 = "DS02";
	public static final String TESTDB_DS02_URL = "jdbc:hsqldb:mem:target/testdb_02";
	public static final String JPA_UNIT_DS02 = "JPA_UNIT_" + DS02;
	public static final String JPA_UNIT_DS02_PUM = JPA_UNIT_DS02 + "_PUM";
	public static final String JPA_UNIT_DS02_PACKAGES = "com.farmafene.geronimo.jpa.ds02";
	private TestConstants() {
	}
}
