package com.tzx.datasource;

import java.sql.Types;
import java.util.Map;

import org.geotools.data.oracle.OracleDialect;
import org.geotools.jdbc.JDBCDataStore;


public class CustomOracleDialect extends OracleDialect {

	public CustomOracleDialect(JDBCDataStore dataStore) {
		super(dataStore);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerSqlTypeToSqlTypeNameOverrides(Map<Integer, String> overrides) {
		super.registerSqlTypeToSqlTypeNameOverrides(overrides);
        overrides.put(Types.REAL, "DOUBLE PRECISION");
        overrides.put(Types.DOUBLE, "DOUBLE PRECISION");
        overrides.put(Types.FLOAT, "FLOAT");
        // starting with Oracle 11 + recent JDBC drivers the DATE type does not have a mapping
        // anymore in the JDBC driver, manually register it instead
        overrides.put(Types.DATE, "DATE");
        // overriding default java.sql.Timestamp to Oracle DATE mapping
        overrides.put(Types.TIMESTAMP, "TIMESTAMP");
	}

	@Override
	public void registerSqlTypeToClassMappings(Map<Integer, Class<?>> mappings) {
		super.registerSqlTypeToClassMappings(mappings);
		mappings.put(new Integer(Types.LONGVARBINARY), byte[].class);
	}

}
