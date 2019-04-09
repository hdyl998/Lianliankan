package com.cqut.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import com.cqut.fruitllk.R;

public class MyDialog{

	Context context;
	android.content.DialogInterface.OnClickListener li;
	public MyDialog(Context context, android.content.DialogInterface.OnClickListener posiListener ) {
		this.context=context;
		li=posiListener;
	}
	public void showQuit(){
		Dialog dia = new AlertDialog.Builder(context)
				.setIcon(R.drawable.fruit_5)
				.setTitle(R.string.dialog_quit_title)
				.setMessage(R.string.dialog_quit_msg)
				.setPositiveButton(R.string.dialog_quit_p,li)
				.setNegativeButton(R.string.dialog_quit_n,null)
				.create();
		dia.show();
	}
	public void showBack(){
		Dialog dia = new AlertDialog.Builder(context)
				.setIcon(R.drawable.fruit_5)
				.setTitle(R.string.dialog_back_title)
				.setMessage(R.string.dialog_back_msg)
				.setPositiveButton(R.string.dialog_quit_p,li)
				.setNegativeButton(R.string.dialog_quit_n,null)
				.create();
		dia.show();
	}
}
