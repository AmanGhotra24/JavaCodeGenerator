package com.cw.na.codegen;

import java.util.HashMap;

public class mCreateDMFunctions {
	//Below functions are for Get function.....
	public static String createDMGetStmt(mClass clsObj) {

		String getText = "";
		String attrString = "";
		String retAttrString = "\t";
		String pkDataType  = "";
		String tableName = "";
		String scoCol = ""; 
		String scoXmi = ""; 
		String separator = "";
		String columnName = "";
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		int counter = 0;

		//iteration on attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);
			String attrName  = itrOBJ.attrName;
			String vStr  = null;
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;
			//only persistable columns will be used to make strings.....
			if(persistFlag) {

				//if DBname is given in the XMI then Use it....else attribute name will be used...

				if(mClass.isKeyPresent(taggedValuesMap, "DBName")) {
					columnName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
				}else {
					columnName =  attrName.toString();
				}

				if(itrAttr == 0) {
					separator = " ";
				}else {
					separator = ", ";
				}

				attrString = attrString + separator + columnName;

				//if SCO type column is present in the class..then its _cd column needs to be created....
				if(mClass.isKeyPresent(taggedValuesMap, "SCOCD")) {
					scoCol = itrOBJ.attrName;
					scoXmi = itrOBJ.xmi_id;
					retAttrString = retAttrString + "inputObj."+scoCol+"_cd = MyConstants."+App.taggedValues.get(scoXmi).get("SCOCD")+";\n\t\t\t";
				}

				//Storing the Datatype of the PK column....
				if(attrName.equalsIgnoreCase(clsObj.pk)) {
					pkDataType = itrOBJ.attrDataType.toString();
				}
				String dType = "";
				if(!itrOBJ.attrDataType.contains("[]"))

				{
					dType = mMaps.javaToSqlGetterDtMap.get(itrOBJ.attrDataType);

					//Setting Return string ....parsing and default conditions are made based on datatype of the column... Special cases : Date and Datetime....
					if(dType.equalsIgnoreCase("getDate")) {
						retAttrString = retAttrString + "tempDate = resultSet."+dType+"(colIndex++).toLocalDate();\n\t\t\t";
						retAttrString = retAttrString+ "if(tempDate.isEqual(DEFDATE))\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = null;\n\t\t\t}else\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = tempDate;\n\t\t\t}\n\t\t\t";
					}else if(dType.equalsIgnoreCase("getTimestamp")){
						retAttrString = retAttrString + "tempDateTime =  resultSet."+dType+"(colIndex++).toLocalDateTime();\n\t\t\t";
						retAttrString = retAttrString+ "if(tempDateTime.isEqual(DEFDATETIME))\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = null;\n\t\t\t}else\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = tempDateTime;\n\t\t\t}\n\t\t\t";
					}
					else {
						retAttrString = retAttrString + "inputObj."+ attrName+ " = resultSet."+dType+"(colIndex++);\n\t\t\t";	
					}
				}
				counter = counter+1;
			}
		}

		String isTBName = null;
		if(App.taggedValues.get(clsObj.xmi_id)!= null)
			isTBName = App.taggedValues.get(clsObj.xmi_id).get("DBName");

		//Setting value to table name..
		if(isTBName==null)
			tableName = clsObj.className;
		else 
			tableName = isTBName;

		//Geting Java datatype from the corresponding sql datatype of the PK column....
		String dType1 = mMaps.JavaToSqlSetterDtMap.get(pkDataType);

		//Setting the created dynamic strings into the final class text....
		getText = getText + "//DMGet function for "+ clsObj.className+ " .....\n\t";
		getText = getText + clsObj.accessSpecifier + " static " + clsObj.className + "DO dmGet"+ "("+clsObj.className+"DO inputObj) "
				+ "throws BusException {\n\t";
		getText = getText + pkDataType +" "+clsObj.pk + " = inputObj."+clsObj.pk+
				";\n\tLocalDate tempDate;\n\tLocalDateTime tempDateTime;\n\tint nor = 0;\n\t" + 
				"ResultSet resultSet = null;\n\t\t\tConnection con=null;\n\t\t\t"
				+ "try {\n\t\t//SQL Connection ....\n\t\t" + 
				" con = InfraObject.getConnection();\n\t\tString sql = " +
				"\"select "+ attrString+" from "+ tableName+ " where "+ clsObj.pk +" = ? \";\n\t\t//Prepared Statement.....\n\t\t" +
				"PreparedStatement getStatement = con.prepareStatement(sql);\n\t\t"+
				"getStatement."+dType1+"(1, "+ clsObj.pk +" );\n\t\t//Result Set obj.....\n\t\t" +
				" resultSet = getStatement.executeQuery();\n\t\t" +
				"if (resultSet == null)"+
				"\n\t\t\tnor = 0;\n\t\t"+
				"else if(resultSet.next()) {\n\t\t\t"+
				"nor++;\n\t\t\tint colIndex = 1;\n\t\t"+ retAttrString +
				"//Returning the resultset....\n\t\t\t" + 
				"return inputObj;\n\t\t}\n\t\t"+

				"if ( nor == 0 ){\n\t\t" + 
				"throw new BusException (\"ELR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\" );\n\t\t}\n\t\n\t" + 
				"}catch(BusException busException) {\n\t\t"  + 
				"throw busException;\n\t"  + 
				"}"
				+ "catch(SQLException sqlException) {\n\t\t" + 
				"throw new BusException (\"EFR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", sqlException );\n\t" + 
				"}catch(Exception exp ) {\n\t\t" +
				"throw new BusException (\"EFR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", exp );\n\t" + 
				"\n\t" +
				"}\n\t\tfinally\n\t\t\t{\n\t\t\t\t"	+
				"if (resultSet != null)\n\t\t{\n\t\t\ttry {\n\t\t\tresultSet.close();\n\t\t\t}\n\t\t\tcatch (Exception e1 )  "
				+ "{}\n\t\t}\n\t" +"\n\tInfraObject.releaseConnection();\n\t"+
				"}\n\t\t"	+			

				"return null;\r\n\t}" ;
		return getText;

	}
	//Below functions are for Get function.....
	public static String createDMGetHistStmt(mClass clsObj) {

		String getText = "";
		String attrString = "";
		String retAttrString = "\t";
		String pkDataType  = "";
		String tableName = "";
		String scoCol = ""; 
		String scoXmi = ""; 
		String separator = "";
		String columnName = "";
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		int counter = 0;

		//iteration on attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);
			String attrName  = itrOBJ.attrName;
			String vStr  = null;
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;
			//only persistable columns will be used to make strings.....
			if(persistFlag) {

				//if DBname is given in the XMI then Use it....else attribute name will be used...

				if(mClass.isKeyPresent(taggedValuesMap, "DBName")) {
					columnName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
				}else {
					columnName =  attrName.toString();
				}

				if(itrAttr == 0) {
					separator = " ";
				}else {
					separator = ", ";
				}

				attrString = attrString + separator + columnName;

				//if SCO type column is present in the class..then its _cd column needs to be created....
				if(mClass.isKeyPresent(taggedValuesMap, "SCOCD")) {
					scoCol = itrOBJ.attrName;
					scoXmi = itrOBJ.xmi_id;
					retAttrString = retAttrString + "inputObj."+scoCol+"_cd = MyConstants."+App.taggedValues.get(scoXmi).get("SCOCD")+";\n\t\t\t";
				}

				//Storing the Datatype of the PK column....
				if(attrName.equalsIgnoreCase(clsObj.pk)) {
					pkDataType = itrOBJ.attrDataType.toString();
				}
				String dType = "";
				if(!itrOBJ.attrDataType.contains("[]"))

				{
					dType = mMaps.javaToSqlGetterDtMap.get(itrOBJ.attrDataType);

					//Setting Return string ....parsing and default conditions are made based on datatype of the column... Special cases : Date and Datetime....
					if(dType.equalsIgnoreCase("getDate")) {
						retAttrString = retAttrString + "tempDate = resultSet."+dType+"(colIndex++).toLocalDate();\n\t\t\t";
						retAttrString = retAttrString+ "if(tempDate.isEqual(DEFDATE))\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = null;\n\t\t\t}else\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = tempDate;\n\t\t\t}\n\t\t\t";
					}else if(dType.equalsIgnoreCase("getTimestamp")){
						retAttrString = retAttrString + "tempDateTime =  resultSet."+dType+"(colIndex++).toLocalDateTime();\n\t\t\t";
						retAttrString = retAttrString+ "if(tempDateTime.isEqual(DEFDATETIME))\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = null;\n\t\t\t}else\n\t\t\t{\n\t\t\t";
						retAttrString = retAttrString + "inputObj."+ attrName+ " = tempDateTime;\n\t\t\t}\n\t\t\t";
					}
					else {
						retAttrString = retAttrString + "inputObj."+ attrName+ " = resultSet."+dType+"(colIndex++);\n\t\t\t";	
					}
				}
				counter = counter+1;
			}
		}

		String isTBName = null;
		if(App.taggedValues.get(clsObj.xmi_id)!= null)
			isTBName = App.taggedValues.get(clsObj.xmi_id).get("DBName");

		//Setting value to table name..
		if(isTBName==null)
			tableName = clsObj.className;
		else 
			tableName = isTBName;

		//Geting Java datatype from the corresponding sql datatype of the PK column....
		String dType1 = mMaps.JavaToSqlSetterDtMap.get(pkDataType);

		//Setting the created dynamic strings into the final class text....
		getText = getText + "//DMGet function for "+ clsObj.className+ " .....\n\t";
		getText = getText + clsObj.accessSpecifier + " static " + clsObj.className + "DO dmGetHist"+ "("+clsObj.className+"DO inputObj) "
				+ "throws BusException {\n\t";
		getText = getText + pkDataType +" "+clsObj.pk + " = inputObj."+clsObj.pk+";\n"+
				"int version1 = inputObj.version1;\r\n\t" + 
				"int version2 = inputObj.version2;\n\t"+
				"\n\tLocalDate tempDate;\n\tLocalDateTime tempDateTime;\n\tint nor = 0;\n\t" + 
				"ResultSet resultSet = null;\n\t\t\tConnection con=null;\n\t\t\t"
				+ "try {\n\t\t//SQL Connection ....\n\t\t" + 
				" con = InfraObject.getConnection();\n\t\tString sql = " +
				"\"select "+ attrString+" from "+ tableName+ "_Hist where "+ clsObj.pk +" = ? "
				+ " and version1 = ? and version2 = ? \";\n\t\t//Prepared Statement.....\n\t\t" +
				"PreparedStatement getStatement = con.prepareStatement(sql);\n\t\t"+
				"getStatement."+dType1+"(1, "+ clsObj.pk +" );"
				+ "getStatement.setInt(2, version1 );\r\n" + 
				"getStatement.setInt(3, version2 );"
				+ "\n\t\t//Result Set obj.....\n\t\t" +
				" resultSet = getStatement.executeQuery();\n\t\t" +
				"if (resultSet == null)\n\t\t\t"+
				"nor = 0;\n\t\t"+
				"else if(resultSet.next()) {\n\t\t\t"+
				"nor++;\n\t\t\tint colIndex = 1;\n\t\t"+ retAttrString +
				"//Returning the resultset....\n\t\t\t" + 
				"return inputObj;\n\t\t}\n\t\t"+

					"if ( nor == 0 ){\n\t\t" + 
					"throw new BusException (\"ELR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\" );\n\t\t}\n\t\n\t" + 
					"}catch(BusException busException) {\n\t\t"  + 
					"throw busException;\n\t"  + 
					"}"
					+ "catch(SQLException sqlException) {\n\t\t" + 
					"throw new BusException (\"EFR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", sqlException );\n\t" + 
					"}catch(Exception exp ) {\n\t\t" +
					"throw new BusException (\"EFR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", exp );\n\t" + 
					"\n\t" +
					"}\n\t\tfinally\n\t\t\t{\n\t\t\t\t"	+
					"if (resultSet != null)\n\t\t{\n\t\t\ttry {\n\t\t\tresultSet.close();\n\t\t\t}\n\t\t\tcatch (Exception e1 )  "
					+ "{};\n\t\t}\n\t" +"\n\tInfraObject.releaseConnection();\n\t"+
					"}\n\t\t"	+			

					"return null;\r\n\t}" ;
		return getText;

	}
	//Below function set the content of the Insert Function of the DM Class......
	public static String createDMInsertStmt(mClass clsObj) {

		String insertText = "";
		String insertGetterString = "\t";
		String insertSetterString = "\t";
		String attrString = "";
		String quesString = "";
		String tableName = "";
		int colCounter = 1;
		String pkDataType = "";
		String adt = "";
		boolean scoFlag = false;
		String columnName = "";
		String separator = "";
		String qSep = "";
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		String pk = clsObj.pk;
		boolean globalAutoGenFlag = false;


		//Iterating through the attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);
			String attrName = itrOBJ.attrName;
			//make content only of the persistable columns....
			String vStr  = null;
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;
			if(attrName.equalsIgnoreCase(pk)) {
				pkDataType = itrOBJ.attrDataType;
			}

			if(persistFlag) {
				String aString  = null;
				if(taggedValuesMap!=null)
					aString = taggedValuesMap.get("AutoGen");
				if(aString!=null && aString.equalsIgnoreCase("true"))
				{
					globalAutoGenFlag = true;
					continue;
				}

				//if DBname is given in the XMI then Use it....else attribute name will be used...
				if(mClass.isKeyPresent(taggedValuesMap, "DBName")) {
					columnName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
				}else {
					columnName =  attrName.toString();
				}

				//if SCO type column is present in the class..then its _cd column needs to be created....
				if(mClass.isKeyPresent(taggedValuesMap, "SCOCD")) {
					scoFlag = true;
				}

				separator = ",";
				qSep = ",?";

				attrString = attrString + separator + columnName;
				quesString=quesString+qSep;

				//Generating additional attributes...
				if(scoFlag) {
					attrString = attrString + separator+ columnName+"_cd ";
					quesString = quesString + qSep;
				}

				//Storing the Datatype of the PK column....
				String setDataType = mMaps.JavaToSqlSetterDtMap.get(itrOBJ.attrDataType);

				adt = itrOBJ.attrDataType;

				insertGetterString = insertGetterString + " " + adt + " " + attrName+
						" = inputObj.get"+attrName.substring(0, 1).toUpperCase()+attrName.substring(1,attrName.length())+"();\n\t\t";

				//Setting Getter string ....parsing and default conditions are made based on datatype of the column... Special cases : Date and Datetime....
				if(itrOBJ.isNull) {
					if(adt.equalsIgnoreCase("String"))
					{	
						insertGetterString = insertGetterString + " if("+attrName+"==null){\n\t\t\t"+attrName+"=\"\";\n\t\t }\n\t\t";
					}else if(adt.equalsIgnoreCase("LocalDate"))
					{	
						insertGetterString = insertGetterString + " if("+attrName+"==null){\n\t\t\t"+attrName+"=DEFDATE;\n\t\t }\n\t\t";
					}
					else if(adt.equalsIgnoreCase("LocalDateTime"))
					{	
						insertGetterString = insertGetterString + " if("+attrName+"==null){\n\t\t\t"+attrName+"=DEFDATETIME;\n\t\t }\n\t\t";
					}
				}
				//Setting Prepared string ....parsing and default conditions are made based on datatype of the column... Special cases : Date and Datetime....
				if(setDataType.equalsIgnoreCase("setDate"))
				{
					insertSetterString = insertSetterString + " if ( "+ itrOBJ.attrName + "!= null){\n\t\t\t";
					insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, java.sql.Date.valueOf("+
							itrOBJ.attrName+"));\n\t\t\t }\n\t\t\t";
				}else if(scoFlag) {

					insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, "+itrOBJ.attrName+");\n\t\t\t";
					insertSetterString = insertSetterString + " prepInStmt." + setDataType + "(colIndex++, MyConstants."+taggedValuesMap.get("SCOCD")+");\n\t\t\t";
				}else if(setDataType.equalsIgnoreCase("setTimestamp")){
					insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, Timestamp.valueOf("+itrOBJ.attrName
							+"));\n\t\t\t";
				}else
				{
					insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, "+itrOBJ.attrName+");\n\t\t\t";	
				}
			}
			if(scoFlag)
				colCounter = colCounter+2;
			else
				colCounter = colCounter+1;

			scoFlag = false;
		}

		//Setting Table Name ....
		tableName = clsObj.className;
		System.out.println("insert tblname:"+ tableName);
		//Geting Java datatype from the corresponding sql datatype of the PK column....
		String getDType = mMaps.javaToSqlGetterDtMap.get(pkDataType);
		String getKeyStr = "";
		if(globalAutoGenFlag) 
		{
			getKeyStr = "ResultSet rs=prepInStmt.getGeneratedKeys();"
					+"\n\t\tif(rs==null) {"+
					"\n\t\t\tthrow new BusException (\"EFR104\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\");\n\t\t}\n\t\n\t" 
					+"if(rs.next()){\n\t\t\tinputObj."+clsObj.pk+"=rs."+getDType+"(1);\n\t\t}\n\t\trs.close();\n\t\t";
		}
		//Setting the dynamic strings into the final text.......
		insertText = insertText + "//DMCreate function for "+ clsObj.className+ " .....\n\t";
		insertText = insertText + clsObj.accessSpecifier + " static void dmInsert" + "("+clsObj.className+"DO inputObj) "
				+ "throws BusException {\n\t" +
				insertGetterString + " try {\n\t\t\t//SQL Connection ....\n\t\t" + 
				"Connection con = InfraObject.getConnection();\n\t\t\tString mySql = " +
				"\"insert into "+tableName+"(" + attrString.substring(1)+") values( "+quesString.substring(1)+ ");\";\n\t\t\t" +
				"//Prepared Statement.....\n\t\tPreparedStatement prepInStmt = con.prepareStatement(mySql);\n\t\tint colIndex=1;\n\t\t"+
				insertSetterString+"\n\t\t"+
				"int returnValue = prepInStmt.executeUpdate();\n\t\t\n\t\t"+				
				"if(returnValue!=1) {\n\t\t"+
				"throw new BusException (\"EFR101\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\");\n\t\t}\n\t\n\t" + 
				getKeyStr +
				"}catch(BusException busException) {\n\t\t"  + 
				"throw busException;\n\t"  + 
				"}"+
				"catch(SQLException sqlException) {\n\t\t" + 
				"sqlException.printStackTrace();\n\t\t" + 
				"throw new BusException (\"EFR101\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\", sqlException );\n\t" + 
				"}catch(Exception exp ) {\n\t\t" + 
				"throw new BusException (\"EFR101\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\", exp );\n\t" + 
				"\n\t}\n\t\r\n\t}" ;

		return insertText;
	}

	//Below function set the content of the Insert Function of the DM Class......
	public static String createDMInsertHistStmt(mClass clsObj) {

		String insertText = "";
		String insertGetterString = "\t";
		String insertSetterString = "\t";
		String attrString = "";
		String quesString = "";
		String tableName = "";
		int colCounter = 1;
		String pkDataType = "";
		String adt = "";
		boolean scoFlag = false;
		String columnName = "";
		String separator = "";
		String qSep = "";
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		String pk = clsObj.pk;
		boolean globalAutoGenFlag = false;


		//Iterating through the attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);
			String attrName = itrOBJ.attrName;
			//make content only of the persistable columns....
			String vStr  = null;
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;
			if(attrName.equalsIgnoreCase(pk)) {
				pkDataType = itrOBJ.attrDataType;
			}

			if(persistFlag) {
				String aString  = null;
				if(taggedValuesMap!=null)
					aString = taggedValuesMap.get("AutoGen");
				if(aString!=null && aString.equalsIgnoreCase("true"))
				{
					globalAutoGenFlag = true;
					continue;
				}

				//if DBname is given in the XMI then Use it....else attribute name will be used...
				if(mClass.isKeyPresent(taggedValuesMap, "DBName")) {
					columnName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
				}else {
					columnName =  attrName.toString();
				}

				//if SCO type column is present in the class..then its _cd column needs to be created....
				if(mClass.isKeyPresent(taggedValuesMap, "SCOCD")) {
					scoFlag = true;
				}

				separator = ",";
				qSep = ",?";

				attrString = attrString + separator + columnName;
				quesString=quesString+qSep;

				//Generating additional attributes...
				if(scoFlag) {
					attrString = attrString + separator+ columnName+"_cd ";
					quesString = quesString + qSep;
				}

				//Storing the Datatype of the PK column....
				String setDataType = mMaps.JavaToSqlSetterDtMap.get(itrOBJ.attrDataType);

				adt = itrOBJ.attrDataType;
				if(attrName.equalsIgnoreCase(clsObj.pk))
				{
					insertGetterString = insertGetterString + " " + adt + " " + attrName+
							" = inputObj.get"+attrName.substring(0, 1).toUpperCase()+attrName.substring(1,attrName.length())+"();\n\t\t";

					//Setting Getter string ....parsing and default conditions are made based on datatype of the column... Special cases : Date and Datetime....
					if(itrOBJ.isNull) {
						if(adt.equalsIgnoreCase("String"))
						{	
							insertGetterString = insertGetterString + " if("+attrName+"==null){\n\t\t\t"+attrName+"=\"\";\n\t\t }\n\t\t";
						}else if(adt.equalsIgnoreCase("LocalDate"))
						{	
							insertGetterString = insertGetterString + " if("+attrName+"==null){\n\t\t\t"+attrName+"=DEFDATE;\n\t\t }\n\t\t";
						}
						else if(adt.equalsIgnoreCase("LocalDateTime"))
						{	
							insertGetterString = insertGetterString + " if("+attrName+"==null){\n\t\t\t"+attrName+"=DEFDATETIME;\n\t\t }\n\t\t";
						}
					}

					//Setting Prepared string ....parsing and default conditions are made based on datatype of the column... Special cases : Date and Datetime....
					if(setDataType.equalsIgnoreCase("setDate"))
					{
						insertSetterString = insertSetterString + " if ( "+ itrOBJ.attrName + "!= null){\n\t\t\t";
						insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, java.sql.Date.valueOf("+
								itrOBJ.attrName+"));\n\t\t\t }\n\t\t\t";
					}else if(scoFlag) {

						insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, "+itrOBJ.attrName+");\n\t\t\t";
						insertSetterString = insertSetterString + " prepInStmt." + setDataType + "(colIndex++, MyConstants."+taggedValuesMap.get("SCOCD")+");\n\t\t\t";
					}else if(setDataType.equalsIgnoreCase("setTimestamp")){
						insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, Timestamp.valueOf("+itrOBJ.attrName
								+"));\n\t\t\t";
					}else
					{
						insertSetterString = insertSetterString + " prepInStmt." + setDataType+ "(colIndex++, "+itrOBJ.attrName+");\n\t\t\t";	
					}
				}
			}
			if(scoFlag)
				colCounter = colCounter+2;
			else
				colCounter = colCounter+1;

			scoFlag = false;
		}

		//Setting Table Name ....
			tableName = clsObj.className;

		//Geting Java datatype from the corresponding sql datatype of the PK column....
		String getDType = mMaps.javaToSqlGetterDtMap.get(pkDataType);
		String getKeyStr = "";

		if(globalAutoGenFlag) 
		{
			getKeyStr = "ResultSet rs=prepInStmt.getGeneratedKeys();"
					+"\n\t\tif(rs==null) {"+
					"\n\t\t\tthrow new BusException (\"EFR104\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\");\n\t\t}\n\t\n\t" 
					+"if(rs.next()){\n\t\t\tinputObj."+clsObj.pk+"=rs."+getDType+"(1);\n\t\t}\n\t\trs.close();\n\t\t";
		}

		//Setting up getter string for the Versions to set it later in the prepared statement...
		insertGetterString = insertGetterString + "int prev1 = inputObj.version1;\n\t\t";
		insertGetterString = insertGetterString + "int prev2 = inputObj.version2;\n\t\t";
		insertSetterString = insertSetterString + "prepInStmt.setInt(colIndex++, prev1);\n\t";
		insertSetterString = insertSetterString + "prepInStmt.setInt(colIndex++, prev2);\n\t";

		//Setting the dynamic strings into the final text.......
		insertText = insertText + "//DMCreate function for "+ clsObj.className+ " .....\n\t";
		insertText = insertText + clsObj.accessSpecifier + " static void dmInsertHist" + "("+clsObj.className+"DO inputObj) "
				+ "throws BusException {\n\t" +
				insertGetterString + " try {\n\t\t\t//SQL Connection ....\n\t\t" + 
				"Connection con = InfraObject.getConnection();\n\t\t\tString mySql = " +
				"\"insert into "+tableName+"_Hist(" + attrString.substring(1)+") " +"\"\n+\""
				+ " select "+ attrString.substring(1)+" from "+tableName +"\"\n+\""
				+ " where "+clsObj.pk+" = ? and version1 = ? and version2 = ? \";\n\t\t\t" +
				"//Prepared Statement.....\n\t\tPreparedStatement prepInStmt = con.prepareStatement(mySql);\n\t\tint colIndex=1;\n\t\t"+
				insertSetterString+"\n\t\t"+
				"int returnValue = prepInStmt.executeUpdate();\n\t\t\n\t\t"+				
				"if(returnValue!=1) {\n\t\t"+
				"throw new BusException (\"EFR101\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\");\n\t\t}\n\t\n\t" + 
				getKeyStr +
				"}catch(BusException busException) {\n\t\t"  + 
				"throw busException;\n\t"  + 
				"}"+
				"catch(SQLException sqlException) {\n\t\t" + 
				"sqlException.printStackTrace();\n\t\t" + 
				"throw new BusException (\"EFR101\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\", sqlException );\n\t" + 
				"}catch(Exception exp ) {\n\t\t" + 
				"throw new BusException (\"EFR101\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\", exp );\n\t" + 
				"\n\t}\n\t\r\n\t}" ;

		return insertText;
	}


	//Below functions are for Modify function.....
	public static String createDMModStmt(mClass clsObj) {

		String modifyText = "";
		String insertGetterString = "\t";
		String modSetString = "\t";
		String modParamString = "\t";
		String tableName = "";
		String paramAtrName = "";
		String setPKey = "";
		boolean persistFlag = false;
		String adt = "";
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;

		//iterate through the attributes list.....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);
			String vStr  = null;
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;
			//make content only of the persistable columns....
			if(persistFlag) {

				String attrName = itrOBJ.attrName;
				String atrDtype = itrOBJ.attrDataType;
				String setDataType = mMaps.JavaToSqlSetterDtMap.get(atrDtype);
				String nullorzero = "";

				//if DBname is given in the XMI then Use it....else attribute name will be used...
				if(mClass.isKeyPresent(taggedValuesMap, "DBName")) 
					paramAtrName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
				else
					paramAtrName = itrOBJ.attrName;

				//getting java datatype from the XMi datatype....
				if(mClass.isKeyPresent(mMaps.XmiToJavaDTMap,itrOBJ.attrDataType)) {
					adt = mMaps.XmiToJavaDTMap.get(itrOBJ.attrDataType);
				}else
					adt = itrOBJ.attrDataType;

				//creating intermediate string for if conditions to follow....
				if(adt.equalsIgnoreCase("boolean"))
					nullorzero = "false";
				else
					nullorzero = "null";

				//creating param string on the if condition...
				if(!attrName.equalsIgnoreCase(clsObj.pk))
				{
					if(adt.equalsIgnoreCase("int") ||adt.equalsIgnoreCase("double")|| adt.equalsIgnoreCase("float") ) {
						modParamString = modParamString+"\n\t\t\t parameters+= \""+paramAtrName+"=?,\";\n\t\t";	
					}else
					{
						modParamString = modParamString+"if("+attrName+"!="+nullorzero+") {\n\t\t\t parameters+= \""+
								paramAtrName+"=?,\";\n\t\t}\n\t\t";	
					}
				}

				String setValue = "";

				//setting up content for the prepared string....
				if(!attrName.equalsIgnoreCase(clsObj.pk)) {
					if(setDataType.equalsIgnoreCase("setDate")) {
						setValue = "if("+attrName+"!="+nullorzero+") {\n\t\t\tprepUpStmt."+setDataType+"(i++,  java.sql.Date.valueOf("+
								attrName+"));\n\t\t}\n\t\t";
					}else if(setDataType.equalsIgnoreCase("setTimestamp")){
						setValue =  "if("+attrName+"!="+nullorzero+") {\n\t\t\tprepUpStmt."+setDataType+"(i++,Timestamp.valueOf("+
								attrName+"));\n\t\t}\n\t\t";
					}else if(adt.equalsIgnoreCase("int") ||adt.equalsIgnoreCase("double")|| adt.equalsIgnoreCase("float") ) {
						setValue = "\n\t\t\tprepUpStmt."+setDataType+"(i++,"+attrName+");\n\t\t";
					}
					else {
						setValue = "if("+attrName+"!="+nullorzero+") {\n\t\t\tprepUpStmt."+setDataType+"(i++,"+attrName+");\n\t\t}\n\t\t";
					}
					modSetString = modSetString + " " +setValue; 
				}else {
					if(setDataType.equalsIgnoreCase("setDate")) {
						setPKey = setPKey + "prepUpStmt."+setDataType+"(i++, new java.sql.Date("+
								attrName+"));\n\t\t";
					}else if(setDataType.equalsIgnoreCase("setTimestamp")) {
						setPKey = setPKey + "prepUpStmt."+setDataType+"(i++, Timestamp.valueOf("+
								attrName+"));\n\t\t";
					}
					else{
						setPKey = setPKey 
								+"prepUpStmt."+setDataType+"(i++,"+attrName+");\n\t\t";
					}

				}

				insertGetterString = insertGetterString + " " + adt + " " + attrName+
						" = inputObj.get"+attrName.substring(0, 1).toUpperCase()+attrName.substring(1,attrName.length())+"();\n\t\t";
			}
		}

		//Setting up tablename....
		if(App.taggedValues.containsKey(clsObj.xmi_id))
			if(App.taggedValues.get(clsObj.xmi_id).containsKey("DBName"))
				tableName = App.taggedValues.get(clsObj.xmi_id).get("DBName");
			else
				tableName = clsObj.className;
		else
			tableName = clsObj.className;

		//Setting the dynamic strings into the final text.......
		modifyText = modifyText + "//DMModify function for "+ clsObj.className+ " .....\n\t";
		modifyText = modifyText + clsObj.accessSpecifier + " static void dmUpdate("+
				clsObj.className+"DO inputObj) throws BusException {\n\t" +
				insertGetterString + "String parameters = \"\";\n\t" + modParamString+
				"int len = parameters.length();\r\n\t" + 
				"if(len>0)\r\n\t\t" + 
				"parameters = parameters.substring(0, len-1);\n\t" + 
				"else\r\n\t\t return;\n\t"+ "String mySql = \"update " + tableName +" set \"+parameters+\" where "+ clsObj.pk +" =?;\";\n\t"+
				"try {\n\t\t//SQL Connection ....\n\t\t" + 
				"Connection con = InfraObject.getConnection();\n\t\t//Prepared Statement.....\n\t\t" +
				"PreparedStatement prepUpStmt = con.prepareStatement(mySql);\n\t\tint i=1;\n\t"+modSetString+"\n\t\t"+setPKey+
				"int a = prepUpStmt.executeUpdate();\n\t\t"+
				"}catch(SQLException sqlException) {\n\t\t" +
				"throw new BusException (\"EFR102\" ,\""+clsObj.className+"\", \"create"+clsObj.className+"\", sqlException );\n\t}"
				+ "catch(Exception exp) {\n\t" + 
				"throw new BusException (\"EFR102\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", exp );\n\t" + 
				"}\n}";

		return modifyText;
	}
	//Below functions are for GetList function.....
	public static String createDMGetListStmt(mClass clsObj) {

		String classGetListText = "";
		String attrString = "";
		String pkDataType  = "";
		String tableName = "";
		String scoCol = ""; 
		String boolAttrDec = "\n\tboolean whereFlag = false;";
		String srchObjStr = "";
		String rstString = "";
		String insertGetterString = "";
		String retString = "";
		String searchstmt = "";
		String inputList = "";
		String retObjString = "";
		mClass srchObj = null;
		mClass returnObj = null;
		String returnxmi = "";
		String srchxmi = "";
		boolean persistFlag = false;
		boolean rstFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		HashMap<String, String> OprTaggedValuesMap = new HashMap<String, String>() ;
		int counter = 0;
		String listOrder = "";
		String fromClause = "";
		String whereClause = " where ";
		//iterating through the operation list....
		for(int itrOpr = 0; itrOpr <= clsObj.oprList.size()-1; itrOpr++) {

			mOperation itrObj =  clsObj.oprList.get(itrOpr);
			OprTaggedValuesMap = App.taggedValues.get(itrObj.xmi_id);
			boolean gListFlag = false;
			String vStr  = null;

			if(OprTaggedValuesMap!=null)
				vStr = OprTaggedValuesMap.get("IsGList");
			if(vStr==null)
				gListFlag = false;
			else if(vStr.equalsIgnoreCase("true"))
				gListFlag = true;
			else
				gListFlag = false;


			if(gListFlag) {

				String inputVar = "";

				if(OprTaggedValuesMap!=null)
					vStr = OprTaggedValuesMap.get("ListOrder");
				if(vStr==null)
					listOrder = "asc";
				else 
					listOrder = vStr;

				//iteratering through the params of the operation...
				for(int itrParam = 0; itrParam <= itrObj.params.size()-1; itrParam++) {
					mParams itrPObj = itrObj.params.get(itrParam);
					//		Params indexes should be fixed...0 for return param....1 for input search object....2 for Rst Key...
					if(itrPObj.kind.equalsIgnoreCase("in")) {
						if(itrParam == 0 ) {
							inputVar = " inpSrchObj";
							srchxmi = itrPObj.type;
							clsObj.createSearchVOClasses(itrPObj.type,false);
						}
						else if(itrParam == 1 ) {
							inputVar = " inpRstKey";
							clsObj.createRSTClass(itrPObj.type);
							rstFlag = true;
						}
						inputList = inputList +", "+ itrPObj.type + inputVar;	
					}
					if(itrPObj.kind.equalsIgnoreCase("return")) {	
						clsObj.createSearchVOClasses(itrPObj.type, true);
						retObjString = retObjString + "List<"+itrPObj.type + "> ";
						returnxmi = itrPObj.type;
					}
				}
			}

		}

		//Following for loop will generate content for pagination using RST attribute...which is presumed  to be primary key....
		if(rstFlag) {
			rstString = "if(inpRstKey!=null) {\n\t\t\t\t";

			for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

				mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
				String sqlAttrName = itrOBJ.attrName;

				//Do below stuff only if the column is PK of the class.....
				if(sqlAttrName.equalsIgnoreCase(clsObj.pk)) {

					pkDataType = itrOBJ.attrDataType.toString();

					rstString = rstString +pkDataType+ " paginationId = inpRstKey.get"+
							sqlAttrName.substring(0, 1).toUpperCase()+sqlAttrName.substring(1,sqlAttrName.length())+"();\n\t\t\t";

					if(pkDataType.equalsIgnoreCase("int") ||pkDataType.equalsIgnoreCase("double")|| pkDataType.equalsIgnoreCase("float") ) {
						rstString = rstString + "if(paginationId!= 0) {\n\t\t\t";
					}else
						rstString = rstString + "if(paginationId!=null && !paginationId.equals(\"\")) {\n\t\t\t";

					rstString = rstString + "\n\t\t\t\tif ( whereFlag)\n\t\t\t\t\tsql += \" and \";\n\t\t\t\telse\n\t\t\t\t\tsql += \" where \";";

					rstString = rstString + "\n\t\t\t\tsql +=\" a."+sqlAttrName+" >= '\" + paginationId+\"'\";\n\t\t\t}";
				}
			}
			rstString = rstString + "\n\t\t}\n\t\t\t\t";

		}
		// storing the pointers to search and return object for later use......
		for(int i = 0; i<= App.classList.size()-1; i++)
		{
			if(App.classList.get(i).className.equalsIgnoreCase(srchxmi)) {
				srchObj = App.classList.get(i);
			}
			if(App.classList.get(i).className.equalsIgnoreCase(returnxmi)) {
				returnObj = App.classList.get(i);
			}
		}

		//Below code will generate 
		//1) Where clause
		//2) Declaration for variables corresponding to parameters of srch obj and their settings...
		//3) Join clause in case of SCO Code

		boolean globalSCOFLag = false;
		//Getting the Attribute object of class....
		for(int it = 0; it<clsObj.attrList.size();it++) {

			mAttribute tempClsAtr = clsObj.attrList.get(it);
			taggedValuesMap = App.taggedValues.get(tempClsAtr.xmi_id);
			String scoVal = null;


			if(taggedValuesMap!=null)
				scoVal = taggedValuesMap.get("SCOCD");

			if(scoVal!=null)
			{
				globalSCOFLag = true;
				break;
			}

		}
		if(srchObj!=null) {
			srchObjStr  ="if ( inpSrchObj != null){\n\t\t\t\t";

			for(int itrAttr = 0; itrAttr <= srchObj.attrList.size()-1; itrAttr++) {

				mAttribute itrOBJ = srchObj.attrList.get(itrAttr);

				taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);

				String attrName  = itrOBJ.attrName;
				String adt  = itrOBJ.attrDataType;
				String vStr = null;
				if(taggedValuesMap!=null)
					vStr = taggedValuesMap.get("IsPersist");
				if(vStr==null)
					persistFlag = true;
				else if(vStr.equalsIgnoreCase("false"))
					persistFlag = false;
				else
					persistFlag = true;

				//make content only of the persistable columns....
				if(persistFlag) {

					boolAttrDec = boolAttrDec + "\n\tboolean "+ itrOBJ.attrName + "Flag = false;";


					//Geting Java datatype from the corresponding Xmi datatype ...
					if(mClass.isKeyPresent(mMaps.XmiToJavaDTMap,itrOBJ.attrDataType)) {
						adt = mMaps.XmiToJavaDTMap.get(itrOBJ.attrDataType);
					}else
						adt = itrOBJ.attrDataType;


					//Preparing for the conditions for the prepared statement....
					String nullorzero = "";
					String sqlAttrName =  itrOBJ.attrName;
					if(adt.equalsIgnoreCase("int") ||adt.equalsIgnoreCase("double")|| adt.equalsIgnoreCase("float") ) {
						nullorzero = sqlAttrName+" != 0 ) {";
					}else
						nullorzero = sqlAttrName+" !=null && !"+sqlAttrName+".equals(\"\")){";


					srchObjStr = srchObjStr + "\n\t\t\tif("+ nullorzero;

					String srchStrSep = "";

					if(globalSCOFLag)
					{
						srchObjStr = srchObjStr + "\n\t\t\t\tsql += \" and \";";

					}
					else if(itrAttr == 0 )
						srchStrSep = " where a.";
					else
						srchObjStr = srchObjStr + "\n\t\t\t\tif ( whereFlag)\n\t\t\t\t\tsql += \" and \";"
								+ "\n\t\t\t\telse\n\t\t\t\t\tsql += \" where \";\n";


					srchObjStr = srchObjStr + " \n\t\t\t\t\tsql += \""+ srchStrSep +itrOBJ.attrName+" = ? \"; \n\t\t\t\t\t"
							+itrOBJ.attrName+"Flag = true;\n\t\t\t\t\t";

					if(!globalSCOFLag) 
						srchObjStr = srchObjStr + "\n\t\t\twhereFlag=true;\n\t\t\t}";
					else
						srchObjStr = srchObjStr + "}\n\t\t\t";


					//Geting Java datatype from the corresponding sql datatype
					String dType = mMaps.JavaToSqlSetterDtMap.get(itrOBJ.attrDataType);

					insertGetterString = insertGetterString + " " + adt + " "+attrName+
							" = inpSrchObj.get"+attrName.substring(0, 1).toUpperCase()+attrName.substring(1,attrName.length())+"();\n\t\t";

					//Setting up search statement(prepared stmt)....
					String concatAttrName  =   itrOBJ.attrName;
					searchstmt = searchstmt + "if("+itrOBJ.attrName+"Flag) {\n\t\t";
					String setValue = "";
					if(dType.equalsIgnoreCase("setDate")) {
						setValue = "java.sql.Date.valueOf("+ concatAttrName+")";
					}else if(dType.equalsIgnoreCase("setTimestamp")){
						setValue = "Timestamp.valueOf("+ concatAttrName+")";
					}else
						setValue =  concatAttrName;
					searchstmt = searchstmt + "searchStatement."+dType + "(index++, "+  setValue +" );\n\t\t\t";

					searchstmt = searchstmt + "}\n\t\t";

					counter = counter+1;
				}
			}
			srchObjStr = srchObjStr + "\n\t\t}\n\t\t";
		}
		if(returnObj!= null)
		{
			mAttribute doAttributeObj = null;
			int scoCount = 0;

			for(int itrAttr = 0; itrAttr <= returnObj.attrList.size()-1; itrAttr++) {

				mAttribute itrOBJ = returnObj.attrList.get(itrAttr);

				String attrName  = itrOBJ.attrName;

				String clsAttrDataType = "";
				//Getting the Attribute object of class....
				for(int it = 0; it<clsObj.attrList.size();it++) {

					String tempClsAtrName = clsObj.attrList.get(it).attrName;

					if(attrName.equalsIgnoreCase(tempClsAtrName)) {

						doAttributeObj = clsObj.attrList.get(it);
						clsAttrDataType = doAttributeObj.attrDataType;
					}
				}


				taggedValuesMap = App.taggedValues.get(doAttributeObj.xmi_id);

				String vStr = null;
				if(taggedValuesMap!=null)
					vStr = taggedValuesMap.get("IsPersist");
				if(vStr==null)
					persistFlag = true;
				else if(vStr.equalsIgnoreCase("false"))
					persistFlag = false;
				else
					persistFlag = true;

				//make content only of the persistable columns....
				if(persistFlag) {

					//if DBname is given in the XMI then Use it....else attribute name will be used...
					String sep = "";
					String attrColName = "";

					if(mClass.isKeyPresent(taggedValuesMap, "DBName")) {
						attrColName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
					}else {
						attrColName = attrName.toString();
					}

					if(itrAttr == 0) {
						sep = "";
					}else {
						sep = ", ";
					}
					if(itrAttr == 0) {
						sep = "";
					}else {
						sep = ", ";
					}


					boolean scoFlag = false;
					String scoVal = null;
					if(taggedValuesMap!=null)
						scoVal = taggedValuesMap.get("SCOCD");

					if(scoVal==null)
						scoFlag = false;
					else 
						scoFlag = true;

					//if SCO type column is present in the class..then its _cd column needs to be created....
					if(scoFlag) {
						scoCount++;
						attrString = attrString + sep + "sco"+scoCount+".cdname" ;
						scoCol = itrOBJ.attrName;
						fromClause = fromClause + " ,systemcodes sco"+scoCount ;
						if(scoCount>1)
							whereClause = whereClause + " and ";

						whereClause = whereClause + "  "+scoCol+" = sco"+scoCount+".cdval and " + scoCol + "_cd = sco"+scoCount+".cdtype and "+
								scoCol+"_cd = \"+MyConstants."+scoVal+"+\"";
					}else
					{
						attrString = attrString + sep + attrColName;
					}

					String getDType = mMaps.javaToSqlGetterDtMap.get(clsAttrDataType);

					//Setting up return statement......
					String retValue = "";

					if(scoFlag) {
						getDType = "getString";
					}


					if(getDType.equalsIgnoreCase("getDate")) {
						retValue = getDType + "(colIndex++).toLocalDate()";
					}else if(getDType.equalsIgnoreCase("getTimestamp")) {
						retValue =  getDType +   "(colIndex++).toLocalDateTime()";
					}
					else if(getDType.equalsIgnoreCase("getInt") ||getDType.equalsIgnoreCase("getFloat")|| getDType.equalsIgnoreCase("getDouble") ) 
						retValue =   getDType + "(colIndex++)";
					else
						retValue =   getDType + "(colIndex++)";

					String retStringContent = "";
					String tempRetString = "resultSet." +retValue;

					retStringContent = tempRetString;

					if(scoFlag) {
						retStringContent = tempRetString;	

					}else if(!itrOBJ.attrDataType.equalsIgnoreCase(clsAttrDataType)) {


						if(itrOBJ.attrDataType.equalsIgnoreCase("String")) {

							if(getDType.equalsIgnoreCase("getDate")){
								retStringContent = "MyUtils.dateToString(" + tempRetString + ")";

							}else if( getDType.equalsIgnoreCase("getTimestamp")){
								retStringContent = "MyUtils.dateTimeToString(" + tempRetString + ")";

							}else
								retStringContent = "String.valueOf("+tempRetString+")";	
						}
						else
						{
							System.out.println("************ERROR****************");
							break;
						}
					}


					retString = retString + "retObj.set"+attrName.substring(0, 1).toUpperCase()+attrName.substring(1,attrName.length())
					+"(" +retStringContent+ ");\n\t\t";
				}
			}

		}
		if(srchObj==null) {
			srchObjStr = "";
			insertGetterString = "";
			searchstmt = "";
		}
		//Setting up tablename....
		if(App.taggedValues.containsKey(clsObj.xmi_id))
			if(App.taggedValues.get(clsObj.xmi_id).containsKey("DBName"))
				tableName = App.taggedValues.get(clsObj.xmi_id).get("DBName");
			else
				tableName = clsObj.className;
		else
			tableName = clsObj.className;
		String finalSqlStmt = "";


		if(globalSCOFLag)
			finalSqlStmt = "Select "+ attrString+" from "+tableName+" a "+ fromClause + " " + whereClause;
		else 
			finalSqlStmt = "Select "+ attrString+" from "+tableName+" a ";


		if(inputList.length()>=1)
			inputList = inputList.substring(1);



		//setting up final class text.....
		classGetListText = classGetListText + "//DMGetList function for "+ clsObj.className+ " .....\n\t";
		classGetListText = classGetListText + clsObj.accessSpecifier + " static "+retObjString+" dmGetList"+
				"("+inputList+")" + " throws BusException {\n\t";
		classGetListText = classGetListText +"\n\t"+retObjString+" retList = new ArrayList<>();\n\tint nor = 0;\n\t" + boolAttrDec +"\n\t" 
				+ insertGetterString+
				"\n\tConnection con = null;\n\tResultSet resultSet = null;\n\ttry {\n\t\t//SQL Connection ....\n\t\t" + 
				" con = InfraObject.getConnection();\n\t\tString sql = \"" +
				finalSqlStmt+"\";\n\t\t"+ srchObjStr + rstString 
				+"\n\t\tsql = sql + \" order by "+clsObj.pk+" "+listOrder+" Limit 20\";"
				+ "//Prepared Statement.....\n\t\t" +
				"PreparedStatement searchStatement = con.prepareStatement(sql);\n\t\tint index = 1;\n\t\t"+ searchstmt
				+"\n\t\t//Result Set obj.....\n\t\t" +
				" resultSet = searchStatement.executeQuery();\n\t\tint colIndex = 1;\n\t\t" +
				"while(resultSet.next()) {\n\t\t"+ returnxmi
				+" retObj = new "+ returnxmi +"();\n\t\t"+retString +
				"retList.add(retObj);\n\t\t" + 
				"colIndex = 1;\n\t\t"+
				"}\n\t\t"+
				"\n\t}catch(SQLException sqlException) {\n\t" + 
				"throw new BusException (\"EFR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", sqlException );\n\t" + 
				"}" + "catch(Exception exp) {\n\t" + 
				"throw new BusException (\"EFR100\" ,\""+clsObj.className+"\", \"get"+clsObj.className+"\", exp );\n\t" + 
				"}\n\t\tfinally\n\t\t\t{\n\t\t\t\t"	+
				"if (resultSet != null)\n\t\t{\n\t\t\ttry {\n\t\t\tresultSet.close();\n\t\t\t}\n\t\t\tcatch (Exception e1 )  "
				+ "{}\n\t\t}\n\t" +"\n\tInfraObject.releaseConnection();\n\t"+
				"}\n\t\t"	+			

				"return retList;\r\n\t}" ;
		return classGetListText;
	}
	public static String createDMDeleteStmt(mClass clsObj) {

		String deleteText = "";
		String pkDataType  = "";
		String tableName = "";
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		int counter = 0;

		//iteration on attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);
			String attrName  = itrOBJ.attrName;
			String vStr  = null;
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;
			//only persistable columns will be used to make strings.....
			if(persistFlag) {

				//if DBname is given in the XMI then Use it....else attribute name will be used...

				if(mClass.isKeyPresent(taggedValuesMap, "DBName")) {
					App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
				}else {
					attrName.toString();
				}

				//Storing the Datatype of the PK column....
				if(attrName.equalsIgnoreCase(clsObj.pk)) {
					pkDataType = itrOBJ.attrDataType.toString();
				}
				counter = counter+1;
			}
		}

		String isTBName = null;
		if(App.taggedValues.get(clsObj.xmi_id)!= null)
			isTBName = App.taggedValues.get(clsObj.xmi_id).get("DBName");

		//Setting value to table name..
		if(isTBName==null)
			tableName = clsObj.className;
		else 
			tableName = isTBName;

		//Geting Java datatype from the corresponding sql datatype of the PK column....
		String dType1 = mMaps.JavaToSqlSetterDtMap.get(pkDataType);

		//Setting the created dynamic strings into the final class text....

		deleteText = deleteText + "//DMDelete function for "+ clsObj.className+ " .....\n\t";

		deleteText = deleteText + clsObj.accessSpecifier + " static void  dmDelete"+ "("+clsObj.className+"DO inputObj) "
				+ "throws BusException {\n\t";
		deleteText = deleteText + pkDataType +" "+clsObj.pk + " = inputObj."+clsObj.pk+";"
				+ "\n\ttry {\n\t\t//SQL Connection ....\n\t\t" + 
				" Connection con = InfraObject.getConnection();\n\t\tString sql = " +
				"\"delete from " + tableName+ " where "+ clsObj.pk +" = ? \";\n\t\t//Prepared Statement.....\n\t\t" +
				"PreparedStatement deleteStatement = con.prepareStatement(sql);\n\t\t"+
				"deleteStatement."+dType1+"(1, "+ clsObj.pk +" );\n\t\t//Result Set obj.....\n\t\t" +
				" int value = deleteStatement.executeUpdate();\n\t\t" +
				"if (value == 0){\n\t\t\t"+
				"throw new BusException (\"EFR105\" ,\""+clsObj.className+"\", \"delete"+clsObj.className+"\" );\n\t\t}\n\t\n\t" + 
				"}catch(BusException busException) {\n\t\t"  + 
				"throw busException;\n\t"  + 
				"}"
				+ "catch(SQLException sqlException) {\n\t\t" + 
				"throw new BusException (\"EFR105\" ,\""+clsObj.className+"\", \"delete"+clsObj.className+"\", sqlException );\n\t" + 
				"}catch(Exception exp ) {\n\t\t" +
				"throw new BusException (\"EFR105\" ,\""+clsObj.className+"\", \"delete"+clsObj.className+"\", exp );\n\t\t" + 
				"}\n\t\n\t}" ;
		return deleteText;

	}
}
