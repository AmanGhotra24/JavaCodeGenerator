package com.cw.na.codegen;

import java.util.HashMap;

public class mCreateChaincode {
	//Below functions are for Get function.....
	public static String ccCreateStmt(mClass clsObj) {

		String createText = "";
		String argsString = "";

		argsString = "let key = args[0];\n\t";
		argsString = argsString + "let jsonString = args[1];";

		//Setting the created dynamic strings into the final class text....
		createText = createText + "//Create function for "+ clsObj.className+ " .....\n\t";
		createText = createText + "async Create(stub, args) {";
		createText = createText + "\n\tif (args.length != 2)"
				+ "{\n\t\tthrow new Error('Incorrect number of arguments. Expecting 2...');\n\t}\n\t";

		createText = createText + argsString + " \n\t" 
				+ "try {\n\t\tawait stub.putState(key, Buffer.from(jsonString));\n\t}catch(err){\n\t"
				+"return shim.error(err);\n\t}\n}";
		return createText;

	}

	public static String ccGetStmt(mClass clsObj) {
		String getText = "";
		String pkColName = "";
		//iteration on attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);
			String attrName  = itrOBJ.attrName;

				//Storing the Datatype of the PK column....
				if(attrName.equalsIgnoreCase(clsObj.pk)) {
					pkColName = attrName;
				}
		}
		getText = getText + "//Get function for "+ clsObj.className+ " .....\n\t";
		getText = getText + "async Get(stub, args) {";
		getText = getText + "\n\tif (args.length != 1)"
				+ "{\n\t\tthrow new Error('Incorrect number of arguments. Expecting 1...');\n\t}\n\t";
		getText = getText + "let jsonResp = {};\n\tlet "+pkColName+" = args[0];\n\tlet "+pkColName+"ValueBytes = await stub.getState("+pkColName+");";
		getText = getText + "\n\tif("+pkColName+"ValueBytes){\n\t\tjsonResp.error = 'Failed to get state for ' + "+pkColName+";"
				+ "\n\t\tthrow new Error(JSON.stringify(jsonResp));\n\t}";
		getText = getText + "\n\tjsonResp.name = "+pkColName+";\n\treturn "+pkColName+"ValueBytes;\n\t}";

		return getText;
	}

	public static String boCCGetStmt(mClass clsObj) {
		String boCCGetStmt = "";
		clsObj.attrList.size();
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;


		taggedValuesMap = App.taggedValues.get(clsObj.xmi_id);
		String cCode  = null;
		if(taggedValuesMap!=null)
			cCode = taggedValuesMap.get("ClassCode");

		boCCGetStmt = boCCGetStmt + "\n\tpublic void ccGet()  throws BusException {\n\t\ttry {\n\t\t\t";
		boCCGetStmt = boCCGetStmt + "\n\t\t\ttry {";
		boCCGetStmt = boCCGetStmt + "\n\t\t\tString[] responseArray = new String[2];\n\t\t\t";
		boCCGetStmt = boCCGetStmt + "String url = InfraObject.cacheObj.getCCUrl();";
		boCCGetStmt = boCCGetStmt + "\n\t\t\tString key = String.valueOf(doObj."+clsObj.pk+");";
		boCCGetStmt = boCCGetStmt + "\n\t\t\tString classCode = \""+cCode+"\";";
		boCCGetStmt = boCCGetStmt + "\n\t\t\tint keyLen = key.length();\n\t\t\tStringBuilder keyString = new StringBuilder();";
		boCCGetStmt = boCCGetStmt + "\n\t\t\twhile(keyString.length()+keyLen<5) {\n\t\t\tkeyString.append('0');\n\t\t\t}\n\t\tkeyString.append(key);"+
				"\n\t\tkeyString.insert(0,classCode);";
		boCCGetStmt = boCCGetStmt + "\n\t\tString json = \"{\\\"channel\\\":  \\\"\"+InfraObject.cacheObj.getCCChannel()+\"\\\",\"" + 
				"\n\t\t+ \"\\\"chaincode\\\":  \\\""+clsObj.className+"ChainCode\\\",\"" + 
				"\n\t\t+ \"\\\"method\\\":  \\\"Get\\\",\"" + 
				"\n\t\t+ \"\\\"chaincodeVer\\\":  \\\"1.0\\\",\" + " + 
				"\n\t\t\"\\\"args\\\":  [\\\"\"+keyString+\"\\\"],\" + " + 
				"\n\t\t\"\\\"proposalWaitTime\\\": \"+MyConstants.ProposalWaitTime+\",\" + " + 
				"\n\t\t\"\\\"transactionWaitTime\\\": \"+MyConstants.TransactionWaitTime+\"\" + " + 
				"\n\t\t\"}\";";
		boCCGetStmt = boCCGetStmt + "\n\t\tresponseArray = CallRestAPI.ccCallUrl(url, json);\r\n" + 
				"\n\t\tString respCode = responseArray[0];\r\n" + 
				"\n\t\tString respValue = responseArray[1];";
		boCCGetStmt = boCCGetStmt + "\n\t\tif(respCode.equalsIgnoreCase(\"200\")) {" + 
				"\n\t\t\tJSONObject myResponse = new JSONObject(respValue);" + 
				"\n\t\t\tJSONObject result = myResponse.getJSONObject(\"result\");" + 
				"\n\t\t\tJSONObject payload = new JSONObject(result.getString(\"payload\"));" + 
				"\n\t\t\tSystem.out.println(\"ccGet respCode->\"+respCode);" + 
				"\n\t\t\tdoObj = "+clsObj.className+"RequestBodyParser.get"+clsObj.className+"ObjectFromJSON(payload);\n\t\t}";
		boCCGetStmt = boCCGetStmt + "\n\t\t} catch (Exception be) \n\t\t{\n\t\t\tthrow new BusException(\"EFR111\", \""+clsObj.className+"BO\", \"ccGet\", be);\n\t\t}";
		boCCGetStmt = boCCGetStmt + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";
		//	System.out.println(boCCGetStmt);
		return boCCGetStmt;
	}
	public static String boCCPutStmt(mClass clsObj) {
		String boCCPutStmt = "";
		clsObj.attrList.size();
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;

		taggedValuesMap = App.taggedValues.get(clsObj.xmi_id);
		String cCode  = null;
		if(taggedValuesMap!=null)
			cCode = taggedValuesMap.get("ClassCode");


		boCCPutStmt = boCCPutStmt + "\n\tpublic String ccPut()  throws BusException {\n\t\ttry {\n\t\t\t";

		boCCPutStmt = boCCPutStmt + "\n\t\t\tString[] responseArray = new String[2];\n\t\t\t";
		boCCPutStmt = boCCPutStmt + "\n\t\t\tString jsonString = \"\";\n\t\t\t";
		boCCPutStmt = boCCPutStmt + "String url = InfraObject.cacheObj.getCCUrl();";
		boCCPutStmt = boCCPutStmt + "\n\t\t\tString key = String.valueOf(doObj."+clsObj.pk+");";
		boCCPutStmt = boCCPutStmt + "\n\t\t\tString classCode = \""+cCode+"\";";
		boCCPutStmt = boCCPutStmt + "\n\t\t\tint keyLen = key.length();\n\t\t\tStringBuilder keyString = new StringBuilder();";
		boCCPutStmt = boCCPutStmt + "\n\t\t\twhile(keyString.length()+keyLen<5) {\n\t\t\tkeyString.append('0');\n\t\t\t}\n\t\tkeyString.append(key);"+
				"\n\t\tkeyString.insert(0,classCode);";
		boCCPutStmt = boCCPutStmt + "\n\t\t\tObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();";
		boCCPutStmt = boCCPutStmt + "\n\t\t\ttry {\n\t\t\t";
		boCCPutStmt = boCCPutStmt + "jsonString = ow.writeValueAsString(doObj);" + 
				"\n\t\t\tjsonString = jsonString.replace(\"\\r\\n\", \"\");" + 
				"\n\t\t\tjsonString = jsonString.replace(\"\\n\", \"\");" + 
				"\n\t\t\tjsonString = jsonString.replace(\"\\\"\", \"\\\\\\\"\");";
		boCCPutStmt = boCCPutStmt + "\n\t\t\tString json = \"{\\\"channel\\\":  \\\"\"+InfraObject.cacheObj.getCCChannel()+\"\\\",\"" + 
				"\n\t\t\t+ \"\\\"chaincode\\\":  \\\""+clsObj.className+"ChainCode\\\",\"" + 
				"\n\t\t\t+ \"\\\"method\\\":  \\\"Create\\\",\"" + 
				"\n\t\t\t+ \"\\\"chaincodeVer\\\":  \\\"1.0\\\",\" + " + 
				"\n\t\t\t\"\\\"args\\\":  [\\\"\"+keyString+\"\\\",\\\"\"+jsonString+\"\\\"],\" + " + 
				"\n\t\t\"\\\"proposalWaitTime\\\": \"+MyConstants.ProposalWaitTime+\",\" + " + 
				"\n\t\t\"\\\"transactionWaitTime\\\": \"+MyConstants.TransactionWaitTime+\"\" + " +
				"\n\t\t\t\"}\";";
		boCCPutStmt = boCCPutStmt + "\n\t\tresponseArray = CallRestAPI.ccCallUrl(url, json);\r\n" + 
				"\n\t\tString respCode = responseArray[0];\r\n" + 
				"\n\t\tString respValue = responseArray[1];";
		boCCPutStmt = boCCPutStmt + "if(respCode.equalsIgnoreCase(\"200\")) {" + 
				"\n\t\t\tSystem.out.println(\"Data Insertion Successful for key-> \"+ key);" + 
				"\n\t\t\t}" + 
				"\n\t\t\telse" + 
				"\n\t\t\tSystem.out.println(\"Could not insert data...response code-> \"+respCode);";
		boCCPutStmt = boCCPutStmt + "\n\t\t} catch (Exception be) \n\t\t{\n\t\t\tthrow new BusException(\"EFR111\", \""+clsObj.className+"BO\", \"ccGet\", be);\n\t\t}";
		boCCPutStmt = boCCPutStmt + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";
		//	System.out.println(boCCPutStmt);
		return boCCPutStmt;
	}
	//Below functions are for Get function.....
	public static String boCCUpdateStmt(mClass clsObj) {
		String boCCUpdateStmt = "";
		String argsString = "";
		boolean persistFlag = false;
		HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
		clsObj.attrList.size();

		//iteration on attribute list....
		for(int itrAttr = 0; itrAttr <= clsObj.attrList.size()-1; itrAttr++) {

			mAttribute itrOBJ = clsObj.attrList.get(itrAttr);

			taggedValuesMap = App.taggedValues.get(itrOBJ.xmi_id);

			String attrName  = itrOBJ.attrName;
			String attrDatatype = itrOBJ.attrDataType;
			String vStr  = null;

			if(taggedValuesMap!=null)
				vStr = taggedValuesMap.get("IsPersist");
			if(vStr==null)
				persistFlag = true;
			else if(vStr.equalsIgnoreCase("false"))
				persistFlag = false;
			else
				persistFlag = true;

			if(attrName.contains("version"))
			{
				continue;
			}
			//only persistable columns will be used to make strings.....
			if(persistFlag) {
				if(attrDatatype.equalsIgnoreCase("Integer") || attrDatatype.equalsIgnoreCase("double")|| attrDatatype.equalsIgnoreCase("float")) {
					argsString = argsString + "\n\t\t\tif(inpDOObj."+ attrName + " != 0 && inpDOObj."+ attrName 
							+ " != ledgerDOObj."+attrName+"){\n\t\t\t\t";
				}else if(attrDatatype.equalsIgnoreCase("String")) {
					argsString = argsString + "\n\t\t\tif(inpDOObj."+ attrName + " != \"\" && !inpDOObj."+ attrName
							+ ".equalsIgnoreCase(ledgerDOObj."+attrName+")){\n\t\t\t\t";
				}else if(attrDatatype.equalsIgnoreCase("LocalDateTime")) {
					argsString = argsString + "\n\t\t\tif(!inpDOObj."+ attrName + ".isEqual(DEFDATETIME) && !inpDOObj."+ attrName
							+ ".isEqual(ledgerDOObj."+attrName+")){\n\t\t\t\t";
				}else if(attrDatatype.equalsIgnoreCase("LocalDate")) {
					argsString = argsString + "\n\t\t\tif(!inpDOObj."+ attrName + ".isEqual(DEFDATE) && !inpDOObj."+ attrName
							+ ".isEqual(ledgerDOObj."+attrName+")){\n\t\t\t\t";
				}else
					argsString = argsString + "\n\t\t\tif(inpDOObj."+ attrName + " != \"\" && inpDOObj."+ attrName 
					+ " != ledgerDOObj."+attrName+"){\n\t\t\t\t";


				argsString = argsString + "\n\t\t\tledgerDOObj."+ attrName + " = inpDOObj."+ attrName+";\n\t\t\t}\n\t\t\t";
			}
		}

		boCCUpdateStmt = boCCUpdateStmt + "\n\tpublic String ccUpdate()  throws BusException {\n\t\ttry {\n\t\t\t";
		boCCUpdateStmt = boCCUpdateStmt + clsObj.className+"DO inpDOObj = doObj;\n\t\t\t";
		boCCUpdateStmt = boCCUpdateStmt + "this.ccGet();\n\t\t\t";
		boCCUpdateStmt = boCCUpdateStmt +clsObj.className+"DO ledgerDOObj = doObj;\n\t\t\t";
		boCCUpdateStmt = boCCUpdateStmt + argsString;
		boCCUpdateStmt = boCCUpdateStmt + "\n\t\t\tdoObj = ledgerDOObj;";
		boCCUpdateStmt = boCCUpdateStmt + "\n\t\t\treturn this.ccPut();";
		boCCUpdateStmt = boCCUpdateStmt + "\n\t\t}catch (BusException be)\n\t\t{\n\t\t\tthrow be;\n\t\t}\n\t}";

		return boCCUpdateStmt;

	}

	}

