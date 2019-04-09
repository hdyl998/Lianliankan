package com.cqut.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqut.fruitllk.GameActivity;
import com.cqut.fruitllk.R;
import com.cqut.other.ImageManage;

public class ResultDialog extends Dialog  implements android.view.View.OnClickListener{

	GameActivity gameActi;
	View btnQuit,btnReplay,btnNext;
	ImageView imageRank;
	TextView textInfo;
	public ResultDialog(Context context,boolean isWin,char rank) {
		super(context,R.style.dialog);
		this.setContentView(R.layout.dialog_result);
		gameActi=(GameActivity)context;
		initIdEvent();
		int id[]={R.drawable.point0,R.drawable.point1,R.drawable.point2,R.drawable.point3};
		int index=rank-'0';
		imageRank.setImageResource(id[index]);
		if(isWin){
			textInfo.setText("胜利啦！再来一局");
		}
		else{
			textInfo.setText("别气馁！好成绩属于你");
			btnNext.setVisibility(View.GONE);
		}
	}
	private void initIdEvent() {
		btnQuit=this.findViewById(R.id.btn_dia_quit);
		btnReplay=this.findViewById(R.id.btn_dia_replay);
		btnNext=this.findViewById(R.id.btn_dia_next);

		imageRank=(ImageView) findViewById(R.id.image_dia_result);
		textInfo=(TextView)findViewById(R.id.text_dia_info);

		btnQuit.setOnClickListener(this);
		btnReplay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
	}
	private void quitDialog(){
		MyDialog q=new MyDialog(gameActi, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				gameActi.finish();
			}
		});
		q.showBack();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_dia_quit:
				quitDialog();
				break;
			case R.id.btn_dia_replay:
				this.dismiss();
				gameActi.replayGame();
				break;
			case R.id.btn_dia_next:
				this.dismiss();
				gameActi.nextGame();
				break;
		}
	}
	@Override
	public void onBackPressed() {
		quitDialog();
	}

}
