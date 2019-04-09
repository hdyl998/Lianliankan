package com.cqut.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class LoadStageMap {//加载关卡地图主要是对文件操作
	public int [][]getMap(Context context,int level){
		int row=10,col=8;
		int[][]map=new int[row][col];
		Resources res=context.getResources();
		try {
			InputStream is=res.getAssets().open("map"+level+".txt");//字节输入流
			InputStreamReader isr = new InputStreamReader(is);//字符输入流
			BufferedReader br = new BufferedReader(isr);//缓冲区输入流
			String str=br.readLine();
			int count=0;
			while(str!=null&&count<row){
				for(int i=0;i<str.length()&&i<col;i++)
					map[count][i]=str.charAt(i)-'0';
				count++;
				str=br.readLine();
			}
			br.close();
			isr.close();
			is.close();
		} catch (IOException e) {
			return null;
		}
		return map;
	}
}
