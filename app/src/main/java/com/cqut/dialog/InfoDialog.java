package com.cqut.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cqut.fruitllk.R;

public class InfoDialog extends Dialog implements android.view.View.OnClickListener{

	View btn;
	public InfoDialog(Context context,String info) {
		super(context, R.style.dialog);
		this.setContentView(R.layout.dialog_info);
		TextView t=(TextView)this.findViewById(R.id.text_dialog_info);
		t.setText(info);
		btn=this.findViewById(R.id.btnConfirm);
		btn.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		this.dismiss();
	}

}
