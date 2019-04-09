package com.cqut.other;

import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.cqut.data.GameSetting;
import com.cqut.fruitllk.R;

public class ImageManage {//图片资源管理
	Activity act;
	Resources res;
	public ImageManage(Activity v){
		act=v;
		res=v.getResources();
	}
	public Bitmap[]getBitmapNum(){//数字资源
		Bitmap []bt=new Bitmap[10];
		bt[0]=getBitmapFromResources(R.drawable.num_0);
		bt[1]=getBitmapFromResources(R.drawable.num_1);
		bt[2]=getBitmapFromResources(R.drawable.num_2);
		bt[3]=getBitmapFromResources(R.drawable.num_3);
		bt[4]=getBitmapFromResources(R.drawable.num_4);
		bt[5]=getBitmapFromResources(R.drawable.num_5);
		bt[6]=getBitmapFromResources(R.drawable.num_6);
		bt[7]=getBitmapFromResources(R.drawable.num_7);
		bt[8]=getBitmapFromResources(R.drawable.num_8);
		bt[9]=getBitmapFromResources(R.drawable.num_9);
		return bt;
	}
	public Bitmap[]getBitmapStar(){//评分资源
		Bitmap []bt=new Bitmap[4];
		bt[0]=getBitmapFromResources(R.drawable.point0);
		bt[1]=getBitmapFromResources(R.drawable.point1);
		bt[2]=getBitmapFromResources(R.drawable.point2);
		bt[3]=getBitmapFromResources(R.drawable.point3);
		return bt;
	}
	public Bitmap[]getBitmapToSize(int iconStyle,int size){//加载图标资源，参数1样式，参数2长，参数3宽
		int []iconId=null;
		int num=19;
		if(iconStyle==GameSetting.GAME_STYLE_ICON_RANDOM)
			iconStyle=new Random().nextInt(3);
		String str="f";
		switch(iconStyle){
			case GameSetting.GAME_STYLE_ICON_1:break;
			case GameSetting.GAME_STYLE_ICON_2:str="fruit_";break;
			case GameSetting.GAME_STYLE_ICON_3:str="star_";break;
		}
		String pack= act.getPackageName();//获取包名
		iconId=new int[num];
		for(int i=1;i<=num;i++)
			iconId[i-1]=res.getIdentifier(str+i, "drawable",pack);
		Bitmap[]arrBt=new Bitmap[num];
		for(int i=0;i<iconId.length;i++)
			arrBt[i]=loadBitmapToSize(res.getDrawable(iconId[i]),size,size);
		return arrBt;
	}
	public Bitmap loadBitmapToSize(Drawable d,int iconSize,int iconSize2) {//把指定的drawable资源转成指定大小的位图
		Bitmap bitmap = Bitmap.createBitmap(iconSize,iconSize2,Bitmap.Config.ARGB_8888);//设置一个iconSize大小的块的位图
		Canvas canvas = new Canvas(bitmap);//获取bitMap的画布
		d.setBounds(0, 0, iconSize, iconSize);//设置drawable边距
		d.draw(canvas);//在画布上上drawable
		return bitmap;
	}
	public Bitmap getOneBitmapToSize(int resId,int iconSize,int iconSize2){
		return loadBitmapToSize(res.getDrawable(resId),iconSize,iconSize2);
	}
	//resId->Bitmap
	public Bitmap getBitmapFromResources(int resId) {
		return BitmapFactory.decodeResource(res, resId);
		//return ((BitmapDrawable)res.getDrawable(resId)).getBitmap();两种都可以
	}

	//Drawable->Bitmap
	public Bitmap convertDrawable2BitmapSimple(Drawable drawable){
		BitmapDrawable bd=(BitmapDrawable)drawable;
		return bd.getBitmap();
	}

	public Drawable convertBitmap2Drawable(Bitmap bitmap){
		BitmapDrawable bd=new BitmapDrawable(bitmap);//因为BitmapDrawable是Drawable的子类，最终直接使用bd对象
		return bd;
	}
	public Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	public Bitmap convertDrawable2BitmapByCanvas(int resid) {
		return convertDrawable2BitmapByCanvas(res.getDrawable(resid));
	}
}
