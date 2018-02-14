package com.camerakit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

abstract class SurfaceViewPreview extends PreviewView implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;

    public SurfaceViewPreview(Context context, Resolution previewResolution) {
        super(context, previewResolution);
    }

    @Override
    protected View createOutputView() {
        surfaceView = new SurfaceView(getContext());
        surfaceView.getHolder().addCallback(this);
        return surfaceView;
    }

    @Override
    public void stop() {
        super.stop();
        surfaceView.getHolder().removeCallback(this);
    }

    @Override
    void attachSurface(SurfaceTexture surfaceTexture) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        holder.setFixedSize(previewResolution.getWidth(), previewResolution.getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (width == previewResolution.getWidth() && height == previewResolution.getHeight()) {
            attachSurface(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        detachSurface();
    }

}
