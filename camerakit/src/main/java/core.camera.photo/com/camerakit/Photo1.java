package com.camerakit;

class Photo1 extends Camera1.Module implements PhotoModule {

    @Override
    public CameraPromise setResolution(Resolution resolution) {
        return null;
    }

    @Override
    public CameraPromise setJpegQuality(int quality) {
        return null;
    }

    @Override
    public CameraPromise<byte[]> captureStandard() {
        return null;
    }

    @Override
    public CameraPromise<byte[]> capturePreview() {
        return null;
    }

}
