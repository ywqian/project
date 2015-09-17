package com.example.vitamiodemo;

import java.lang.reflect.Method;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.xlink.linkwil.sdk.DevInfo;
import com.xlink.linkwil.sdk.IpcApi;
import com.xlink.linkwil.sdk.WifiSetting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final String AP_SSID = "AP_TEST";
	private final String AP_PWD = "20150414";
	
	private final int CONNECT_STATUS_OPEN_WIFI = 1;
	private final int CONNECT_STATUS_CONNECT_TO_CAMERA = 2;
	private final int CONNECT_STATUS_SET_WIFI_CONFIG = 3;
	private final int CONNECT_STATUS_OPEN_AP = 4;
	private final int CONNECT_STATUS_WAIT_CAMERA_READY = 5;
	private final int CONNECT_STATUS_GOT_IP_SUCCESS = 6;
	private final int CONNECT_STATUS_COMPLETE = 7;
	
	private Button btn_start;
	private TextView tv_connStatus;
	private EditText et_SN;
	
	private WifiManager wifiManager;
	private WifiAdmin wifiAdmin;
	private ProgressDialog mProgressDialog;
	
	private String mSearchedIP;
	
	private Handler mHandler = new Handler(){
		 @Override  
	     public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case CONNECT_STATUS_OPEN_WIFI:  
	        	tv_connStatus.setText("正在打开WIFI");
	        	break;
	        case CONNECT_STATUS_CONNECT_TO_CAMERA:  
	        	tv_connStatus.setText("WIFI打开成功， 正在连接到Camera");
	        	break;
	        case CONNECT_STATUS_SET_WIFI_CONFIG:  
	        	tv_connStatus.setText("Camera连接成功，正在配置Camera的WIFI");
	        	break;
	        case CONNECT_STATUS_OPEN_AP:  
	        	tv_connStatus.setText("WIFI配置成功，正在打开手机AP");
	        	break;
	        case CONNECT_STATUS_WAIT_CAMERA_READY:  
	        	tv_connStatus.setText("手机AP打开成功，等");
	        	break;
	        case CONNECT_STATUS_GOT_IP_SUCCESS:
	        	if( mSearchedIP != null )
	        	{
		        	new AlertDialog.Builder(MainActivity.this).setTitle("摄像机配对成功")
		        	.setMessage("摄像头RTSP地址: rtsp://"+mSearchedIP+":555/VideoSub")
		        	.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	
		        		public void onClick(DialogInterface dialog, int which) {
		        			String path = "rtsp://"+mSearchedIP+":555/VideoSub";
		        			
		        			Intent intent = new Intent(MainActivity.this, VideoViewDemo.class);
		        			intent.putExtra("path", path);
		        			startActivity(intent);
		        		}
		        		
		        	}).show();
	        	}
	        	else
	        	{
		        	new AlertDialog.Builder(MainActivity.this).setTitle("摄像机配对失败")
		        	.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	
		        		public void onClick(DialogInterface dialog, int which) {
		        			
		        		}
		        		
		        	}).show();
	        	}
	        	break;
	        case CONNECT_STATUS_COMPLETE:  
				if( mProgressDialog != null )
					mProgressDialog.dismiss();
	        	break;
	        }
        }
	};
	
	protected void startActivity(String path) {
		Intent intent = new Intent(MainActivity.this, VideoViewDemo.class);
		intent.putExtra("path", path);
		startActivity(intent);
	}
	
	public static String getBroadcast() throws SocketException {
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
	        NetworkInterface ni = niEnum.nextElement();
	        if (!ni.isLoopback()) {
	            for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
	            	if (interfaceAddress.getBroadcast() != null) {
	            		return interfaceAddress.getBroadcast().toString().substring(1);
					}
	            }
	        }
	    }
	    return null;
	}
	
	private OnClickListener mClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if( v == btn_start )
			{
				mProgressDialog = ProgressDialog.show(MainActivity.this, 
						null,  
						"正在配对相机", true);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						mHandler.sendEmptyMessage(CONNECT_STATUS_OPEN_WIFI); // 1、打开WIFI 
						
						setWifiApEnabled(false);
						wifiAdmin.openWifi(); // 打开WIFI  
						
						// 2、WIFI打开成功， 连接到Camera
						mHandler.sendEmptyMessage(CONNECT_STATUS_CONNECT_TO_CAMERA);
						
						String sn = et_SN.getText().toString();
						String ssid = "Camera_";
						ssid += sn.substring(sn.length()-4); // ssid=Camera_000C
						String password = "cam*";
						password += sn; // psd=cam*0000000C
						WifiConfiguration wcg = wifiAdmin.CreateWifiInfo(ssid, password, 3/*WPA*/);  
		                boolean issuccess = wifiAdmin.addNetwork(wcg);  
		                Log.i("qqq", "连接网络并添加是否成功="+issuccess+",ssid="+ssid);
		                if(issuccess){  
		                    //Toast.(context, "WiFi已经连接成功！") ; 
		                }else{  
		                    //Toastclass.displayToast(context, "WiFi连接失败，请检查密码输入是否正确！") ;  
		                }  
		                
		                // wait wifi connect to camera
		                while( !isWifiConnect() )
		                {
		                	try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                }
		                
		                
		                mHandler.sendEmptyMessage(CONNECT_STATUS_SET_WIFI_CONFIG);// 3、Camera连接成功，正在配置Camera的WIFI
		                
		                
		             // 搜索同一局域网内的设备
		                while( true ){
		                	DevInfo []devs = IpcApi.searchDev(60*1000);
		                	if( devs != null ){
		                		//-------------------搜索到设备
		                		break;
		                	}else{
			                	try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
		                	}
		                }
		                
		                // 登陆到相机
		                Integer privilege = new Integer(0);
		                int handle = IpcApi.usrLogIn(privilege, "192.168.1.1", "", "admin", "admin", 80, 0);
		                Log.i("qqq", "登录到相机="+handle);
		                
		                // 配置摄像机的WIFI连接信息
		                WifiSetting wifiSetting = new WifiSetting();
		                wifiSetting.ssid = AP_SSID;
		                wifiSetting.key = AP_PWD;
		                wifiSetting.securityType = 5;
		                int loginSuccess = IpcApi.setWirelessSetting(handle, wifiSetting); 
		                Log.i("qqq", "配置摄像机的WIFI连接信息="+loginSuccess);
		                //IpcApi.reboot(handle);
		                IpcApi.usrLogOut(handle);
		                
		                setWifiApEnabled(true);
		                
		                String bcastAddr = "";
		                while( true ) // wait ap ready
		                {
							try {
								bcastAddr = getBroadcast();
								if( (bcastAddr!=null) && (bcastAddr.length()>0) )
								{
									break;
								}
							} catch (SocketException e1) {
								e1.printStackTrace();
							}
		                }
		                Log.i("qqq", "bcastAddr="+bcastAddr);
		                if( bcastAddr != null )
		                {
		                	IpcApi.setBCastAddr(bcastAddr);
		                }
		                while( true ){
		                	DevInfo []devs = IpcApi.searchDev(10*1000);
		                	if( devs != null ){
		                		//-------------------搜索到设备
		                		mSearchedIP = devs[0].ip;
		                		mHandler.sendEmptyMessage(CONNECT_STATUS_GOT_IP_SUCCESS);
		                		break;
		                	}else{
			                	try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                	}
		                }
		                mHandler.sendEmptyMessage(CONNECT_STATUS_COMPLETE);
		                
					}
				}).start();
			}
		}
	};
	
	 @Override  
	 protected void onCreate(Bundle savedInstanceState) {  
		 super.onCreate(savedInstanceState);  
		 setContentView(R.layout.activity_main);  
		 
		 btn_start = (Button)findViewById(R.id.btn_start);
		 tv_connStatus = (TextView)findViewById(R.id.connect_status);
		 et_SN = (EditText)findViewById(R.id.et_sn);
		 btn_start.setOnClickListener(mClickListener);
		 
		 wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		 wifiAdmin = new WifiAdmin(this);  
		 
		 TextView test = (TextView) findViewById(R.id.test);
		 test.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, VideoViewDemo.class);
    			startActivity(intent);
			}
		});
	 }
	 
	 public boolean isWifiConnect() {
	       ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	       NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	       return mWifi.isConnected();
	 }
	 
	// wifi热点开关  
    public boolean setWifiApEnabled(boolean enabled) {  
        if (enabled) { // disable WiFi in any case  
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi  
            wifiManager.setWifiEnabled(false);  
        }  
        try {  
            //热点的配置类  
    		WifiConfiguration apConfig = new WifiConfiguration();
    		apConfig.allowedAuthAlgorithms.clear();
    		apConfig.allowedGroupCiphers.clear();
    		apConfig.allowedKeyManagement.clear();
    		apConfig.allowedPairwiseCiphers.clear();
    		apConfig.allowedProtocols.clear();
//    		config.SSID = "\"" + SSID + "\"";
    		apConfig.SSID =  AP_SSID ;
    		
            apConfig.preSharedKey =  AP_PWD ;
            apConfig.hiddenSSID = true;
            apConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
            apConfig.status = WifiConfiguration.Status.ENABLED;
			
			
                //通过反射调用设置热点  
            Method method = wifiManager.getClass().getMethod(  
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);  
            //返回热点打开状态  
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);  
        } catch (Exception e) {  
            return false;  
        }
    }  
}


