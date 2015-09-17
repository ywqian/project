package com.xlink.linkwil.sdk;

public class DDDNSSetting {
	public enum DDNS_SERVER{
		DDNS_SERVER_FACTORY,
		DDNS_SERVER_ORAY,
		DDNS_SERVER_3322,
		DDNS_SERVER_NO_IP,
		DDNS_SERVER_DYNDNS
	};
	
	public int isEnable;
	public int server;
	public String domain;
	public String userName;
	public String password;
}
