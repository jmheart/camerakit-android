package com.camerakit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CameraView extends FrameLayout {

    private CameraSession session;

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void start(Facing facing) {
        if (session != null) {
            session.release();
            removeView(session);
            session = null;
        }

        new CameraPromise<CameraSession>(((result, error) -> result.resolve(new CameraSession(getContext(), facing))))
                .thenUi((result -> {
                    session = result;
                    addView(session);
                }));
    }

    public void stop() {
        if (session != null) {
            session.release();
        }
    }

}
