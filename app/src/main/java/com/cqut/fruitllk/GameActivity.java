package com.cqut.fruitllk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.cqut.data.GameData;
import com.cqut.data.GameLogic;
import com.cqut.data.GameSetting;
import com.cqut.data.GameVIInfo;
import com.cqut.dialog.InfoDialog;
import com.cqut.dialog.PauseDialog;
import com.cqut.dialog.ResultDialog;
import com.cqut.other.ImageManage;
import com.cqut.other.LoadStageMap;
import com.cqut.other.ShareHelper;
import com.cqut.sound.BackMediaPlayer;
import com.cqut.sound.SoundPlayer;

public class GameActivity extends Activity implements OnClickListener{//控制Controler

	public static final int LINK = 0;
	public static final int WIN = 1;
	public static final int LOSE = 2;
	public static final int PAUSE = 3;
	public static final int SET_TIME = 4;
	public static final int NEW_GAME = 5;
	public static final int START_EFFECT=6;

	public static final int MODE_CLASSIC=0;
	public static final int MODE_LEVEL_LEVEL=1;
	public static final int MODE_ENDLESS=2;
	public static final int MODE_LEVEL_MAP=3;

	GameView  gameView;
	View btnPause,btnRefresh,btnRemind,btnBomb,btnAddTime;
	ImageView imageLoading;
	TextView txtLevel,txtScore,txtDiamond,txtRefresh,txtRemind,txtBomb,txtAddTime;
	SeekBar seekBar;
	AnimationDrawable loadingEff;
	public Bitmap[] icons=null;
	private boolean isReadyBomb=false;//准备使用炸弹
	boolean isPause;
	protected List<Point> selectIcon = new ArrayList<Point>();//选中的点
	private Timer timer=null;
	private boolean isInGame;

	private boolean canPressRemind=true;//能否使用提示道具,1秒只能用一次

	GameLogic gameLogic;//游戏逻辑
	GameSetting gameSet;//游戏设置
	String stageString;//关卡地图
	GameVIInfo gameVI;//游戏重要信息类，需要及时存档
	ShareHelper share;//存储帮助类，（自定义）
	GameData data;
	BackMediaPlayer mediaPlayer;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==NEW_GAME){
				loadingEff.stop();
				imageLoading.setVisibility(View.GONE);
				newGame2();
			}
			else if(msg.what==START_EFFECT){
				imageLoading.setVisibility(View.VISIBLE);
				loadingEff.start();
			}


			if(!isInGame)
				return;
			switch(msg.what){
				case LINK:
					if(gameSet.iconAlign&&(gameSet.mode!=GameSetting.GAME_MODE_MAP))//只有才非地图模式下才允许排列
						gameLogic.push(data.curLevel%4);//压了后再找
					if (gameLogic.die())
						gameLogic.change(gameSet.mode);
					gameView.invalidate();
					break;
				case SET_TIME://设置计间
					GameActivity.this.setTimeText();
					if(data.leftTime<=0)
					{
						timer.cancel();
						setGameState(LOSE);
						break;
					}
					else if(data.leftTime<6)
						playSound(SoundPlayer.ID_SOUND_TIME_COUNT);
			}
		}
	};
	//入口函数
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		initModeAndUserData();//初始化游戏模式及配置文件
		initIdEvent();//初始化ID及事件
		setIconSize();//设置图标的大小
		initImageResourse();//加载游戏图片资源
		initGame();
	}
	public void initModeAndUserData(){//初始化游戏模式及配置文件

		share=new ShareHelper(this);
		gameLogic=new GameLogic();
		data=new GameData();
		gameSet=share.getGameSet();//读取游戏配置文件声音设置，图标风格等

		Intent i=getIntent();
		gameSet.mode=i.getIntExtra("mode", GameSetting.GAME_MODE_CLASSIC);
		if(gameSet.mode==GameSetting.GAME_MODE_MAP||gameSet.mode==GameSetting.GAME_MODE_TIME){
			data.curLevel=i.getIntExtra("level", 1);
			stageString=i.getStringExtra("stagestring");
		}
		mediaPlayer=new BackMediaPlayer(this,gameSet.soundBack);
		SoundPlayer.setSoundSt(gameSet.soundEffect);

		gameVI=share.getGameVIInfo();//获取游戏中重要的五个数据
	}
	public void iniNumText(){//设置分数等信息
		setLevelText();//级别
		setRefreshText();//刷新
		setRemindText();//提示
		setScoreText();//分数
		setDiamondText();//钻石
		setMaxTime();//设置最大时间条
		setDelayText();//添加时间道具
		setBombText();//炸弹道具
	}
	void initGame(){//从文件中读取游戏数据
		//设置游戏中要用到的数据
		gameLogic.setGameData(data.xCount,data.yCount,icons.length);
		data.map=gameLogic.map;
		gameView.initReferences(data,gameLogic.map);//初始化View对地图，及游戏数据的引用
		//文件存在读文件，否则
		newGame();

	}
	private void setIconSize(){//计算图标的长宽
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager()
				.getDefaultDisplay().getMetrics(dm);
		if(gameSet.mode==GameSetting.GAME_MODE_MAP){//加载地图需要很大的空间可以布密些
			data.xCount=10;
			data.yCount=12;
		}
		int width=dm.widthPixels;//游戏界面宽度
		int height=dm.heightPixels-100;//游戏界面高度

		float rateWH=width/height;//游戏面板的宽高比
		float rateXY=data.xCount/data.yCount;//图标数量宽高比
		if(rateWH<=rateXY){//屏幕高度足够
			data.iconSize=width/data.xCount;
		}
		else//屏幕宽度足够
		{
			data.iconSize=height/data.yCount;
		}
	}
	private void initImageResourse() {//加载资源
		icons=new ImageManage(this).getBitmapToSize(gameSet.styleIcon,data.iconSize);//图片资源
	}
	void initIdEvent(){
		gameView=(GameView)findViewById(R.id.game_view);
		btnPause=findViewById(R.id.btnPauseGame);
		btnRefresh=findViewById(R.id.btnRefreshGame);
		btnBomb=findViewById(R.id.btnBomb);
		btnAddTime=findViewById(R.id.btnAddTime);
		btnRemind=findViewById(R.id.btnRemindGame);
		seekBar=(SeekBar)findViewById(R.id.seekBar_Time);
		Resources r=getResources();
		seekBar.setIndeterminateDrawable(r.getDrawable(R.drawable.diamond));
		seekBar.setProgressDrawable(r.getDrawable(R.drawable.tiao));
		seekBar.setOnTouchListener(new OnTouchListener() {//不响应Touch事件
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		imageLoading=(ImageView)findViewById(R.id.imageLoading);
		loadingEff = (AnimationDrawable)imageLoading.getBackground();

		txtLevel=(TextView)findViewById(R.id.text_level);
		txtScore=(TextView)findViewById(R.id.text_score);
		txtDiamond=(TextView)findViewById(R.id.text_diamond);
		txtRefresh=(TextView)findViewById(R.id.textRefreshNum);
		txtRemind=(TextView)findViewById(R.id.textRemindNum);
		txtBomb=(TextView)findViewById(R.id.textBombNum);
		txtAddTime=(TextView)findViewById(R.id.textTimeNum);

		btnPause.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		btnBomb.setOnClickListener(this);
		btnAddTime.setOnClickListener(this);
		btnRemind.setOnClickListener(this);
		gameView.setOnTouchListener(new OnTouchListener() {//游戏面板设置触摸事件
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(!isInGame)
					return false;
				int x = (int) arg1.getX();
				int y = (int) arg1.getY();
				Point p = screenToIndex(x, y);
				if(p==null)//坐标出界
					return false;
				if (gameLogic.map[p.x][p.y] > 0)//选中图标的数组值大于0
				{
					if(isReadyBomb){//准备消除任意方格
						playSound(SoundPlayer.ID_SOUND_CLEAR);
						isReadyBomb=false;
						selectIcon.clear();
						gameLogic.clearSameIcon(p);
						if (gameLogic.win()) {//胜利
							countDiamond();//计算钻石
							timer.cancel();//计时器停止
							gameView.setPath(null);//设置路径为空不再画连线
							selectIcon.clear();//当前选中图标请掉
							gameView.invalidate();//重绘制
							setGameState(WIN);
							return false;
						}
						if(gameLogic.die()){
							gameLogic.change(gameSet.mode);
						}
						gameView.invalidate();
						return false;
					}
					if (selectIcon.size() == 1)//已经选中了一个
					{
						if (gameLogic.link(selectIcon.get(0), p)) //选中的图标能连
						{
							countScore();
							selectIcon.add(p);//添加进来以便下次消失
							playSound(SoundPlayer.ID_SOUND_CLEAR);
							drawLineAndWin();
						}
						else//选中图标不能连
						{
							selectIcon.clear();
							selectIcon.add(p);

							playSound(SoundPlayer.ID_SOUND_CHOOSE);
							gameView.invalidate();
						}
					}
					else//选中0个
					{
						selectIcon.add(p);
						playSound(SoundPlayer.ID_SOUND_CHOOSE);
						gameView.invalidate();
					}
				}
				return false;
			}
		});
	}

	/*----------------下面是重写事件-----------------*/
	@Override
	public void onBackPressed() {//点击了返回按键
		if(timer!=null)
			timer.cancel();
		setGameState(PAUSE);
	}
	@Override
	public void onClick(View v) {//点击按键
		switch(v.getId()){
			case R.id.btnPauseGame:setGameState(PAUSE);break;
			case R.id.btnRefreshGame:clickRefresh();break;
			case R.id.btnBomb:clickBomb();break;
			case R.id.btnAddTime:clickAddTime();break;
			case R.id.btnRemindGame:clickRemind();break;
		}
	}
	@Override
	protected void onDestroy() {
		mediaPlayer.relaseMusic();
		for(int i=0;i<icons.length;i++)
			icons[i].recycle();
		super.onDestroy();

	}
	@Override
	protected void onPause() {
		mediaPlayer.pauseMusic();
		setGameState(PAUSE);
		super.onPause();
	}
	@Override
	protected void onResume() {
		mediaPlayer.startMusic();
		super.onResume();

	}
	/*----------------下面是游戏功能：计分，计算钻石，新游戏等-----------------*/
	void countScore(){//计算分数
		Random rm=new Random();
		data.scoreCurLine=gameLogic.path.size()*100+rm.nextInt(30);
		data.scoreTotal+=data.scoreCurLine;
		data.scoreCurLevel+=data.scoreCurLine;
		setScoreText();
	}
	void countDiamond(){//计算钻石
		gameVI.diamondNum+=data.scoreCurLevel/1000;
		data.scoreCurLevel=0;
		this.setDiamondText();
	}
	void playSound(int soundId){
		SoundPlayer.playSound(soundId);
	}
	public void replayGame(){//重玩游戏，积分清零
		data.scoreTotal=0;
		if(gameSet.mode==GameSetting.GAME_MODE_CLASSIC||gameSet.mode==GameSetting.GAME_MODE_ENDLESS)
			data.curLevel=1;//非过关模式关卡置1
		newGame();
	}
	public void resumeGame(){//从暂停游戏中恢复游戏
//    	GameData g=share.readGameData();
//    	if(g!=null){
//    		data=g;
//    		gameLogic.map=g.map;
//    		gameLogic.
//    		gameView.invalidate();
//    	}
		isPause=false;
		mediaPlayer.startMusic();
		isInGame=true;
	}
	public void newGame(){//新游戏,这里是为启动动画
		mediaPlayer.changeAndPlayMusic();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(START_EFFECT);
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(NEW_GAME);
					}},800);
			}},200);
	}
	public void newGame2(){//新游戏
		if(timer!=null)
			timer.cancel();
		isReadyBomb=false;
		switch(gameSet.mode){
			case GameSetting.GAME_MODE_CLASSIC:
				gameLogic.newGameMode1();
				data.leftTime = data.totalTime;
				break;
			case GameSetting.GAME_MODE_ENDLESS:
				data.leftTime=gameLogic.newGameMode2();//一对图标给2秒时间
				break;
			case GameSetting.GAME_MODE_TIME:
				data.leftTime=gameLogic.newGameMode3(data.curLevel);
				data.totalTime=data.leftTime;
				break;
			case GameSetting.GAME_MODE_MAP:
				int arr[][]=new LoadStageMap().getMap(this, data.curLevel);
				if(arr==null){
					InfoDialog in=new InfoDialog(this,"更多关卡尽请期待。。。");
					in.show();
					this.finish();
					return;
				}
				data.leftTime=gameLogic.newGameMode4(arr, data.curLevel);
				data.totalTime=data.leftTime;
				break;
		}
		iniNumText();
		isPause=false;
		isInGame=true;
		timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!isPause)
					data.leftTime--;
				handler.sendEmptyMessage(SET_TIME);
			}
		}, 1000, 1000);//计时线程每秒种计一次时
		gameView.invalidate();
		effectTransIn(gameView);
	}
	public void nextGame(){//下一次游戏
		if(data.totalTime-5>50)//当为最少时间就不减了
			data.totalTime-=5;
		data.curLevel++;
		newGame();
	}
	public void saveData(){
//		share.saveGameData(data);
	}
	/*----------------下面是游戏效果-----------------*/
	public void effectShake(View v){//左右摇晃效果
		Animation s = AnimationUtils.loadAnimation(this,R.anim.shake);
		v.startAnimation(s);
	}
	public void effectBig(View v){//着重变大效果
		Animation s = AnimationUtils.loadAnimation(this,R.anim.my_scale_action);
		v.startAnimation(s);
	}
	public void effectTransIn(View v){//慢慢进入
		Animation s = AnimationUtils.loadAnimation(this,R.anim.trans_in);
		v.startAnimation(s);
	}
	/*----------------下面是游戏道具-----------------*/
	public void clickRefresh(){//点击刷新

		if(!isInGame||gameVI.refreshNum == 0){
			playSound(SoundPlayer.ID_SOUND_ERROR);
			effectShake(btnRefresh);
			return;
		}else{
			effectBig(btnRefresh);
			playSound(SoundPlayer.ID_SOUND_REFRESH);
			gameVI.refreshNum--;
			setRefreshText();
			gameLogic.change(gameSet.mode);
			gameView.invalidate();
		}
	}
	public void clickBomb(){//炸弹道具
		if(!isInGame||isReadyBomb||gameVI.bombNum == 0){
			effectShake(btnBomb);
			playSound(SoundPlayer.ID_SOUND_ERROR);
		}else{
			playSound(SoundPlayer.ID_SOUND_BOMB);
			gameVI.bombNum--;
			effectBig(btnBomb);
			isReadyBomb=true;//可以使用炸弹道具
			setBombText();
		}
	}
	public void clickAddTime(){//延时道具
		if (!isInGame||gameVI.delayNum == 0) {
			effectShake(btnAddTime);
			playSound(SoundPlayer.ID_SOUND_ERROR);
		}else{
			playSound(SoundPlayer.ID_SOUND_DELAY);
			effectBig(btnAddTime);
			gameVI.delayNum--;
			data.leftTime+=10;//使用1个加10秒
			setMaxTime();
			setDelayText();
		}
	}
	public void clickRemind(){//提示道具
		if(!isInGame)
			return;
		if (gameVI.remindNum == 0) {
			effectShake(btnRemind);
			playSound(SoundPlayer.ID_SOUND_ERROR);
		}else{
			if(!canPressRemind)//不能点击
				return;
			canPressRemind=false;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					canPressRemind=true;
				}},1000);
			effectBig(btnRemind);
			countScore();
			playSound(SoundPlayer.ID_SOUND_REMIND);
			gameVI.remindNum--;
			setRemindText();
			drawLineAndWin();
		}
	}
	/*----------------下面是游戏连线并判定是否胜利-----------------*/
	private void drawLineAndWin() {//画连线并判定是否胜利
		if(!isInGame)
			return;
		Point[] p=gameLogic.path.toArray(new Point[] {});
		gameLogic.clearConnectIcon(p);//清除要绘制的两个图标
		if (gameLogic.win()) {//胜利
			countDiamond();//计算钻石
			timer.cancel();//计时器停止
			gameView.setPath(null);//设置路径为空不再画连线
			selectIcon.clear();//当前选中图标请掉
			gameView.invalidate();//重绘制
			setGameState(WIN);
			return;
		}
		gameView.setPath(p);//设置要绘制的路径
		gameView.invalidate();//重绘制
		handler.sendEmptyMessageDelayed(LINK,600);//延时提交连线消息
	}
	/*----------------下面是通关数据处理-----------------*/
	public char countGrade(){//计算游戏的评级
		char grade='1';
		int rn=data.leftTime*10/data.totalTime;
		if(rn>5)
			grade='3';
		else if(rn>2)
			grade='2';
		return grade;
	}
	public void savePassStageData(char grade){//关卡评分模式下通关数据存储
		if(gameSet.mode!=GameSetting.GAME_MODE_MAP&&gameSet.mode!=GameSetting.GAME_MODE_TIME)
			return;
		if(stageString.charAt(data.curLevel-1)>=grade){//原先的得分更高
			return;
		}
		char[]arr=stageString.toCharArray();//stageString长度为27
		arr[data.curLevel-1]=grade;//curLevel 1-27
		stageString=new String(arr);

		if(gameSet.mode==GameSetting.GAME_MODE_MAP)
			share.saveStage2Data(stageString);
		else
			share.saveStage1Data(stageString);
	}
	/*----------------下面是设置游戏状态，通关，暂停，失败等-----------------*/
	public void setGameState(int state){
		if(!isInGame)//不在游戏中
			return;
		isInGame=false;
		switch(state){
			case PAUSE:
				isPause=true;
				mediaPlayer.pauseMusic();
				playSound(SoundPlayer.ID_SOUND_PAUSE);
				PauseDialog p=new PauseDialog(this);
				p.show();
				break;
			case WIN:
				mediaPlayer.pauseMusic();
				playSound(SoundPlayer.ID_SOUND_WIN);
				char grade=countGrade();
				if(data.curLevel<28)//大于27的关卡暂时没有设计
					savePassStageData(grade);
				ResultDialog r=new ResultDialog(this,true,grade);
				r.show();
				break;
			case LOSE:
				mediaPlayer.pauseMusic();
				playSound(SoundPlayer.ID_SOUND_LOSE);
				ResultDialog r2=new ResultDialog(this,false,'0');
				r2.show();
				break;
		}
	}
	/*----------------下面是游戏相关数据在面板上显示-----------------*/
	public void setLevelText(){//设置关卡
		txtLevel.setText(String.valueOf(data.curLevel));
	}
	public void setMaxTime(){
		seekBar.setMax(data.leftTime);
		seekBar.setProgress(data.leftTime);
	}
	public void setBombText(){//设置炸弹道具数量
		txtBomb.setText(String.valueOf(gameVI.bombNum));
		share.saveBombNum(gameVI);
	}
	public void setDelayText(){//设置添加时间道具数量
		txtAddTime.setText(String.valueOf(gameVI.delayNum));
		share.saveDelayNum(gameVI);
	}
	public void setTimeText(){//设置时间
		seekBar.setProgress(data.leftTime);
	}
	public void setScoreText(){//分数
		txtScore.setText(String.valueOf(data.scoreTotal));
	}
	public void setDiamondText(){//钻石数
		txtDiamond.setText(String.valueOf(gameVI.diamondNum));
		share.saveDiamondNum(gameVI);
	}
	public void setRefreshText(){//刷新次数
		txtRefresh.setText(String.valueOf(gameVI.refreshNum));
		share.saveRefreshNum(gameVI);
	}
	public void setRemindText(){//提示次数
		txtRemind.setText(String.valueOf(gameVI.remindNum));
		share.saveRemindNum(gameVI);
	}
	/*----------------下面是工具方法-----------------*/
	public Point indexToScreen(int x,int y){//将图标在数组中的坐标转成在屏幕上的真实坐标
		return new Point(x* data.iconSize , y * data.iconSize );
	}
	public Point screenToIndex(int x,int y){ //将图标在屏幕中的坐标转成在数组上的虚拟坐标
		int ix = x/ data.iconSize;
		int iy = y / data.iconSize;
		if(ix < gameLogic.xCount && iy <gameLogic.yCount)
			return new Point( ix,iy);
		return null;
	}
	public void makeToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
}
