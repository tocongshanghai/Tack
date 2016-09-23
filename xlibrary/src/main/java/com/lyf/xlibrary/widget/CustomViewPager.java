package com.lyf.xlibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * 此类描述的是:自定义ViewPager实现手动控制是否滑动以及设置滑动速度
 * 
 * @author: 肖雷
 * @version:1.0
 * @date:2016年3月16日 上午9:41:04
 */
public class CustomViewPager extends ViewPager {

	private boolean isScrollEnable = true;// 是否开起滑动
	private int mScrollDuration = 300;// 滑动速度默认0.3秒
	private ViewPagerScroller scroller;// 控制ViewPager滑动类

	public void setScrollDuration(int mScrollDuration) {
		this.mScrollDuration = mScrollDuration;
		scroller.setScrollDuration(mScrollDuration);
		scroller.initViewPagerScroll(this);
	}

	public void setScrollEnable(boolean isScrollEnable) {
		this.isScrollEnable = isScrollEnable;
	}

	public CustomViewPager(Context context) {
		super(context);
		scroller = new ViewPagerScroller(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		scroller = new ViewPagerScroller(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return this.isScrollEnable && super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return this.isScrollEnable && super.onInterceptTouchEvent(ev);
	}

	/**
	 * ViewPager 滚动速度设置
	 */
	public class ViewPagerScroller extends Scroller {
		private int mScrollDuration = 2000;// 滑动速度

		/**
		 * 设置速度速度
		 *
		 * @param duration
		 */
		public void setScrollDuration(int duration) {
			this.mScrollDuration = duration;
		}

		public ViewPagerScroller(Context context) {
			super(context);
		}

		public ViewPagerScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, mScrollDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mScrollDuration);
		}

		public void initViewPagerScroll(ViewPager viewPager) {
			try {
				Field mScroller = ViewPager.class.getDeclaredField("mScroller");
				mScroller.setAccessible(true);
				mScroller.set(viewPager, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
