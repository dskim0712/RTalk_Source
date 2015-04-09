package yjc.rtalk_v2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import org.json.JSONObject;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

/**
 * Created by Rin on 2015-03-29.
 * Description : Get Audio Data on UDP Streaming
 */
public class AudioReceiver extends AsyncTask<Void, Integer, Void> implements AudioConfigure {
    //# Error Debug Tag
    private static final String TAG = "UDP_ARev";

    //# Network member values
    private DatagramSocket  revSocket;       //UDP Socket (Receive)
    private DatagramPacket  inPacket;        //UDP Packet (Receive)
    private static int      portNum;         //Streaming Port Number
    private ArrayList<String> audioBox;
    //# Audio member values
    private AudioTrack      audioPlayer;      //Audio Player

    public AudioReceiver( int port_Number){
     //make AudioPlayer
        //Type : Streaming voice
      audioPlayer = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                                   SAMPLING_RATE, AUDIO_CHANNEL, AUDIO_FORMAT, BUFFER_SIZE,
                                   AudioTrack.MODE_STREAM);
    //construct port number, BufferSize
      this.portNum  = port_Number;
   
    }
    @Override
    protected Void doInBackground(Void... params) {

        //Player start
        audioPlayer.play();
        try {
        	UDPModule module = UDPModule.getModule();
            //Make Socket
            //revSocket = new DatagramSocket();
        	revSocket = module.getSocket();
            //Make Buffer
            byte[] buffer = new byte[BUFFER_SIZE*2];
            audioBox = new ArrayList<String>();
            
            //wait Send Packet (listen)
            while(true) {
                //get Packet
                inPacket = new DatagramPacket(buffer, buffer.length);
                revSocket.receive(inPacket);
                
                Log.d(TAG,"DDD");
                String str = new String(inPacket.getData()).trim();
                JSONObject json = new JSONObject(str);
                Log.d(TAG,json.toString());
                String strAudio = json.getString("AUDIO");
                byte[] rawData = Base64.decode(strAudio, Base64.DEFAULT);
                //write Player
                    //write ( target Data, start, Destination)
                audioBox.add(strAudio);
                //3초동안 모음 
                //한번에 처리
                audioPlayer.write(rawData,0, rawData.length);
                Log.d(TAG,inPacket.getData().toString());
            }
        }catch(Exception e){
           Log.e(TAG, "S: ERROR Audio Error",e);
        }

        return null;
    }
}
