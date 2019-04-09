package com.cqut.fruitllk;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.cqut.data.GameSetting;

public class SelectActivity extends Activity {//游戏模式选择界面

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
	}
	public void clickTimeLevel(View v){//争分夺秒，过关那种
		Intent i=new Intent(this,StageSelectActivity.class);
		i.putExtra("mode", GameSetting.GAME_MODE_TIME);
		startActivity(i);
	}
	public void clickClassic(View v){//经典模式
		Intent i=new Intent(this,GameActivity.class);
		i.putExtra("mode", GameSetting.GAME_MODE_CLASSIC);
		startActivity(i);
	}
	public void clickEndless(View v){//无尽
		Intent i=new Intent(this,GameActivity.class);
		i.putExtra("mode", GameSetting.GAME_MODE_ENDLESS);
		startActivity(i);
	}
	public void clickMapLevel(View v){//地图过关
		Intent i=new Intent(this,StageSelectActivity.class);
		i.putExtra("mode", GameSetting.GAME_MODE_MAP);
		startActivity(i);
	}
	public void clickMall(View v){
		Intent i=new Intent(this,MallActivity.class);
		startActivity(i);
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
}
