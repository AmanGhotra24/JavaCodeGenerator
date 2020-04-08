package com.cw.na.codegen;

public class BusException extends Exception {

	public String errorCode;
	public String className, errordetail;


	public BusException(String lerrorcode, String lClass, String errorDetail)
	{
		errorCode = lerrorcode;
		className = lClass;
		errordetail = errorDetail;
	}

}


