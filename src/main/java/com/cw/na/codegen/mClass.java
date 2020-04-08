package com.cw.na.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

public class mClass {

	String className="";
	String pk="";
	String accessSpecifier = "";
	String xmi_id = "";
	List<mAttribute> attrList = new ArrayList<>();
	List<mOperation> oprList = new ArrayList<>();

	//This Function Generates the File for the class....i/p's are Fully Classified path to dir, name of the file, content of the file.....
	public void writeToFile(String pathToFiles, String fileName, String fileText, int fileTypeCode) {

		//Formatting the received text using Google Java formatter 
		//		if(!ddlFlag){
		//			try {
		//				fileText = new Formatter().formatSource(fileText);
		//			} catch (FormatterException e1) {
		//
		//			}
		//		}
		new File(pathToFiles).mkdirs();
		FileOutputStream out;

		try {
			if(fileTypeCode == 1)
				out = new FileOutputStream(pathToFiles+"\\"+fileName + ".sql");
			else if(fileTypeCode == 2 )
				out = new FileOutputStream(pathToFiles+"\\"+fileName + ".js");
			else if(fileTypeCode == 3 )
				out = new FileOutputStream(pathToFiles+"\\"+fileName + ".json");
			else
				out = new FileOutputStream(pathToFiles+"\\"+fileName + ".java");

			out.write(fileText.getBytes());
			out.close();	
		} catch (Exception e) {

		}
	}

	//Below function create the DO class for you....
	public void createDOClass() {
		String pathToFiles =  App.pathMap.get("DOPath");// location of the file...
		String classOutName = this.className+"DO";//name of the file...
		String classText = "";
		boolean DOFlag = true;

		//Create Class Name and till Constructor
		classText = classText + "package " +App.pathMap.get("DOPkgName") + ";\n";
		classText = classText + "import java.util.*;\n\n";
		classText = classText + "import java.time.LocalDateTime;\n\n";
		classText = classText + "import java.time.LocalDate;\n\n";		
		classText = classText + this.accessSpecifier + " class " + classOutName + " {\n\t//Constructor for DO...\n";
		classText = classText + this.setClassStmt(classOutName, DOFlag, false);//function to create the class statement..
		classText = classText + "\n}";

		writeToFile(pathToFiles, classOutName, classText, 0);//Function to write to the file...
		classText = "";
	}

	//This function generated the RST class for you.....It is presummed that the RST Key will be PK of the class....
	public void createRSTClass(String className) {

		String pathToFiles =  App.pathMap.get("VOPath");
		String classOutName = className ;
		String classText = "";
		String content = "";

		for(int itrAttr = 0; itrAttr <= this.attrList.size()-1; itrAttr++) {
			mAttribute itrOBJ = this.attrList.get(itrAttr);
			String attrName  = itrOBJ.attrName;
			String attrDatatype  = itrOBJ.attrDataType;

			//Do below stuff only if the column is PK of the class.....
			if(attrName.equalsIgnoreCase(this.pk))
			{
				content = content + "\tpublic " + attrDatatype + " " + attrName+ ";\n";
				content =  content + "\tpublic " + attrDatatype + " get"+ attrName.substring(0, 1).toUpperCase()
						+attrName.substring(1,attrName.length())+ "(){;\n\t\t\t";
				content =  content + " return "+ attrName+";\n\t\t}\n";
				content =  content + "\tpublic void set"+ attrName.substring(0, 1).toUpperCase()
						+attrName.substring(1,attrName.length())+ "(" + attrDatatype + " "+ attrName+"){;\n\t\t\t";
				content =  content + " this."+ attrName+" = "+ attrName+";\n\t\t}\n";
			}

		}

		//Create Class Name and till Constructor
		classText = classText + "package " +App.pathMap.get("VOPkgName") + ";\n\n\n";
		classText = classText + this.accessSpecifier + " class " + classOutName + " {\n";
		classText = classText + content;
		classText = classText + "\n}";

		writeToFile(pathToFiles, classOutName, classText , 0);

		classText = "";
	}

	//function for Search VO Class....
	public void createSearchVOClasses(String className, boolean retVOFlag) {

		String pathToFiles =  App.pathMap.get("VOPath");
		String classSearchVOName = "";
		if(className.equalsIgnoreCase(""))
			classSearchVOName = this.className;
		else
			classSearchVOName = className;
		String classText = "";

		//Create Class Name and till Constructor
		classText = classText + "package " +App.pathMap.get("VOPkgName") + ";\n";
		classText = classText + "import java.util.*;\n\n";
		classText = classText + "import java.time.LocalDateTime;\n\n";
		classText = classText + "import java.time.LocalDate;\n\n";	
		classText = classText + this.accessSpecifier + " class " + classSearchVOName + " {\n";
		classText = classText + this.setClassStmt(classSearchVOName, false,  retVOFlag);
		classText = classText + "\n}";

		writeToFile(pathToFiles, classSearchVOName, classText, 0);

		classText = "";
	}

	//function for BO Classes...
	public void createBOClass() {

		String pathToFiles =  App.pathMap.get("BOPath");
		String classOutName = this.className+"BO";
		String classText = "";
		HashMap<String, String> OprTaggedValuesMap = new HashMap<String, String>() ;

		if(this.accessSpecifier == "")
			this.accessSpecifier = "public";

		String retClsName = "void";
		String retString = "";
		String boInputString = "";
		String dmInputString =""; 
		String methodSign = "";
		String methodBody ="";
		String pkDataType = "";
		String extendStr = "";
		boolean intKeyFlag = false;
		classText = classText + "package "+ App.pathMap.get("BOPkgName")+";\n";
		classText = classText + "import java.util.*;\n\n";
		classText = classText + "import java.sql.*;\n";
		classText = classText + "import "+ App.pathMap.get("DOPkgName")+".*;\n";
		classText = classText + "import "+ App.pathMap.get("SVCPkgName")+".*;\n";
		classText = classText + "import "+ App.pathMap.get("DMPkgName")+".*;\n";
		classText = classText + "import "+ App.pathMap.get("VOPkgName")+".*;\n";
		classText = classText + "import java.text.SimpleDateFormat;\n";


		classText = classText + "import com.fasterxml.jackson.databind.ObjectMapper;\n";
		classText = classText + "import com.fasterxml.jackson.databind.ObjectWriter;\n";
		classText = classText + "import com.cw.na.arch.*;\n";
		classText = classText + "import java.time.*;\n";

		classText = classText + "import com.cw.na.common.utils.CallRestAPI;\n";
		classText = classText + "import org.json.JSONObject;\n";
		if(App.selectedModel==2) {
			classText = classText + "import com.cw.na.queuereader.EntityBase;\n";
			extendStr = "extends EntityBase";
		}
		for(int itrAtr=0; itrAtr < this.attrList.size(); itrAtr++) {
			mAttribute tempAttrObj = this.attrList.get(itrAtr);
			if(tempAttrObj.attrName.equalsIgnoreCase(this.pk))
				pkDataType = tempAttrObj.attrDataType;
		}

		classText = classText + this.accessSpecifier + " class " + classOutName + "  "+extendStr+"{\n\t public "
				+this.className +"DO doObj;\n\t";
		classText = classText + "\n\t\t" + 
				"public static LocalDate DEFDATE = LocalDate.ofEpochDay(0);\r\n\t" + 
				"public static LocalDateTime DEFDATETIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);\n\t";
		if(App.selectedModel==2) {
			classText = classText + "public void setDO(Object inputObj) {\n\t\t\n\t\t\t//Setting DO obj....\n\t\t\t doObj = ("
					+this.className+"DO) inputObj;";
			classText = classText + "\n\t}\n\t";

			if(pkDataType.equalsIgnoreCase("int"))
				intKeyFlag = true;

			classText = classText + "public void setKey(int key) {\n\t\t\n\t\t\t//Setting key of for integer type pk....\n\t\t\t ";
			if(intKeyFlag)
				classText = classText + " this.doObj."+this.pk+" = key; ";
			classText = classText + "\n\t}\n\t";

			classText = classText + "public void setKey(String key) {\n\t\t\n\t\t\t//Setting key of for String type pk....\n\t\t\t ";
			if(!intKeyFlag)
				classText = classText + " this.doObj."+this.pk+" = key; ";
			classText = classText + "\n\t}\n\t";
			classText = classText + "public void setVersion(String version)  {\n\t\t \n\t\t\t//Setting version"
					+ "....\n\t\t\t this.doObj.version1 = Integer.valueOf(version.split(\":\")[0]);"
					+ "\n\t\t\t this.doObj.version2 = Integer.valueOf(version.split(\":\")[1]);";
			classText = classText + "\n\t}\n\t";
		}
		if(App.selectedModel==1 || App.selectedModel ==2) {
			classText = classText + "public void boInsert()  throws BusException {\n\t\ttry {\n\t\t\t//Calling DM Create....\n\t\t\t "
					+this.className+"DM.dmInsert(doObj);";
			classText = classText + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}\n\t";

			classText = classText + "\n\tpublic "+this.className+"DO boGet()  throws BusException {\n\t\ttry "
					+ "{\n\t\t\t//Calling DM Get....\n\t\t\t return "+
					this.className+"DM.dmGet(doObj);";
			classText = classText + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";

			classText = classText + "\n\tpublic void boUpdate()  throws BusException {\n\t\ttry {\n\t\t\t"
					+ "//Calling DM Modify....\n\t\t\t "+
					this.className+"DM.dmUpdate(doObj);";
			classText = classText + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";

			classText = classText + "\n\tpublic void boDelete()  throws BusException {\n\t\ttry {\n\t\t\t"
					+ "//Calling DM Modify....\n\t\t\t "+
					this.className+"DM.dmDelete(doObj);";
			classText = classText + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";
		}
		if(App.selectedModel==2) {
			classText = classText + "public void boInsertHist()  throws BusException {\n\t\ttry {\n\t\t\t//Calling DM Create....\n\t\t\t "
					+this.className+"DM.dmInsertHist(doObj);";
			classText = classText + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";

			classText = classText + "\n\tpublic "+this.className+"DO boGetHist()  throws BusException {\n\t\ttry "
					+ "{\n\t\t\t//Calling DM Get....\n\t\t\t return "+
					this.className+"DM.dmGetHist(doObj);";
			classText = classText + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";
		}
		if(App.selectedModel == 3) {
			classText = classText + mCreateChaincode.boCCGetStmt(this) + "\n\t"+mCreateChaincode.boCCPutStmt(this)
			+"\n\t"+ mCreateChaincode.boCCUpdateStmt(this);
		}

		for(int it=0;it<this.oprList.size();it++)
		{
			if(App.selectedModel==3)
				continue;
			mOperation oprObj = this.oprList.get(it);
			OprTaggedValuesMap = App.taggedValues.get(oprObj.xmi_id);
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

			for(int itParam=0;itParam < oprObj.params.size(); itParam ++)
			{
				mParams paramObj = oprObj.params.get(itParam);

				if(paramObj.kind.equalsIgnoreCase("return"))
					retClsName = paramObj.type;
				else
				{
					boInputString = boInputString + " " +paramObj.type+" ip" +paramObj.ParamName+",";
					dmInputString = dmInputString + " ip" + paramObj.ParamName+",";
				}
			}

			if(dmInputString.length()>1)
				dmInputString = dmInputString.substring(0,dmInputString.length()-1);
			if(boInputString.length()>1)
				boInputString = boInputString.substring(0,boInputString.length()-1);

			if(gListFlag)
				retString = "List<"+retClsName+">";
			else
				retString = retClsName;

			methodSign = methodSign + "\n\tpublic "+ retString + "  "+oprObj.oprName+" ( "+boInputString + ")\n\t\t\t" 
					+ "  throws BusException {";

			if(gListFlag)
			{
				methodBody = methodBody +  "\n\t\ttry {\n\t\t\t//Calling DM Modify....\n\t\t\t "+
						"return "+this.className+"DM.dmGetList("+dmInputString+");";
				methodBody = methodBody + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}";
			}

		}
		classText = classText + methodSign +" " + methodBody + "\n\t}\n}";

		writeToFile(pathToFiles, classOutName, classText, 0);
		classText = "";
	}

	//Below function check if the given key is present in the HashMap...returns true or false ...
	public static boolean isKeyPresent(HashMap<String, String> tempMap, String keyToBeChecked) {

		boolean isKeyPresent = false;
		if(tempMap!= null) {
			Iterator<HashMap.Entry<String, String>> iterator = tempMap.entrySet().iterator(); 
			while (iterator.hasNext()) { 

				Map.Entry<String, String> entry = iterator.next(); 

				if (keyToBeChecked.equalsIgnoreCase(entry.getKey())) { 

					isKeyPresent = true; 
				} 
			}  
		}
		return isKeyPresent;
	}

	// This function is used in creating Class statement consisting of Var Declaration along with corresponding Getters and Setters......
	public String setClassStmt(String classOutName, boolean DOFlag, boolean retVOFlag) {

		String retClsString = "";
		if(this.accessSpecifier == "")
			this.accessSpecifier = "public";
		String getterSetter = "";
		String varDec = "";
		String toStrStmt = "";

		//itreration through the attribute list to create dynamic strings...
		for(int itrAttr = 0; itrAttr <= this.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = this.attrList.get(itrAttr);
			String attrName  = itrOBJ.attrName;
			String attrDatatype  = itrOBJ.attrDataType;
			boolean scoflag = false;

			if(App.taggedValues.containsKey(itrOBJ.xmi_id)){
				if(App.taggedValues.get(itrOBJ.xmi_id).containsKey("SCOCD")) {
					scoflag = true;
					App.globalSCOFLag = true;
				}
			}

			//Getting the Java Datatype from the corresponding XMI datatype... 
			if(isKeyPresent(mMaps.XmiToJavaDTMap, attrDatatype))
				attrDatatype = mMaps.XmiToJavaDTMap.get(attrDatatype);

			if(attrDatatype.contains("[]"))
				attrDatatype = "List<"+attrDatatype.substring(0,attrDatatype.length()-2)+"DO>";

			//Creating getter and setter for DO classes...
			getterSetter = getterSetter + "\n\tpublic " + attrDatatype + " get"+ attrName.substring(0, 1).toUpperCase()
					+attrName.substring(1,attrName.length())+
					"(){\n\t\t return "+ attrName+";\n\t}";
			getterSetter = getterSetter + "\n\tpublic void set"+attrName.substring(0, 1).toUpperCase()
					+attrName.substring(1,attrName.length())+
					"("+attrDatatype + " " + attrName + "){\n\t\t this."+attrName+ " = " + attrName+";\n\t}";

			//if any SCO type variable is present....we have to make corresponding attrName_cd attribute also......
			if(scoflag)
			{
				getterSetter = getterSetter + "\n\tpublic " + attrDatatype + " get"+ attrName.substring(0, 1).toUpperCase()
						+attrName.substring(1,attrName.length())+
						"_cd(){\n\t\t return "+ attrName+"_cd;\n\t}";

				getterSetter = getterSetter + "\n\tpublic void set"+attrName.substring(0, 1).toUpperCase()
						+attrName.substring(1,attrName.length())+
						"_cd("+attrDatatype + " " + attrName + "_cd){\n\t\t this."+attrName+ "_cd = " + attrName+"_cd;\n\t}";
			}

			//Constitute the Attribute Statement ...
			varDec = varDec + itrOBJ.setAttributeStmt( retVOFlag);	

			//Creating to String statement for the DO class......
			String sepVar = "";
			if(itrAttr==0) {
				sepVar = "";
			}
			else
			{
				sepVar = ", ";
			}

			toStrStmt = toStrStmt + sepVar + attrName+ "=\" + " + attrName +"+\"";
			if(scoflag) {
				toStrStmt = toStrStmt + sepVar + attrName+ "_cd=\" + " + attrName +"_cd+\"";
			}

		}

		//Setting the generated strings into the final return string....
		retClsString = retClsString + "\n\t//Variable Declaration...\n\t";
		retClsString = retClsString + varDec;
		retClsString = retClsString + "//Getters and Setters for the variables....";
		retClsString = retClsString + getterSetter;

		//Below ToString statement is only needed in DO type Object....this flag will be passed when calling this function....
		if(DOFlag)
			retClsString = retClsString + "\n\t@Override\n\t" +"public String toString() {\n\t return \""+this.className+ " [" + toStrStmt + "]\";\n\t}";

		return retClsString;
	}

	//Setting up content for DM class......
	public void createDMClass() {

		String pathToDMFiles =  App.pathMap.get("DMPath");
		String classOutName = this.className+"DM";
		String finalClassText = "";
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		if(this.accessSpecifier == "")
			this.accessSpecifier = "public";

		boolean gListFlag = false;
		for(int k=0; k<=this.oprList.size()-1;k++)
		{
			taggedValuesMap = App.taggedValues.get(this.oprList.get(k).xmi_id);
			String vStr= "";
			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsGList");
			if(vStr==null)
				gListFlag = false;
			else if(vStr.equalsIgnoreCase("false"))
				gListFlag = false;
			else
				gListFlag = true;
		}	
		finalClassText = finalClassText + "package " +App.pathMap.get("DMPkgName") + ";\n";
		finalClassText = finalClassText + "import java.util.*;\n";
		finalClassText = finalClassText + "import java.util.Date;\n";
		finalClassText = finalClassText + "import java.sql.*;\n";
		finalClassText = finalClassText + "import "+ App.pathMap.get("DOPkgName")+".*;\n";
		finalClassText = finalClassText + "import "+ App.pathMap.get("BOPkgName")+".*;\n";
		finalClassText = finalClassText + "import "+ App.pathMap.get("VOPkgName")+".*;\n";
		finalClassText = finalClassText + "import java.text.SimpleDateFormat;\n";
		finalClassText = finalClassText + "import java.time.LocalDate;\n";
		finalClassText = finalClassText + "import java.time.LocalDateTime;\n";
		finalClassText = finalClassText + "import java.time.ZoneOffset;\n";
		finalClassText = finalClassText + "import com.cw.na.arch.*;\n";
		finalClassText = finalClassText + "public class "+ classOutName + "{\n\t";
		finalClassText = finalClassText + "//Global Variables declaration....\n\t";
		finalClassText = finalClassText + "public static LocalDate DEFDATE = LocalDate.ofEpochDay(0);\n\t";
		finalClassText = finalClassText + "public static LocalDateTime DEFDATETIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);\n\t";

		//calling corresponding function for function generation...
		finalClassText = finalClassText + mCreateDMFunctions.createDMGetStmt(this) + "\n\n" 
				+ mCreateDMFunctions.createDMInsertStmt(this) + "\n\n" 
				+ mCreateDMFunctions.createDMModStmt(this) + "\n\n" 
				+ mCreateDMFunctions.createDMDeleteStmt(this) + "\n\n";
		if(App.selectedModel==2) {
			finalClassText = finalClassText + mCreateDMFunctions.createDMGetHistStmt(this) + "\n\n"
					+ mCreateDMFunctions.createDMInsertHistStmt(this) + "\n\n";
		}
		if(gListFlag)
			finalClassText = finalClassText + mCreateDMFunctions.createDMGetListStmt(this); 
		finalClassText = finalClassText + "\n}";	

		writeToFile(pathToDMFiles, classOutName, finalClassText, 0);
	}

	//Setting up Request Body Parser class.....
	public void createParserClass() {

		String pathToParserFiles =  App.pathMap.get("ServicePath");
		String parserClassName = this.className+"RequestBodyParser";
		String parserClassText = mCreateServices.createParserClass(this);
		writeToFile(pathToParserFiles, parserClassName, parserClassText, 0);
	}

	//Setting up content for the Service File...which in turn calls createDMFunction class for respective function statements
	public void createServiceFile() {
		String pathToServiceFiles =  App.pathMap.get("ServicePath");
		String classOutName = this.className+"Controller";
		String finalClassText = mCreateServices.createServiceClass(this);
		writeToFile(pathToServiceFiles, classOutName, finalClassText, 0);
	}

	// Creating SQL DDL statement for the classes....
	public void createDDLFile() {

		String pathToDDLFile = App.pathMap.get("DDLPath");
		String classOutName = this.className+"DDL";
		String tableName = "";
		String ddlStmtText = "";
		String colString = "\t";
		String colName = ""; 
		String pkColName = "";
		int colLength = 0;
		String nullablility = "";
		String autoGen = "AUTO_INCREMENT";
		boolean autoGenFlag = false;
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap ;

		//iterating through the attribute list.....
		for(int itrAttr = 0; itrAttr <= this.attrList.size()-1; itrAttr++) {
			boolean scoFlag = false;
			mAttribute itrOBJ = this.attrList.get(itrAttr);
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

				//if DBname is given in the XMI then Use it....else attribute name will be used...
				if(taggedValuesMap!=null)
				{
					if(taggedValuesMap.containsKey("DBName"))
						colName = App.taggedValues.get(itrOBJ.xmi_id).get("DBName");
					else
						colName = itrOBJ.attrName;
				}
				if(taggedValuesMap!=null)
				{
					if(taggedValuesMap.containsKey("Length"))
						colLength = Integer.valueOf(App.taggedValues.get(itrOBJ.xmi_id).get("Length"));
					else
						colLength = 20;
				}
				//if attribute is of type nullable or not....
				if(itrOBJ.isNull) {
					nullablility = "NULL";
				}else {
					nullablility = "NOT NULL";
				}

				//if dataype of is string and length is <10 then sql datatype is CHAR else VARCHAR
				String sqlDType = "";
				sqlDType = mMaps.XmiToSqlDTMap.get(itrOBJ.attrDataType);
				if(sqlDType.contains("CHAR") && colLength < 10) {
					sqlDType = "CHAR";
				}

				// is sql datatype contains CHAR then add length to the statement....
				if(sqlDType!= null && sqlDType.contains("CHAR"))
				{
					sqlDType= sqlDType + "( "+ colLength+" )";
				}
				String lastComma = ",";
				if(taggedValuesMap!=null)
				{
					//primary key flag....
					if(taggedValuesMap.containsKey("IsPK")) {
						autoGenFlag = true;
						pkColName = colName;
					}

					//if SCO type column is present in the class..then its _cd column needs to be created....
					if(taggedValuesMap.containsKey("SCOCD")) {
						scoFlag = true;
					}

					if(!taggedValuesMap.containsKey("AutoGen"))
						autoGen = "";

					//creating final column string for the ddl statement....
					if(itrAttr == this.attrList.size()-1) {
						lastComma = ",";
					}
				}
				colString = colString + colName + " " + sqlDType +" "+ nullablility +" "+autoGen+lastComma+" \n\t\t";

				if(scoFlag) {
					colString = colString + colName + "_cd " + sqlDType+" "+nullablility+lastComma+" ,\n\t\t";
				}
				if(itrAttr == this.attrList.size()-1) {
					if(autoGenFlag)
						colString = colString + "PRIMARY KEY ( " + pkColName +" )\n\t";
				}
			}
		}

		//setting up table name.....
		if(App.taggedValues.containsKey(this.xmi_id))
			tableName = App.taggedValues.get(this.xmi_id).get("DBName");
		else
			tableName = this.className;

		//Final DDL text......
		ddlStmtText =  ddlStmtText + "--Please Copy Paste this in MySQL Console before executing rest of the project...\n";
		ddlStmtText =  ddlStmtText + "DROP TABLE IF EXISTS "+tableName+";\n CREATE TABLE " + tableName + "(\n\t" + colString+");";

		writeToFile(pathToDDLFile, classOutName, ddlStmtText, 1);
		ddlStmtText = "";
	}

	public void createChaincodeFile() {
		String finalChaincodeText = "";
		String packageFileText = "";
		String pathToCCFile = App.pathMap.get("CCPath");
		String classOutName = this.className+"ChainCode";
		finalChaincodeText = finalChaincodeText+"/*Copyright Chainworks. All Rights Reserved.SPDX-License-Identifier: Apache-2.0*/";

		finalChaincodeText = finalChaincodeText+"\nconst shim = require('fabric-shim');\nconst util = require('util');\n";
		finalChaincodeText = finalChaincodeText+"var Chaincode = class {\n\t";
		finalChaincodeText = finalChaincodeText+"// Initialize the chaincode\n\tasync Init(stub) {\n\t\treturn shim.success();\n\t}\n"; 
		finalChaincodeText = finalChaincodeText+"\n\tasync Invoke(stub) {\n\tlet ret = stub.getFunctionAndParameters();\n\tconsole.info(ret);";
		finalChaincodeText = finalChaincodeText+"\n\tlet method = this[ret.fcn];\n\tif (!method) {\n\t\treturn shim.error('no method of name:' + ret.fcn + ' found');\n\t}";
		finalChaincodeText = finalChaincodeText+"\n\ttry {\n\t\tlet payload = await method(stub, ret.params);\n\t\treturn shim.success(payload);\n\t}";
		finalChaincodeText = finalChaincodeText+"\n\tcatch (err) {\n\t\treturn shim.error(err);\n\t\t}\n\t}";
		finalChaincodeText = finalChaincodeText+"\n\t"+mCreateChaincode.ccCreateStmt(this) + "\n\t" + mCreateChaincode.ccGetStmt(this) ;

		finalChaincodeText = finalChaincodeText+"\n};\n\n\tshim.start(new Chaincode());";
		writeToFile(pathToCCFile, classOutName, finalChaincodeText, 2);
		finalChaincodeText = "";

		packageFileText = packageFileText+ "{\r\n" + 
				"\n\t\"name\": \""+classOutName+"\",\r\n" + 
				"\n\t\"version\": \"1.0.0\",\r\n" + 
				"\n\t\"description\": \"International Banking Functions\",\r\n" + 
				"\n\t\"engines\": {\r\n" + 
				"\n\t\t\"node\": \">=8.4.0\",\r\n" + 
				"\n\t\t\"npm\": \">=5.3.0\"\r\n" + 
				"\n\t},\r\n" + 
				"\n\t\"scripts\": { \"start\" : \"node "+classOutName+".js\" },\r\n" + 
				"\n\t\"engine-strict\": true,\r\n" + 
				"\n\t\"license\": \"Apache-2.0\",\r\n" + 
				"\n\t\"dependencies\": {\r\n" + 
				"\n\t\t\"fabric-shim\": \"~1.3.0\"\r\n" + 
				"\n\t}\r\n" + 
				"}\r\n";
		writeToFile(pathToCCFile, "package", packageFileText, 3);
		packageFileText = "";
	}
}






