package com.cqut.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Point;

public class GameLogic {//游戏逻辑类Model
	public static final int WIN = 1;
	public static final int LOSE = 2;
	public static final int PAUSE = 3;
	public static final int PLAY = 4;
	public static final int QUIT = 5;
	public static final int ID_SOUND_CHOOSE = 0;
	public static final int ID_SOUND_DISAPEAR = 1;
	public static final int ID_SOUND_WIN = 4;
	public static final int ID_SOUND_LOSE = 5;
	public static final int ID_SOUND_REFRESH = 6;
	public static final int ID_SOUND_TIP = 7;
	public static final int ID_SOUND_ERROR = 8;
	public int yTotalCount;//y轴上的图标总量+2
	public int xTotalCount;//x轴上的图标总量+2
	public int xCount;//
	public int yCount;
	public int xStart;
	public int yStart;
	public int[][]map;
	public int iconCount;
	public List<Point> path = new ArrayList<Point>();//记录路径的点
	boolean isStop;
	public void setGameData(int xc,int yc,int iconCount){
		xTotalCount=xc;
		yTotalCount=yc;
		map=new int[xTotalCount][yTotalCount];
		this.iconCount=iconCount;//图的数量
	}
	private void clearArrMap(){
		for(int i=1;i<xTotalCount-1;i++)
			for(int j=1;j<yTotalCount-1;j++)
				map[i][j]=0;
	}
	public void push(int direction){//向一个方向压缩
		if(direction==0){//up
			for(int r=xStart+1;r<xCount;r++){//行
				int cur=yStart+1;
				for(int c=yStart+1;c<yCount;c++){//列
					if(map[r][c]!=0)
						cur++;
					if(cur-1!=c)
					{
						if(map[r][c]!=0){
							map[r][cur-1]=map[r][c];
							map[r][c]=0;
						}
					}
				}
			}
		}
		else if(direction==1){//left
			for(int c=yStart+1;c<yCount;c++)//列
			{
				int cur=xStart+1;
				for(int r=xStart+1;r<xCount;r++)//行
				{
					if(map[r][c]!=0)
						cur++;
					if(cur-1!=r)
					{
						if(map[r][c]!=0){
							map[cur-1][c]=map[r][c];
							map[r][c]=0;
						}
					}
				}
			}
		}
		else if(direction==2){//right
			for(int c=yStart+1;c<yCount;c++)//列
			{
				int cur=xCount;
				for(int r=xCount-1;r>=xStart;r--)//行
				{
					if(map[r][c]!=0)
						cur--;
					if(cur-1!=r)//游标与当前相同，不用交换
					{
						if(map[r][c]!=0){
							map[cur-1][c]=map[r][c];
							map[r][c]=0;
						}
					}
				}
			}
		}
		else{//down
			for(int r=xStart+1;r<xCount;r++)//列
			{
				int cur=yCount;
				for(int c=yCount-1;c>=yStart;c--)//行
				{
					if(map[r][c]!=0)
						cur--;
					if(cur-1!=c)//游标与当前相同，不用交换
					{
						if(map[r][c]!=0){
							map[r][cur-1]=map[r][c];
							map[r][c]=0;
						}
					}
				}
			}
		}
	}
	public int newGameMode3(int level){//游戏模式3，难度随着lv增加而改变
		clearArrMap();
		Point p=countLevelXY(level+10);//增加难度
		xStart=(xTotalCount-p.x)/2;//X起点
		yStart=(yTotalCount-p.y)/2;//Y起点
		xCount=xStart+p.x;//X终点
		yCount=yStart+p.y;//Y终点
		initMap();
		return (p.x-2)*(p.y-2);//返回有多少个的图标数量用于算时间
	}
	public int newGameMode4(int mapArr[][],int level){//通过地图关卡加载地图
		clearArrMap();
		boolean flag=false;
		int x=1;
		int count=0;
		xStart=0;
		yStart=0;
		xCount=xTotalCount;
		yCount=yTotalCount;
		for(int i=1;i<xTotalCount-1;i++)
			for(int j=1;j<yTotalCount-1;j++){
				if(mapArr[j-1][i-1]==1){//mapArr是10*8
					count++;
					map[i][j]=x;
					if(flag){
						map[i][j]=x;//map是10*12 除掉边框，相当于8*10 故X,Y 交换下
						x++;
						flag=false;
						if(x==iconCount)
							x=1;
					}
					else
						flag=true;
				}
			}
		change(GameSetting.GAME_MODE_MAP);
		return count;
	}
	private Point countLevelXY(int level){//计算指定关卡的XY的数量,游戏模式2中用到
		int xt=xTotalCount-2;
		int yt=yTotalCount-2;
		int x;
		int y;
		int lv=0;
		for(x=1;x<=xt;x++)
			for(y=1;y<=yt;y++){
				if(!(x%2==1&&y%2==1))//同为奇数不加
					lv++;
				if(lv==level)
					return new Point(x+2,y+2);
			}
		return new Point(xTotalCount,yTotalCount);
	}
	public int newGameMode2(){//新游戏并初始化地图2//返回有多少个的图标数量
		clearArrMap();
		Random r=new Random();
		int xc;
		int yc;
		while(true){
			xc=r.nextInt(xTotalCount-2)+3;//X轴上的数量，头尾一个空格.xc取3到xCount,实际图标数为1到xCount-2
			yc=r.nextInt(yTotalCount-5)+6;//Y轴上的数量，头尾一个空格 yc取6到yCount,实际图标数为4到yCount-3
			if(xc%2==1&&yc%2==1){//同为奇数时不能正常游戏
				continue;
			}
			if(xc+yc==7)//一行，且有两个图标，难度太小故舍弃
				continue;
			break;
		}
		xStart=(xTotalCount-xc)/2;//X起点
		yStart=(yTotalCount-yc)/2;//Y起点
		xCount=xStart+xc;//X终点
		yCount=yStart+yc;//Y终点
		initMap();
		return (xc-2)*(yc-2);//返回有多少个的图标数量用于算时间
	}
	public void initMap(){
		int x = 1;
		boolean y = false;
		for (int i =xStart+ 1; i < xCount - 1; i++) //产生成对的
		{
			for (int j = yStart+1; j < yCount - 1; j++)
			{
				map[i][j] = x;
				if (y)
				{
					x++;
					y = false;
					if (x == iconCount)
						x = 1;
				}
				else
					y = true;
			}
		}
		change(GameSetting.GAME_MODE_CLASSIC);//随机交换
	}
	public void newGameMode1() {//新游戏1并初始化地图
		xStart=0;
		yStart=0;
		xCount=xTotalCount;
		yCount=yTotalCount;
		initMap();
	}
	public void change(int gameMode)//随机交换
	{
		if(gameMode!=GameSetting.GAME_MODE_MAP){
			Random random = new Random();
			int tmpV, tmpX, tmpY;
			int xc=xCount-xStart;
			int yc=yCount-yStart;
			for (int x = xStart+1; x < xCount - 1; x++)
			{
				for (int y =yStart+1; y < yCount - 1; y++)
				{
					tmpX = xStart+1 + random.nextInt(xc - 2);
					tmpY = yStart+1 + random.nextInt(yc - 2);
					tmpV = map[x][y];
					map[x][y] = map[tmpX][tmpY];
					map[tmpX][tmpY] = tmpV;
				}
			}
		}
		else//地图模式的交换
		{
			List<Point>list=new ArrayList<Point>();//先记录要交换的点的坐标
			for(int i=1;i<xTotalCount-1;i++)
				for(int j=1;j<yTotalCount-1;j++){
					if(map[i][j]!=0)
						list.add(new Point(i,j));
				}

			int max=list.size();
			if(max==0)
				return;
			Random r=new Random();
			int temp;
			Point px;
			for(Point p :list){//随机交换
				px=list.get(r.nextInt(max));
				temp=map[p.x][p.y];
				map[p.x][p.y]=map[px.x][px.y];
				map[px.x][px.y]=temp;
			}
		}
		if (die())//死掉了
		{
			change(gameMode);//递归
		}
	}
	public void countPath(){

	}
	/*************判断有没有死掉，要记录能连通的路径**************/
	public boolean die() {

		for (int y = yStart+1; y < yCount - 1; y++) {
			for (int x = xStart+1; x < xCount - 1; x++){
				if (map[x][y] != 0) {
					for (int j = y; j < yCount - 1; j++){
						if (j == y){//两个点在同一行
							for (int i = x + 1; i < xCount - 1; i++) {
								if (map[i][j] == map[x][y]&& link(new Point(x, y),new Point(i, j)))
									return false;
							}
						}
						else//一个点在同一行，另个点在别的行
						{
							for (int i = xStart+1; i < xCount - 1; i++)
							{
								if (map[i][j] == map[x][y]&& link(new Point(x, y),new Point(i, j)))
								{
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	public List<Point> clearSameIcon(Point x){//清掉相同图标的图案
		int var=map[x.x][x.y];
		List<Point>list=new ArrayList<Point>();
		for(int i=xStart+1;i<xTotalCount-1;i++)
			for(int j=yStart+1;j<yTotalCount-1;j++){
				if(map[i][j]==var){
					list.add(new Point(i,j));
					map[i][j]=0;
				}
			}
		return list;
	}
	public void clearConnectIcon(Point p[]){//清掉一对图案
		if(p!=null&&p.length>=2){
			map[p[0].x][p[0].y] = 0;
			Point p2 =p[p.length-1] ;
			map[p2.x][p2.y] = 0;
		}
	}
	/*********以下是核心算法***************/
	public boolean link(Point p1, Point p2) {//判断两个点之间是否能相连
		if (p1.equals(p2))//点相同
			return false;
		if(map[p1.x][p1.y]!=map[p2.x][p2.y])//不同返回false
			return false;

		List<Point> pathTemp= new ArrayList<Point>(path);//先存起来
		path.clear();
		if (map[p1.x][p1.y] == map[p2.x][p2.y]) {//值同，则图案相同
			if (linkD(p1, p2)) {//两个点在同一直线上
				path.add(p1);
				path.add(p2);
				return true;
			}
			//转一个角
			Point p = new Point(p1.x, p2.y);
			if (map[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			p = new Point(p2.x, p1.y);
			if (map[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			List<Point> p1E = new ArrayList<Point>();
			List<Point> p2E = new ArrayList<Point>();
			expandX(p1, p1E);//向X方向扩展
			expandX(p2, p2E);//向X方向扩展

			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.x == pt2.x) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}
			expandY(p1, p1E);//向Y方向扩展
			expandY(p2, p2E);
			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.y == pt2.y) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}
		}
		path=pathTemp;
		return false;
	}
	private boolean linkD(Point p1, Point p2) {//判断两个点是否在同一X轴或Y轴，且之间是否全为0
		if (p1.x == p2.x) {
			int y1 = Math.min(p1.y, p2.y);
			int y2 = Math.max(p1.y, p2.y);
			boolean flag = true;
			for (int y = y1 + 1; y < y2; y++) {
				if (map[p1.x][y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag)
				return true;
		}
		if (p1.y == p2.y) {
			int x1 = Math.min(p1.x, p2.x);
			int x2 = Math.max(p1.x, p2.x);
			boolean flag = true;
			for (int x = x1 + 1; x < x2; x++) {
				if (map[x][p1.y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag)
				return true;
		}
		return false;
	}
	private void expandX(Point p, List<Point> l) {//向X方向扩展直到遇到不为0时停止
		l.clear();
		for (int x = p.x + 1; x < xCount; x++) {
			if (map[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
		for (int x = p.x - 1; x >= 0; x--) {
			if (map[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
	}

	private void expandY(Point p, List<Point> l) {//向Y方向扩展直到遇到不为0时停止
		l.clear();
		for (int y = p.y + 1; y < yCount; y++) {
			if (map[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
		for (int y = p.y - 1; y >= 0; y--) {
			if (map[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
	}

	public boolean win() {//胜利
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}
}
