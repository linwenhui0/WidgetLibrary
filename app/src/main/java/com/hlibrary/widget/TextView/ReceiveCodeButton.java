package com.hlibrary.widget.TextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.hlibrary.util.Utils;
import com.hlibrary.widget.R;
import com.hlibrary.widget.listener.IReceiverCodeListener;


/**
 * 获取验证码按钮，倒数60秒
 * Created by Administrator on 2015/5/25 14:53
 */
public class ReceiveCodeButton extends android.support.v7.widget.AppCompatCheckBox implements CompoundButton.OnCheckedChangeListener,Runnable {
    /**
     * 绑定手机号码的edittext
     */
    private EditText phoneEdittext;
    private String phoneNum;

    /**
     * 获取验证码间隔
     */
    private final long codeInterval = 60000l;
    private final long delayTime = 1000l;

    /**
     * 默认国家代码为中国
     */
    private OnCheckedChangeListener mListener;
    private String defaultText, receiveText;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private IReceiverCodeListener receiverCodeListener;


    public ReceiveCodeButton(Context context) {
        this(context, null);
    }

    public ReceiveCodeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReceiveCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ReceiveCodeButton);
        receiveText = a.getString(R.styleable.ReceiveCodeButton_receive_text);
        super.setOnCheckedChangeListener(this);
        defaultText = getText().toString();
    }

    public void setReceiveText(String receiveText) {
        this.receiveText = receiveText;
    }

    public void setReceiverCodeListener(IReceiverCodeListener receiverCodeListener) {
        this.receiverCodeListener = receiverCodeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = isChecked();
        if (result) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     * 绑定手机的edittext
     *
     * @param phoneEdittext
     */
    public void setPhoneEdittext(EditText phoneEdittext) {
        this.phoneEdittext = phoneEdittext;
    }

    private void initPhoneNum() {
        if (phoneEdittext != null)
            phoneNum = phoneEdittext.getText().toString().replace(" ", "").trim();
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mListener != null) {
            mListener.onCheckedChanged(buttonView, isChecked);
        }
        setChecked(isChecked);
        if (isChecked) {

            initPhoneNum();

            if (TextUtils.isEmpty(phoneNum)) {
                if (receiverCodeListener != null)
                    receiverCodeListener.onEmptyPhoneNumber();
                setChecked(false);
                return;
            }

            if (!Utils.isMobileNO(phoneNum)) {
                if (receiverCodeListener != null)
                    receiverCodeListener.onPhoneNumberValib();
                setChecked(false);
                return;
            }

            mainHandler.removeCallbacks(this);
            totalTime = codeInterval;
            mainHandler.post(this);

        }
    }

    public void destory() {
        mainHandler.removeCallbacks(null);
    }

    private long totalTime = 0l;

    @Override
    public void run() {
        totalTime = totalTime - delayTime;
        if (totalTime > 0l) {
//                if (receiveText == null)
//                    setText(defaultText + "(" + (totalTime / 1000) + ")");
//                else setText(receiveText + "(" + (totalTime / 1000) + ")");
            if (receiveText == null)
                setText(new StringBuilder(6).append(totalTime / 1000).append("s").toString());
            else
                setText(new StringBuilder(10).append(receiveText).append("(").append(totalTime / 1000).append(")").toString());
            mainHandler.postDelayed(this, delayTime);
            if (receiverCodeListener != null)
                receiverCodeListener.onTick(totalTime);
        } else {
            setText(defaultText);
            setChecked(false);
            if (receiverCodeListener != null)
                receiverCodeListener.onFinish();
        }
    }
}
