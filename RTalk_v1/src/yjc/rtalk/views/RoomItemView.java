package yjc.rtalk.views;

import yjc.rtalk.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RoomItemView extends LinearLayout{
	TextView txvRoomID;
	TextView txvRoomName;
	
	public RoomItemView(Context context) {
		super(context);
		//Layout 초기화
		inflation_init(context);
		
		//ID 와 Name 연결
		txvRoomID = (TextView) findViewById(R.id.room_id);
		txvRoomName= (TextView) findViewById(R.id.room_name);
	}

	//Layout 초기화 메서드
	private void inflation_init(Context context){
		LayoutInflater inflater 
				= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.room, this, true);
	}
}
