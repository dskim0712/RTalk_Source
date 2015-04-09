package yjc.rtalk_v2;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

/**
 * Created by Rin on 2015-03-29.
 * Description : Audio UDP Packet Sender
 */
public class AudioSender implements AudioConfigure{
    //# Error Debug Tag
    private static final String TAG = "UDP_ASnd";
    //# Network member values
    private final static String 		srvIP   = "107.191.117.218";
    private int 						rID;
    private DatagramSocket              sndSocket;                // UDP Socket (Sending)
    private boolean                     isSendingAudio = false;   // is Sending Audio Streaming?

    //# Audio member values
    private AudioRecord audioRecord;
    private byte[]      buffer;                         // Data Buffer
    private ArrayList<DatagramPacket>  rawDatalist;
    
    public AudioSender(int arID){
    	rID = arID;
        //make Buffer
        buffer = new byte[BUFFER_SIZE];
        UDPModule module = UDPModule.getModule();
        rawDatalist = new ArrayList<DatagramPacket>();
        //make socket
        try {
            //sndSocket = new DatagramSocket();
        	sndSocket = module.getSocket();
        }catch(Exception e){
            Log.e(TAG,TAG,e);
        }
        
    }

    //Start Streaming
    public void startStreamingAudio() {
        isSendingAudio = true;
        //make Streaming Thread
        Thread streamThread = new Thread(new StreamThread());
        streamThread.start();

    }
    //Stop Streaming _UDP Packet Send
    public void stopStreamingAudio(){
        isSendingAudio = false;
/*
        Thread th = new Thread(new Runnable(){
            public void run(){
                try{
                    //Send Packet
                    DatagramPacket packet;
                    InetAddress address = Inet6Address.getByName(srvIP);
                    
                    //타입 변경
                    String audioData = Base64.encodeToString(buffer, Base64.DEFAULT);
                    JSONObject json = new JSONObject();
                    json.put("TYPE","AUDIO");
                    json.put("rID", rID);
                    json.put("AUDIO",audioData);
                    
                    packet = new DatagramPacket(json.toString().getBytes(),
                    							json.toString().getBytes().length,
                    							address,12345);
                    sndSocket.send(packet);
                }catch(Exception e){
                	Log.e(TAG,TAG+": Receiver Error",e);
                }
            }
        });

        th.start();*/
        audioRecord.release();
        audioRecord = null;
        //전송
        Thread th = new Thread(){
        	public void run(){
        		Iterator<DatagramPacket> it = rawDatalist.iterator();
        		while(it.hasNext()){
        			try{
        				sndSocket.send(it.next());
        			}catch(Exception e){
        				Log.e(TAG,TAG+" Send Error",e);
        			}
        		}
        		rawDatalist.clear();
        	}
        };
        th.start();
    }

    //Streaming Thread class
    private class StreamThread implements Runnable{
    	public void run(){
    		try{
    			//make packet
    			DatagramPacket packet;
    			//make Address
    			InetAddress address = InetAddress.getByName(srvIP);
    			//make Record
    			//type : MIC
    			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
    					SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AUDIO_FORMAT,
    					BUFFER_SIZE);
    			//start Recording
    			audioRecord.startRecording();

    			while(isSendingAudio){
    				//buffer loading
    				int read = audioRecord.read(buffer,0, buffer.length);
                    
                    //타입 변경
                    String audioData = Base64.encodeToString(buffer, Base64.DEFAULT);
                    JSONObject json = new JSONObject();
                    json.put("TYPE","AUDIO");
                    json.put("rID", rID);
                    json.put("AUDIO",audioData);
                    
                    packet = new DatagramPacket(json.toString().getBytes(),
                    							json.toString().getBytes().length,
                    							address,12345);
                    //sndSocket.send(packet);
                    rawDatalist.add(packet);
    			}
    		}catch(Exception e){
    			Log.e(TAG,": RECORD ERROR",e);
    		}
    	}
    }
}
