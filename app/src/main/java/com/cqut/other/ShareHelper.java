package com.cqut.other;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.cqut.data.GameData;
import com.cqut.data.GameSetting;
import com.cqut.data.GameVIInfo;

public class ShareHelper {//文件存储帮助类
	public final static String SETTING_NAME="setting";//设置文件名字


	public final static String STAGE_DATA1="stagedata1";//模式2的进度
	public final static String STAGE_DATA2="stagedata2";//模式3的进度

	public final static String SOUND_BACK="sound_back";//背景音乐
	public final static String SOUND_EFFECT="sound_effect";//背景音乐
	public final static String STYLE_CHOOSE="choice_style";//选中风格
	public final static String STYLE_ICON="icon_style";//选中风格
	public final static String SET_ALING="icon_align";//图标对齐


	public final static String TOOL_DELAY_KEY="tool_time_num";//加时道具数
	public final static String TOOL_BOMB_KEY="tool_bomb_num";//炸弹道具数
	public final static String TOOL_REMIND_KEY="tool_remind_num";//提示道具数
	public final static String TOOL_REFRESH_KEY="tool_refresh_num";//刷新道具数
	public final static String DIAMOND_KEY="tool_diamond_num";//钻石数

	public final static String GAME_DATA_KEY1="game_data1";
	public final static String GAME_DATA_KEY2="game_data2";
	public final static String GAME_DATA_KEY3="game_data3";
	public final static String GAME_DATA_KEY4="game_data4";
	SharedPreferences share;
	SharedPreferences.Editor editor;
	public ShareHelper(Context context){
		share=context.getSharedPreferences(SETTING_NAME,Activity.MODE_PRIVATE);
		editor=share.edit();
	}
	public String getString(String key,String defaultVarlue){
		return share.getString(key,defaultVarlue);
	}
	public int getInt(String key,int defaultVarlue){
		return share.getInt(key, defaultVarlue);
	}
	public boolean getBoolean(String key,boolean defaultVarlue){
		return share.getBoolean(key, defaultVarlue);
	}

	public void putBoolean(String key,boolean setVarlue){
		editor.putBoolean(key,setVarlue );
	}
	public void putInt(String key,int setVarlue){
		editor.putInt(key,setVarlue );
	}
	public void putString(String key,String setVarlue){
		editor.putString(key,setVarlue );
	}
	public void commit(){
		editor.commit();
	}
	public GameVIInfo getGameVIInfo(){//查询用户重要数据
		GameVIInfo gv=new GameVIInfo();
		gv.diamondNum=getInt(ShareHelper.DIAMOND_KEY, 1000);
		gv.refreshNum=getInt(ShareHelper.TOOL_REFRESH_KEY, 100);
		gv.remindNum=getInt(ShareHelper.TOOL_REMIND_KEY, 100);
		gv.delayNum=getInt(ShareHelper.TOOL_DELAY_KEY, 100);
		gv.bombNum=getInt(ShareHelper.TOOL_BOMB_KEY, 100);
		return gv;
	}
	public boolean getMusicSwitcher(){
		return getBoolean(ShareHelper.SOUND_BACK, true);//背景音乐
	}
	public GameSetting getGameSet(){
		GameSetting g=new GameSetting();
		g.soundBack=getBoolean(ShareHelper.SOUND_BACK, true);//背景音乐
		g.soundEffect=getBoolean(ShareHelper.SOUND_EFFECT, true);//音效
		g.linkEffect=Integer.parseInt(share.getString(ShareHelper.STYLE_CHOOSE, "0"));
		g.styleIcon=Integer.parseInt(share.getString(ShareHelper.STYLE_ICON, "0"));
		g.iconAlign=getBoolean(ShareHelper.SET_ALING, false);//图标对齐
		return g;
	}
	public void saveDiamondNum(GameVIInfo g){
		editor.putInt(DIAMOND_KEY,g.diamondNum);
		editor.commit();
	}
	public void saveBombNum(GameVIInfo g){
		editor.putInt(TOOL_BOMB_KEY,g.bombNum);
		editor.commit();
	}
	public void saveRefreshNum(GameVIInfo g){
		editor.putInt(TOOL_REFRESH_KEY,g.refreshNum);
		editor.commit();
	}
	public void saveRemindNum(GameVIInfo g){
		editor.putInt(TOOL_REMIND_KEY,g.remindNum);
		editor.commit();
	}
	public void saveDelayNum(GameVIInfo g){
		editor.putInt(TOOL_DELAY_KEY,g.delayNum);
		editor.commit();
	}
	public void saveStage1Data(String var){
		editor.putString(STAGE_DATA1,var);
		editor.commit();
	}
	public void saveStage2Data(String var){
		editor.putString(STAGE_DATA2,var);
		editor.commit();
	}

	// 存储游戏数据
	public boolean saveGameData(GameData gamedata,int mode){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(gamedata);
			oos.close();
			baos.close();
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
		String strBase64=new String(Base64.encode(baos.toByteArray(), 0));
		String str=GAME_DATA_KEY1;
		switch(mode){
			case GameSetting.GAME_MODE_TIME:str=GAME_DATA_KEY2;break;
			case GameSetting.GAME_MODE_MAP:str=GAME_DATA_KEY3;break;
			case GameSetting.GAME_MODE_ENDLESS:str=GAME_DATA_KEY4;break;
		}
		putString(str,strBase64);
		commit();
		return true;
	}
	// 读取游戏数据
	public GameData readGameData(int mode){
		String str=GAME_DATA_KEY1;
		switch(mode){
			case GameSetting.GAME_MODE_TIME:str=GAME_DATA_KEY2;break;
			case GameSetting.GAME_MODE_MAP:str=GAME_DATA_KEY3;break;
			case GameSetting.GAME_MODE_ENDLESS:str=GAME_DATA_KEY4;break;
		}
		String strBase64=getString(str, "");
		if(strBase64.equals(""))
			return null;
		byte[]base64Bytes=Base64.encode(strBase64.getBytes(), 0);
		ByteArrayInputStream bais=new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois;
		try {
			ois=new ObjectInputStream(bais);//这里抛异常
			GameData g=(GameData) ois.readObject();
			ois.close();
			bais.close();
			return g;
		} catch (Exception e) {
			return null;
		}
	}
}
