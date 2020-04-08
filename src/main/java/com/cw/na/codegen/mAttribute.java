package com.cw.na.codegen;

public class mAttribute {
	String attrName = "";
	String attrDataType = "";
	String attrAccessSpecifier = "";
	int attrlength = 0;
	String xmi_id = "";
	boolean isNull = false;
	boolean scoFlag = false;

	public  String setAttributeStmt(boolean retVOFlag) {
		String dType = this.attrDataType;
		String retAttrString = "";
		if(this.attrAccessSpecifier == "")
			dType = "public";

		if(retVOFlag)
			dType = "String";
		else {
			if(this.attrDataType.contains("[]"))
				dType = "List<"+this.attrDataType.substring(0,this.attrDataType.length()-2)+"DO>";
		}

		if(App.taggedValues.containsKey(this.xmi_id)){
			if(App.taggedValues.get(this.xmi_id).containsKey("SCOCD")) {
				scoFlag = true;
			}
		}

		retAttrString =  this.attrAccessSpecifier + " " + dType+ " " + this.attrName+ " ;\n\t";

		if(scoFlag) {
			retAttrString =  retAttrString + this.attrAccessSpecifier + " " + dType + " " + this.attrName+ "_cd ;\n\t";
		}

		return retAttrString;
	}

}
