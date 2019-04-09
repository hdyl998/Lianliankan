package com.cqut.data;

import java.io.Serializable;

public class GameData implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public int curLevel=1;//关卡
	public int scoreTotal=0;//总得分
	public int scoreCurLine=0;//当前连线得分
	public int scoreCurLevel=0;//当前关卡得分，用于算得到多少个钻石
	public int totalTime=100;//总时间
	public int leftTime;//剩余时间
	public int map[][]=null;
	public int iconSize;//图标大小
	public int xCount=8;
	public int yCount=11;
	public GameData(){

	}
}
