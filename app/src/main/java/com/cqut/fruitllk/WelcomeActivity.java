package com.cqut.fruitllk;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;



public class WelcomeActivity extends Activity {

	View imgView;
	AnimationDrawable frame;
	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3??
	private static final long SPLASH_DELAY_MILLIS = 1500;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	Handler h=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			frame.start();
		}
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GO_HOME:
					goHome();
					break;
				case GO_GUIDE:
					goGuide();
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		imgView=(View)findViewById(R.id.img_loading);
		frame = (AnimationDrawable)imgView.getBackground();
		frame.setOneShot(false);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				h.sendEmptyMessage(0);
			}
		}, 200);
		init();
	}

	private void init() {
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		// 取得相应的???，如果没有该???，说明还未写入，用true作为默认??
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (!isFirstIn) {
			// 使用Handler的postDelayed方法??3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		WelcomeActivity.this.finish();
		startActivityForResult(intent, 11);
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if(version >= 5) {
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		}
	}

	private void goGuide() {
		Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
		WelcomeActivity.this.startActivity(intent);
		WelcomeActivity.this.finish();
	}
}
