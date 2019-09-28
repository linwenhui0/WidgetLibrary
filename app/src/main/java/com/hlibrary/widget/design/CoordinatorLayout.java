package com.hlibrary.widget.design;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by linwenhui on 2015/11/1.
 */
public class CoordinatorLayout extends androidx.coordinatorlayout.widget.CoordinatorLayout {
    public CoordinatorLayout(Context context) {
        super(context);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        try {
            return super.drawChild(canvas, child, drawingTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
