package com.cqut.data;

public class GameSetting {//????
	public final static int GAME_MODE_CLASSIC=0;//??????????????
	public final static int GAME_MODE_ENDLESS=1;//?????????????????????????
	public final static int GAME_MODE_TIME=2;
	public final static int GAME_MODE_MAP=3;//????????????????????????????

	public final static int GAME_STYLE_ICON_1=0;
	public final static int GAME_STYLE_ICON_2=1;
	public final static int GAME_STYLE_ICON_3=2;
	public final static int GAME_STYLE_ICON_RANDOM=3;

	public final static int GAME_STYLE_BACKGROUND_1=0;
	public final static int GAME_STYLE_BACKGROUND_2=1;
	public final static int GAME_STYLE_BACKGROUNDN_3=2;

	public final static int LINK_BIG=0;
	public final static int LINK_BORDER=1;

	public int mode;
	public boolean soundBack;//????,????
	public boolean soundEffect;//??
	public boolean iconAlign;//????
	public int styleIcon;//??????
	public int styleBackground;//??????
	public int linkEffect;//????

	public GameSetting(){

	}
}