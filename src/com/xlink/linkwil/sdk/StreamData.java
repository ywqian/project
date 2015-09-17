package com.xlink.linkwil.sdk;

public class StreamData
{
	public int frameType; // 0=video 1=audio 3=snap
	public int videoFormat; //0=H264 1=MJPEG
	public byte[] data;
	public int dataLen;
	public int isKeyFrame;
	public int videoWidth;
	public int videoHeight;
	public long timeStamp;
}
