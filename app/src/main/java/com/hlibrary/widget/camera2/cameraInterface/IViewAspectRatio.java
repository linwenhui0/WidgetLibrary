package com.hlibrary.widget.camera2.cameraInterface;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;

/**
 * Created by linwenhui on 2017/11/18.
 */

public interface IViewAspectRatio {
    void onAspectRatio(int w, int h);

    void onTransform(Matrix matrix);

    SurfaceTexture getSurfaceTexture();

    void onMessageInfo(String msg);
}
