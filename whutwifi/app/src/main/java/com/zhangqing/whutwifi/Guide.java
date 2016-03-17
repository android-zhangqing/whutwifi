package com.zhangqing.whutwifi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class Guide extends Activity implements OnPageChangeListener {
	private ViewPager vp;
	private ViewPageAdapter vpAdapter;
	private List<View> views;
	private ImageView[] dots;
	private int[] ids = { R.id.imv1, R.id.imv2, R.id.imv3,R.id.imv4 };

	private boolean misScrolled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		initViews();
		initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.welcome1, null));
		views.add(inflater.inflate(R.layout.welcome2, null));
		views.add(inflater.inflate(R.layout.welcome3, null));
		views.add(inflater.inflate(R.layout.welcome4, null));
		vpAdapter = new ViewPageAdapter(views, this);
		vp = (ViewPager) findViewById(R.id.viewpage);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
	}

	private void initDots() {
		dots = new ImageView[views.size()];
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) findViewById(ids[i]);
		}
	}

	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

		switch (state) {
		case ViewPager.SCROLL_STATE_DRAGGING:
			misScrolled = false;
			break;
		case ViewPager.SCROLL_STATE_SETTLING:
			misScrolled = true;
			break;
		case ViewPager.SCROLL_STATE_IDLE:
			// // 整个滚动事件结束，只触发一次
			if (vp.getCurrentItem() == vp.getAdapter().getCount() - 1
					&& !misScrolled) {

				Guide.this.finish();
			}
			misScrolled = true;
			break;
		}
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), String.valueOf(arg0),
		// 1000).show();

	}

	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		// 0,1,2 arg0
		for (int i = 0; i < ids.length; i++) {
			if (arg0 == i) {
				dots[i].setImageResource(R.drawable.point_selected);
			} else {
				dots[i].setImageResource(R.drawable.point_unselected);
			}
		}
	}
}

class ViewPageAdapter extends PagerAdapter {
	private List<View> views;

	public ViewPageAdapter(List<View> views, Context context) {
		this.views = views;

	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position));
		return views.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return (arg0 == arg1);
	}

}