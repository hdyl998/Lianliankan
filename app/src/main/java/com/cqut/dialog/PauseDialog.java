package com.cqut.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.cqut.fruitllk.GameActivity;
import com.cqut.fruitllk.R;

public class PauseDialog extends Dialog implements android.view.View.OnClickListener{

	GameActivity gameActi;
	View btnResume,btnReplay,btnQuit;
	public PauseDialog(Context context) {
		super(context,R.style.dialog);
		this.setContentView(R.layout.dialog_pause);
		initIdEvent();
		gameActi=(GameActivity)context;
	}
	private void initIdEvent() {
		btnQuit=this.findViewById(R.id.exit_game);
		btnReplay=this.findViewById(R.id.replay_game);
		btnResume=this.findViewById(R.id.resume_game);

		btnQuit.setOnClickListener(this);
		btnReplay.setOnClickListener(this);
		btnResume.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		this.dismiss();
		switch(v.getId()){
			case R.id.exit_game:
				//gameActi.saveData();
				gameActi.finish();
				break;
			case R.id.replay_game:
				gameActi.replayGame();
				break;
			case R.id.resume_game:
				gameActi.resumeGame();
				break;
		}
	}
	@Override
	public void onBackPressed() {//点返回键默认恢复游戏
		this.dismiss();
		gameActi.resumeGame();
		super.onBackPressed();
	}

}