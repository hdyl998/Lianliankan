package com.cqut.fruitllk;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.cqut.dialog.MyDialog;
import com.cqut.sound.SoundPlayer;

public class MainActivity extends Activity implements OnClickListener,android.content.DialogInterface.OnClickListener{//游戏选择界面

	View btn_start,btn_set,btn_about,btn_help,btn_exit;
	View layout_main;
	ImageView img_bubble,img_bird;
	AnimationDrawable eff ;
	AnimationDrawable eff2 ;
	boolean isPlay=false;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			eff.start();
			eff2.start();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initIdEvent();
		initViewEffect();
		SoundPlayer.init(this);//初始化音效设置，只加载一次
	}
	private void initViewEffect() {
		eff=(AnimationDrawable)img_bubble.getBackground();
		eff2=(AnimationDrawable)img_bird.getBackground();
		handler.sendEmptyMessageDelayed(0, 200);
		Animation s = AnimationUtils.loadAnimation(this,R.anim.trans_in);
		layout_main.startAnimation(s);
	}
	void initIdEvent(){
		//以下是查找按钮的ID
		btn_start= findViewById(R.id.btn_start);
		btn_set= findViewById(R.id.btn_set);
		btn_about= findViewById(R.id.btn_about);
		btn_help= findViewById(R.id.btn_help);
		btn_exit=findViewById(R.id.btn_exit);
		layout_main=findViewById(R.id.linearLayoutMenu);
		img_bubble=(ImageView)findViewById(R.id.img_bubble);
		img_bird=(ImageView)findViewById(R.id.img_bird);
		//按键的监听单击事件
		btn_start.setOnClickListener(this);
		btn_set.setOnClickListener(this);
		btn_about.setOnClickListener(this);
		btn_help.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_start:clickStart();break;
			case R.id.btn_set:clickSet();break;
			case R.id.btn_help:clickHelp();break;
			case R.id.btn_about:clickAbout();break;
			case R.id.btn_exit:clickExit();break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	private void clickSet() {
		Intent i=new Intent(this,SettingActivity.class);
		startActivity(i);
	}
	void clickExit(){
		MyDialog q=new MyDialog(this,this);
		q.showQuit();
	}
	void clickStart(){
		Intent i=new Intent(this,SelectActivity.class);
		startActivity(i);
	}

	void clickHelp(){
		Intent i=new Intent(this,HelpActivity.class);//HelpActivity
		startActivity(i);
	}
	void clickAbout(){
		Intent i=new Intent(this,AboutActivity.class);
		startActivity(i);
	}
	@Override
	public void onClick(DialogInterface arg0, int a) {
		switch(a){
			case Dialog.BUTTON_POSITIVE:this.finish();break;
		}
	}
	@Override
	public void onBackPressed() {//点击了返回按键
		clickExit();
	}
}
