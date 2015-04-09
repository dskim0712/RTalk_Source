package yjc.rtalk_v2;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


//JSON TYPE 값들의 집합
class JSON_TYPE{
	public static final String CONN 	="CONN";
	public static final String CREATE 	="CREATE";
	public static final String JOIN 	="JOIN";
}
public class RtalkActivity extends Activity {
	private static final String TAG = "RTalk";
	RoomAdapter rAdapter;
	ListView roomlist;
	//네트워크 관련 변수
	private DatagramSocket socket;
	private DatagramPacket outPacket;
	private DatagramPacket inPacket;

	private byte[] 		   buffer;
	private static final int 	PORT 		= 12345;
	private static final String SRV_IP 		= "107.191.117.218";
	private static final int 	BUFFER_SIZE = 2048;
	private boolean isConn	= false;
	private InetAddress srvINet;
	private UDPModule module;



	ArrayList<RoomItem> arItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rtalk);

		module = UDPModule.getModule();
		arItem = new ArrayList<RoomItem>();
		Thread netTh = new Thread(){
			public void run(){
				//Network 초기화
				try{
			
					//Inet 생성
					srvINet = InetAddress.getByName(SRV_IP);
					//소켓 생성
					//socket = new DatagramSocket(PORT);
					socket = module.getSocket();
					//JSON 생성
					JSONObject json = new JSONObject();
					json.put("TYPE", JSON_TYPE.CONN);
					buffer = json.toString().getBytes();
					//SERVER 와 연결시도
					//패킷 생성
					outPacket 
					= new DatagramPacket(buffer, buffer.length,srvINet,PORT);
					//전송
					socket.send(outPacket);

					/*try{
						Thread.sleep(1000*3);
					}catch(Exception e){}
					isConn =true;*/
					//연결 이후 리스트 수령
					buffer = new byte[BUFFER_SIZE];
					inPacket = new DatagramPacket(buffer,buffer.length);
					Log.d(TAG,"dd");
					socket.receive(inPacket);
					Log.d(TAG,"ff");
					//String 으로 변환
					String str = new String(inPacket.getData()).trim();
					createRoomlist(str);

					isConn = true;
				}catch(Exception e){
					Log.e(TAG,TAG+":Network",e);
					isConn =true;
				}
			}
		};
		netTh.start();
		//전송이 안오면 동작 X
		while(!isConn){}


		//리스트로 목록 채움

		//roomlist adapter 세팅		
		RoomAdapter rAdapter = new RoomAdapter(this, R.layout.room_item, arItem);
		//roomlist 호출
		roomlist = (ListView)findViewById(R.id.roomlist);
		//roomlist 세팅
		roomlist.setAdapter(rAdapter);
	}
	//JSON 만드는 메서드
	public String makeJSON(String aType, String aName){
		//JSON 객체 제작


		return null;
	}

	//btn_create 핸들러
	public void mOnClick_create(View v){
		//text 값 확인 
		final String rName = ((EditText)findViewById(R.id.edit_room_name))
				.getText().toString();
		Log.d("Test",rName);
		//입력된 값이 없으면 끝
		if(rName.equals("")){
			Toast.makeText(getApplicationContext(), "잘 못 된 값입니다.", Toast.LENGTH_LONG).show();
			return;
		}
		Thread netThread = new Thread(){
			public void run(){
				try{
					//서버에 요청
					JSONObject json = new JSONObject();
					json.put("TYPE", JSON_TYPE.CREATE);
					json.put("name",rName);
					buffer = json.toString().getBytes();
					outPacket = new DatagramPacket(buffer,buffer.length,srvINet,PORT);
					socket.send(outPacket);
					//수신 대기
					inPacket = new DatagramPacket(buffer,buffer.length);
					socket.receive(inPacket);
					//JSON 파싱
					String str = new String(inPacket.getData()).trim();
					json = new JSONObject(str);
					//화면 전환
					Intent intent = new Intent(getApplicationContext(), RadioActivity.class);
					//방 ID 삽입
					intent.putExtra("rID",json.getInt("rID"));
					
					startActivity(intent);
					finish();
				}catch(Exception e){
					Log.e(TAG,":CREATE ERROR",e);
				}
			}
		};		
		netThread.start();

	}
	//방 리스트 최신화 메서드
	public void createRoomlist(String context){
		//JSONObject json
		try {
			JSONObject json = new JSONObject(context);
			//get roomlist
			JSONArray jArr = new JSONArray(json.getString("Room"));
			for(int iCnt =0; iCnt < jArr.length(); iCnt++){
				json = jArr.getJSONObject(iCnt);
				RoomItem ri 
				= new RoomItem(R.drawable.ic_launcher,
						json.getInt("id"),
						json.getString("name"));
				arItem.add(ri);
			}
		} catch (JSONException e) {
			Log.e(TAG,TAG+":JSON ",e);
		}
	}
	//Adapter class 
	class RoomAdapter extends BaseAdapter{
		Context maincon;
		LayoutInflater inflater;
		ArrayList<RoomItem> arSrc;
		int layout; 

		public RoomAdapter(Context context, int alayout, ArrayList<RoomItem> aarSrc){
			//각 변수 초기화
			maincon = context;
			layout  = alayout;
			arSrc 	= aarSrc;
			//inflate 전개
			inflater = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		public void updateList(RoomItem aitem){
			arSrc.add(aitem);
			this.notifyDataSetChanged();
		}
		public int getCount() { 
			return arSrc.size(); 
		}

		public Object getItem(int position) { 
			return arSrc.get(position).name; 
		}

		public long getItemId(int position) {
			return position;
		}

		//View를 추가하는 메서드
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			//View가 존재하지 않으면 전개
			if(convertView == null){
				convertView = inflater.inflate(layout, parent,false);
			}
			//아이콘 생성 & 삽입
			ImageView img = (ImageView)convertView.findViewById(R.id.room_img);
			img.setImageResource(arSrc.get(position).icon);
			//ID 생성 & 삽입
			TextView txtID = (TextView)convertView.findViewById(R.id.room_id);
			txtID.setText(arSrc.get(position).id+"");
			//텍스트 생성 & 삽입
			TextView txt = (TextView)convertView.findViewById(R.id.room_name);
			txt.setText(arSrc.get(position).name);
			//버튼 연결
			Button btn = (Button)convertView.findViewById(R.id.btn_join);
			//핸들러 구성
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Thread netThread = new Thread(){
						public void run(){
							try{
								//서버에 요청
								JSONObject json = new JSONObject();
								json.put("TYPE", JSON_TYPE.JOIN);
								json.put("rID",arSrc.get(pos).id);
								buffer = json.toString().getBytes();
								outPacket = new DatagramPacket(buffer,buffer.length,srvINet,PORT);
								socket.send(outPacket);
								//수신 대기
								inPacket = new DatagramPacket(buffer,buffer.length);
								socket.receive(inPacket);
								//화면 전환
								Intent intent = new Intent(getApplicationContext(), RadioActivity.class);
								intent.putExtra("rID",arSrc.get(pos).id);
								//방 ID 삽입
								startActivity(intent);
								finish();
							}catch(Exception e){
								Log.e(TAG,":CREATE ERROR",e);
							}
						}
					};		
					netThread.start();
				}
			});
			//반환
			return convertView;
		}

	}
}
class RoomItem{
	int 	icon;
	int 	id;
	String  name;
	RoomItem(int aIcon, int aID, String aName){
		icon = aIcon;
		id	 = aID;
		name = aName;
	}
}

