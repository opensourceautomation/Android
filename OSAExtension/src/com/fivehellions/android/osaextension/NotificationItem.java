
package com.fivehellions.android.osaextension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NotificationItem {  

	private int id;
	private int osaid;  
	private String message="";
	private String category="";
	private int level;
	private Boolean deleted=false;
	private Date notifydate= new Date();
	private Boolean notification=false;
	private Boolean alert=false;
	private Boolean speech=false;
	private Boolean toast=false;
	private Boolean popup=false;
	private Boolean vibrate=false;
	
	public void setAll(ArrayList<String> stringlist){
		
 		setId(Integer.parseInt(stringlist.get(0)));
 		setOsaid(Integer.parseInt(stringlist.get(1)));
 		setMessage(stringlist.get(2));
 		setCategory(stringlist.get(3));
 		setLevel(Integer.parseInt(stringlist.get(4)));
 		setDeleted((Integer.parseInt(stringlist.get(5))) > 0 ? true : false);
 		
 		String dtStart = stringlist.get(6);  
 		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
 		try {  
 		    Date date = format.parse(dtStart);  
 		    setNotifydate(date);
 		} catch (ParseException e) {  
 		    e.printStackTrace();  
 		}
 		
 		
 		setNotification((Integer.parseInt(stringlist.get(7))) > 0 ? true : false);
 		setAlert((Integer.parseInt(stringlist.get(8))) > 0 ? true : false);
 		setSpeech((Integer.parseInt(stringlist.get(9))) > 0 ? true : false);
 		setToast((Integer.parseInt(stringlist.get(10))) > 0 ? true : false);
 		setPopup((Integer.parseInt(stringlist.get(11))) > 0 ? true : false);
 		setVibrate((Integer.parseInt(stringlist.get(12))) > 0 ? true : false);
 		
	}
	

	
	@Override
	public String toString() {  
		return(message);  
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getOsaid() {
		return osaid;
	}
	public void setOsaid(int osaid) {
		this.osaid = osaid;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public Date getNotifydate() {
		return notifydate;
	}
	public void setNotifydate(Date notifydate) {
		this.notifydate = notifydate;
	}

	public Boolean getNotification() {
		return notification;
	}
	public void setNotification(Boolean notification) {
		this.notification = notification;
	}

	public Boolean getAlert() {
		return alert;
	}
	public void setAlert(Boolean alert) {
		this.alert = alert;
	}

	public Boolean getSpeech() {
		return speech;
	}
	public void setSpeech(Boolean speech) {
		this.speech = speech;
	}

	public Boolean getToast() {
		return toast;
	}
	public void setToast(Boolean toast) {
		this.toast = toast;
	}

	public Boolean getPopup() {
		return popup;
	}
	public void setPopup(Boolean popup) {
		this.popup = popup;
	}  
	
	public Boolean getVibrate() {
		return vibrate;
	}
	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
	}  	
} 