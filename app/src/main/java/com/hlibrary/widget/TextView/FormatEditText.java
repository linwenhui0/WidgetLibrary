package com.hlibrary.widget.TextView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.hlibrary.util.Logger;


/**
 * @version v 1.0.0
 * @since 2015/8/19
 */
public class FormatEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher, View.OnKeyListener {

    private final static String TAG = "FormatEditText";

    private TextWatcher mTextWatcher;
    private OnKeyListener mKeyListener;

    //注意将super(context)改为 this(context,null),即调用了第二个构造函数
    public FormatEditText(Context context) {
        this(context, null);
    }

    public FormatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        super.addTextChangedListener(this);
        super.setOnKeyListener(this);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        mKeyListener = l;
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        mTextWatcher = watcher;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mTextWatcher != null)
            mTextWatcher.beforeTextChanged(s, start, count, after);
    }

    //注意：必须添加一下判断，防止干扰
    @Override
    public void afterTextChanged(Editable s) {
        if (mTextWatcher != null)
            mTextWatcher.afterTextChanged(s);
    }

    @Override

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mTextWatcher != null)
            mTextWatcher.onTextChanged(s, start, before, count);
        phoneNumFormat(s, start, before);
    }

    private int moveNum;

    private void phoneNumFormat(CharSequence s, int start, int before) {
        if (s == null || s.length() == 0) return;
        StringBuilder sb = new StringBuilder();
        moveNum = 0;
        int selectionStart = getSelectionStart();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                    moveNum = 1;
                    Logger.getInstance().i(TAG, "selectionStart = " + selectionStart + " === " + sb.charAt(selectionStart - 2) + "");
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            selectionStart += moveNum;
            setText(sb.toString());
            int len = getText().length();
            setSelection(selectionStart > len ? len : selectionStart);
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                int len = getText().length() - 1;
                String text = getText().toString();
                if (len > -1) {
                    char c = getText().toString().charAt(len);
                    if (c == 32) {
                        setText(text.substring(0, len));
                        setSelection(getText().length());
                    }
                }
                return true;
            }
        }
        return false;
    }
}
