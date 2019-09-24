package com.tzx.datasource;

import java.sql.Types;
import java.util.Map;

import org.geotools.data.mysql.MySQLDialectPrepared;
import org.geotools.jdbc.JDBCDataStore;


public class CustomMySQLDialect extends MySQLDialectPrepared {

	public CustomMySQLDialect(JDBCDataStore dataStore) {
		super(dataStore);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerSqlTypeToSqlTypeNameOverrides(Map<Integer, String> overrides) {
		// overrides.put(Types.BOOLEAN, "BOOL");
		// overrides.put(Types.VARCHAR, "varchar");
		overrides.put(Types.BLOB, "blob");
//        overrides.put(Types.BLOB, "varbinary(9999)");
	}

	@Override
	public void registerSqlTypeToClassMappings(Map<Integer, Class<?>> mappings) {
		super.registerSqlTypeToClassMappings(mappings);
		mappings.put(new Integer(Types.LONGVARBINARY), byte[].class);
	}

}
