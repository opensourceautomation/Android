package com.fivehellions.android.osaextension;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class NotificationType implements Serializable {  

	private int id=-1;  
	private String category="NewCategory";
	private int level_start=1;
	private int level_end=10;
	private Boolean notification=false;
	private Boolean alert=false;
	private String alertsound="default";
	private Boolean speech=false;
	private Boolean toast=false;
	private Boolean popup=false;
	private Boolean vibrate=false;
	
	public void setAll(ArrayList<String> stringlist){
		
 		setId(Integer.parseInt(stringlist.get(0)));
 		setCategory(stringlist.get(1));
 		setLevel_start(Integer.parseInt(stringlist.get(2)));
 		setLevel_end(Integer.parseInt(stringlist.get(3)));
 		setNotification((Integer.parseInt(stringlist.get(4))) > 0 ? true : false);
 		setAlert((Integer.parseInt(stringlist.get(5))) > 0 ? true : false);
 		setAlertsound(stringlist.get(6));
 		setSpeech((Integer.parseInt(stringlist.get(7))) > 0 ? true : false);
 		setToast((Integer.parseInt(stringlist.get(8))) > 0 ? true : false);
 		setPopup((Integer.parseInt(stringlist.get(9))) > 0 ? true : false);
 		setVibrate((Integer.parseInt(stringlist.get(10))) > 0 ? true : false);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category.trim();
	}

	public int getLevel_start() {
		return level_start;
	}
	public void setLevel_start(int level_start) {
		this.level_start = level_start;
	}

	public int getLevel_end() {
		return level_end;
	}
	public void setLevel_end(int level_end) {
		this.level_end = level_end;
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

	public String getAlertsound() {
		return alertsound;
	}
	public void setAlertsound(String alertsound) {
		this.alertsound = alertsound;
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
	
	@Override
	public String toString() {  
		return(Integer.toString(id));  
	}  
	
} 