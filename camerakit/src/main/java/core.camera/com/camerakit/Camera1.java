package com.camerakit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.util.List;
import java.util.TreeSet;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;
import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_EDOF;
import static android.hardware.Camera.Parameters.FOCUS_MODE_MACRO;

class Camera1 extends CameraApi implements CameraModuleProvider {

    private Camera camera;

    Camera1(Context context,  Facing facing) throws Exception {
        super(context, facing);
    }

    @Override
    protected CameraPromise<CameraAttributes> connect(Facing facing) {
        return new CameraPromise<>((result, error) -> {
            int cameraId = facing == Facing.BACK ? CAMERA_FACING_BACK : CAMERA_FACING_FRONT;
            camera = Camera.open(cameraId);

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            Camera.Parameters parameters = camera.getParameters();

            result.resolve(new Attributes(info, parameters).get());
        });
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
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(resolution.getWidth(), resolution.getHeight());
            camera.setParameters(parameters);

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            result.resolve(this);
        }));
    }

    @Override
    public CameraPromise<CameraApi> startPreview(Resolution resolution, SurfaceTexture surfaceTexture) {
        return new CameraPromise<>(((result, error) -> {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(resolution.getWidth(), resolution.getHeight());
            camera.setParameters(parameters);

            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            result.resolve(this);
        }));
    }

    @Override
    public CameraPromise<CameraApi> setDisplayOrientation(int orientation) {
        return new CameraPromise<>(((result, error) -> {
            final int previewRotation;
            if (attributes.facing == Facing.FRONT) {
                previewRotation = (360 - ((attributes.orientation + orientation) % 360)) % 360;
            } else {
                previewRotation = (attributes.orientation - orientation + 360) % 360;
            }

            camera.setDisplayOrientation(previewRotation);
            result.resolve(this);
        }));
    }

    @Override
    public CameraPromise<CameraApi> stopPreview() {
        return new CameraPromise<>(((result, error) -> {
            camera.stopPreview();

            result.resolve(this);
        }));
    }

    @Override
    public CameraPromise<CameraApi> disconnect() {
        return new CameraPromise<>(((result, error) -> {
            camera.release();
            camera = null;

            result.resolve(this);
        }));
    }

    @Override
    public FlashModule provideFlash() {
        return new Flash1();
    }

    @Override
    public FocusModule provideFocus() {
        return new Focus1();
    }

    @Override
    public ZoomModule provideZoom() {
        return new Zoom1();
    }

    @Override
    public PhotoModule providePhoto() {
        return new Photo1();
    }

    @Override
    public VideoModule provideVideo() {
        return new Video1();
    }

    static class Module {

        protected Camera camera;

        Module() {
        }

    }

    private static class Attributes {

        private Camera.CameraInfo info;
        private Camera.Parameters parameters;

        public Attributes(Camera.CameraInfo info, Camera.Parameters parameters) {
            this.info = info;
            this.parameters = parameters;
        }

        public CameraAttributes get() {
            return new CameraAttributes() {

                @Override
                protected Facing getFacing() {
                    if (info.facing == CAMERA_FACING_BACK) {
                        return Facing.BACK;
                    } else {
                        return Facing.FRONT;
                    }
                }

                @Override
                protected int getOrientation() {
                    return info.orientation;
                }

                @Override
                protected TreeSet<Resolution> getSupportedPreviewSizes() {
                    TreeSet<Resolution> sizes = new TreeSet<>();
                    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                        sizes.add(Resolution.get(size));
                    }
                    return sizes;
                }

                @Override
                protected boolean getFocusModeAutoSupported() {
                    return parameters.getSupportedFocusModes()
                            .contains(FOCUS_MODE_AUTO);
                }

                @Override
                protected boolean getFocusModeContinuousPhotoSupported() {
                    return parameters.getSupportedFocusModes()
                            .contains(FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                @Override
                protected boolean getFocusModeContinuousVideoSupported() {
                    return parameters.getSupportedFocusModes()
                            .contains(FOCUS_MODE_CONTINUOUS_VIDEO);
                }

                @Override
                protected boolean getFocusModeMacroSupported() {
                    return parameters.getSupportedFocusModes()
                            .contains(FOCUS_MODE_MACRO);
                }

                @Override
                protected boolean getFocusModeEdofSupported() {
                    return parameters.getSupportedFocusModes()
                            .contains(FOCUS_MODE_EDOF);
                }

                @Override
                protected int getMaxFocusAreas() {
                    return parameters.getMaxNumFocusAreas();
                }

                @Override
                protected boolean getZoomSupported() {
                    return parameters.isZoomSupported();
                }

                @Override
                protected boolean getSmoothZoomSupported() {
                    return parameters.isSmoothZoomSupported();
                }

                @Override
                protected int getMaxZoom() {
                    return parameters.getMaxZoom();
                }

                @Override
                protected TreeSet<ZoomRatio> getSupportedZooms() {
                    TreeSet<ZoomRatio> zooms = new TreeSet<>();
                    if (parameters.isZoomSupported()) {
                        List<Integer> supported = parameters.getZoomRatios();
                        if (supported == null || supported.size() == 0) return zooms;
                        for (Integer zoom : supported) {
                            zooms.add(new ZoomRatio(zoom, supported.get(supported.size() - 1)));
                        }
                    }
                    return zooms;
                }

                @Override
                protected boolean getFlashModeOffSupported() {
                    return parameters.getSupportedFlashModes() != null &&
                            parameters.getSupportedFlashModes().contains(FLASH_MODE_OFF);
                }

                @Override
                protected boolean getFlashModeOnSupported() {
                    return parameters.getSupportedFlashModes() != null &&
                            parameters.getSupportedFlashModes().contains(FLASH_MODE_ON);
                }

                @Override
                protected boolean getFlashModeTorchSupported() {
                    return parameters.getSupportedFlashModes() != null &&
                            parameters.getSupportedFlashModes().contains(FLASH_MODE_TORCH);
                }

                @Override
                protected TreeSet<Resolution> getSupportedPhotoSizes() {
                    TreeSet<Resolution> sizes = new TreeSet<>();
                    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                        sizes.add(Resolution.get(size));
                    }
                    return sizes;
                }

                @Override
                protected boolean getVideoSupported() {
                    return getSupportedPreviewSizes().size() > 0;
                }

                @Override
                protected Resolution getPreferredVideoSize() {
                    if (parameters.getPreferredPreviewSizeForVideo() == null) {
                        return null;
                    }

                    return Resolution.get(parameters.getPreferredPreviewSizeForVideo());
                }

                @Override
                protected TreeSet<Resolution> getSupportedVideoSizes() {
                    TreeSet<Resolution> sizes = new TreeSet<>();
                    List<Camera.Size> supported = parameters.getSupportedVideoSizes();
                    if (supported != null && supported.size() > 0) {
                        for (Camera.Size size : supported) {
                            sizes.add(Resolution.get(size));
                        }
                    }
                    return sizes;
                }

            };
        }

    }

}
