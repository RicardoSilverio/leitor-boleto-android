package br.com.silvr.leitorboleto;

import android.Manifest;
import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * Created by silvr on 06/07/17.
 */

public class CameraPreview extends ViewGroup {

    private boolean layoutPronto;
    private boolean invocado;

    private Context context;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                layoutPronto = true;
                try {
                    start();
                } catch (IOException|SecurityException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                layoutPronto = false;
            }
        });
        addView(surfaceView);
    }

    public void start(CameraSource cameraSource) throws IOException, SecurityException {
        if(cameraSource == null) {
            stop();
        }

        this.cameraSource = cameraSource;
        if(this.cameraSource != null) {
            invocado = true;
            start();
        }
    }

    public void stop() {
        if(this.cameraSource != null) {
            this.cameraSource.stop();
        }
    }

    public void release() {
        if(this.cameraSource != null) {
            this.cameraSource.release();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start() throws IOException, SecurityException {
        if(invocado && layoutPronto) {
            cameraSource.start(surfaceView.getHolder());
            invocado = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, layoutWidth, layoutHeight);
        }

        try {
            start();
        } catch (IOException|SecurityException e) {
            e.printStackTrace();
        }
    }
}
