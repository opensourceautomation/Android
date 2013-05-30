package com.opensourceautomation.android.osaextension.object;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

public class OSAObject {

	private String Address="";

	private String BaseType="";
	private String Container="";
	private String Description="";
	private Boolean Enabled=false;
	private String LastUpd;
	private String Methods="";
	private String Name="";
	private String Properties="";
	private String State="";
	private int TimeInState=0;
	private String Type="";
	
	@Override
	public String toString() {
		return "Object [Address=" + Address + "]";
	}	
	
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getBaseType() {
		return BaseType;
	}
	public void setBaseType(String baseType) {
		BaseType = baseType;
	}
	public String getContainer() {
		return Container;
	}
	public void setContainer(String container) {
		Container = container;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public Boolean getEnabled() {
		return Enabled;
	}
	public void setEnabled(Boolean enabled) {
		Enabled = enabled;
	}
	public void setEnabled(String enabled){
		
		try {
			int myint=Integer.parseInt(enabled);
			Enabled = (myint) > 0 ? true : false;			
		} catch (NumberFormatException e) {
			// must not be an int
			Enabled = Boolean.parseBoolean(enabled);
		}

	}
	
	public String getLastUpd() {
		return LastUpd;
	}
	public void setLastUpd(String lastUpd) {
		LastUpd = lastUpd;
	}
	public String getMethods() {
		return Methods;
	}
	public void setMethods(String methods) {
		Methods = methods;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getProperties() {
		return Properties;
	}
	public void setProperties(String properties) {
		Properties = properties;
	}
	
	public void parseState(String statejson){
	
		try {
			JSONObject jsonObject = new JSONObject(statejson);

			if (jsonObject.has("Value")){
				State = jsonObject.getString("Value");
			}
			
			if (jsonObject.has("TimeInState")){
				try {
					TimeInState = Integer.parseInt(jsonObject.getString("TimeInState"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public int getTimeInState() {
		return TimeInState;
	}
	public void setTimeInState(int timeInState) {
		TimeInState = timeInState;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}

	public void parseJSON(JSONObject jsonObject){

		try {
			if (jsonObject.has("Address")){
				setAddress(jsonObject.getString("Address"));
			}
			if (jsonObject.has("BaseType")){
				setBaseType(jsonObject.getString("BaseType"));
			}
			if (jsonObject.has("Container")){
				setContainer(jsonObject.getString("Container"));
			}
			if (jsonObject.has("Description")){
				setDescription(jsonObject.getString("Description"));
			}
			if (jsonObject.has("Enabled")){
				setEnabled(jsonObject.getString("Enabled"));
			}
			if (jsonObject.has("LastUpd")){
				setLastUpd(jsonObject.getString("LastUpd"));
			}
			if (jsonObject.has("Methods")){
				setMethods(jsonObject.getString("Methods")); //eventually we will parse this out and store a method array inside OSAObject
			}
			if (jsonObject.has("Name")){
				setName(jsonObject.getString("Name"));
			}
			if (jsonObject.has("Properties")){
				setProperties(jsonObject.getString("Properties")); //eventually we will parse this out and store a Properties array inside OSAObject
			}
			if (jsonObject.has("State")){
				parseState(jsonObject.getString("State"));
			}
			if (jsonObject.has("Type")){
				setType(jsonObject.getString("Type"));
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
