package yjc.rtalk_v2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

public class UDPModule {
	//# class Tag
	private static final String TAG = "UDPModule";
	
	private static UDPModule 	udpModule;
	private static final String SRV_IP 	  = "107.191.117.218";
	private static final int 	SRV_PORT  = 12345;
	private static final int 	BUFF_SIZE = 8196;
	
	
	//Network 관련 변수
	private DatagramSocket socket;
	private DatagramPacket inPacket;
	private DatagramPacket outPacket;
	private byte[] 	buffer = new byte[8196];
	
	private UDPModule(){
		//객체 생성
		try{
			socket = new DatagramSocket(SRV_PORT);
		}catch(Exception e){
			Log.e(TAG,": Error Network Error");
		}
	}
	public static UDPModule getModule(){ 
		if(udpModule == null){
			udpModule = new UDPModule();
		}
		
		return udpModule;
	}
	public DatagramSocket getSocket(){
		return socket;
	}
	
	//getting Server IP
	public String getSrvIP(){	return SRV_IP;	}
}
