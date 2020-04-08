package com.cw.na.codegen;

import java.util.HashMap;
import java.util.Map;

public class mMaps {

	public static HashMap< String,String> javaToSqlGetterDtMap =  new HashMap< String,String>();
	public static HashMap< String,String> JavaToSqlSetterDtMap =  new HashMap< String,String>(); 
	public static HashMap< String,String> XmiToSqlDTMap =  new HashMap< String,String>();
	public static HashMap< String,String> XmiToJavaDTMap =  new HashMap< String,String>();
	
	
	public static  void setMapValues() {
		javaToSqlGetterDtMap.put("String","getString");
		javaToSqlGetterDtMap.put("string","getString");
		javaToSqlGetterDtMap.put("boolean","getBoolean");
		javaToSqlGetterDtMap.put("java.math.BigDecimal","getBigDecimal");
		javaToSqlGetterDtMap.put("byte","getByte");
		javaToSqlGetterDtMap.put("short","getShort");
		javaToSqlGetterDtMap.put("int","getInt");
		javaToSqlGetterDtMap.put("long","getLong");
		javaToSqlGetterDtMap.put("float","getFloat");
		javaToSqlGetterDtMap.put("Float","getFloat");
		javaToSqlGetterDtMap.put("double","getDouble");
		javaToSqlGetterDtMap.put("Double","getDouble");
		javaToSqlGetterDtMap.put("byte[ ]","getBytes");
		javaToSqlGetterDtMap.put("byte[ ]","getBytes");
		javaToSqlGetterDtMap.put("java.sql.Date","getDate");
		javaToSqlGetterDtMap.put("Date","getDate");
		javaToSqlGetterDtMap.put("java.sql.Time","getTime");
		javaToSqlGetterDtMap.put("java.sql.Timestamp","getTimestamp");
		javaToSqlGetterDtMap.put("java.sql.Clob","getClob");
		javaToSqlGetterDtMap.put("java.sql.Blob","getBlob");
		javaToSqlGetterDtMap.put("java.sql.Array","getARRAY");
		javaToSqlGetterDtMap.put("SCO","getInt");
		javaToSqlGetterDtMap.put("DateTime","getTimestamp");
		javaToSqlGetterDtMap.put("Timestamp","getTimestamp");
		javaToSqlGetterDtMap.put("Amount","getDouble");
		javaToSqlGetterDtMap.put("LocalDate","getDate");
		javaToSqlGetterDtMap.put("LocalDateTime","getTimestamp");
		
		JavaToSqlSetterDtMap.put("String","setString");
		JavaToSqlSetterDtMap.put("string","setString");
		JavaToSqlSetterDtMap.put("boolean","setBoolean");
		JavaToSqlSetterDtMap.put("java.math.BigDecimal","setBigDecimal");
		JavaToSqlSetterDtMap.put("byte","setByte");
		JavaToSqlSetterDtMap.put("short","setShort");
		JavaToSqlSetterDtMap.put("int","setInt");
		JavaToSqlSetterDtMap.put("long","setLong");
		JavaToSqlSetterDtMap.put("float","setFloat");
		JavaToSqlSetterDtMap.put("Float","setFloat");
		JavaToSqlSetterDtMap.put("double","setDouble");
		JavaToSqlSetterDtMap.put("Double","setDouble");
		JavaToSqlSetterDtMap.put("byte[ ]","setBytes");
		JavaToSqlSetterDtMap.put("byte[ ]","setBytes");
		JavaToSqlSetterDtMap.put("java.sql.Date","setDate");
		JavaToSqlSetterDtMap.put("Date","setDate");
		JavaToSqlSetterDtMap.put("java.sql.Time","setTime");
		JavaToSqlSetterDtMap.put("java.sql.Timestamp","setTimestamp");
		JavaToSqlSetterDtMap.put("DateTime","setTimestamp");
		JavaToSqlSetterDtMap.put("Timestamp","setTimestamp");
		JavaToSqlSetterDtMap.put("java.sql.Clob","setClob");
		JavaToSqlSetterDtMap.put("java.sql.Blob","setBlob");
		JavaToSqlSetterDtMap.put("java.sql.Array","setARRAY");
		JavaToSqlSetterDtMap.put("SCO","setInt");
		JavaToSqlSetterDtMap.put("Amount","setDouble");
		JavaToSqlSetterDtMap.put("LocalDate","setDate");
		JavaToSqlSetterDtMap.put("LocalDateTime","setTimestamp");
		

		XmiToSqlDTMap.put("boolean","BIT");
		XmiToSqlDTMap.put("Boolean","BIT");
		XmiToSqlDTMap.put("byte","TINYINT");
		XmiToSqlDTMap.put("short","SMALLINT");
		XmiToSqlDTMap.put("int","INTEGER");
		XmiToSqlDTMap.put("Integer","INTEGER");
		XmiToSqlDTMap.put("Int","INTEGER");
		XmiToSqlDTMap.put("long","BIGINT");
		XmiToSqlDTMap.put("float","FLOAT");
		XmiToSqlDTMap.put("Float","FLOAT");
		XmiToSqlDTMap.put("Double","FLOAT");
		XmiToSqlDTMap.put("double","FLOAT");
		XmiToSqlDTMap.put("java.sql.Date","DATE");
		XmiToSqlDTMap.put("Date","DATE");
		XmiToSqlDTMap.put("LocalDate","DATE");
		XmiToSqlDTMap.put("java.sql.Time","TIME");
		XmiToSqlDTMap.put("java.sql.Timestamp","TIMESTAMP");
		XmiToSqlDTMap.put("LocalDateTime","DATETIME");
		XmiToSqlDTMap.put("Datetime","DATETIME");
		XmiToSqlDTMap.put("Timestamp","TIMESTAMP");
		XmiToSqlDTMap.put("String","VARCHAR");
		XmiToSqlDTMap.put("string","VARCHAR");
		XmiToSqlDTMap.put("java.sql.Blob","BLOB");
		XmiToSqlDTMap.put("SCO","SMALLINT");
		XmiToSqlDTMap.put("Amount","Numeric(16,3)");
		
		XmiToJavaDTMap.put("Integer", "int");
		XmiToJavaDTMap.put("integer", "int");
		XmiToJavaDTMap.put("int", "int");
		XmiToJavaDTMap.put("Float", "float");
		XmiToJavaDTMap.put("float", "float");
		XmiToJavaDTMap.put("mAttribute","mAttribute[]");
		XmiToJavaDTMap.put("mOperation","mOperation[]");
		XmiToJavaDTMap.put("Double", "double");
		XmiToJavaDTMap.put("double", "double");
		XmiToJavaDTMap.put("Boolean", "boolean");
		XmiToJavaDTMap.put("boolean", "boolean");
		XmiToJavaDTMap.put("String", "String");
		XmiToJavaDTMap.put("string", "String");
		XmiToJavaDTMap.put("Date", "LocalDate");
		XmiToJavaDTMap.put("DateTime", "LocalDateTime");
		XmiToJavaDTMap.put("TimeStamp", "LocalDateTime");
		XmiToJavaDTMap.put("SCO","int");
		XmiToJavaDTMap.put("Amount","double");
		
		
		

	}
}
	