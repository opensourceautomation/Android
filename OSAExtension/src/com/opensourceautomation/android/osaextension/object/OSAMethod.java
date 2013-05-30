package com.opensourceautomation.android.osaextension.object;

import org.json.JSONException;
import org.json.JSONObject;

public class OSAMethod {

	@Override
	public String toString() {
		return "OSAMethod [MethodName=" + MethodName + "]";
	}

	private String Address;
	private String MethodName;
	private String ObjectName;
	private String Owner;
	private String Parameter1;
	private String Parameter2;
	
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getMethodName() {
		return MethodName;
	}
	public void setMethodName(String methodName) {
		MethodName = methodName;
	}
	public String getObjectName() {
		return ObjectName;
	}
	public void setObjectName(String objectName) {
		ObjectName = objectName;
	}
	public String getOwner() {
		return Owner;
	}
	public void setOwner(String owner) {
		Owner = owner;
	}
	public String getParameter1() {
		return Parameter1;
	}
	public void setParameter1(String parameter1) {
		if (parameter1.equalsIgnoreCase("null"))
			parameter1="";
		Parameter1 = parameter1;
	}
	public String getParameter2() {
		return Parameter2;
	}
	public void setParameter2(String parameter2) {
		if (parameter2.equalsIgnoreCase("null"))
			parameter2="";
		Parameter2 = parameter2;
	}
	
	public void parseJSON(JSONObject jsonObject){

		try {
			if (jsonObject.has("Address")){
				setAddress(jsonObject.getString("Address"));
			}
			if (jsonObject.has("MethodName")){
				setMethodName(jsonObject.getString("MethodName"));
			}
			if (jsonObject.has("ObjectName")){
				setObjectName(jsonObject.getString("ObjectName"));
			}
			if (jsonObject.has("Owner")){
				setOwner(jsonObject.getString("Owner"));
			}
			if (jsonObject.has("Parameter1")){
				setParameter1(jsonObject.getString("Parameter1"));
			}
			if (jsonObject.has("Parameter2")){
				setParameter2(jsonObject.getString("Parameter2"));
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}


