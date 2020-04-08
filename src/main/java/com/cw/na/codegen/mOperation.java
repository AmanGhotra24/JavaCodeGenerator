package com.cw.na.codegen;

import java.util.ArrayList;
import java.util.List;

public class mOperation {
	String oprName = "";
	String oprDataType = "";
	String oprAccessSpecifier = "";
	String xmi_id = "";
	List<mParams> params = new ArrayList<>();
	String ownerScope = "";
	public String setOperationStmt() {
		String retOprString = "";
		if(this.oprAccessSpecifier == "")
			this.oprAccessSpecifier = "public";

		if(this.oprDataType == "")
			this.oprDataType = "void";

		retOprString = this.oprAccessSpecifier + " " + this.oprDataType + " " + this.oprName+ "() { \n";
		retOprString = retOprString + "\t//Function Def Goes Here \n";
		retOprString = retOprString + "\t}";

		return retOprString;
	}
}
