package com.camerakit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;

abstract class TextureViewPreview extends PreviewView implements TextureView.SurfaceTextureListener {

    private TextureView textureView;

    public TextureViewPreview(Context context, Resolution previewResolution) {
        super(context, previewResolution);
    }

    @Override
    protected View createOutputView() {
        textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        return textureView;
    }

    @Override
    public void stop() {
        super.stop();
        textureView.setSurfaceTextureListener(null);
    }

    @Override
    void attachSurface(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        surface.setDefaultBufferSize(previewResolution.getWidth(), previewResolution.getHeight());
        attachSurface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (width == previewResolution.getWidth() && height == previewResolution.getHeight()) {
            attachSurface(surface);
        } else {
            surface.setDefaultBufferSize(previewResolution.getWidth(), previewResolution.getHeight());
            attachSurface(surface);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        detachSurface();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

}
