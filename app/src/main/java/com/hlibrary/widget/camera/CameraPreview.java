package com.hlibrary.widget.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraPreview extends SurfaceView implements Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int facing = CameraInfo.CAMERA_FACING_BACK;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        mCamera = getCameraInstance();
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /***
     * 获取摄像头实例
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            int cameraId = getDefaultCameraId(getContext());
            if (cameraId != -1)
                c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public Camera getCamera() {
        if (mCamera == null)
            mCamera = getCameraInstance();
        return mCamera;
    }

    /**
     * 得到默认相机的ID
     *
     * @return
     */
    private int getDefaultCameraId(Context ctx) {
        int defaultId = -1;

        // Find the total number of cameras available
        int mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                defaultId = i;
            }
        }
        if (-1 == defaultId) {
            if (mNumberOfCameras > 0) {
                // 如果没有后向摄像头
                defaultId = 0;
            } else {
                // 没有摄像头
                Toast.makeText(ctx, "没有摄像头", Toast.LENGTH_LONG).show();
            }
        }
        return defaultId;
    }

    public void releaseCarema() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        try {
            getCamera().stopPreview();
            getCamera().setPreviewDisplay(mHolder);
            getCamera().startPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            getCamera().setPreviewDisplay(holder);
            getCamera().startPreview();
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
