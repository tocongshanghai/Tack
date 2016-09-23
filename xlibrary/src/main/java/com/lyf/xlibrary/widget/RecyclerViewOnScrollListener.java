package com.lyf.xlibrary.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 此类描述的是：重写RecyclerView.OnScrollListener达到加载更多的功能
 * 作者：肖雷
 * 时间：2016/5/23 16:45
 * 公司：
 */
public class RecyclerViewOnScrollListener<T> extends RecyclerView.OnScrollListener {
    int lastItem;
    T mLayoutManager;
    boolean loadingMore=true;
    LoadingMoreListener moreListener;
    RecyclerView.Adapter mAdapter;

    public void setLastItem(int lastItem) {
        this.lastItem = lastItem;
    }

    public void setMoreListener(LoadingMoreListener moreListener) {
        this.moreListener = moreListener;
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public RecyclerViewOnScrollListener(T layoutManager, RecyclerView.Adapter adapter) {
        this.mLayoutManager = layoutManager;
        this.mAdapter=adapter;

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (loadingMore&&newState == RecyclerView.SCROLL_STATE_IDLE && lastItem + 1 == mAdapter.getItemCount()) {
            loadingMore=false;
            if(moreListener!=null){
                moreListener.loadMore();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mLayoutManager instanceof LinearLayoutManager)
            lastItem = ((LinearLayoutManager) mLayoutManager).findLastCompletelyVisibleItemPosition();
        if (mLayoutManager instanceof GridLayoutManager)
            lastItem = ((GridLayoutManager) mLayoutManager).findLastCompletelyVisibleItemPosition();
    }

    public interface LoadingMoreListener{
        void loadMore();
    }
}
