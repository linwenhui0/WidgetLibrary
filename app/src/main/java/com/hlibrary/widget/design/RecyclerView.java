package com.hlibrary.widget.design;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hlibrary.widget.R;

public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {

    private OnBottomCallback mOnBottomCallback;
    private boolean firstViualBottom;
    private boolean onlyCallOnly;//控制OnBottomCallback只回调一次

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 获取自定义属性的值
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.RecyclerView, defStyle, defStyle);
        firstViualBottom = a.getBoolean(R.styleable.RecyclerView_firstViualBottom, true);
        a.recycle();
    }

    public void setOnBottomCallback(OnBottomCallback onBottomCallback) {
        this.mOnBottomCallback = onBottomCallback;
    }


    @Override
    public void onScrolled(int dx, int dy) {
        if (mOnBottomCallback != null) {
            if (firstViualBottom) {
                if (isSlideToBottom1() && !onlyCallOnly) {
                    onlyCallOnly = true;
                    mOnBottomCallback.onBottom();
                }
            } else {
                if (isSlideToBottom2()) {
                    mOnBottomCallback.onBottom();
                }
            }

        }
    }

    /**
     * 其实就是它在起作用。
     */
    public boolean isSlideToBottom1() {
        int postion = -1;
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            postion = linearLayoutManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            postion = gridLayoutManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
            postion = findMax(lastPositions);
        }
        if (postion != -1) {
            if (getLayoutManager().getItemCount() - 1 == postion) {
                return true;
            } else {
                onlyCallOnly = false;
                return false;
            }
        }
        return isSlideToBottom2();
    }

    public boolean isSlideToBottom2() {
        boolean result = ((this.computeVerticalScrollExtent() + this.computeVerticalScrollOffset())
                >= this.computeVerticalScrollRange());
        if (!result) {
            onlyCallOnly = false;
        }
        return result;
    }

    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public interface OnBottomCallback {
        void onBottom();
    }

}
