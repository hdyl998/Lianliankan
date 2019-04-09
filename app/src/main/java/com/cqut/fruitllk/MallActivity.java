package com.cqut.fruitllk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.cqut.data.GameVIInfo;
import com.cqut.dialog.InfoDialog;
import com.cqut.other.ShareHelper;

@SuppressLint("HandlerLeak")
public class MallActivity extends Activity implements OnGestureListener, OnTouchListener ,OnClickListener{

	private TextView date_TextView;
	private ImageButton return_ImageButton;
	private ViewFlipper viewFlipper;
	private boolean showNext = true;
	private boolean isRun = true;
	private int currentPage = 0;
	private final int SHOW_NEXT = 0011;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	private View btnAddTime,btnRefresh,btnRemind,btnBomb;
	private int priceDelay,priceRefresh,priceRemind,priceBomb;
	private TextView textDiamondNum;
	private GestureDetector mGestureDetector;
	GameVIInfo gameInfo;
	ShareHelper share;
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mall_main);
		date_TextView = (TextView) findViewById(R.id.home_date_tv);
		date_TextView.setText(getDate()+"  适度广告有益健康");




		return_ImageButton = (ImageButton) findViewById(R.id.buy_return);
		return_ImageButton.setOnClickListener(clickListener_home);


		viewFlipper = (ViewFlipper) findViewById(R.id.mViewFliper_vf);
		mGestureDetector = new GestureDetector(this);
		viewFlipper.setOnTouchListener(this);
		viewFlipper.setLongClickable(true);
		viewFlipper.setOnClickListener(clickListener);
		displayRatio_selelct(currentPage);

		MyScrollView myScrollView = (MyScrollView) findViewById(R.id.viewflipper_scrollview);
		myScrollView.setOnTouchListener(onTouchListener);
		myScrollView.setGestureDetector(mGestureDetector);


		initIdEvent();//初始化ID及事件
		initPrice();//道具价格
		initPlayerData();//查询玩家数据
		thread.start();
	}
	void initPrice(){
		priceDelay=5;priceRefresh=1;priceRemind=2;priceBomb=4;
		TextView t=(TextView)findViewById(R.id.buy_sx_tv2);
		t.setText("X"+priceRefresh);
		t=(TextView)findViewById(R.id.buy_ys_tv2);
		t.setText("X"+priceDelay);
		t=(TextView)findViewById(R.id.buy_ts_tv2);
		t.setText("X"+priceRemind);
		t=(TextView)findViewById(R.id.buy_zd_tv2);
		t.setText("X"+priceBomb);
	}
	void setDiamondNumText(){
		textDiamondNum.setText(gameInfo.diamondNum+"");
	}
	void initPlayerData(){//查询玩家数据
		share=new ShareHelper(this);
		gameInfo=share.getGameVIInfo();
		setDiamondNumText();
	}
	void initIdEvent(){//初始化ID及事件
		btnAddTime=findViewById(R.id.buy_ys_btn);
		btnRefresh=findViewById(R.id.buy_sx_btn);
		btnRemind=findViewById(R.id.buy_ts_btn);
		btnBomb=findViewById(R.id.buy_zd_btn);
		textDiamondNum=(TextView)findViewById(R.id.own_zhuanshi);

		btnAddTime.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		btnRemind.setOnClickListener(this);
		btnBomb.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.buy_sx_btn:
				if(canBuy(priceRefresh,"刷新",gameInfo.refreshNum+1)){
					gameInfo.refreshNum++;//一个道具一个一个买
					share.saveRefreshNum(gameInfo);
				}
				break;
			case R.id.buy_ts_btn:
				if(canBuy(priceRemind,"提示",gameInfo.remindNum+1)){
					gameInfo.remindNum++;
					share.saveRemindNum(gameInfo);
				}
				break;
			case R.id.buy_ys_btn:
				if(canBuy(priceDelay,"延时",gameInfo.delayNum+1)){
					gameInfo.delayNum++;
					share.saveDelayNum(gameInfo);
				}
				break;
			case R.id.buy_zd_btn:
				if(canBuy(priceBomb,"炸弹",gameInfo.bombNum+1)){
					gameInfo.bombNum++;
					share.saveBombNum(gameInfo);
				}
				break;
		}
	}
	private boolean canBuy(int price,String toolName,int num){//能否购买,参数1价格，参数2道具名
		if(price>gameInfo.diamondNum){
			new InfoDialog(this,"您的钻石数不足以购买["+toolName+"]道具").show();
			return false;
		}
		gameInfo.diamondNum-=price;//减少钻石数，完成购买
		setDiamondNumText();//设置钻石数
		share.saveDiamondNum(gameInfo);//把钻石数存起来
		new InfoDialog(this,"您用["+price+"]个钻石购买1个["+toolName+"]道具      "+toolName+" X "+num).show();
		return true;
	}
	public void makeToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		}
	};
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mGestureDetector.onTouchEvent(event);
		}
	};

	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case SHOW_NEXT:
					if (showNext) {
						showNextView();
					} else {
						showPreviousView();
					}
					break;

				default:
					break;
			}
		}
	};
	@SuppressLint("SimpleDateFormat")
	private String getDate(){

		Date date = new Date();
		SimpleDateFormat from = new SimpleDateFormat("yyyy年MM月dd日   ");
		String times = from.format(date);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		int w = c.get(Calendar.DAY_OF_WEEK) - 1 ;
		if (w < 0) {
			w = 0;
		}
		return times+weekDays[w];
	}
	private OnClickListener clickListener_home = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			return_ImageButton.setSelected(true);
			Intent intent = new Intent();
			intent.setClass(MallActivity.this,SelectActivity.class);
			startActivity(intent);
			return_ImageButton.setSelected(false);
		}
	};

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,//滑动
						   float velocityY) {
		// TODO Auto-generated method stub
		Log.e("view", "onFling");
		if (e1.getX() - e2.getX()> FLING_MIN_DISTANCE  //降噪处理
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY ) {
			Log.e("fling", "left");
			showNextView();
			showNext = true;
//			return true;
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY){
			Log.e("fling", "right");
			showPreviousView();
			showNext = false;
//			return true;
		}
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	Thread thread = new Thread(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRun){
				try {
					Thread.sleep(1000 * 4);
					Message msg = new Message();
					msg.what = SHOW_NEXT;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};
	private void showNextView(){

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.mall_push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.mall_push_left_out));
		viewFlipper.showNext();
		currentPage ++;
		if (currentPage == viewFlipper.getChildCount()) {
			displayRatio_normal(currentPage - 1);
			currentPage = 0;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage - 1);
		}
		Log.e("currentPage", currentPage + "");

	}
	private void showPreviousView(){
		displayRatio_selelct(currentPage);
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.mall_push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.mall_push_right_out));
		viewFlipper.showPrevious();
		currentPage --;
		if (currentPage == -1) {
			displayRatio_normal(currentPage + 1);
			currentPage = viewFlipper.getChildCount() - 1;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage + 1);
		}
		Log.e("currentPage", currentPage + "");
	}
	private void displayRatio_selelct(int id){
		int[] ratioId = {R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)findViewById(ratioId[id]);
		img.setSelected(true);
	}
	private void displayRatio_normal(int id){
		int[] ratioId = {R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)findViewById(ratioId[id]);
		img.setSelected(false);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isRun = false;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		finish();
		super.onDestroy();
	}
}
class MyScrollView extends ScrollView {

	GestureDetector gestureDetector;
	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		super.onTouchEvent(ev);
		return gestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		gestureDetector.onTouchEvent(ev);
		super.dispatchTouchEvent(ev);
		return true;
	}

}