package com.cqut.fruitllk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cqut.data.GameSetting;
import com.cqut.other.ImageManage;
import com.cqut.other.ShareHelper;

import java.util.ArrayList;

/**
 * Android实现左右滑动指引效果
 *
 * @Description: Android实现左右滑动指引效果
 */
@SuppressLint("NewApi")
public class StageSelectActivity extends Activity implements OnClickListener {//关卡选择
    private ViewPager viewPager;
    private ArrayList<View> pageViews;
    private ImageView imageView;
    private ImageView[] imageViews;
    private ImageView imageViewTitle;
    private ImageButton[] imageButtons;
    // 包裹滑动图片LinearLayout
    private ViewGroup main;
    // 包裹小圆点的LinearLayout
    private ViewGroup group;

    private int gameMode;
    private String stageString;
    private int maxStage = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置无标题窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.item01, null));
        pageViews.add(inflater.inflate(R.layout.item02, null));
        pageViews.add(inflater.inflate(R.layout.item03, null));
        imageViews = new ImageView[pageViews.size()];
        main = (ViewGroup) inflater.inflate(R.layout.main, null);

        group = (ViewGroup) main.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) main.findViewById(R.id.guidePages);

        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(StageSelectActivity.this);
            imageView.setLayoutParams(new LayoutParams(20, 20));
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i] = imageView;

            if (i == 0) {
                //默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.home_img_ratio_selected);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.home_img_ratio);
            }
            group.addView(imageViews[i]);

            View group = pageViews.get(0);
            group.findViewById(R.id.imageButton1);
        }
        setContentView(main);
        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
        initIntent();
        initIdEvent();
    }

    @Override
    protected void onStart() {
        initStage();
        super.onStart();
    }

    private void initIntent() {
        Intent i = getIntent();
        gameMode = i.getIntExtra("mode", GameSetting.GAME_MODE_TIME);
    }

    private void initIdEvent() {
        int num = 27;//27个
        int count = 1;//编号1-27
        Resources res = getResources();//获取资源
        imageButtons = new ImageButton[num];//imageButton
        String pack = getPackageName();//获取包名
        for (int i = 0; i < pageViews.size(); i++) {//3个分页
            for (int j = 0; j < 9; j++) {//每页9个ImageView
                int key = res.getIdentifier("imageButton" + count, "id", pack);
                imageButtons[count - 1] = (ImageButton) (pageViews.get(i).findViewById(key));
                imageButtons[count - 1].setOnClickListener(this);
                imageButtons[count - 1].setTag(count);//表示的关卡信息
                count++;
            }
        }
        imageViewTitle = (ImageView) findViewById(R.id.image_stage_title);
    }

    @Override
    public void onClick(View v) {
        int clickStage = (Integer) v.getTag();
        if (clickStage > maxStage) {
            Toast.makeText(this, "您还没有解锁关卡" + (clickStage - 1) + "无法挑战此关", Toast.LENGTH_SHORT).show();
        } else {
            Intent in = new Intent(this, GameActivity.class);
            in.putExtra("mode", gameMode);
            in.putExtra("level", clickStage);
            in.putExtra("stagestring", stageString);
            startActivity(in);
        }
    }

    private String getGameData() {//获取关卡进度
        ShareHelper sh = new ShareHelper(this);
        String key = ShareHelper.STAGE_DATA1;
        if (gameMode == GameSetting.GAME_MODE_MAP)
            key = ShareHelper.STAGE_DATA2;
        stageString = sh.getString(key, "");
        if (stageString == "") {
            stageString = "000000000000000000000000000";//长度27
            sh.putString(key, stageString);
            sh.commit();
        }
        return stageString;
    }

    private void initStage() {
        ImageManage ime = new ImageManage(this);//图片资源处理类
        Bitmap[] nums = ime.getBitmapNum();//获取0-9 10张数字
        Bitmap[] stars = ime.getBitmapStar();//获取4张星图
        int numWid = nums[0].getWidth();
        int starWid = stars[0].getWidth();

        Resources res = getResources();
        String str = getGameData();
        maxStage = 28;

        int len = str.length();
        for (int level = 1; level <= len; level++) {
            int index = str.charAt(level - 1) - '0';//得到0为0个星，1为1个星，2为2个星，3为3个星
            Bitmap bitmap = ime.convertDrawable2BitmapByCanvas(R.drawable.pass_bg);//背景
            Canvas canvas = new Canvas(bitmap);//获取bitMap的画布

            int bitWidth = bitmap.getWidth();
            canvas.drawBitmap(stars[index], (bitWidth - starWid) / 2, bitmap.getHeight() / 2 + 5, null);
            if (level < 10) {//画一个数字
                canvas.drawBitmap(nums[level], (bitWidth - numWid) / 2, 10, null);
            } else {//两个数字
                int shiwei = level / 10;
                int gewei = level % 10;
                int left = (bitWidth - numWid * 2) / 2;
                canvas.drawBitmap(nums[shiwei], left, 10, null);
                canvas.drawBitmap(nums[gewei], left + numWid, 10, null);
            }
            imageButtons[level - 1].setBackgroundDrawable(ime.convertBitmap2Drawable(bitmap));
            if (str.charAt(level - 1) == '0') {
                maxStage = level;
                break;
            }
        }

        //绘制关卡进度标题
        int numHei = nums[0].getHeight();
        Bitmap bt = Bitmap.createBitmap(numWid * 5, numHei, Config.ARGB_8888);
        Canvas canvas = new Canvas(bt);//获取bitMap的画布
        int gewei = (maxStage - 1) % 10;
        int shiwei = (maxStage - 1) / 10;
        canvas.drawBitmap(nums[shiwei], 0, 0, null);//十位
        canvas.drawBitmap(nums[gewei], numWid, 0, null);//个位
        Paint pt = new Paint();
        pt.setStyle(Paint.Style.STROKE);
        pt.setColor(Color.RED);
        pt.setAntiAlias(true);//抗锯齿
        pt.setStrokeWidth(10);
        canvas.drawLine(numWid * 2, numHei, numWid * 3, 0, pt);
        canvas.drawBitmap(nums[2], numWid * 3, 0, null);
        canvas.drawBitmap(nums[7], numWid * 4, 0, null);
        imageViewTitle.setImageBitmap(bt);

        for (int m = 0; m < nums.length; m++)
            nums[m].recycle();
        for (int n = 0; n < stars.length; n++)
            stars[n].recycle();
    }

    // 指引页面数据适配器
    class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }
    }

    // 指引页面更改事件监听器
    class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0].setBackgroundResource(R.drawable.home_img_ratio_selected);

                if (arg0 != i) {
                    imageViews[i].setBackgroundResource(R.drawable.home_img_ratio);
                }
            }
        }
    }


}