package com.hlibrary.widget.listview;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hlibrary.widget.listener.DefaultIndexer;

public class SideBarListView extends ListView {

	private SideBar sideBar;

	public SideBarListView(Context context) {
		super(context);
	}

	public SideBarListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBarListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setSideBar(SideBar sideBar) {
		this.sideBar = sideBar;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (sideBar != null)
			sideBar.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (sideBar != null)
			sideBar.draw(canvas);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (sideBar != null)
			sideBar.setAdapter(adapter);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (sideBar != null && sideBar.onTouchEvent(ev))
			return true;
		return super.dispatchTouchEvent(ev);
	}

	public interface Indexerable {

		DefaultIndexer getIndexer();

	}

}
