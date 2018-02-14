package com.camerakit;

interface FocusModule {
    CameraPromise setMode(FocusMode focusMode);

    CameraPromise addArea(FocusArea focusArea);

    CameraPromise clearAreas();

    CameraPromise autoFocus();
}