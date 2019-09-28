package com.hlibrary.widget.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;

import com.hlibrary.util.file.FileManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraRecorder {

    private CameraPreview cameraPreview;
    private MediaRecorder mMediaRecorder;
    private String filepath;
    private File mRecAudioFile;
    private boolean isRecording = false;
    private int quality = CamcorderProfile.QUALITY_HIGH;

    public CameraRecorder(CameraPreview cameraPreview) {
        this.cameraPreview = cameraPreview;
        filepath = FileManager.INSTANCE.getSdCardPath(cameraPreview.getContext()) + File.separator
                + "Android" + File.separator + "data" + File.separator
                + cameraPreview.getContext().getPackageName() + File.separator + "video";
        File mRecVedioPath = new File(filepath);
        if (!mRecVedioPath.exists()) {
            mRecVedioPath.mkdirs();
        }


    }

    /***
     * 检测是否有摄像头
     *
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * @param quality CamcorderProfile.QUALITY_HIGH 高清<br/>
     *                CamcorderProfile.QUALITY_LOW 标清
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void start() {
        if (prepareVideoRecorder()) {
            mMediaRecorder.start();
            isRecording = true;
        } else {
            releaseMediaRecorder();
        }
    }

    public void stop() {
        mMediaRecorder.stop();
        releaseMediaRecorder();
        cameraPreview.getCamera().lock();
        cameraPreview.releaseCarema();
        cameraPreview.getCamera();
        isRecording = false;
    }

    public boolean create() {
        Camera camera = cameraPreview.getCamera();
        if (camera == null)
            return false;
        return true;
    }

    public void destory() {
        releaseMediaRecorder();
        cameraPreview.releaseCarema();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public File getRecAudioFile() {
        return mRecAudioFile;
    }

    private String createFilePath() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private boolean prepareVideoRecorder() {
        Camera mCamera = cameraPreview.getCamera();
        mCamera.stopPreview();
        mMediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        CamcorderProfile profile = CamcorderProfile.get(quality);
        profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        profile.videoCodec = MediaRecorder.VideoEncoder.H264;
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        mMediaRecorder.setProfile(profile);
        // mMediaRecorder.setVideoSize(640, 480);
        // mMediaRecorder.setVideoFrameRate(16);
        if (quality == CamcorderProfile.QUALITY_480P) {
            mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        }
        // int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // if (currentapiVersion >= 14) {
        // mMediaRecorder.setVideoSize(profile.videoFrameWidth,
        // profile.videoFrameHeight);
        // mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        // }

        // Step 4: Set output file
        mRecAudioFile = new File(filepath + createFilePath() + ".3gp");
        if (mRecAudioFile.exists())
            mRecAudioFile.delete();
        else
            try {
                mRecAudioFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
        // Step 5: Set the preview output
        mMediaRecorder
                .setPreviewDisplay(cameraPreview.getHolder().getSurface());
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset(); // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            cameraPreview.getCamera().lock(); // lock camera for later use
        }
    }

}
