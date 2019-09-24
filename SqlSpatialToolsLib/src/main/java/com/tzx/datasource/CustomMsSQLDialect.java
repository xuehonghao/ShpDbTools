package com.tzx.datasource;

import java.sql.Types;
import java.util.Map;

import org.geotools.data.sqlserver.SQLServerDialect;
import org.geotools.jdbc.JDBCDataStore;

public class CustomMsSQLDialect extends SQLServerDialect {
	
	public CustomMsSQLDialect(JDBCDataStore dataStore) {
		super(dataStore);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerSqlTypeToClassMappings(Map<Integer, Class<?>> mappings) {
		super.registerSqlTypeToClassMappings(mappings);
		mappings.put(new Integer(Types.LONGVARBINARY), byte[].class);
	}

}
