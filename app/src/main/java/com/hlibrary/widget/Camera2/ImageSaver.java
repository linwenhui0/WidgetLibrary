package com.hlibrary.widget.Camera2;

import android.media.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Saves a JPEG {@link Image} into the specified {@link File}.
 */
public class ImageSaver implements Runnable {
    /**
     * The JPEG image
     */
    private final Image mImage;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    private Callback callback;

    private String outputFile;



    public ImageSaver(Image image, File file) {
        mImage = image;
        mFile = file;
    }

    public ImageSaver setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;

        File filenameFile = new File(mFile, "dd");
        try {
            output = new FileOutputStream(filenameFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (callback != null)
                    callback.onSaveImage(filenameFile.getAbsolutePath());
            }
        }
    }

    public interface Callback {
        void onSaveImage(String filename);
    }
}
