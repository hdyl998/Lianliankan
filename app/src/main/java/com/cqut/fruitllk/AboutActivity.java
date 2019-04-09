package com.cqut.fruitllk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class AboutActivity extends Activity {
	
	View btn_about_hlep,btn_about_introduce,btn_about_pepole;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}
	public void click1(View v){
		Intent i=new Intent(this,IntroduceAboutActivity.class);
		startActivity(i);
	}
	public void click2(View v){
		Intent i=new Intent(this,PepoleActivity.class);
		startActivity(i);
	}
	public void click3(View v){
		this.finish();
		Intent i=new Intent(this,GuideActivity.class);
		startActivity(i);
	}
}
