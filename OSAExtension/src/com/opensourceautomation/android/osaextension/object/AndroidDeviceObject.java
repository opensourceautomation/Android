package com.opensourceautomation.android.osaextension.object;


public class AndroidDeviceObject {  

	private String address="";  
	private String basetype="";
	private String container="";
	private String description="";
	private String enabled="";
	private String name="";
	private String type="";
	private String GCMID="";
	
	public String getaddress() {  
		return(address);  
	}  
	public void setaddress(String value) {  
		this.address = value;  
	}  

	public String getbasetype() {  
		return(basetype);  
	}  
	public void setbasetype(String value) {  
		this.basetype = value;  
	}  
	
	public String getcontainer() {  
		return(container);  
	}  
	public void setcontainer(String value) {  
		this.container = value;  
	}  
	
	public String getdescription() {  
		return(description);  
	}  
	public void setdescription(String value) {  
		this.description = value;  
	}  
	
	public String getenabled() {  
		return(enabled);  
	}  
	public void setenabled(String value) {  
		this.enabled = value;  
	}  
	
	public String getname() {  
		return(name);  
	}  
	public void setname(String value) {  
		this.name = value;  
	}  
	
	public String gettype() {  
		return(type);  
	}  
	public void settype(String value) {  
		this.type = value;  
	}  
	
	public String getGCMID() {
		return GCMID;
	}
	public void setGCMID(String gCMID) {
		GCMID = gCMID;
	}
	
	@Override
	public String toString() {  
		return(name);  
	}  
	
} 