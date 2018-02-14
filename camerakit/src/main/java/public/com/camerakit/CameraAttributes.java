package com.camerakit;

import java.util.TreeSet;

public abstract class CameraAttributes {

    public final Facing facing = getFacing();
    public final int orientation = getOrientation();

    public final TreeSet<Resolution> supportedPreviewSizes = getSupportedPreviewSizes();

    public final boolean focusModeAutoSupported = getFocusModeAutoSupported();
    public final boolean focusModeContinuousPhotoSupported = getFocusModeContinuousPhotoSupported();
    public final boolean focusModeContinuousVideoSupported = getFocusModeContinuousVideoSupported();
    public final boolean focusModeMacroSupported = getFocusModeMacroSupported();
    public final boolean focusModeEdofSupported = getFocusModeEdofSupported();
    public final int maxFocusAreas = getMaxFocusAreas();

    public final boolean zoomSupported = getZoomSupported();
    public final boolean smoothZoomSupported = getSmoothZoomSupported();
    public final int maxZoom = getMaxZoom();
    public final TreeSet<ZoomRatio> supportedZooms = getSupportedZooms();

    public final boolean flashModeOffSupported = getFlashModeOffSupported();
    public final boolean flashModeOnSupported = getFlashModeOnSupported();
    public final boolean flashModeTorchSupported = getFlashModeTorchSupported();

    public final TreeSet<Resolution> supportedPhotoSizes = getSupportedPhotoSizes();

    public final boolean videoSupported = getVideoSupported();
    public final Resolution preferredVideoSize = getPreferredVideoSize();
    public final TreeSet<Resolution> supportedVideoSizes = getSupportedVideoSizes();

    public CameraAttributes() {
    }

    protected abstract Facing getFacing();

    protected abstract int getOrientation();

    protected abstract TreeSet<Resolution> getSupportedPreviewSizes();

    protected abstract boolean getFocusModeAutoSupported();

    protected abstract boolean getFocusModeContinuousPhotoSupported();

    protected abstract boolean getFocusModeContinuousVideoSupported();

    protected abstract boolean getFocusModeMacroSupported();

    protected abstract boolean getFocusModeEdofSupported();

    protected abstract int getMaxFocusAreas();

    protected abstract boolean getZoomSupported();

    protected abstract boolean getSmoothZoomSupported();

    protected abstract int getMaxZoom();

    protected abstract TreeSet<ZoomRatio> getSupportedZooms();

    protected abstract boolean getFlashModeOffSupported();

    protected abstract boolean getFlashModeOnSupported();

    protected abstract boolean getFlashModeTorchSupported();

    protected abstract TreeSet<Resolution> getSupportedPhotoSizes();

    protected abstract boolean getVideoSupported();

    protected abstract Resolution getPreferredVideoSize();

    protected abstract TreeSet<Resolution> getSupportedVideoSizes();

}
