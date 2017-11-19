package com.hlibrary.widget.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * ListView与EditText数据错乱
 */
public abstract class LstTextWatch implements TextWatcher {

	private EditText edtxt;

	public LstTextWatch(EditText edtxt) {
		this.edtxt = edtxt;
	}

	@Override
	public void afterTextChanged(Editable s) {
		afterTextChanged(s, edtxt);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public abstract void afterTextChanged(Editable s, EditText edtxt);

}

// 1、配置相应的Activity键盘属性
// 2、
// EditText et = (EditText) convertView.findViewById(R.id.et_demo);
// et.setTag(position);
// et.addTextChangedListener(new CustTextWatch(holder.et) {
// @Override
// public void afterTextChanged(Editable s, EditText edtxt) {
// int p = (Integer) edtxt.getTag();
// cacheData(s.toString(), p);
// }
// });
