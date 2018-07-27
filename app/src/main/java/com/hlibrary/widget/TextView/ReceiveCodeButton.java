package com.hlibrary.widget.TextView;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.v4.app.SupportActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.hlibrary.util.Logger;
import com.hlibrary.util.Utils;
import com.hlibrary.widget.R;
import com.hlibrary.widget.listener.IReceiverCodeListener;


/**
 * 获取验证码按钮，倒数60秒
 * Created by Administrator on 2015/5/25 14:53
 */
public class ReceiveCodeButton extends android.support.v7.widget.AppCompatCheckBox implements CompoundButton.OnCheckedChangeListener, LifecycleObserver {
    /**
     * 绑定手机号码的edittext
     */
    private int edittextId = 0;
    private EditText phoneEdittext;
    private String phoneNum;

    /**
     * 获取验证码间隔
     */
    private int codeInterval = 60000;
    private final long delayTime = 1000;

    /**
     * 默认国家代码为中国
     */
    private OnCheckedChangeListener mListener;
    private String defaultText, receiveText;
    private IReceiverCodeListener receiverCodeListener;
    private CountDownTimer countDownTimer;


    public ReceiveCodeButton(Context context) {
        this(context, null);
    }

    public ReceiveCodeButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public ReceiveCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ReceiveCodeButton);
        receiveText = a.getString(R.styleable.ReceiveCodeButton_receive_text);
        edittextId = a.getResourceId(R.styleable.ReceiveCodeButton_edittext_id, 0);
        defaultText = a.getString(R.styleable.ReceiveCodeButton_default_text);
        codeInterval = a.getInteger(R.styleable.ReceiveCodeButton_code_interval, 60) * 1000;
        setText(defaultText);
        a.recycle();

        super.setOnCheckedChangeListener(this);

        if (context instanceof SupportActivity) {
            ((SupportActivity) context).getLifecycle().addObserver(this);
        }
    }

    public void setReceiveText(String receiveText) {
        this.receiveText = receiveText;
    }

    public void setReceiverCodeListener(IReceiverCodeListener receiverCodeListener) {
        this.receiverCodeListener = receiverCodeListener;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume() {
        if (edittextId > 0 && phoneEdittext == null) {
            phoneEdittext = getRootView().findViewById(edittextId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destory() {
        Logger.getInstance().defaultTagD("destory");
        if (countDownTimer != null)
            countDownTimer.cancel();
        Context context = getContext();
        if (context instanceof SupportActivity) {
            ((SupportActivity) context).getLifecycle().removeObserver(this);
        }
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
            phoneNum = phoneEdittext.getText().toString();
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

            boolean isMobileNumber;
            if (phoneEdittext instanceof FormatEditText) {
                FormatEditText formatEditText = (FormatEditText) phoneEdittext;
                String separator = formatEditText.getSeparator();
                isMobileNumber = Utils.Companion.isFormatMobileNO(phoneNum, separator);
            } else {
                isMobileNumber = Utils.Companion.isMobileNO(phoneNum);
            }
            Logger.getInstance().defaultTagD("isMobileNumber = ", isMobileNumber);
            if (!isMobileNumber) {
                if (receiverCodeListener != null)
                    receiverCodeListener.onPhoneNumberValib();
                setChecked(false);
                return;
            }

            startCountTime();
        }
    }

    private void updateViewText(long millisUntilFinished) {
        Logger.getInstance().defaultTagD(millisUntilFinished);
        if (receiveText == null)
            setText(String.format("%ds", millisUntilFinished / 1000));
        else
            setText(String.format(receiveText, millisUntilFinished / 1000));
    }

    private void startCountTime() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        countDownTimer = new CountDownTimer(codeInterval + 1500, delayTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateViewText(millisUntilFinished - 1000);
                if (receiverCodeListener != null)
                    receiverCodeListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {

                countDownTimer = null;
                Logger.getInstance().defaultTagD("onFinish");
                setText(defaultText);
                setChecked(false);
                if (receiverCodeListener != null)
                    receiverCodeListener.onFinish();
            }
        };
        countDownTimer.start();
    }


}
