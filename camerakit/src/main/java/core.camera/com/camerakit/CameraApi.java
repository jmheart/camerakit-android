package com.camerakit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;

abstract class CameraApi implements CameraModuleProvider {

    public final FlashModule flash = provideFlash();
    public final FocusModule focus = provideFocus();
    public final ZoomModule zoom = provideZoom();
    public final PhotoModule photo = providePhoto();
    public final VideoModule video = provideVideo();

    public final CameraAttributes attributes;

    protected final Context context;

    private CameraApi() {
        throw new RuntimeException("No empty constructor.");
    }

    CameraApi(Context context, Facing facing) throws Exception {
        this.context = context;
        this.attributes = connect(facing).await();
    }

    protected abstract CameraPromise<CameraAttributes> connect(Facing facing);

    public abstract CameraPromise<CameraApi> prepare();

    public abstract CameraPromise<CameraApi> startPreview(Resolution resolution, SurfaceHolder surfaceHolder);

    public abstract CameraPromise<CameraApi> startPreview(Resolution resolution, SurfaceTexture surfaceTexture);

    public abstract CameraPromise<CameraApi> setDisplayOrientation(int orientation);

    public abstract CameraPromise<CameraApi> stopPreview();

    public abstract CameraPromise<CameraApi> disconnect();

}
