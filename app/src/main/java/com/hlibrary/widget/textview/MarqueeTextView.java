package com.hlibrary.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.hlibrary.widget.R;

/**
 * 跑马灯
 */
public class MarqueeTextView extends AppCompatTextView implements Runnable, LifecycleObserver {

    private int circleTimes = 1;
    private int hasCircled = 0;
    private int currentScrollPos = 0;
    private int time_interval = 1;
    private int textWidth = 0;
    private boolean isMeasured = false;
    private boolean flag = false;
    private boolean autoStart = true;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性的值
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.MarqueeTextView, defStyleAttr, 0);
        // 时间间隔
        time_interval = a.getInt(R.styleable.MarqueeTextView_time_interval, 1);
        autoStart = a.getBoolean(R.styleable.MarqueeTextView_auto_start, true);
        circleTimes = a.getInt(R.styleable.MarqueeTextView_times, -1);
        a.recycle();
        if (context instanceof FragmentActivity) {
            ((FragmentActivity) context).getLifecycle().addObserver(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isMeasured) {
            getTextWidth();
            isMeasured = true;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume() {
        if (autoStart) {
            startScrollShow();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause() {
        removeCallbacks(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destory() {
        Context context = getContext();
        if (context instanceof FragmentActivity) {
            ((FragmentActivity) context).getLifecycle().removeObserver(this);
        }
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        // Log.i(TAG, "run。。。。。。。。。"+textWidth+this.circleTimes+hasCircled);
        // 起始滚动位置
        currentScrollPos += 2;
        scrollTo(currentScrollPos, 0);
        // Log.i(TAG, "pos"+currentScrollPos);
        // 判断滚动一次
        if (currentScrollPos >= textWidth) {
            // 从屏幕右侧开始出现
            currentScrollPos = -this.getWidth();
            if (circleTimes != -1 && hasCircled >= this.circleTimes) {
                this.setVisibility(View.GONE);
                flag = true;
            }
            hasCircled += 1;
        }
        if (!flag) {
            // 滚动时间间隔
            postDelayed(this, time_interval);
        }
    }

    /**
     * 获取文本显示长度
     */
    private void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        if (str == null) {
            textWidth = 0;
        }
        textWidth = (int) paint.measureText(str);
    }

    /**
     * 设置滚动次数，达到次数后设置不可见
     *
     * @param circleTimes
     */
    public void setCircleTimes(int circleTimes) {
        this.circleTimes = circleTimes;
    }

    public void setTimeInterval(int timeInterval) {
        this.time_interval = timeInterval;
    }

    public void startScrollShow() {
        if (this.getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
        }
        this.removeCallbacks(this);
        post(this);
    }

}
