/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xlink.linkwil.sdk;

import android.util.Log;

import com.xlink.linkwil.sdk.DeviceInfo;
import com.xlink.linkwil.sdk.StatusListener;
import com.xlink.linkwil.sdk.StreamData;


public class IpcApi 
{
	public static int LOGIN_RESULT_SUCCESS					= 0x00;
	public static int LOGIN_RESULT_ERROR_USR_PWD_ERROR		= -0x01;
	public static int LOGIN_RESULT_ERROR_ACCESS_DENY		= -0x02;
	public static int LOGIN_RESULT_ERROR_EXCEED_MAX_USER	= -0x03;
	public static int LOGIN_RESULT_ERROR_CONNECT_FAIL		= -0x04;
	public static int LOGIN_RESULT_ERROR_ERROR_UNKNOW		= -0x05;
	
	public static int CONNECTION_MODE_TCP = 0;
	public static int CONNECTION_MODE_P2P = 1;
	public static int CONNECTION_MODE_RELAY = 2;
	public static int CONNECTION_MODE_UNKNOWN = 3;
	
	private static StatusListener mStatusListener;
	
	private static void StatusCbk(int handle, int statusID, int reserve1, int reserve2, int reserve3, int reserve4)
	{
		Log.d("LinkCam", "StatusCbk statusID:"+statusID );
		if( statusID == StatusListener.STATUS_ALARM )
		{
			Log.d("LinkCam", "=========Recv alarm" );
		}
		
		if( mStatusListener != null ){
			mStatusListener.OnStatusCbk( handle, statusID, reserve1, reserve2, reserve3, reserve4 );
		}
	}
	
	public static void setStatusListener(StatusListener listener)
	{
		mStatusListener = listener;
	}
    
    /*
     * Start search cameras in your LAN 
     */
	public static native int setBCastAddr(String bcastAddr);
    public static native DevInfo[] searchDev(int timeout/*ms*/);
    public static native void cancelSearchDev();
    
    public static native int setInitIpInfo(String userName, String password, String devId, IpInfo ipInfo);
    
    /*
     * Login to camera.
     * return handle
     */
	public static native int usrLogIn(Integer privilege, String ip, String uid, String userName, String password, int webPort, int isForProbe);
	public static native int interruptUsrLogIn(String ip, int port, String uid);
	public static native int usrLogOut(int handle);
    public static native int usrLogOut2(String ip, int port, String uid);
    public static native OnlineUserInfo[] getOnlineUsers(int handle);
    
    public static native int getConnectionMode(int handle);
    
    public static native int  ptzMoveUp(int handle);
    public static native int  ptzMoveDown(int handle);
    public static native int  ptzMoveLeft(int handle);
    public static native int  ptzMoveRight(int handle);
    public static native int  ptzMoveTopLeft(int handle);
    public static native int  ptzMoveTopRight(int handle);
    public static native int  ptzMoveBottomLeft(int handle);
    public static native int  ptzMoveBottomRight(int handle);
    public static native int  ptzStopRun(int handle);
    public static native int  ptzMoveAngle(int handle, int dir, int angle);
    public static native PresetInfo[] getPresets(int handle);
    public static native int addPreset(int handle, String name);
    public static native int delPreset(int handle, String name);
    public static native int gotoPreset(int handle, String name, int isSync);
    
    
    public static native int startVideoStream(int handle, int streamType);
    public static native int getVideoStreamData(int handle, StreamData streamData);
    public static native int stopVideoStream(int handle);
    public static native int startAudioStream(int handle);
    public static native int getAudioStreamData(int handle, StreamData streamData);
    public static native int stopAudioStream(int handle);
    
    public static native int startTalk(int handle);
    public static native int sendTalkFrame(int handle, byte[] frame, int frameLen);
    public static native int sendVoice(int handle, String pathName);
    public static native int stopTalk(int handle);
    
    public static native int snapPic(int handle, String saveDir);
    public static native int snapPic2(int handle, int quality, SnapData snapData);
    
    public static native int startRecord(int handle, String saveDir);
    public static native int stopRecord(int handle);
    
    public static native int startSDRecord(int handle, Integer startResult);
    public static native int stopSDRecord(int handle);
    public static native RecordSegInfo[] getRecordList(int handle, long startTime, long endTime);
    public static native int startPlaySDCard(int handle, long startTime, long endTime, int recordType);
    public static native int stopPlaySDCard(int handle);
    public static native int getPlaybackStreamData(int handle, StreamData streamData);
    
    public static native int InitSmartConnection();
    public static native int StartSmartConnection(String ssid, String password, String tlv, int authMode);
    public static native int StopSmartConnection();
    
    public static native int getDeviceInfo(int handle, DeviceInfo info);
    public static native String getDevName(int handle);
    public static native int setDevName(int handle, String name);
    public static native UserAccountInfo[] getUserAccounts(int handle);
    public static native int setUserAccounts(int handle, UserAccountInfo[] account);
    public static native int getDevTime(int handle);
    public static native int setDevTimeConfig(int handle, DevTimeConfig config);
    public static native int getIpInfo(int handle, IpInfo ip);
    public static native int setIpInfo(int handle, IpInfo ip);
    public static native int getPortInfo(int handle, PortInfo port);
    public static native int setPortInfo(int handle, PortInfo port);
    public static native int getWirelessSetting(int handle, WifiSetting setting);
    public static native int setWirelessSetting(int handle, WifiSetting setting);
    public static native int testWirelessSetting(int handle, WifiSetting setting, Integer testResult);
    public static native WifiAPInfo[] scanWifi(int handle);
    public static native int wpsConnect(int handle, WifiSetting setting);
    public static native int getMailSetting(int handle, MailSetting setting);
    public static native int setMailSetting(int handle, MailSetting setting);
    public static native int testMailSetting(int handle, MailSetting setting);
    public static native int getFtpSetting(int handle, FtpSetting setting);
    public static native int setFtpSetting(int handle, FtpSetting setting);
    public static native int testFtpSetting(int handle, FtpSetting setting, Integer testResult);
    public static native int getDDNSSetting(int handle, DDNSSetting setting);
    public static native int setDDNSSetting(int handle, DDNSSetting setting);
    public static native int getVideoParam(int handle, VideoParam param);
    public static native int setVideoParam(int handle, VideoParam param);
    public static native int getMirrorAndFlipSetting(int handle, MirrorAndFlipSetting setting);
    public static native int mirrorVideo(int handle, int isMirror);
    public static native int flipVideo(int handle, int isFlip);
    public static native int getMotionAlarmSetting(int handle, MotionAlarmSetting setting);
    public static native int setMotionAlarmSetting(int handle, MotionAlarmSetting setting);
    public static native int getInfraLedSetting(int handle, InfraLedSetting setting);
    public static native int setInfraLedSetting(int handle, InfraLedSetting setting);
    public static native int openInfraLed(int handle);
    public static native int closeInfraLed(int handle);
    public static native int allReset(int handle);
    public static native int reboot(int handle);
    
    public static native int fwUpgradeFromUrl(int handle, String url);
    
    public static native FtpDirInfo[] ftpList(int handle, String dir);
    public static native void closeFtp(int handle);
    
    public static native int generateDtmf(String str, byte[] pcm);
   
    static {
    	try{
    		System.loadLibrary("PPPP_API"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("LinkCam", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("IOTCAPIs"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("LinkCam", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("RDTAPIs"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("LinkCam", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("faad"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("LinkCam", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("mp4v2"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("LinkCam", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("IpcSdk"); 
    		Log.d("LinkCam", "IpcSdk load success");
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("LinkCam", ule.getMessage() );
    	}
    }

}
