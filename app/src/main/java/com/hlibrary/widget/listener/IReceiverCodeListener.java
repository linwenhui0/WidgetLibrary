package com.hlibrary.widget.listener;

/**
 * Created by linwenhui on 2017/8/30.
 */

public interface IReceiverCodeListener {

    void onEmptyPhoneNumber();

    void onPhoneNumberValib();

    void onTick(long tick);

    void onFinish();

}
