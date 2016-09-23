package com.lyf.xlibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 此类描述的是：处理与ViewPager滑动冲突
 * 作者：肖雷
 * 时间：2015/12/15 16:39
 * 公司：
 */
public class CustomViewFlow extends ViewFlow {
    public ViewPager mViewPager;

    public void setViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    public CustomViewFlow(Context context) {
        super(context);
    }

    public CustomViewFlow(Context context, int sideBuffer) {
        super(context, sideBuffer);
    }

    public CustomViewFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mViewPager != null)
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mViewPager.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    mViewPager.requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mViewPager.requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mViewPager.requestDisallowInterceptTouchEvent(true);
                    break;
            }
        return super.onInterceptTouchEvent(ev);
    }
}
