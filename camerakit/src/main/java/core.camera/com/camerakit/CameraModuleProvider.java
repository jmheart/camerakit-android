package com.camerakit;

interface CameraModuleProvider {

    FlashModule provideFlash();

    FocusModule provideFocus();

    ZoomModule provideZoom();

    PhotoModule providePhoto();

    VideoModule provideVideo();

}