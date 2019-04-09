package com.cqut.fruitllk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.cqut.data.GameData;
import com.cqut.data.GameSetting;

/*----------------下面是游戏视图类-----------------*/
public  class GameView extends View {//视图View
	GameActivity gA;
	public Point[]path;//路线
	GameData data;//游戏数据
	Bitmap[]icons=null;
	int map[][]=null;//地图
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true); //允许获得焦点
		setFocusableInTouchMode(true); //获取焦点时允许触控
		gA=(GameActivity)context;
	}
	public void initReferences(GameData d,int arr[][]){//初始化引用
		data=d;
		map=arr;
		icons=gA.icons;
	}
	public void setPath(Point[]path){
		this.path=path;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/**
		 * 绘制连通路径，然后将路径以及两个图标清除
		 */
		boolean flag=false;
		Point pointScore=null;
		if (path != null && path.length >= 2) {
			flag=true;
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(5);
			for (int i = 0; i < path.length - 1; i++)
			{
				Point p1 = gA.indexToScreen(path[i].x, path[i].y);
				Point p2 = gA.indexToScreen(path[i+1].x,path[i+1].y);
				canvas.drawLine(p1.x + data.iconSize / 2, p1.y + data.iconSize / 2,
						p2.x + data.iconSize / 2, p2.y + data.iconSize / 2, paint);
			}
			pointScore=gA.indexToScreen(path[0].x,path[0].y);
			path = null;
			gA.selectIcon.clear();
		}
		/**
		 * 绘制棋盘的所有图标 当这个坐标内的值大于0时绘制
		 */
		for(int x=0;x<data.xCount;x++)
		{
			for(int y=0;y<data.yCount;y++)
			{
				if(map[x][y]>0)
				{
					Point p = gA.indexToScreen(x, y);
					canvas.drawBitmap(icons[map[x][y]-1], p.x,p.y,null);
				}
			}
		}
		/**
		 * 绘制选中图标，当选中时图标选中显示
		 */
		if(gA.gameSet.linkEffect==GameSetting.LINK_BORDER)
		{
			for(Point position:gA.selectIcon){
				Paint pt = new Paint();
				pt.setColor(Color.RED);
				pt.setStyle(Style.STROKE);
				pt.setStrokeWidth(5);
				Point p = gA.indexToScreen(position.x, position.y);
				if(gA.gameLogic.map[position.x][position.y] >= 1)
					canvas.drawRect(new Rect(p.x+5, p.y+5, p.x+data.iconSize-5, p.y+data.iconSize-5), pt);
			}
		}else{
			for(Point position:gA.selectIcon){
				Point p = gA.indexToScreen(position.x, position.y);
				if(gA.gameLogic.map[position.x][position.y] >= 1)
				{
					canvas.drawBitmap(icons[map[position.x][position.y]-1],
							null,
							new Rect(p.x-10, p.y-10, p.x + data.iconSize + 10, p.y + data.iconSize + 10), null);
				}
			}
		}
		if(flag)
		{
			//画分数
			Paint paint = new Paint();
			paint.setColor(Color.BLUE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(30);
			String str="+"+data.scoreCurLine;
			canvas.drawText(str, pointScore.x+data.iconSize / 2,pointScore.y+data.iconSize / 2, paint);
		}
	}
//	@Override
//	protected Parcelable onSaveInstanceState() { //处理窗口保存事件
//	Parcelable pSaved = super.onSaveInstanceState();
//	Bundle bundle = new Bundle();
//	//dosomething
//	return bundle;
//	}
//	 @Override
//	protected void onRestoreInstanceState(Parcelable state) {//处理窗口还原事件
//	Bundle bundle = (Bundle) state;
//	//dosomething
//		super.onRestoreInstanceState(bundle.getParcelable("cwj"));
//	}
}