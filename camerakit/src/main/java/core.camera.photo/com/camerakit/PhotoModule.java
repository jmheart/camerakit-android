package com.camerakit;

interface PhotoModule {
    CameraPromise setResolution(Resolution resolution);

    CameraPromise setJpegQuality(int quality);

    CameraPromise<byte[]> captureStandard();

    CameraPromise<byte[]> capturePreview();
}