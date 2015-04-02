package yjc.rtalk;

import java.util.ArrayList;

import yjc.rtalk.views.RoomItemView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RTalkActivity extends Activity {
	
	ArrayList<String> roomList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rtalk);
		//Linear에 추가
		
	}

	/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rtalk, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	//listView Adapter
	class roomAdapter extends BaseAdapter{
		public int getCount(){ return roomList.size(); }
		public Object getItem(int position){ return roomList.get(position); }
		public long getItemId(int position){ return position; }
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			RoomItemView view = new RoomItemView(getApplicationContext());
			return null;
		}
	}
}
