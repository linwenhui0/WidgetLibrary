package com.hlibrary.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.hlibrary.util.Logger;


public class ArcMenu extends ViewGroup implements OnClickListener {
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    private Position mPosition = Position.RIGHT_BOTTOM;
    private int mRadius;
    private double mAngle = Math.PI;

    /**
     * 菜单的状态
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 菜单的主按钮
     */
    private View mCButton;
    private int controlIdIndex;

    private OnMenuItemClickListener mMenuItemClickListener;

    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 菜单的位置枚举类
     */
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    private int marginTop, marginLeft, marginRight, marginBottom;

    /**
     * 点击子菜单项的回调接口
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100, getResources().getDisplayMetrics());

        // 获取自定义属性的值
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcMenu, defStyle, 0);

        int pos = a.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM);
        switch (pos) {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) a.getDimension(R.styleable.ArcMenu_radius, TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources().getDisplayMetrics()));
        marginLeft = a.getDimensionPixelSize(R.styleable.ArcMenu_marginLeft, 0);
        marginTop = a.getDimensionPixelSize(R.styleable.ArcMenu_marginTop, 0);
        marginBottom = a.getDimensionPixelSize(R.styleable.ArcMenu_marginBottom, 0);
        marginRight = a.getDimensionPixelSize(R.styleable.ArcMenu_marginRight, 0);
        mAngle = Math.PI * a.getInt(R.styleable.ArcMenu_angle, 1) / 2.0;
        controlIdIndex = a.getInteger(R.styleable.ArcMenu_controlIdIndex, 0);

        Logger.getInstance().e("TAG", "position = " + mPosition + " , radius =  " + mRadius);

        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            // 测量child
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButton();

            int count = getChildCount();

            int detal = 0;
            for (int i = 0; i < count; i++) {
                if (i == controlIdIndex) {
                    detal = 1;
                    continue;
                }
                View child = getChildAt(i);

                child.setVisibility(View.GONE);

                int cl = (int) (mRadius * Math.sin(mAngle / (count - 2)
                        * (i - detal)));
                int ct = (int) (mRadius * Math.cos(mAngle / (count - 2)
                        * (i - detal)));

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                int tempW;
                int tempH;

                switch (mPosition) {
                    case LEFT_TOP:
                        cl += marginLeft;
                        ct += marginTop;
                        break;
                    case LEFT_BOTTOM:
                        ct = getMeasuredHeight() - cHeight - ct - marginBottom;
                        cl += marginLeft;
                        break;
                    case RIGHT_TOP:
                        cl = getMeasuredWidth() - cWidth - cl - marginRight;
                        ct += marginTop;
                        break;
                    case RIGHT_BOTTOM:
                        tempW = (mCButton.getMeasuredWidth() - child.getMeasuredWidth()) / 2;
                        tempH = (mCButton.getMeasuredHeight() - child.getMeasuredHeight()) / 2;
                        ct = getMeasuredHeight() - cHeight - ct - marginBottom - tempH;
                        cl = getMeasuredWidth() - cWidth - cl - marginRight - tempW;
                        break;
                }
                child.layout(cl, ct, cl + cWidth, ct + cHeight);

            }

        }

    }

    /**
     * 定位主菜单按钮
     */
    private void layoutCButton() {
        mCButton = getChildAt(controlIdIndex);
        mCButton.setOnClickListener(this);

        int l = 0;
        int t = 0;

        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();

        switch (mPosition) {
            case LEFT_TOP:
                l = marginLeft;
                t = marginTop;
                break;
            case LEFT_BOTTOM:
                l = marginLeft;
                t = getMeasuredHeight() - height - marginBottom;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width - marginRight;
                t = marginTop;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width - marginRight;
                t = getMeasuredHeight() - height - marginBottom;
                break;
        }
        mCButton.layout(l, t, l + width, t + width);
    }

    @Override
    public void onClick(View v) {
        // mCButton = findViewById(R.id.id_button);
        // if(mCButton == null)
        // {
        // mCButton = getChildAt(0);
        // }

        rotateCButton(v, 0f, 360f, 300);

        toggleMenu(300);

    }

    /**
     * 切换菜单
     */
    public void toggleMenu(int duration) {
        // 为menuItem添加平移动画和旋转动画
        int count = getChildCount();

        int detal = 0;
        for (int i = 0; i < count; i++) {
            if (i == controlIdIndex) {
                detal = 1;
                continue;
            }
            final View childView = getChildAt(i);
            childView.setVisibility(View.VISIBLE);
            childView.setAlpha(1f);
            childView.setScaleX(1f);
            childView.setScaleY(1f);

            // end 0 , 0
            // start
            int cl = (int) (mRadius * Math.sin(mAngle / (count - 2) * (i - detal)));
            int ct = (int) (mRadius * Math.cos(mAngle / (count - 2) * (i - detal)));

            int xflag = 1;
            int yflag = 1;

            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            // to open
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else
            // to close
            {
                tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);

            tranAnim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });
            // 旋转动画
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);

            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null)
                        mMenuItemClickListener.onClick(childView, pos);

                    menuItemAnim(pos - 1);
                    changeStatus();

                }
            });
        }
        // 切换菜单状态
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     *
     * @param pos
     */
    private void menuItemAnim(int pos) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i == controlIdIndex)
                continue;
            View childView = getChildAt(i);
            if (i == pos) {
                scaleBigAnim(childView, 300);
            } else {
                scaleSmallAnim(childView, 300);
            }

            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    private void scaleSmallAnim(View v, int duration) {

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleAnimX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0f);
        ObjectAnimator scaleAnimY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
        animatorSet.setDuration(duration);
        animatorSet.playTogether(scaleAnimX, scaleAnimY, alphaAnim);
        animatorSet.start();
    }

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     *
     * @param duration
     * @return
     */
    private void scaleBigAnim(final View v, int duration) {
        AnimatorSet animationSet = new AnimatorSet();
        ObjectAnimator scaleAnimX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 4f);
        ObjectAnimator scaleAnimY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 4f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0);
        animationSet.playTogether(alphaAnim, scaleAnimX, scaleAnimY);
        animationSet.setDuration(duration);
        animationSet.start();
    }

    /**
     * 切换菜单状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }


    private void rotateCButton(View v, float start, float end, int duration) {
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", start, end);
        rotationAnim.setDuration(duration);
        rotationAnim.start();
    }

}
