package com.camerakit;

interface ZoomModule {
    CameraPromise zoomTo(float zoomFactor);

    CameraPromise smoothZoomTo(float zoomFactor);
}
