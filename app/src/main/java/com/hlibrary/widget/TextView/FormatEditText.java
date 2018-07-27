package com.hlibrary.widget.TextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.hlibrary.util.Logger;
import com.hlibrary.widget.R;


/**
 * @version v 1.0.0
 * @since 2015/8/19
 */
public class FormatEditText extends android.support.v7.widget.AppCompatEditText
        implements View.OnKeyListener {

    private final static String TAG = "FormatEditText";

    private String separator = " ";
    private OnKeyListener mKeyListener;

    //注意将super(context)改为 this(context,null),即调用了第二个构造函数
    public FormatEditText(Context context) {
        this(context, null);
    }

    public FormatEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public FormatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setSeparator(String separator) {
        if (TextUtils.isEmpty(separator))
            this.separator = " ";
        else
            this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        super.setOnKeyListener(this);
        setInputType(InputType.TYPE_CLASS_PHONE);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});

        // 获取自定义属性的值
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.FormatEditText, defStyleAttr, 0);
        // 分隔符
        separator = a.getString(R.styleable.FormatEditText_separator);
        if (TextUtils.isEmpty(separator))
            separator = " ";
        a.recycle();
    }


    @Override
    public void setOnKeyListener(OnKeyListener l) {
        mKeyListener = l;
    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
        phoneNumFormat(s, start, before);
    }

    private int moveNum;

    private void phoneNumFormat(CharSequence s, int start, int before) {
        if (s == null || s.length() == 0)
            return;

        if (!s.toString().startsWith("1")) {
            setText("");
            return;
        }

        String lastChar = String.valueOf(s.charAt(s.length() - 1));
        boolean isDigit = TextUtils.isDigitsOnly(lastChar);
        if (!isDigit) {
            int len = s.length();
            if (!((len == 4 || len == 9) && String.valueOf(s.charAt(len - 1)).equals(separator))) {
                setText(s.subSequence(0, len - 1));
                setSelection(len - 1);
                return;
            }
        }

        StringBuilder sb = new StringBuilder();
        moveNum = 0;
        int selectionStart = getSelectionStart();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && String.valueOf(s.charAt(i)).equals(separator)) {
                continue;
            } else {
                sb.append(s.charAt(i));
                int sbLen = sb.length();
                if ((sbLen == 4 || sbLen == 9) && !String.valueOf(sb.charAt(sbLen - 1)).equals(separator)) {
                    sb.insert(sb.length() - 1, separator);
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
        if (mKeyListener != null) {
            mKeyListener.onKey(v, keyCode, event);
        }
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
