package com.zhangqing.whutwifi;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;

public class WhutwifiActivity extends Activity implements OnClickListener {

    private Mobile1Fragment mainFragment;
    private LinearLayout fragmentLayout;

    private View mainLayout;

    private View[] mobileLayouts = new View[10];
    private ImageView[] mobileImages = new ImageView[10];
    private TextView[] mobileTexts = new TextView[10];
    private Mobile2Fragment[] mobileFragments = new Mobile2Fragment[10];

    private ImageView mainImage;

    private TextView mainText;

    private TextView mainTitle;

    private ImageView btnHelp;

    GestureDetector detector;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        // 初始化布局元素
        initViews();
        fragmentManager = getFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(-1);
        UmengUpdateAgent.update(this);
    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {

        mobileLayouts[0] = findViewById(R.id.mobile0_main_layout);
        mobileLayouts[1] = findViewById(R.id.mobile1_main_layout);
        mobileLayouts[2] = findViewById(R.id.mobile2_main_layout);
        mobileLayouts[3] = findViewById(R.id.mobile3_main_layout);
        mobileLayouts[4] = findViewById(R.id.mobile4_main_layout);
        mobileLayouts[5] = findViewById(R.id.mobile5_main_layout);
        mobileLayouts[6] = findViewById(R.id.mobile6_main_layout);
        mobileLayouts[7] = findViewById(R.id.mobile7_main_layout);
        mobileLayouts[8] = findViewById(R.id.mobile8_main_layout);
        mobileLayouts[9] = findViewById(R.id.mobile9_main_layout);
        mobileImages[0] = (ImageView) findViewById(R.id.mobile0_main_image);
        mobileImages[1] = (ImageView) findViewById(R.id.mobile1_main_image);
        mobileImages[2] = (ImageView) findViewById(R.id.mobile2_main_image);
        mobileImages[3] = (ImageView) findViewById(R.id.mobile3_main_image);
        mobileImages[4] = (ImageView) findViewById(R.id.mobile4_main_image);
        mobileImages[5] = (ImageView) findViewById(R.id.mobile5_main_image);
        mobileImages[6] = (ImageView) findViewById(R.id.mobile6_main_image);
        mobileImages[7] = (ImageView) findViewById(R.id.mobile7_main_image);
        mobileImages[8] = (ImageView) findViewById(R.id.mobile8_main_image);
        mobileImages[9] = (ImageView) findViewById(R.id.mobile9_main_image);
        mobileTexts[0] = (TextView) findViewById(R.id.mobile0_main_text);
        mobileTexts[1] = (TextView) findViewById(R.id.mobile1_main_text);
        mobileTexts[2] = (TextView) findViewById(R.id.mobile2_main_text);
        mobileTexts[3] = (TextView) findViewById(R.id.mobile3_main_text);
        mobileTexts[4] = (TextView) findViewById(R.id.mobile4_main_text);
        mobileTexts[5] = (TextView) findViewById(R.id.mobile5_main_text);
        mobileTexts[6] = (TextView) findViewById(R.id.mobile6_main_text);
        mobileTexts[7] = (TextView) findViewById(R.id.mobile7_main_text);
        mobileTexts[8] = (TextView) findViewById(R.id.mobile8_main_text);
        mobileTexts[9] = (TextView) findViewById(R.id.mobile9_main_text);
        fragmentLayout = (LinearLayout) findViewById(R.id.layoutfragment);

        mainTitle = (TextView) findViewById(R.id.main_main_title);
        for (int i = 0; i < mobileLayouts.length; i++) {
            mobileLayouts[i].setOnClickListener(this);
        }

        btnHelp = (ImageView) findViewById(R.id.btn_help_guide);
        btnHelp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(WhutwifiActivity.this, Guide.class);
                startActivity(intent);
            }
        });

        mainLayout = findViewById(R.id.main_main_layout);
        mainImage = (ImageView) findViewById(R.id.main_main_image);
        mainText = (TextView) findViewById(R.id.main_main_text);
        mainLayout.setOnClickListener(this);

        fragmentLayout.setLongClickable(true);

        // View partLayout;
        // bottomLayout = (LinearLayout) findViewById(R.id.bottomMenu);
        // inflater = (LayoutInflater) this
        // .getSystemService(LAYOUT_INFLATER_SERVICE);
        // inflater=LayoutInflater.from(this);
        // partLayout = inflater.inflate(R.layout.part_layout, bottomLayout);
        // ImageView imageView = (ImageView) partLayout
        // .findViewById(R.id.part_image);
        // if (imageView != null) {
        // imageView.setImageResource(R.drawable.ic_launcher);
        // } else {
        // // finish();
        // }
        // textView.getText().toString();
        // bottomLayout.addView(mainLayout);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(mainLayout)) {
            setTabSelection(-1);
            return;
        }

        int find = -1;
        for (int i = 0; i < mobileLayouts.length; i++) {
            if (v.equals(mobileLayouts[i])) {
                find = i;
            }
        }
        if (find == -1)
            return;

        setTabSelection(find);
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {

        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
       //  transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        if (index == -1) {
            //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            mainTitle.setText("首页");

            mainImage.setImageResource(R.drawable.icon_tab_home_press);
            mainText.setTextColor(Color.rgb(255, 132, 0));
            if (mainFragment == null) {
                // 如果MessageFragment为空，则创建一个并添加到界面上
                mainFragment = new Mobile1Fragment();
                transaction.add(R.id.content, mainFragment);
            } else {
                transaction.show(mainFragment);
            }
        } else {
            //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            mainTitle.setText("环境" + index + "参数设置");

            mobileImages[index]
                    .setImageResource(R.drawable.icon_tab_discover_press);
            mobileTexts[index].setTextColor(Color.rgb(255, 132, 0));
            if (mobileFragments[index] == null) {
                // 如果ContactsFragment为空，则创建一个并添加到界面上
                mobileFragments[index] = new Mobile2Fragment();
                mobileFragments[index].setWhichFragment(index);
                transaction.add(R.id.content, mobileFragments[index]);
            } else {
                transaction.show(mobileFragments[index]);
            }
        }


        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        mainImage.setImageResource(R.drawable.icon_tab_home_normal);
        mainText.setTextColor(Color.parseColor("#82858b"));
        for (int i = 0; i < mobileLayouts.length; i++) {
            mobileImages[i]
                    .setImageResource(R.drawable.icon_tab_discover_normal);
            mobileTexts[i].setTextColor(Color.parseColor("#82858b"));

        }

        // mobile1Image.setImageResource(R.drawable.icon_tab_discover_normal);
        // mobile1Text.setTextColor(Color.parseColor("#82858b"));
        // mobile2Image.setImageResource(R.drawable.icon_tab_discover_normal);
        // mobile2Text.setTextColor(Color.parseColor("#82858b"));
        // mobile3Image.setImageResource(R.drawable.icon_tab_discover_normal);
        // mobile3Text.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        for (int i = 0; i < mobileFragments.length; i++) {

            if (mobileFragments[i] != null) {
                transaction.hide(mobileFragments[i]);
            }

        }

    }

    public static String getBetween(String content, String left, String right) {
        if (content.indexOf(right) == -1 || content.indexOf(left) == -1) {
            return "";
        }
        int start = content.indexOf(left) + left.length();
        int end = content.indexOf(right, start);
        return content.substring(start, end);
    }

}