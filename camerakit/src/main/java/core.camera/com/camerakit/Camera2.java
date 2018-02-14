package com.camerakit;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Arrays;
import java.util.TreeSet;

import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;

@TargetApi(21)
class Camera2 extends CameraApi implements CameraModuleProvider {

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;

    private CameraCaptureSession captureSession;

    Camera2(Context context, Facing facing) throws Exception {
        super(context, facing);
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected CameraPromise<CameraAttributes> connect(Facing facing) {
        return new CameraPromise<>(((result, error) -> {
            int facingTarget = facing == Facing.BACK ? LENS_FACING_BACK : LENS_FACING_FRONT;
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) != facingTarget) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {

                    @Override
                    public void onOpened(CameraDevice camera) {
                        cameraDevice = camera;
                        result.resolve(new Attributes(characteristics, map).get());
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {

                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {

                    }

                }, null);
            }
        }));
    }

    @Override
    public CameraPromise<CameraApi> prepare() {
        return new CameraPromise<>(((result, error) -> {
            result.resolve(this);
        }));
    }

    @Override
    public CameraPromise<CameraApi> startPreview(Resolution resolution, SurfaceHolder surfaceHolder) {
        return new CameraPromise<>(((result, error) -> {
            CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());

            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    CaptureRequest previewRequest = previewRequestBuilder.build();
                    try {
                        session.setRepeatingRequest(previewRequest, null, null);
                        result.resolve(Camera2.this);
                    } catch (Exception e) {
                        error.reject(e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        }));
    }

    @Override
    public CameraPromise<CameraApi> startPreview(Resolution resolution, SurfaceTexture surfaceTexture) {
        return new CameraPromise<>(((result, error) -> {
            Surface surface = new Surface(surfaceTexture);

            CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    captureSession = session;
                    CaptureRequest previewRequest = previewRequestBuilder.build();
                    try {
                        session.setRepeatingRequest(previewRequest, null, null);
                        result.resolve(Camera2.this);
                    } catch (Exception e) {
                        error.reject(e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        }));
    }

    @Override
    public CameraPromise<CameraApi> setDisplayOrientation(int orientation) {
        return new CameraPromise<>(((result, error) -> {
            result.resolve(this);
        }));
    }

    @Override
    public CameraPromise<CameraApi> stopPreview() {
        return new CameraPromise<>(((result, error) -> {
            if (captureSession != null) {
                captureSession.close();
                captureSession = null;
            }
        }));
    }

    @Override
    public CameraPromise<CameraApi> disconnect() {
        return new CameraPromise<>(((result, error) -> {
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }));
    }

    @Override
    public FlashModule provideFlash() {
        return new Flash2();
    }

    @Override
    public FocusModule provideFocus() {
        return new Focus2();
    }

    @Override
    public ZoomModule provideZoom() {
        return new Zoom2();
    }

    @Override
    public PhotoModule providePhoto() {
        return new Photo2();
    }

    @Override
    public VideoModule provideVideo() {
        return new Video2();
    }

    static class Module {

        protected CameraManager cameraManager;
        protected CameraDevice cameraDevice;

        protected void setCamera(CameraManager cameraManager, CameraDevice cameraDevice) {
            this.cameraManager = cameraManager;
            this.cameraDevice = cameraDevice;
        }

    }

    private static class Attributes {

        private CameraCharacteristics characteristics;
        private StreamConfigurationMap map;

        public Attributes(CameraCharacteristics characteristics, StreamConfigurationMap map) {
            this.characteristics = characteristics;
            this.map = map;
        }

        public CameraAttributes get() {
            return new CameraAttributes() {
                @Override
                protected Facing getFacing() {
                    if (characteristics.get(CameraCharacteristics.LENS_FACING) == LENS_FACING_BACK) {
                        return Facing.BACK;
                    } else {
                        return Facing.FRONT;
                    }
                }

                @Override
                protected int getOrientation() {
                    return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                }

                @Override
                protected TreeSet<Resolution> getSupportedPreviewSizes() {
                    TreeSet<Resolution> sizes = new TreeSet<>();
                    Size[] supported = map.getOutputSizes(SurfaceHolder.class);
                    if (supported != null && supported.length > 0) {
                        for (Size size : supported) {
                            sizes.add(Resolution.get(size));
                        }
                    }
                    return sizes;
                }

                @Override
                protected boolean getFocusModeAutoSupported() {
                    return false;
                }

                @Override
                protected boolean getFocusModeContinuousPhotoSupported() {
                    return false;
                }

                @Override
                protected boolean getFocusModeContinuousVideoSupported() {
                    return false;
                }

                @Override
                protected boolean getFocusModeMacroSupported() {
                    return false;
                }

                @Override
                protected boolean getFocusModeEdofSupported() {
                    return false;
                }

                @Override
                protected int getMaxFocusAreas() {
                    return 0;
                }

                @Override
                protected boolean getZoomSupported() {
                    return false;
                }

                @Override
                protected boolean getSmoothZoomSupported() {
                    return false;
                }

                @Override
                protected int getMaxZoom() {
                    return 0;
                }

                @Override
                protected TreeSet<ZoomRatio> getSupportedZooms() {
                    return null;
                }

                @Override
                protected boolean getFlashModeOffSupported() {
                    return false;
                }

                @Override
                protected boolean getFlashModeOnSupported() {
                    return false;
                }

                @Override
                protected boolean getFlashModeTorchSupported() {
                    return false;
                }

                @Override
                protected TreeSet<Resolution> getSupportedPhotoSizes() {
                    return null;
                }

                @Override
                protected boolean getVideoSupported() {
                    return false;
                }

                @Override
                protected Resolution getPreferredVideoSize() {
                    return null;
                }

                @Override
                protected TreeSet<Resolution> getSupportedVideoSizes() {
                    return null;
                }
            };
        }

    }

}
