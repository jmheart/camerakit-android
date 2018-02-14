package com.camerakit;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

public class CameraSession extends FrameLayout {

    private CameraApi cameraApi;
    private PreviewView previewView;

    private CameraSession(Context context) {
        super(context);
    }

    private CameraSession(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private CameraSession(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CameraSession(Context context, Facing facing) throws Exception {
        this(context);

        if (Build.VERSION.SDK_INT < 21) {
            cameraApi = new Camera1(context, facing);
            previewView = createSurfaceViewPreview(context);
        } else {
            cameraApi = new Camera2(context, facing);
            previewView = createTextureViewPreview(context);
        }

        addView(previewView);
    }

    public void release() {
        previewView.stop();
        cameraApi.prepare()
                .then(CameraApi::stopPreview)
                .then(CameraApi::disconnect);
    }

    protected Resolution getPreviewResolution() {
        return cameraApi.attributes.supportedPreviewSizes.last();
    }

    protected Resolution getAdjustedPreviewResolution() {
        int sensorOrientation = cameraApi.attributes.orientation;
        int deviceOrientation = getResources().getConfiguration().orientation;

        switch (deviceOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                if (sensorOrientation % 180 == 90) {
                    return getPreviewResolution();
                } else {
                    return getPreviewResolution().inverse();
                }
            }

            case Configuration.ORIENTATION_PORTRAIT:
            default: {
                if (sensorOrientation % 180 == 90) {
                    return getPreviewResolution().inverse();
                } else {
                    return getPreviewResolution();
                }
            }
        }
    }

    private PreviewView createSurfaceViewPreview(Context context) {
        return new SurfaceViewPreview(context, getAdjustedPreviewResolution()) {
            @Override
            void onOrientationChanged(int displayOrientation) {
                cameraApi.setDisplayOrientation(displayOrientation).await();
            }

            @Override
            void attachSurface(SurfaceHolder surfaceHolder) {
                cameraApi.startPreview(getPreviewResolution(), surfaceHolder).await();
            }

            @Override
            void detachSurface() {
                cameraApi.stopPreview().await();
            }
        };
    }

    private PreviewView createTextureViewPreview(Context context) {
        return new TextureViewPreview(context, getAdjustedPreviewResolution()) {
            @Override
            void onOrientationChanged(int displayOrientation) {
                cameraApi.setDisplayOrientation(displayOrientation).await();
            }

            @Override
            void attachSurface(SurfaceTexture surfaceTexture) {
                cameraApi.startPreview(getPreviewResolution(), surfaceTexture).await();
            }

            @Override
            void detachSurface() {
                cameraApi.stopPreview().await();
            }
        };
    }

}
