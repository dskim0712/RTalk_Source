package yjc.rtalk_v2;

import java.net.DatagramSocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class RadioActivity extends Activity{

    //# Network member values
    protected static final int SRV_PORT = 12345;    //Server Port Number

    //# Audio & Mike member values
    //private AudioReceiver audioReceiver;
    private AudioSender   audioSender;
    private AudioReceiver audioReceiver;
	private int 		  rID;
	private UDPModule 	  module;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		//넘어온 값 획득
		Intent intent = getIntent();
		rID = intent.getIntExtra("rID", 0);
		
	
		module = UDPModule.getModule();
		//make Receiver & Sender
		audioSender   = new AudioSender(rID);
	    audioReceiver = new AudioReceiver(SRV_PORT);
	    
	    //Receiver Start
	    audioReceiver.execute();
	    //add Sender Click Event
	    Button btnSend = (Button)findViewById(R.id.btn_send);
	    btnSend.setOnTouchListener(new View.OnTouchListener() {
	    	@Override
	    	public boolean onTouch(View v, MotionEvent event) {
	    		switch(event.getAction()){
	    		case MotionEvent.ACTION_DOWN:
	    			//Start streaming
	    			audioSender.startStreamingAudio();
	    			break;
	    		case MotionEvent.ACTION_UP:
	    			//Stop streaming
	    			audioSender.stopStreamingAudio();
	    			break;
	    		}
	    		return true;
	    	}
	    });
	}
}
