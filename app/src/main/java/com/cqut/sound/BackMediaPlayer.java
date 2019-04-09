
package com.cqut.sound;

import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;

import com.cqut.fruitllk.R;

public class BackMediaPlayer {

	private MediaPlayer music;

	private boolean musicSt = true; //音乐开关
	private Context context;

	private final int[] musicId = {R.raw.bg1,R.raw.bg2,R.raw.bg3};

	public BackMediaPlayer(Context c,boolean sw){
		context = c;
		this.musicSt=sw;
		initMusic();
	}
	//下面是音乐控制

	//初始化音乐播放器
	private void initMusic()
	{
		int r = new Random().nextInt(musicId.length);
		music = MediaPlayer.create(context,musicId[r]);
		music.setLooping(true);
	}
	/**
	 * 暂停音乐
	 */
	public void pauseMusic()
	{
		if(music.isPlaying())
			music.pause();
	}
	/**
	 * 播放音乐
	 */
	public  void startMusic()
	{
		if(music==null)
			return;
		if(musicSt)
			music.start();
	}

	/**
	 * 切换一首音乐并播放
	 */
	public void changeAndPlayMusic()
	{
		if(music != null)
			music.release();
		initMusic();
		startMusic();
	}

	/**
	 * 获得音乐开关状态
	 * @return
	 */
	public boolean isMusicSt() {
		return musicSt;
	}

	/**
	 * 设置音乐开关
	 * @param musicSt
	 */
	public void setMusicSt(boolean musicSt) {
		this.musicSt = musicSt;
		if(musicSt)
			music.start();
		else
			music.stop();
	}
	public void relaseMusic(){
		music.release();
	}
}