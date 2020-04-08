package com.cw.na.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.cw.na.codegen.mClass;


public class mParseXMI {
	public static List<mAttribute> addVersionAttributesToList(Element attrElement, List<mAttribute> attributeList){

		mAttribute mAttrObj = new mAttribute();
		mAttrObj.attrName = "version1";
		mAttrObj.attrAccessSpecifier = attrElement.getAttribute("visibility");
		mAttrObj.attrDataType = "int";
		mAttrObj.xmi_id = attrElement.getAttribute("xmi.id");
		attributeList.add(mAttrObj);

		mAttribute mAttrObj1 = new mAttribute();
		mAttrObj1.attrName = "version2";
		mAttrObj1.attrAccessSpecifier = attrElement.getAttribute("visibility");
		mAttrObj1.attrDataType = "int";
		mAttrObj1.xmi_id = attrElement.getAttribute("xmi.id");
		attributeList.add(mAttrObj1);

		return attributeList;
	}
	//Function to Parse the XMI File and Store the corresponding data in meta Classes (Class, Attribute, Operations, Params)...
	public static void parseXML(Node classNode) throws BusException {

		NodeList classMemberNodes = classNode.getChildNodes();
		NodeList attributeAndOprNodes = null;

		if( classMemberNodes!=null) {
			for (int i = 0; i< classMemberNodes.getLength()-1; i++)
			{
				Node itrClassMemberNode = classMemberNodes.item(i);
				if (itrClassMemberNode.getNodeType() == Node.ELEMENT_NODE)
				{
					if(itrClassMemberNode.getNodeName().equalsIgnoreCase("UML:Classifier.feature"))
						attributeAndOprNodes = itrClassMemberNode.getChildNodes();
				}
			}
		}
		mClass mClassObj = new mClass();
		List<mAttribute> mAttrList = new ArrayList<>();
		List<mOperation> mOprList = new ArrayList<>();

		//Casting of Node to Element is required to extract the attibutes of the tag...
		Element classElement = (Element)classNode;
		mClassObj.className =  classElement.getAttribute("name");
		mClassObj.accessSpecifier = classElement.getAttribute("visibility");
		mClassObj.xmi_id = classElement.getAttribute("xmi.id");

		String xmiTypeID, xmiType;
		int arrayPos;
		boolean isPkFlag ;
		if(attributeAndOprNodes != null) {
			//Iteration through the nodes of the class and populate the meta objects and creating a list of objects in the end...
			for(int itr=0 ; itr <= attributeAndOprNodes.getLength()-1; itr++){

				Node itrNode = attributeAndOprNodes.item(itr);
				String nodeName = itrNode.getNodeName();
				isPkFlag = false;
				//Process if only it is node of type Element Node....

				if (itrNode.getNodeType() == Node.ELEMENT_NODE)
				{
					//Populate mAttribute Object ...
					if(nodeName.equalsIgnoreCase("UML:Attribute")) {//if node is of type Attribute..

						mAttribute mAttrObj = new mAttribute();
						Element attrElement = (Element) itrNode;

						if(attrElement.getAttribute("name").equalsIgnoreCase("version")) {
							mAttrList = addVersionAttributesToList(attrElement, mAttrList);
							continue;
						}
						mAttrObj.attrName = attrElement.getAttribute("name");
						mAttrObj.attrAccessSpecifier = attrElement.getAttribute("visibility");
						xmiTypeID = attrElement.getAttribute("type");
						xmiType = App.xmiDTMap.get(xmiTypeID);
						
						arrayPos =-1;

						arrayPos =  xmiType.indexOf("[");						
						if ( arrayPos != -1  )
						{
							xmiType = xmiType.substring(0, arrayPos);
						}							
						String javaType = "";
						if(App.isValidDataType(xmiType))   
							javaType = mMaps.XmiToJavaDTMap.get(xmiType); 
						else if (App.isValidClass(xmiType))
							javaType = xmiType;
						else
						{

							String exp = "Please enter the valid value for the \"Type\" of the attribute : " 
									+ mAttrObj.attrName + " of the class: " +mClassObj.className ;
							throw new BusException("CG104", "Codegen.App.Java", exp);

						}
						if ( arrayPos != -1)
							mAttrObj.attrDataType= javaType + "[]";
						else
							mAttrObj.attrDataType= javaType;

						//System.out.println(mAttrObj.attrDataType);
						mAttrObj.xmi_id = attrElement.getAttribute("xmi.id");

						//PK will have a tagged value with the key isPK...
						HashMap<String, String> attrTaggedValMap = App.taggedValues.get(mAttrObj.xmi_id);
						String isPkStr  = null;

						if(attrTaggedValMap!=null)
							isPkStr = attrTaggedValMap.get("IsPK");
						if(isPkStr==null)
							isPkFlag = false;
						else if(isPkStr.equalsIgnoreCase("true"))
							isPkFlag = true;
						else
							isPkFlag = false;

						if(isPkFlag)
							mClassObj.pk = mAttrObj.attrName;

						mAttrList.add(mAttrObj);
					}

					//Populate mOperation Object ...
					if(nodeName.equalsIgnoreCase("UML:Operation")) {
						//if node is of type Operation..

						mOperation mOprObj = new mOperation();
						Element attrElement = (Element) itrNode;

						mOprObj.oprName = attrElement.getAttribute("name");
						mOprObj.oprAccessSpecifier = attrElement.getAttribute("visibility");
						mOprObj.oprDataType = attrElement.getAttribute("specification");
						mOprObj.xmi_id = attrElement.getAttribute("xmi.id");
						mOprObj.ownerScope = attrElement.getAttribute("ownerScope");

						//Child nodes of the Operation are Parameter nodes....
						if(itrNode.hasChildNodes()) {

							NodeList oprNodeList = itrNode.getChildNodes();
							NodeList paramNodeList =  null;
							if(oprNodeList!=null) {
								for (int i = 0; i < oprNodeList.getLength()-1; i++)
								{
									Node itrOprMemberNode = oprNodeList.item(i);
									if (itrOprMemberNode.getNodeType() == Node.ELEMENT_NODE)
									{
										if(itrOprMemberNode.getNodeName().equalsIgnoreCase("UML:BehavioralFeature.parameter"))
											paramNodeList = itrOprMemberNode.getChildNodes();
									}
								}
							}
							if(paramNodeList!=null) {
								for(int pitr = 0; pitr <= paramNodeList.getLength()-1; pitr++) {

									Node itrParamNode = paramNodeList.item(pitr);
									String javaParamType = "";
									if (itrParamNode.getNodeType() == Node.ELEMENT_NODE)
									{
										mParams mParamObj = new mParams();
										Element elmParam = (Element) paramNodeList.item(pitr);
										mParamObj.ParamName = elmParam.getAttribute("name");
										mParamObj.kind = elmParam.getAttribute("kind");
										mParamObj.visibility = elmParam.getAttribute("visibility");
										mParamObj.xmi_id = elmParam.getAttribute("xmi.id");

										xmiTypeID = elmParam.getAttribute("type");

										String xmiPType = App.xmiDTMap.get(xmiTypeID);
										arrayPos =-1;
										xmiType = xmiTypeID;
										if(xmiPType!= null) {

											if(xmiPType.contains("["))
												arrayPos =  xmiPType.indexOf("[");						

											if ( arrayPos != -1  )
											{
												xmiType = xmiPType.substring(0, arrayPos);
											}
										}
											if  (App.isValidDataType(xmiType))   
											javaParamType = mMaps.XmiToJavaDTMap.get(xmiType); 
										else if ( App.isValidClass(xmiType)) {
											javaParamType = App.XmiClassMap.get(xmiType);
										}
										else
										{
											String exp = "Please enter the valid value for the \"Type\" of the Parameter : " 
													+ mParamObj.ParamName + " of the class: " +mClassObj.className ;
											throw new BusException("CG105", "Codegen.App.Java", exp);
										}
											System.out.println();

										if ( arrayPos != -1)
											mParamObj.type = javaParamType + "[]";
										else
											mParamObj.type = javaParamType;

										mOprObj.params.add(mParamObj);
									}
								}
							}
						}
						mOprList.add(mOprObj);
					}
				}
			}
		}

		//populating the other attibutes(which comes with in the Tagged Values) values like Length , Isnull....
		for(int i = 0; i<= mAttrList.size()-1; i++)
		{
			mAttribute mAttrVar = mAttrList.get(i);
			boolean isLengthFlag = false;
			if(App.taggedValues.containsKey( mAttrVar.xmi_id)) {
				//PK will have a tagged value with the key isPK...
				HashMap<String, String> attrTaggedValMap = App.taggedValues.get(mAttrVar.xmi_id);
				String isLengthStr  = null;
				int length = 0;
				if(attrTaggedValMap!=null)
					isLengthStr = attrTaggedValMap.get("Length");

				if(isLengthStr==null)
					isLengthFlag = false;
				else {
					try {
						length = Integer.parseInt(isLengthStr);
					}catch (Exception e) {
						String exp =  "Please enter Integer value for the Length tag for attribute: "+ mAttrVar.attrName;
						throw new BusException("CC105", "mParseXMI", exp);
					}
					isLengthFlag = true;
				}

				if(isLengthFlag)
					mAttrVar.attrlength = length;


				String isNullStr = "";

				boolean isNullFlag = false;
				if(attrTaggedValMap!=null)
					isNullStr = attrTaggedValMap.get("IsNull");

				if(isNullStr==null)
					isNullFlag = false;
				else if(isNullStr.equalsIgnoreCase("true"))
					isNullFlag = true;
				else
					isNullFlag = false;

				if(isNullFlag)
					mAttrVar.isNull = true;
				else
					mAttrVar.isNull = false;
			}
		}

		mClassObj.attrList = mAttrList;
		mClassObj.oprList = mOprList;
		//adding the class to the  list of class objects..
		App.classList.add(mClassObj); 
	}
}
