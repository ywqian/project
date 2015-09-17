package com.xlink.linkwil.sdk;

public class MotionAlarmSetting {
	public int isEnable;
	public int sensitivity;//0=Low 1=Middle 2=High
	public int triggerInterval; //5~15s
//ALARM_LINKAGE_MASK_MAIL			0x02
//ALARM_LINKAGE_MASK_SNAP			0x04
//ALARM_LINKAGE_MASK_RECORD			0x08
	public int linkage;
	public int snapInterval; // 1~5s
	public int saveLocation; //0=SD, 1=FTP, 2=Dropbox
}
