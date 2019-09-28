package com.hlibrary.widget.listview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.hlibrary.widget.listener.DefaultIndexer;

public class SideBar {

	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private SimpleTouchingLetterListener simpleTouchingLetterListener;
	// 26个字母
	public String[] alpha;
	private ListView lstvw;
	private int choose = -1;// 选中,-1未选中
	private Paint paint = new Paint();

	private boolean isSetAlphaFont = true;// ture 索引画笔字体大小可以修改
	private float yPos;
	private Bitmap sideBg;
	private RectF sideBarRectF;
	private float sideBarWidth;
	private float singleHeight;

	private boolean isDisplayToastAlpha = false;// 是否显示Toast
	private boolean isSetToastFont = true;// ture 索引Toast字体大小可以修改
	private RectF toastRectf;
	private Bitmap toastBg;
	private Paint toastPaint = new Paint();

	public SideBar(ListView lstvw) {
		this.lstvw = lstvw;
		init(lstvw.getContext());
	}

	private void init(Context context) {
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		toastPaint.setTypeface(Typeface.DEFAULT_BOLD);
		toastPaint.setAntiAlias(true);
		toastPaint.setColor(Color.WHITE);

		simpleTouchingLetterListener = new SimpleTouchingLetterListener();
	}

	public void setTextSize(float textSize) {
		isSetAlphaFont = false;
		paint.setTextSize(Math.max(20, textSize));
	}

	public void setSideBackground(int resId) {
		sideBg = BitmapFactory.decodeResource(lstvw.getResources(), resId);
	}

	public void setSideBackground(Bitmap sideBg) {
		this.sideBg = sideBg;
	}

	public void setToastBackground(int resId) {
		toastBg = BitmapFactory.decodeResource(lstvw.getResources(), resId);
		setDisplayToastAlpha(true);
	}

	public void setToastBackground(Bitmap toastBg) {
		this.toastBg = toastBg;
		setDisplayToastAlpha(true);
	}

	public void setToastTextSize(float textSize) {
		isSetToastFont = false;
		toastPaint.setTextSize(textSize);
	}

	public void setDisplayToastAlpha(boolean isDisplayToastAlpha) {
		this.isDisplayToastAlpha = isDisplayToastAlpha;
	}

	/**
	 * 重写这个方法
	 */
	public void draw(Canvas canvas) {
		if (alpha == null)
			return;
		if (sideBg != null)
			canvas.drawBitmap(sideBg,
					new Rect(0, 0, sideBg.getWidth(), sideBg.getHeight()),
					new RectF(sideBarRectF.left, sideBarRectF.top,
							sideBarRectF.right, sideBarRectF.bottom), paint);
		// canvas.drawRect(
		// new Rect(paddingLeft, 0, lstvw.getWidth(), lstvw.getHeight()),
		// paint);
		for (int i = 0; i < alpha.length; i++) {
			// paint.setColor(Color.rgb(33, 65, 98));
			paint.setColor(Color.rgb(133, 140, 148));

			// 选中的状态
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}

			// x坐标等于中间-字符串宽度的一半.
			float xPos = (sideBarWidth - paint.measureText(alpha[i])) / 2
					+ sideBarRectF.left;
			float yPos = (singleHeight - SideBar.this.yPos) / 2 + (i + 1)
					* singleHeight + sideBarRectF.top;
			canvas.drawText(alpha[i], xPos, yPos, paint);
			// paint.reset();// 重置画笔
		}

		if (isDisplayToastAlpha && toastBg != null
				&& (choose >= 0 && choose < alpha.length)) {
			canvas.drawBitmap(toastBg, new Rect(0, 0, toastBg.getWidth(),
					toastBg.getHeight()), new RectF(toastRectf.left,
					toastRectf.top, toastRectf.right, toastRectf.bottom),
					toastPaint);
			float x = toastRectf.left
					+ (toastRectf.width() - toastPaint
							.measureText(alpha[choose])) / 2;
			float y = toastRectf.top + toastRectf.height() * 3 / 4;
			canvas.drawText(alpha[choose], x, y, toastPaint);
		}
	}

	private boolean contains(float x, float y) {
		// Determine if the point is in index bar region, which includes the
		// right margin of the bar
		return (x >= sideBarRectF.left && (y >= sideBarRectF.top && y <= sideBarRectF.bottom));
	}

	public void setAdapter(ListAdapter adapter) {
		if (adapter instanceof SideBarListView.Indexerable) {
			DefaultIndexer mIndexer = ((SideBarListView.Indexerable) adapter).getIndexer();
			alpha = (String[]) mIndexer.getSections();
			simpleTouchingLetterListener.setmIndexer(mIndexer);
			lstvw.postInvalidate();
		}
	}

	private boolean isTouch = false;

	public boolean onTouchEvent(MotionEvent event) {
		if (alpha == null)
			return false;
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = choose;
		final int c = (int) ((y - sideBarRectF.top) / sideBarRectF.height() * alpha.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
		switch (action) {
		case MotionEvent.ACTION_UP:
			// setBackgroundResource(android.R.color.transparent);
			choose = -1;
			isTouch = false;
			break;

		case MotionEvent.ACTION_DOWN:
			// if (bgDrawable != 0)
			// setBackgroundResource(bgDrawable);
			isTouch = contains(event.getX(), event.getY());
			if (isTouch) {
				if (oldChoose != c) {
					if (c >= 0 && c < alpha.length) {
						simpleTouchingLetterListener.onTouchingLetterChanged(c,
								alpha[c]);
						choose = c;
					} else {
						choose = -1;
					}
					lstvw.postInvalidate();
				}
				return true;
			}
		case MotionEvent.ACTION_MOVE:
			if (isTouch && oldChoose != c) {
				if (c >= 0 && c < alpha.length) {
					simpleTouchingLetterListener.onTouchingLetterChanged(c,
							alpha[c]);
					choose = c;
				} else {
					choose = -1;
				}
				lstvw.postInvalidate();
			}

			break;
		}

		return false;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (alpha == null)
			return;

		// 单个字母的高度
		if (isSetAlphaFont)
			paint.setTextSize(Math.max(20, w / 22.f));
		FontMetrics fm = paint.getFontMetrics();
		yPos = (float) (Math.ceil(fm.descent - fm.ascent) + 2);
		singleHeight = yPos;
		sideBarWidth = singleHeight * 1.5f;

		// 字母索引绘制区域
		MarginLayoutParams params = (MarginLayoutParams) lstvw
				.getLayoutParams();
		float paddingLeft = w - lstvw.getPaddingLeft() - sideBarWidth;
		if (params != null) {
			paddingLeft -= params.leftMargin;
		}
		final float minHeight = Math.min(h, singleHeight
				* (0.5f + alpha.length));
		final float paddingTop = (h - minHeight) / 2.f;
		if (paddingTop == 0) {
			singleHeight = minHeight / alpha.length;
		}
		sideBarRectF = new RectF(paddingLeft, paddingTop, w, paddingTop
				+ minHeight);

		// 字母提示区域
		if (isDisplayToastAlpha && toastBg != null) {
			float temp = w / 7f;
			toastRectf = new RectF(w / 2.f - temp, h / 2.f - temp, w / 2.f
					+ temp, h / 2.f + temp);
			if (isSetToastFont)
				toastPaint.setTextSize(w / 5);
		}
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(int pos, String s);
	}

	private class SimpleTouchingLetterListener implements
			OnTouchingLetterChangedListener {

		private SectionIndexer mIndexer;

		public void setmIndexer(SectionIndexer mIndexer) {
			this.mIndexer = mIndexer;
		}

		@Override
		public void onTouchingLetterChanged(int pos, String s) {
			int postion = mIndexer.getPositionForSection(pos);
			if (postion != -1) {
				lstvw.setSelection(postion);
			}
			if (onTouchingLetterChangedListener != null) {
				onTouchingLetterChangedListener.onTouchingLetterChanged(pos, s);
			}
		}

	}

}
