package com.camerakit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

abstract class PreviewView extends FrameLayout {

    private Display display;
    private RotationListener rotationListener;

    protected Resolution previewResolution;
    private View outputView;

    private PreviewView(Context context) {
        super(context);
    }

    private PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private PreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreviewView(Context context, Resolution previewResolution) {
        this(context);
        this.previewResolution = previewResolution;

        outputView = createOutputView();
        outputView.setLayoutParams(new FrameLayout.LayoutParams(previewResolution.getWidth(), previewResolution.getHeight()));
        addView(outputView);

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
            rotationListener = new RotationListener(getContext(), display) {
                @Override
                void onRotation(int displayRotation) {
                    dispatchOrientation(displayRotation);
                }
            };
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        float widthRatio = (float) (right - left) / (float) previewResolution.getWidth();
        float heightRatio = (float) (bottom - top) / (float) previewResolution.getHeight();

        if (widthRatio > heightRatio) {
            int width = (right - left);
            int height = (int) (previewResolution.getHeight() * widthRatio);
            int heightOffset = (height - (bottom - top)) / 2;

            outputView.layout(0, -heightOffset, width, height - heightOffset);
        } else if (heightRatio > widthRatio) {
            int width = (int) (previewResolution.getWidth() * heightRatio);
            int height = (bottom - top);
            int widthOffset = (width - (right - left)) / 2;

            outputView.layout(-widthOffset, 0, width - widthOffset, height);
        }
    }

    protected abstract View createOutputView();

    public void stop() {
        rotationListener.disable();
    }

    private void dispatchOrientation(int displayOrientation) {
        switch (displayOrientation) {
            case Surface.ROTATION_0: {
                onOrientationChanged(0);
                break;
            }

            case Surface.ROTATION_90: {
                onOrientationChanged(90);
                break;
            }

            case Surface.ROTATION_180: {
                onOrientationChanged(180);
                break;
            }

            case Surface.ROTATION_270: {
                onOrientationChanged(270);
                break;
            }
        }
    }

    abstract void onOrientationChanged(int displayOrientation);

    abstract void attachSurface(SurfaceHolder surfaceHolder);

    abstract void attachSurface(SurfaceTexture surfaceTexture);

    abstract void detachSurface();

    private static abstract class RotationListener extends OrientationEventListener {

        private Display display;
        private int lastKnownDisplayRotation = -1;

        RotationListener(Context context, Display display) {
            super(context);
            this.display = display;
            enable();
            onRotation(this.display.getRotation());
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            final int displayRotation = display.getRotation();
            if (lastKnownDisplayRotation != displayRotation) {
                lastKnownDisplayRotation = displayRotation;
                onRotation(displayRotation);
            }
        }

        abstract void onRotation(int displayRotation);

    }

}
