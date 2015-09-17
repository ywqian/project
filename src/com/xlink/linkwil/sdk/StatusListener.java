package com.xlink.linkwil.sdk;

public interface StatusListener {

	public static final int STATUS_LOGIN_SUCCESS 			= 0x00;
	public static final int STATUS_LOGIN_FAIL_USR_PWD_ERROR = 0x01;
	public static final int STATUS_LOGIN_FAIL_ACCESS_DENY 	= 0x02;
	public static final int STATUS_LOGIN_FAIL_EXCEED_MAX_USER = 0x03;
	public static final int STATUS_LOGIN_FAIL_CONNECT_FAIL 	= 0x04;
	public static final int STATUS_LOGIN_FAIL_UNKNOWN 		= 0x05;
	
	// open video status
	public static final int STATUS_OPEN_VIDEO_SUCCESS		= 0x10;
	public static final int STATUS_OPEN_VIDEO_CONNECTING	= 0x11;
	public static final int STATUS_OPEN_VIDEO_FAIL			= 0x12;
	public static final int STATUS_CLOSE_VIDEO_SUCCESS		= 0x13;
	public static final int STATUS_CLOSE_VIDEO_FAIL			= 0x14;
	
	// open audio status
	public static final int STATUS_OPEN_AUDIO_SUCCESS		= 0x20;
	public static final int STATUS_OPEN_AUDIO_FAIL			= 0x21;
	public static final int STATUS_CLOSE_AUDIO_SUCCESS		= 0x20;
	public static final int STATUS_CLOSE_AUDIO_FAIL			= 0x21;
	
	// talk
	public static final int STATUS_OPEN_TALK_SUCCESS 		= 0x30;
	public static final int STATUS_OPEN_TALK_FAIL_ACCESS_DENY = 0x31;
	public static final int STATUS_OPEN_TALK_FAIL_USED_BY_ANOTHER_USER = 0x32;
	public static final int STATUS_CLOSE_TALK_SUCCESS 		= 0x33;
	public static final int STATUS_CLOSE_TALK_FAIL 			= 0x34;
	
	public static final int STATUS_ALARM					= 0x50;
	public static final int STATUS_PUSH_BUTTON_ALARM		= 0x51;
	
	public static final int STATUS_ONLINE_USER_CHANGED		= 0x72;

	
	public void OnStatusCbk(int handle, int statusID, int reserve1, int reserve2, int reserve3, int reserve4);
}
