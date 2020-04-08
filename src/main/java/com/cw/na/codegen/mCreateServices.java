package com.cw.na.codegen;

import java.util.HashMap;
import java.util.List;

public class mCreateServices {


	public static String printBaseClassParser(mClass classObj, boolean jsonMethodFlag) {
		String defFntnStmt = "";
		String inputDataType = "String json";
		String exceptionParam =  "json";
		if(jsonMethodFlag) {
			inputDataType = "JSONObject mparamDO";
			exceptionParam = "\""+classObj.className+"\"";
		}
		defFntnStmt = defFntnStmt + "\n\tpublic static "+classObj.className+"DO get"+classObj.className
				+"ObjectFromJSON("+inputDataType+") throws BusException {\n\t\t";
		//Var Declaration...
		defFntnStmt = defFntnStmt + "ObjectMapper mapper = new ObjectMapper();\n\t\t";
		defFntnStmt = defFntnStmt + classObj.className+"DO retObjDO = null;\n\t\ttry{\n\t\t\t";
		if(!jsonMethodFlag) {
			//Json Parsing stub....
			defFntnStmt = defFntnStmt + "JSONArray jsonArray = new JSONArray(json);\n\t\t";
			defFntnStmt = defFntnStmt + "JSONObject paramDO = jsonArray.getJSONObject(0);\n\t\t";
			defFntnStmt = defFntnStmt + "JSONObject mparamDO = paramDO.getJSONObject(\""+classObj.className+"\");\n\t\t";
		}
		defFntnStmt = defFntnStmt + "String myparamDO = \"[\"+mparamDO+\"]\";\n\t\t";
		defFntnStmt = defFntnStmt + "SimpleDateFormat df = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n\t\t";
		//Mapper Settings...
		defFntnStmt = defFntnStmt + "mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);\n\t\t";
		defFntnStmt = defFntnStmt + "mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);\n\t\t";
		defFntnStmt = defFntnStmt + "mapper.findAndRegisterModules();\n\t\t";
		defFntnStmt = defFntnStmt + "mapper.setDateFormat(df);\n\t\t";
		//Try catch for converting json to List...
		defFntnStmt = defFntnStmt + "List<Map<String, Object>> multipleDataDO = null;\n\t\t";
		defFntnStmt = defFntnStmt + "\n\t\t\tmultipleDataDO = JsonToMapConvertor.jsonToList(myparamDO,true);\n\t\t\n\t\t";

		//for loop to populate the DO from the json...
		defFntnStmt = defFntnStmt + "for (Map<String, Object> singleData : multipleDataDO){\n\t\t\t";
		defFntnStmt = defFntnStmt + "retObjDO = ("+classObj.className+"DO) mapper.convertValue(singleData, "+classObj.className+"DO.class)"
				+ ";\n\t\t\tbreak;\n\t\t}\n\t\t}";
		defFntnStmt = defFntnStmt + "\n\t\tcatch (JSONException | ParseException | IllegalArgumentException exception) {\n\t\t\t"
				+"throw new BusException(\"EFR202\", \""+classObj.className+"RequestBodyParser\", \"get"+classObj.className+"ObjectFromJSON\",exception);";
		defFntnStmt = defFntnStmt + "\n\t}\n\t\treturn retObjDO;\n\t}";
		return defFntnStmt;
	}

	public static String[] setStmts(String paramName, String paramDtype, int counter, boolean instanceFlag, int jsonCounter) {
		String[] retStrArray = new String[4];
		String mulDec = "";
		String mulData = "";
		String forLoop = "";
		String RBPJsonString = "";
		String jsonObjStr = paramName;

		if(instanceFlag) {
			//99 Corresponds to DO
			counter = 99;
			if(jsonCounter==0) {
				paramName = "paramDO";
				jsonObjStr = paramDtype + "DO";
			}
		}

		//Creating parsing of json string for the params...
		RBPJsonString = RBPJsonString + "JSONObject " + paramName + " = jsonArray.getJSONObject("+(jsonCounter)+");\n\t\t\t";
		RBPJsonString = RBPJsonString + "JSONObject m"+ paramName + " = " + paramName+".getJSONObject(\""+jsonObjStr+"\");\n\t\t\t";
		RBPJsonString = RBPJsonString + "String my"+ paramName + " = \"[\"+m"+paramName+"+\"]\";\n\t\t\t";

		//multiple data variable declaration.....
		mulDec = mulDec + "List<Map<String, Object>> multipleData"+(counter)+" = null;\n\t\t\t";

		//converting from json to list for each parsed json string for any object....
		mulData = mulData+ "multipleData"+(counter)+" = JsonToMapConvertor.jsonToList(my"+paramName+",true);\n\t\t\t";

		//content for the for loop in the end....
		forLoop = forLoop + paramDtype+" retObj"+(counter)+" = null;\n\t\t\t";
		forLoop = forLoop + "for (Map<String, Object> singleData : multipleData"+(counter)+"){\n\t\t\t\t";
		forLoop = forLoop + "retObj"+(counter)+" = ("+paramDtype+") mapper.convertValue(singleData, "+paramDtype+".class);"
				+ "\n\t\t\t\tbreak;\n\t\t\t}";
		forLoop = forLoop + "\n\t\t\tretObjList.add(retObj"+(counter)+");\n\t\t\t";

		retStrArray[0] = mulDec;
		retStrArray[1] = mulData;
		retStrArray[2] = forLoop;
		retStrArray[3] = RBPJsonString;
		return retStrArray;
	}
	public static String createDefaultDMCaseStmts(mClass classObj) {
		String tempCaseStmtText = "";
		int noOfDefCaseStmt = 7;
		boolean ccFlag = false;
		if(App.selectedModel == 1 || App.selectedModel == 2 ) {
			noOfDefCaseStmt = 3;
		}else if(App.selectedModel==3) 
		{
			noOfDefCaseStmt = 2;
			ccFlag = true;
		}
		for(int i = 0; i<= noOfDefCaseStmt; i++)
		{
			String fnType = "";
			if(!ccFlag) {
				if(i==0)
					fnType = "boGet";
				else if(i==1)
					fnType = "boInsert";
				else if(i==2)
					fnType = "boUpdate";
				else if(i==3)
					fnType = "boDelete";
			}else {
				if(i==0)
					fnType = "ccGet";				
				else if(i==1)
					fnType = "ccPut";
				else if(i==2)
					fnType = "ccUpdate";
			}
			tempCaseStmtText = tempCaseStmtText + "\n\t\t\tcase \""+fnType+"\":\n\t\t\t\t";
			if(i!=0)
				tempCaseStmtText = tempCaseStmtText + "InfraObject.BeginTransaction();\n\t\t\t\t";
			tempCaseStmtText = tempCaseStmtText + classObj.className+"BO cls"+classObj.className+"BO"+i+" = new "+classObj.className+"BO();\n\t\t\t\t";
			tempCaseStmtText = tempCaseStmtText + classObj.className+"DO cls"+classObj.className+"DO"+i+"= null;\n\t\t\t\t";
			tempCaseStmtText = tempCaseStmtText + "cls"+classObj.className+"DO"+i+" = "+classObj.className+"RequestBodyParser.get"+classObj.className+"ObjectFromJSON(jsonObject);\n\t\t\t\t";
			tempCaseStmtText = tempCaseStmtText + "cls"+classObj.className+"BO"+i+".doObj = cls"+classObj.className+"DO"+i+";\n\t\t\t\t";
			if(i==0)
				tempCaseStmtText = tempCaseStmtText + "cls"+classObj.className+"DO"+i+" = cls"+classObj.className+"BO"+i+"."+fnType+"();\n\t\t\t\t";
			else
				tempCaseStmtText = tempCaseStmtText +" cls"+classObj.className+"BO"+i+"."+fnType+"();\n\t\t\t\t";

			tempCaseStmtText = tempCaseStmtText + "classString = ow.writeValueAsString(cls"+classObj.className+"DO"+i+");\n\t\t\t\t";
			tempCaseStmtText = tempCaseStmtText + "finalReturnValue = \"[\" +classString+\"]\";\n\t\t\t\t";
			tempCaseStmtText = tempCaseStmtText + "break;";
		}
		return tempCaseStmtText;
	}

	//Setting up content for the service file......
	public static String createServiceClass(mClass classObj) {


		String classOutName = classObj.className+"Controller";
		String parserClassName = classObj.className+"RequestBodyParser";
		String finalClassText = "";
		String ParamDec = "\t";
		String listItrRet = "";
		String oprName = "";
		String paramConcat = "";
		String oprType = "";
		String mapperString = "";
		String caseString = "\t";
		boolean retKindFlag = false;
		boolean inKindFlag = false;
		boolean instanceFlag = false;
		boolean getListFlag = false;
		String retString = "";
		String retDO = "";
		boolean isMullFlag = false;
		String voClassName = "";
		boolean isVOFlag = false;
		HashMap<String, String> OprTaggedValuesMap = new HashMap<String, String>();
		HashMap<String, String> ParamTaggedValuesMap = new HashMap<String, String>();
		HashMap<String, String> ClassTaggedValuesMap = new HashMap<String, String>();


		//create corresponding parser class first.....
		createParserClass(classObj);


		//Setting up case string for all the default dm functions: Get, Create, Modify....
		String defCaseStmt =  createDefaultDMCaseStmts(classObj);

		caseString = caseString + defCaseStmt;

		if(App.selectedModel==1 || App.selectedModel==2 ) {
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_1\",\"boGet\");\n\t\t\t\t";
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_2\",\"boInsert\");\n\t\t\t\t";
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_3\",\"boUpdate\");\n\t\t\t\t";
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_4\",\"boDelete\");\n\t\t\t\t";
		}else if(App.selectedModel==3 ) {
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_6\",\"ccGet\");\n\t\t\t\t";
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_7\",\"ccPut\");\n\t\t\t\t";
			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_8\",\"ccUpdate\");\n\t\t\t\t";
		}
		//iterating through the operaions list...
		for(int itrOpr = 0; itrOpr <= classObj.oprList.size()-1; itrOpr++) {
			if(App.selectedModel==3) 
			{
				continue;
			}
			mOperation itrObj = classObj.oprList.get(itrOpr);
			List<mParams> tempPList = itrObj.params;
			oprName = itrObj.oprName;
			oprType = itrObj.ownerScope;

			OprTaggedValuesMap = App.taggedValues.get(itrObj.xmi_id);

			//if the operation of type getlist is present the set the getListFlag true for later use.....
			String vStr = null;
			if(OprTaggedValuesMap!=null)
				vStr = OprTaggedValuesMap.get("IsGList");
			if(vStr==null)
				getListFlag = false;
			else if(vStr.equalsIgnoreCase("true"))
				getListFlag = true;
			else
				getListFlag = false;

			String beginTrans = "";
			if(!getListFlag)
			{
				beginTrans = "InfraObject.BeginTransaction();\n\t\t\t";
			}

			//Var declaration of the service class.....
			String listString = "retParsedList"+(itrOpr+1);

			mapperString = mapperString + "idToMethodMapper.put(\""+classObj.className+"_"+(itrOpr+5)+"\",\""+oprName+"\");\n\t\t\t\t";

			caseString = caseString + "\n\t\t\tcase \""+oprName + "\":\n\t\t\t\t"+beginTrans+"retString = \"[\";\n\t\t\t\t"
					+classObj.className+"BO objBO"+(itrOpr+1)+" = new "+classObj.className+"BO();"
					+ "\n\t\t\t\tList<Object> "+listString+" = "+parserClassName+".get"+classObj.className+"_"+oprName+"Params(jsonObject);"
					+ "\n\t\t\t\t";

			//if operation is of type instance....then generate declaration string for the DO objects......
			if(oprType.equalsIgnoreCase("instance")) {
				instanceFlag = true;
				caseString = caseString + "\n\t\t\t\tobjBO"+(itrOpr+1)+".doObj = ("+classObj.className+"DO) "+listString+".get(0);\n\t\t\t";
				if(!getListFlag)
					retDO = retDO + "\n\t\t\t\tretDOString = ow.writeValueAsString(objBO"+(itrOpr+1)+".doObj);";
			}

			//going over parameters list.....
			for(int k = 0; k <= tempPList.size()-1; k++) {

				String paramName  = tempPList.get(k).ParamName;
				String paramKind = tempPList.get(k).kind;
				String paramDtype = tempPList.get(k).type;
				String paramXMI = tempPList.get(k).xmi_id;

				ParamTaggedValuesMap = App.taggedValues.get(paramXMI);

				//Getting the classname from the xmi...
				String classXMI = App.getKeyByValue(App.XmiClassMap, paramDtype);
				String mullStr = null;
				String isVO = null;

				if(ParamTaggedValuesMap!=null)
					mullStr = ParamTaggedValuesMap.get("IsMull");

				if(mullStr==null)
					isMullFlag = false;
				else if(mullStr.equalsIgnoreCase("true"))
					isMullFlag = true;
				else
					isMullFlag = false;

				//Getting tagged values from the class....
				ClassTaggedValuesMap = App.taggedValues.get(classXMI);
				if(ClassTaggedValuesMap!=null)
					isVO = ClassTaggedValuesMap.get("IsVO");

				if(isVO==null)
					isVOFlag = false;
				else if(isVO.equalsIgnoreCase("true"))
					isVOFlag = true;
				else
					isVOFlag = false;

				if(isVOFlag)
					voClassName = paramDtype;

				//classname based on whether it is of type getlist or VO else DO...

				retDO = "";
				if(!isVOFlag)
				{
					paramDtype = paramDtype+"DO";
				}

				//Parameter declaration string for "in" type  
				if(paramKind.equalsIgnoreCase("in")) {
					inKindFlag = true;
					if(instanceFlag) {
						ParamDec = ParamDec + paramDtype + " "+paramName+" = ("+paramDtype+") "+listString
								+".get("+(k+1)+");\n\t\t\t\t";
						paramConcat = paramConcat + ", "+paramName;
					}else {
						ParamDec = ParamDec + paramDtype + " "+paramName+" = ("+paramDtype+") "+listString
								+".get("+(k)+");\n\t\t\t\t";
						paramConcat = paramConcat + ", "+paramName;	
					}								
				}

				//Setting retrun type flag...
				if(paramKind.equalsIgnoreCase("return") ||  paramKind.equalsIgnoreCase("inout"))
				{
					retKindFlag = true;
					retString = paramDtype;
				}
			}

			if(!inKindFlag)
				paramConcat = " ";

			caseString = caseString + ParamDec;

			String retTypeStr = "";
			String prmConcat = "";

			// setting up case string from the parameters list....
			if(paramConcat.indexOf(',')!=-1) {
				prmConcat = paramConcat.substring(paramConcat.indexOf(',')+1);

			}
			if(retKindFlag) {
				if(getListFlag || isMullFlag) {
					retTypeStr = "List<" +retString+"> retObj"+(itrOpr+1)+ " =";
				}
				else
					retTypeStr = retString+ " retObj"+(itrOpr+1) + " = ";
			}
			caseString = caseString +voClassName+" objVO"+(itrOpr+1)+" = null;";


			listItrRet = listItrRet + "\n\t\t\t\tfor ( int i = 0, sz = retObj"+(itrOpr+1)+".size(); i < sz ; i++)"+
					"{\n\t\t\t\t\tif ( i != 0)\n\t\t\t\t\t\tretString = retString + \",\";\n\t\t\t\t\t";
			listItrRet = listItrRet +" objVO"+(itrOpr+1)+" = retObj"+(itrOpr+1)+".get(i);\n\t\t\t\t";
			listItrRet = listItrRet + "String objString = ow.writeValueAsString("+" objVO"+(itrOpr+1)+");\n\t\t\t\t";
			listItrRet = listItrRet + "retString = retString +objString;\n\t\t\t\t}\n\t\t\t\t";
			listItrRet = listItrRet + "retString = retString + \"]\";\n\t\t\t\t";
			listItrRet = listItrRet + "finalReturnValue=retString;";

			caseString = caseString +  "\n\t\t\t\t" +retTypeStr	+" objBO"+(itrOpr+1)+"."+oprName+"("+prmConcat+");";

			//if instance type opr...then add DO return string....
			if(instanceFlag)
				caseString = caseString + retDO;

			//if return flag true....then marshall the output object....
			if(retKindFlag)
			{
				if(isMullFlag)
					caseString = caseString + listItrRet;
				else
					caseString = caseString + "\n\t\t\t\tretString = ow.writeValueAsString(retObj"+(itrOpr+1)+");";
			}

			if(!isMullFlag) {
				if(instanceFlag)
					caseString = caseString + "\n\t\t\t\tfinalReturnValue=\"[\"+retDOString+\",\"+retString+\"]\";";
				else 
					caseString = caseString + "\n\t\t\t\tfinalReturnValue=\"[\"+retDOString+\"]\";";
			}
			caseString = caseString + "\n\t\t\t\tbreak;\n\t";
			retKindFlag = false;
			retDO = "";
			listString = "";
			ParamDec  = "";
			prmConcat = "";
			listItrRet = "";
			paramConcat = "";
			inKindFlag = false;

		}
		if(!retKindFlag) {
			retString = classObj.className+"DO ";
		}

		//final text for the service class.....
		finalClassText = finalClassText + "package " +App.pathMap.get("SVCPkgName") + ";\n";
		finalClassText = finalClassText + "import java.util.*;\n";
		finalClassText = finalClassText + "import java.util.Date;\n";
		finalClassText = finalClassText + "import java.sql.*;\n";
		finalClassText = finalClassText + "import "+ App.pathMap.get("DOPkgName")+".*;\n";
		finalClassText = finalClassText + "import "+ App.pathMap.get("BOPkgName")+".*;\n";
		if(isVOFlag)
			finalClassText = finalClassText + "import "+ App.pathMap.get("VOPkgName")+".*;\n";

		finalClassText = finalClassText + "import java.text.SimpleDateFormat;\n";
		finalClassText = finalClassText + "import com.cw.na.arch.*;\n";
		finalClassText = finalClassText + "import com.fasterxml.jackson.core.JsonProcessingException;\n";
		finalClassText = finalClassText + "import com.fasterxml.jackson.databind.ObjectMapper;\n";
		finalClassText = finalClassText + "import com.fasterxml.jackson.databind.ObjectWriter;\n\n";
		finalClassText = finalClassText + "public class "+ classOutName + "{\n\t";
		finalClassText = finalClassText + "public static Map<String, String> idToMethodMapper;\n\t";
		finalClassText = finalClassText + "public "+classOutName+"() {\n\t\tidToMethodMapper = new HashMap<>();\n\t\t";
		finalClassText = finalClassText + mapperString;
		finalClassText = finalClassText + "}\n\tpublic String execute(String id, String jsonObject) throws BusException{\n\t";
		finalClassText = finalClassText + "\tcom.cw.na.arch.Error errorObj = new com.cw.na.arch.Error();\n\t\t\t\tString classString = \"\";";
		finalClassText = finalClassText + "\n\t\tString value = idToMethodMapper.get(id);\n\t\tString finalReturnValue = null;";
		finalClassText = finalClassText + "\n\t\tObject[] values= null;\n\t\tObject[] returnJsonObject = null;";
		finalClassText = finalClassText + "\n\t\tString retString= null;\n\t\tString retDOString= null;\n\t\t"
				+ "ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();";
		finalClassText = finalClassText +  "\n\t\ttry {\n\t\t\tswitch(value) {\n\t\t\t" + caseString;
		finalClassText = finalClassText + "\n\t\t}\n\t\t}catch (BusException busException) {\n\t\tInfraObject.logger.debug(\"RequestParser exception\");\n\t" 
				+ "\tthrow busException;\n\t\t\t} catch (Exception ex) {\r\n" + 
				"\t\tthrow new BusException(\"EFR202\", \""+classObj.className+"Controller\", \"Execute\", ex );\n\t\t\t}\n\t\treturn finalReturnValue;\r\n\t}";
		finalClassText = finalClassText + "\n}";	
		return finalClassText;
	}

	public static String createParserClass(mClass classObj) {
		String parserClassName = classObj.className+"RequestBodyParser";
		String parserClassText = "";
		String RBPJsonString = "";
		String rbpInterimText = "\t";
		String oprType = "";
		String mulData = "\n\t\t\t";
		String mulDataDec = "";
		String forLoop = "";
		String oprName ="";
		boolean isVOFlag = false;
		HashMap<String, String> paramClassTaggedValuesMap = new HashMap<String, String>();

		// creating the default parser function to parse default class mapping
		String defStrStmt = printBaseClassParser(classObj,false );
		String defJSONStmt = printBaseClassParser(classObj, true);

		//iterating through operations list..... 
		for(int itrAttr = 0; itrAttr <= classObj.oprList.size()-1; itrAttr++) {


			mOperation itrObj = classObj.oprList.get(itrAttr);
			oprName = itrObj.oprName;			
			List<mParams> paramList = itrObj.params;
			oprType = itrObj.ownerScope;

			boolean instanceFlag = false;

			//initial static content.....
			rbpInterimText = rbpInterimText + "\n\npublic static List<Object>  get"+classObj.className+"_"+oprName+"Params(String json)  throws BusException{\n\t\t\t";
			rbpInterimText = rbpInterimText + "ObjectMapper mapper = new ObjectMapper();\n\t\t\t";
			rbpInterimText = rbpInterimText + "List<Object> retObjList = new ArrayList<Object>();\n\t\t\ttry {\n\t\t\t";
			rbpInterimText = rbpInterimText + "SimpleDateFormat df = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n\t\t\t";
			rbpInterimText = rbpInterimText + "mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);\n\t\t\t";
			rbpInterimText = rbpInterimText + "mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);\n\t\t\t";
			rbpInterimText = rbpInterimText + "mapper.findAndRegisterModules();\n\t\t"; 
			rbpInterimText = rbpInterimText + "mapper.setDateFormat(df);\n\t\t\t";

			//Make DO objects and string if the operation is of type Instance.....ignore otherwise....
			//DO must be the First object in the JSON array....
			if(oprType.equalsIgnoreCase("instance")) {
				instanceFlag = true;
				String[] retArr = setStmts("paramDO", classObj.className+"DO", 99, true, 0);

				mulDataDec = mulDataDec + retArr[0];
				mulData = mulData + retArr[1];
				forLoop = forLoop + retArr[2];
				RBPJsonString = RBPJsonString + retArr[3];
			}

			//iterating over parameters list.....
			for(int k = 0; k <= paramList.size()-1; k++) {

				String paramName  = paramList.get(k).ParamName;
				String paramDtype = paramList.get(k).type;
				String paramKind = paramList.get(k).kind;

				int jsonCounter = k;

				//Getting the classname from the xmi...
				String paramClassXMI = App.getKeyByValue(App.XmiClassMap, paramDtype);

				//Getting tagged values from the class....
				paramClassTaggedValuesMap = App.taggedValues.get(paramClassXMI);
				String isVO = "";
				if(paramClassTaggedValuesMap!=null)
					isVO = paramClassTaggedValuesMap.get("IsVO");


				if(isVO==null)
					paramDtype = paramDtype + "DO";
				else if(isVO.equalsIgnoreCase("true")) {
					paramDtype = paramDtype;
					isVOFlag = true;
				}
				else
					paramDtype = paramDtype + "DO";

				//RBP will onyl be for the parameters of type in or inout...
				if(paramKind.equalsIgnoreCase("in") || paramKind.equalsIgnoreCase("inout")) {
					if(instanceFlag)
						jsonCounter = k+1;

					String[] retArr = setStmts(paramName, paramDtype, k+1, false, jsonCounter);

					mulDataDec = mulDataDec + retArr[0];
					mulData = mulData + retArr[1];
					forLoop = forLoop + retArr[2];
					RBPJsonString = RBPJsonString + retArr[3];
				}


			}

			//fixing the content to their respective places in the intermediate string....
			rbpInterimText = rbpInterimText + mulDataDec;
			rbpInterimText = rbpInterimText + "JSONArray jsonArray = new JSONArray(json);\n\t\t\t";
			rbpInterimText = rbpInterimText + RBPJsonString;
			mulData = mulData.trim();

			if(!mulData.equalsIgnoreCase(""))
				rbpInterimText = rbpInterimText + mulData;

			rbpInterimText = rbpInterimText + forLoop + "\n\t\t\treturn retObjList;" + "\n\t\t\t}";

			mulData = "";
			forLoop = "";		
			mulDataDec = "";
			RBPJsonString = "";			

		}

		//Making the final class text.....
		parserClassText = parserClassText + "package " +App.pathMap.get("SVCPkgName") + ";\n";
		parserClassText = parserClassText + "import java.text.ParseException;\n";
		parserClassText = parserClassText + "import java.util.List;\n";
		parserClassText = parserClassText + "import java.util.Map;\n";
		parserClassText = parserClassText + "import java.util.ArrayList;\n";
		parserClassText = parserClassText + "import org.json.JSONArray;\n";
		parserClassText = parserClassText + "import com.cw.na.arch.BusException;\n";
		parserClassText = parserClassText + "import org.json.JSONException;\n";
		parserClassText = parserClassText + "import java.text.SimpleDateFormat;\n";
		parserClassText = parserClassText + "import com.cw.na.common.utils.*;\n";
		parserClassText = parserClassText + "import org.json.JSONObject;\n";
		parserClassText = parserClassText + "import "+ App.pathMap.get("DOPkgName")+".*;\n";
		if(isVOFlag)
			parserClassText = parserClassText + "import "+ App.pathMap.get("VOPkgName")+".*;\n";
		parserClassText = parserClassText + "import com.fasterxml.jackson.databind.DeserializationFeature;\n";
		parserClassText = parserClassText + "import com.fasterxml.jackson.databind.ObjectMapper;\n";
		parserClassText = parserClassText + "public class "+parserClassName+" {\n\t" ;
		parserClassText = parserClassText +  defJSONStmt;
		parserClassText = parserClassText +  defStrStmt;
		parserClassText = parserClassText + rbpInterimText 
				+"\n\t\t\tcatch (JSONException | ParseException | IllegalArgumentException  exception) {\n\t\t\t\t\t"
				+ "throw new BusException(\"EFR202\", \""+classObj.className+"RequestBodyParser\", \"get"
				+classObj.className+"ObjectFromJSON\",exception);\n\t\t\t\t\t}\n\n\t\t\t}";
		parserClassText = parserClassText + "\n}";
		return parserClassText;
	}
}

