package com.hlibrary.widget.camera2;

import android.media.Image;

import com.hlibrary.util.date.DateFormatUtil;

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
    private Image mImage;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    private String outputFile;



    public ImageSaver(Image image, File file) {
        mImage = image;
        mFile = file;
    }

    public void setmImage(Image mImage) {
        this.mImage = mImage;
    }

    public String getOutputFile() {
        return outputFile;
    }


    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;

        File filenameFile = new File(mFile, DateFormatUtil.Companion.getDate("yyyy-mm-dd_HH_mm_ss"));
        outputFile = filenameFile.getAbsolutePath();
        try {
            output = new FileOutputStream(filenameFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            outputFile = null;
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
