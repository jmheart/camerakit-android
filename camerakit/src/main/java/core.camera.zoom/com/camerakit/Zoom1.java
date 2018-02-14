package com.camerakit;

class Zoom1 extends Camera1.Module implements ZoomModule {

    @Override
    public CameraPromise zoomTo(float zoomFactor) {
        return null;
    }

    @Override
    public CameraPromise smoothZoomTo(float zoomFactor) {
        return null;
    }

}
