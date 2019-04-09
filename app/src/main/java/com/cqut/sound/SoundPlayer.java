package com.cqut.sound;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.cqut.fruitllk.R;
import com.cqut.fruitllk.R.raw;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundPlayer {

	private static SoundPool soundPool;
	private static boolean soundSt = true; //音效开关
	private static Context context;

	public static final int ID_SOUND_CHOOSE = 0;
	public static final int ID_SOUND_WIN = 1;
	public static final int ID_SOUND_LOSE = 2;
	public static final int ID_SOUND_CLEAR = 3;
	public static final int ID_SOUND_REFRESH = 4;
	public static final int ID_SOUND_ERROR = 5;
	public static final int ID_SOUND_PAUSE = 6;
	public static final int ID_SOUND_TIME_COUNT=7;
	public static final int ID_SOUND_BOMB=8;
	public static final int ID_SOUND_DELAY=9;
	public static final int ID_SOUND_REMIND=10;
	private static Map<Integer,Integer> soundMap; //音效资源id与加载过后的音源id的映射关系表

	/**
	 * 初始化方法
	 * @param c
	 */
	public static void init(Context c)
	{
		context = c;
		initSound();
	}

	//初始化音效播放器
	private static void initSound()
	{
		soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,100);

		soundMap = new HashMap<Integer,Integer>();
		soundMap.put(ID_SOUND_CHOOSE, soundPool.load(context, R.raw.sel, 1));
		soundMap.put(ID_SOUND_WIN, soundPool.load(context,R.raw.win , 1));
		soundMap.put(ID_SOUND_LOSE, soundPool.load(context,R.raw.lose , 1));

		soundMap.put(ID_SOUND_CLEAR, soundPool.load(context, R.raw.clear, 1));
		soundMap.put(ID_SOUND_REFRESH, soundPool.load(context,R.raw.refresh, 1));

		soundMap.put(ID_SOUND_ERROR, soundPool.load(context,R.raw.error, 1));

		soundMap.put(ID_SOUND_REMIND, soundPool.load(context, R.raw.remind, 1));
		soundMap.put(ID_SOUND_BOMB, soundPool.load(context, R.raw.bomb, 1));

		soundMap.put(ID_SOUND_PAUSE, soundPool.load(context, R.raw.pause, 1));

		soundMap.put(ID_SOUND_TIME_COUNT, soundPool.load(context, R.raw.lesstime, 1));
		soundMap.put(ID_SOUND_DELAY, soundPool.load(context, R.raw.delay, 1));

	}

	/**
	 * 播放音效
	 * @param resId 音效资源id
	 */
	public static void playSound(int id)
	{
		if(soundSt == false)
			return;

		Integer soundId = soundMap.get(id);
		if(soundId != null)
			soundPool.play(soundId, 1, 1, 1, 0, 1);
	}

	/**
	 * 获得音效开关状态
	 * @return
	 */
	public static boolean isSoundSt() {
		return soundSt;
	}

	/**
	 * 设置音效开关
	 * @param soundSt
	 */
	public static void setSoundSt(boolean soundSt) {
		SoundPlayer.soundSt = soundSt;
	}

}