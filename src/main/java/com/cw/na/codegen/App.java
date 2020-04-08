package com.cw.na.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.text.StyledEditorKit.ForegroundAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class App 
{
	public static HashMap< String,HashMap<String, String>> taggedValues =  new HashMap< String, HashMap<String, String>>();
	public static HashMap< String,HashMap<String, String>> attrValues =  new HashMap< String, HashMap<String, String>>();
	public static HashMap< String,HashMap<String, String>> paramValues =  new HashMap< String, HashMap<String, String>>(); 
	public static HashMap<String,String> pathMap =  new HashMap<String,String>();
	public static Map< String,String> xmiDTMap =  new HashMap< String,String>(); 
	public static HashMap< String,String> XmiClassMap =  new HashMap< String,String>();
	public static HashMap< String,String> XmiAttrMap =  new HashMap< String,String>();
	public static HashMap< String,String> XmiOprOwnerMap =  new HashMap< String,String>();


	public static List<mClass> classList = new ArrayList<>();
	public static boolean globalSCOFLag = false;
	public static int selectedModel = 1;
	public static void fillTaggedValues(Document document ) {

		//Creating HashMap of String(Model Element) and Hashmap(TagName, TagValue) type variables...
		NodeList nTaggList = document.getElementsByTagName("UML:TaggedValue");
		String modelName = "";
		HashMap<String, String> innerMap = new HashMap<String, String>();

		for (int temp = 0; temp < nTaggList.getLength(); temp++)
		{
			Node tagNode = nTaggList.item(temp);
			Element elmTag = (Element) tagNode;
			if (tagNode.getNodeType() == Node.ELEMENT_NODE)
			{
				modelName = elmTag.getAttribute("modelElement");
				String tag = elmTag.getAttribute("tag");
				String value =  elmTag.getAttribute("value");

				if(tag.equalsIgnoreCase("sco"))
					globalSCOFLag = true;

				if(taggedValues.containsKey(modelName)) {
					innerMap = taggedValues.get(modelName);
					innerMap.put(tag, value);

				}
				else
				{
					innerMap  = new HashMap<String, String>();
					innerMap.put(tag, value);
					taggedValues.put(modelName, innerMap);
				}
			}
		}

	}
	public static void fillAttributeValues(Document document ) {

		//Creating HashMap of String(ClassName) and Hashmap(AttributeName, Type) type variables...
		NodeList nTaggList = document.getElementsByTagName("UML:Attribute");
		String modelName = "";
		HashMap<String, String> innerMap = new HashMap<String, String>();

		for (int temp = 0; temp < nTaggList.getLength(); temp++)
		{
			Node tagNode = nTaggList.item(temp);
			Element elmTag = (Element) tagNode;
			if (tagNode.getNodeType() == Node.ELEMENT_NODE)
			{
				modelName = elmTag.getAttribute("owner");
				String tag = elmTag.getAttribute("xmi.id");
				String value =  elmTag.getAttribute("type");

				if(attrValues.containsKey(modelName)) {
					innerMap = attrValues.get(modelName);
					innerMap.put(tag, value);

				}
				else
				{
					innerMap  = new HashMap<String, String>();
					innerMap.put(tag, value);
					attrValues.put(modelName, innerMap);
				}
			}
		}

	}

	public static void fillParamValues(Document document ) {

		//Creating HashMap of String(ClassName) and Hashmap(AttributeName, Type) type variables...
		NodeList nTaggList = document.getElementsByTagName("UML:Attribute");
		String modelName = "";
		HashMap<String, String> innerMap = new HashMap<String, String>();

		for (int temp = 0; temp < nTaggList.getLength(); temp++)
		{
			Node tagNode = nTaggList.item(temp);
			Element elmTag = (Element) tagNode;
			if (tagNode.getNodeType() == Node.ELEMENT_NODE)
			{
				modelName = elmTag.getAttribute("behavioralFeature");
				String tag = elmTag.getAttribute("xmi.id");
				String value =  elmTag.getAttribute("type");

				if(paramValues.containsKey(modelName)) {
					innerMap = attrValues.get(modelName);
					innerMap.put(tag, value);

				}
				else
				{
					innerMap  = new HashMap<String, String>();
					innerMap.put(tag, value);
					paramValues.put(modelName, innerMap);
				}
			}
		}

	}

	public static String getKeyByValue(HashMap<String, String> map, String value) {
		for (Entry<String, String> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	//Function to populate the HashMap(XMI.ID, DataType) from the Datatype tags.... 
	public static void fillDataTypeMap(Document document ) {
		NodeList nDataTypeList = document.getElementsByTagName("UML:DataType");
		String xmi_id = "";
		String dType = "";
		for (int temp = 0; temp < nDataTypeList.getLength(); temp++)
		{
			Node tagNode = nDataTypeList.item(temp);
			Element elmDTTag = (Element) tagNode;
			if (tagNode.getNodeType() == Node.ELEMENT_NODE)
			{
				xmi_id = elmDTTag.getAttribute("xmi.id");
				dType = elmDTTag.getAttribute("name");
				xmiDTMap.put(xmi_id, dType);
			}
		}
	}

	//Function to populate the HashMap(XMI.ID, ClassName) from the Class tags....
	public static void fillClassMap(Document document) {
		NodeList nList = document.getElementsByTagName("UML:Class");
		String cName = "";
		String xmi_id = "";
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			Element elm = (Element) node;
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				xmi_id = elm.getAttribute("xmi.id");
				cName = elm.getAttribute("name"); 
				XmiClassMap.put(xmi_id, cName);
			}	
		}
	}


	public static void fillAttrNameMap(Document document) {
		NodeList nList = document.getElementsByTagName("UML:Attribute");
		String cName = "";
		String xmi_id = "";
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			Element elm = (Element) node;
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				xmi_id = elm.getAttribute("xmi.id");
				cName = elm.getAttribute("name"); 
				XmiAttrMap.put(xmi_id, cName);
			}	
		}
	}

	public static void fillOprOwnerNameMap(Document document) {
		NodeList nList = document.getElementsByTagName("UML:Operation");
		String cName = "";
		String xmi_id = "";
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			Element elm = (Element) node;
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				xmi_id = elm.getAttribute("xmi.id");
				cName = elm.getAttribute("owner"); 
				XmiOprOwnerMap.put(xmi_id, cName);
			}	
		}
	}

	//Function to parse the class List and populate the List of mClassOBjects....
	public static void parseClassList(Document document) throws BusException {
		NodeList nClassList = document.getElementsByTagName("UML:Class");
		if(nClassList!=null) {
			for (int temp = 0; temp < nClassList.getLength(); temp++)
			{
				Node node = nClassList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					mParseXMI.parseXML(node);
				}	
			}
		}
	}

	//This function iterate through the generated class list and start builiding classes into the respective directories....
	public static  void generateClasses() {
		for(int itrClasses = 0; itrClasses <= classList.size()-1; itrClasses++)
		{
			mClass tempClassObj = classList.get(itrClasses);
			HashMap<String, String> taggedValuesMap = new HashMap<String, String>() ;
			boolean ISVOFlag = false;

			taggedValuesMap = taggedValues.get(tempClassObj.xmi_id);

			String IsVOStr = null;

			if(taggedValuesMap!= null)
				IsVOStr = taggedValuesMap.get("IsVO");

			if(IsVOStr==null)
				ISVOFlag = false;
			else if(IsVOStr.equalsIgnoreCase("true"))
				ISVOFlag = true;
			else
				ISVOFlag = false;

			if(ISVOFlag)
			{

				tempClassObj.createSearchVOClasses("", false);
			}
			else {

				tempClassObj.createDOClass();

				tempClassObj.createBOClass();

				if(selectedModel!=3)
					tempClassObj.createDMClass();

				tempClassObj.createDDLFile();

				tempClassObj.createParserClass();

				tempClassObj.createServiceFile();
				if(selectedModel==3)
					tempClassObj.createChaincodeFile();
			}
		}
	}

	//This function populate the HashMap of ClassName and its Path in the Package for each type of class or service.....
	public static void fillDirStructureMaps(File file, String codeGenPath) {
		String justFileName = file.getName().substring(0, file.getName().length()-4);
		String packagePath = "com\\cw\\na\\"+justFileName;
		String targetPath = codeGenPath;

		String pathToDOFiles = targetPath+"\\"+packagePath +"\\DOs";
		String pathToVOFiles = targetPath+"\\"+packagePath +"\\VOs";
		String pathToDMFiles = targetPath+"\\"+packagePath +"\\DMs";
		String pathToDDLFile =  targetPath+"\\"+packagePath +"\\DDLs";
		String pathToBOFiles =  targetPath+"\\"+packagePath +"\\BOs";
		String pathToServiceFile = targetPath+"\\"+packagePath +"\\Services";
		String pathToChaincodeFile = targetPath+"\\"+packagePath +"\\Chaincodes";

		String importDMPkgName = pathToDMFiles.replace("\\", ".").substring(pathToDMFiles.indexOf("com"));
		String importDOPkgName = pathToDOFiles.replace("\\", ".").substring(pathToDOFiles.indexOf("com"));
		String importVOPkgName = pathToVOFiles.replace("\\", ".").substring(pathToVOFiles.indexOf("com"));
		String importBOPkgName = pathToBOFiles.replace("\\", ".").substring(pathToBOFiles.indexOf("com"));
		String importSVCPkgName = pathToServiceFile.replace("\\", ".").substring(pathToServiceFile.indexOf("com"));

		pathMap.put("PackagePath", packagePath);
		pathMap.put("DOPath", pathToDOFiles);
		pathMap.put("VOPath", pathToVOFiles);
		pathMap.put("BOPath", pathToBOFiles);
		pathMap.put("DMPath", pathToDMFiles);
		pathMap.put("DDLPath", pathToDDLFile);
		pathMap.put("CCPath", pathToChaincodeFile);
		pathMap.put("DMPkgName", importDMPkgName);
		pathMap.put("DOPkgName", importDOPkgName);
		pathMap.put("VOPkgName", importVOPkgName);
		pathMap.put("BOPkgName", importBOPkgName);
		pathMap.put("ServicePath", pathToServiceFile);
		pathMap.put("SVCPkgName", importSVCPkgName);
	}

	//Below function check if the given key is present in the HashMap...returns true or false ...
	public static boolean isValidClass(String name) {

		Iterator<HashMap.Entry<String, String>> iterator = XmiClassMap.entrySet().iterator(); 
		while (iterator.hasNext()) { 

			Map.Entry<String, String> entry = iterator.next(); 
			if (name.equalsIgnoreCase(entry.getKey())) { 

				return true; 
			} 
		}  

		return false;
	}

	public static boolean isValidDataType(String xmiType) {
		// TODO Auto-generated method stub
		Iterator<HashMap.Entry<String, String>> iterator = mMaps.XmiToJavaDTMap.entrySet().iterator(); 
		while (iterator.hasNext()) { 

			Map.Entry<String, String> entry = iterator.next(); 
			if (xmiType.equalsIgnoreCase(entry.getKey())) { 

				return true; 
			} 
		}  

		return false;
	}

	public static void preParsingValidation() throws BusException {
		String classxmi = "";
		String attrxmi = "";
		String typexmi = "";
		String dataType = "";
		HashMap<String, String> attrTypeMap;
		HashMap<String, String> attrTaggedValMap;
		boolean doFlag =  false;
		boolean pkFlag =  false;
		boolean scoErrorFlag =  false;
		for (Entry<String, HashMap<String, String>> entry : attrValues.entrySet())  {
			classxmi = entry.getKey();
			attrTypeMap = entry.getValue();
			attrTaggedValMap = taggedValues.get(classxmi);
			String vStr  = null;

			if(attrTaggedValMap!=null)
				vStr = attrTaggedValMap.get("IsVO");
			if(vStr==null)
				doFlag = true;
			else if(vStr.equalsIgnoreCase("true"))
				doFlag = false;
			else
				doFlag = true;

			boolean globaPkFlag = false;
			boolean globaSCOErrorFlag = false;
			String scoXmi = "";
			for (Entry<String, String> innerEntry : attrTypeMap.entrySet())  {
				attrxmi = innerEntry.getKey();
				typexmi = innerEntry.getValue();
				//Datatype should be present in every 
				if(typexmi == null || typexmi == "") {
					String exp = "Please set the \"Type\" of the attribute : "
							+ XmiAttrMap.get(attrxmi) + " of the class: " + XmiClassMap.get(classxmi);
					throw new BusException("CG100", "Codegen.App.Java", exp);
				}else {

					dataType =  xmiDTMap.get(typexmi);
					int arrayPos =-1;

					arrayPos =  dataType.indexOf("[");						
					if ( arrayPos != -1  )
					{
						dataType = dataType.substring(0, arrayPos);
					}	
					//System.out.println(attrxmi +  " "+ dataType + " " + scoXmi);
					if(dataType.equalsIgnoreCase("SCO"))
					{
						scoXmi = attrxmi;
						attrTaggedValMap = taggedValues.get(scoXmi);
						String scoStr  = null;

						if(attrTaggedValMap!=null)
							scoStr = attrTaggedValMap.get("SCOCD");

						if(scoStr == null)
							scoErrorFlag = true;
						else if(scoStr.equalsIgnoreCase(""))
							scoErrorFlag = true;
						else
							scoErrorFlag = false;

						if(scoErrorFlag)
						{
							String exp = "Please set the SCOCD value for the attribute with XMI ID: " + scoXmi ;
							throw new BusException("CG106", "Codegen.App.Java", exp);
						}
					}
					String tempVal = mMaps.XmiToJavaDTMap.get(dataType);

					if(tempVal==null)
					{
						tempVal = XmiClassMap.get(typexmi);

						if(tempVal == null)
						{
							String exp = "Please enter the valid value for the \"Type\" of the attribute : " 
									+ XmiAttrMap.get(attrxmi) + " of the class: " + XmiClassMap.get(classxmi);
							throw new BusException("CG101", "Codegen.App.Java", exp);
						}
					}

				}
				//Pk should be present for every class which is not VO
				if(doFlag) {
					attrTaggedValMap = taggedValues.get(attrxmi);
					String pkStr  = null;
					if(attrTaggedValMap!=null)
						pkStr = attrTaggedValMap.get("IsPK");
					if(pkStr==null)
						pkFlag = false;
					else if(pkStr.equalsIgnoreCase("true"))
						pkFlag = true;
					else
						pkFlag = false;

					if(pkFlag)
						globaPkFlag = pkFlag;


				}

			}
			if(doFlag && !globaPkFlag)
			{
				String exp = "Please set the primary key for the class : " + XmiClassMap.get(classxmi);
				throw new BusException("CG103", "Codegen.App.Java", exp);
			}



			doFlag = false;
			pkFlag = false;
			scoErrorFlag = false;
		}

	}

	public static void postParsingValidation() throws BusException {
		int classListLength = App.classList.size()-1;
		boolean gListFlag = false;


		String priClassName = "";

		Set<String> priClassAttrSet= new HashSet<String>();
		Set<String> retClassAttrSet = new HashSet<String>();
		String retClassName = "";
		for (int itrCls = 0 ; itrCls<=classListLength; itrCls++)
		{
			mClass currentClassVar = App.classList.get(itrCls);
			mClass priClassVar = null;
			int oprListLength = currentClassVar.oprList.size()-1;
			int attrListLength = currentClassVar.attrList.size()-1;
			HashMap<String, String> oprTaggedValMap;

			for (int itrOpr = 0 ; itrOpr <= oprListLength; itrOpr++) {

				mOperation currOprVar = currentClassVar.oprList.get(itrOpr);
				int paramListLength = currOprVar.params.size()-1;
				oprTaggedValMap = taggedValues.get(currOprVar.xmi_id);
				String gListStr  = null;

				if(oprTaggedValMap!=null)
					gListStr = oprTaggedValMap.get("IsGList");
				if(gListStr==null)
					gListFlag = false;
				else if(gListStr.equalsIgnoreCase("true"))
					gListFlag = true;
				else
					gListFlag = false;

				for(int itrParam = 0 ; itrParam<= paramListLength; itrParam++) {

					mParams currParamVar = currOprVar.params.get(itrParam);

					if(currParamVar.kind.equalsIgnoreCase("return")) {
						retClassName = currParamVar.type;
						priClassVar = currentClassVar;
						priClassName = currentClassVar.className;
					}
				}
			}

			for (int itrAttr = 0 ; itrAttr <= currentClassVar.attrList.size()-1; itrAttr++) {

				mAttribute currAttrVar = currentClassVar.attrList.get(itrAttr);
				if(currAttrVar.attrName.startsWith("version"))
					continue;
				if(gListFlag)
					priClassAttrSet.add(currAttrVar.attrName);
			}

			mClass retClassVar = null;
			if(gListFlag) {
				for (int itrCls1 = 0 ; itrCls1<=classListLength; itrCls1++)
				{
					mClass tempVar = App.classList.get(itrCls1);

					if(tempVar.className.equalsIgnoreCase(retClassName)) {
						retClassVar = tempVar;
					}


				}
			}
			if(retClassVar!= null) {

				for (int itrAttr = 0 ; itrAttr <= retClassVar.attrList.size()-1; itrAttr++) {

					mAttribute currAttrVar = retClassVar.attrList.get(itrAttr);

					if(currAttrVar.attrName.startsWith("version"))
						continue;
					retClassAttrSet.add(currAttrVar.attrName);
				}
			}

		}
		System.out.println(priClassAttrSet.size() + " " +  retClassAttrSet.size());
		if(priClassAttrSet.size() != retClassAttrSet.size() )
		{
			String exp = "Please keep the same number of attributes for Primary class : "+ priClassName+" , and Return VO class : " + retClassName;
			throw new BusException("CG106", "Codegen.App.Java", exp);
		}

		priClassAttrSet.removeAll(retClassAttrSet);

		if(priClassAttrSet.size()!=0)
		{
			String exp = "Please keep the same attributes for Primary class : "+ priClassName+" , and Return VO class : " + retClassName;
			throw new BusException("CG107", "Codegen.App.Java", exp);
		}

	}
	//*******************************START FROM HERE*************************************

	public static void main( String[] args ) throws SAXException, IOException, ParserConfigurationException
	{
		try {
			String xmiFileName = args[0];
			String codeGenPath = args[1];

			//			String xmiFileName = "E:\\UML\\test.xml";
			//			String codeGenPath = "E:\\Test";

			//			E:\\UML\\TxnInfo.xml


			//			Scanner scanner = new Scanner(System.in);
			//			int modelType;
			//			do {
			//				System.out.println("Please select type of Model to be generated: \n1) Generic Model \n2) Business Model \n3) Chaincode Model");
			//				while (!scanner.hasNextInt()) {
			//					String input = scanner.next();
			//					System.out.printf("\"%s\" is not a valid choice.\n", input);
			//				}
			//				modelType = scanner.nextInt();
			//			} while (modelType <1 || modelType >3);
			//
			//			selectedModel = modelType;
			//
			//			System.out.println("Model Selected : "+selectedModel); 
			//
			//			scanner.close();

			//Populate Some Cache Maps which are being used later.....
			mMaps.setMapValues();

			//Set up document factory object to parse the DOM type XMI file....
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);

			//Setting up the Document builder...
			DocumentBuilder builder = factory.newDocumentBuilder();
			File file = new File(xmiFileName);  // XML file to read

			//Parsing the XMI file....
			Document document = builder.parse(file);

			//Creating map of tagged values of the XMI files....
			fillTaggedValues(document);

			//Creating map of Opr map for its owner class .....
			fillOprOwnerNameMap(document);
			//Creating map of attribute map for its owner class and datatype.....		
			fillAttributeValues(document);

			//creating map of the Classes available....
			fillClassMap(document);

			//creating map of the datatype values....
			fillDataTypeMap(document);

			//creating map of the attribute values....		
			fillAttrNameMap(document);

			//Validating the XMI received.........
			preParsingValidation();

			//Parsing the whole document structure into List of objects.....
			parseClassList(document);

			postParsingValidation();
			//Setting up directory structure for the whole project....
			fillDirStructureMaps(file,codeGenPath);

			//Atlast generate the content of all the classes and services....
			generateClasses();
		}catch (BusException e) {
			System.out.println("*****Invalid XMI*****\nError Code : "+e.errorCode+" \nErrorDetail: " + e.errordetail);
		}
	}



}


