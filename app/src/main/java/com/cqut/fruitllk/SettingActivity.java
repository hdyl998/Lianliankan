package com.cqut.fruitllk;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;


import com.cqut.data.GameSetting;
import com.cqut.other.ShareHelper;

public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(ShareHelper.SETTING_NAME);
		addPreferencesFromResource(R.xml.preferences_setting);


		//获取值
		ShareHelper sh=new ShareHelper(this);
		GameSetting gs=sh.getGameSet();//获取游戏设置

		//找到组件
		CheckBoxPreference c1= (CheckBoxPreference)findPreference(ShareHelper.SOUND_BACK);
		CheckBoxPreference c2= (CheckBoxPreference)findPreference(ShareHelper.SOUND_EFFECT);
		CheckBoxPreference c3= (CheckBoxPreference)findPreference(ShareHelper.SET_ALING);

		ListPreference l1= (ListPreference)findPreference(ShareHelper.STYLE_CHOOSE);
		ListPreference l2= (ListPreference)findPreference(ShareHelper.STYLE_ICON);
		//设置值
		c1.setChecked(gs.soundBack);
		c2.setChecked(gs.soundEffect);
		c3.setChecked(gs.iconAlign);
		l1.setValueIndex(gs.linkEffect);
		l2.setValueIndex(gs.styleIcon);

		//添加监听事件
		c1.setOnPreferenceChangeListener(this);
		c2.setOnPreferenceChangeListener(this);
		c3.setOnPreferenceChangeListener(this);
		l1.setOnPreferenceChangeListener(this);
		l2.setOnPreferenceChangeListener(this);

		//设置背景
		getListView().setBackgroundResource(R.drawable.baaa);
		getListView().setCacheColorHint(Color.TRANSPARENT);
		getListView().setPadding(5, 5, 5, 5);
	}
	@Override
	public boolean onPreferenceChange(Preference pre, Object ob) {
		pre.setSummary(String.valueOf(ob));//保存键值
		return true;//返回true否则无法保存设置
	}
}
