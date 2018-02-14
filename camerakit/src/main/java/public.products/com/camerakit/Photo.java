package com.camerakit;

public class Photo {

//    protected Context context;
//
//    private byte[] bytes;
//
//    private Photo() {
//    }
//
//    Photo(Context context, byte[] bytes) {
//        this.context = context;
//    }
//
//    public CameraPromise<byte[]> toJpegBytes() {
//        mBytesPromise = new CameraPromise<>((resolve, reject) -> {
//            if (bytes != null) {
//                resolve.call(bytes);
//            } else if (mError != null) {
//                reject.call(mError);
//            }
//        });
//
//        return mBytesPromise;
//    }
//
//    public CameraPromise<CameraBitmap> toBitmap() {
//        return new CameraPromise<>(((resolve, reject) -> {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            resolve.call(new CameraBitmap(CameraJpegBytes.this, bitmap));
//        }));
//    }
//
//    public CameraPromise<CameraJpegFile> toFile() {
//        String label = "camerakit";
//        return toGalleryFile(label);
//    }
//
//    public CameraPromise<CameraJpegFile> toFile(String folderName) {
//        return toGalleryFile(folderName, System.currentTimeMillis() + ".jpg");
//    }
//
//    public CameraPromise<CameraJpegFile> toFile(String folderName, String fileName) {
//        return new CameraPromise<>(((resolve, reject) -> {
//            File directory = new File(context.getFilesDir(), folderName);
//
//            if (!directory.isDirectory()) {
//                directory.mkdirs();
//            }
//
//            File imageFile = new File(directory, fileName);
//            FileOutputStream out = new FileOutputStream(imageFile);
//            out.write(getBytes());
//            out.flush();
//            out.close();
//
//            resolve.call(new CameraJpegFile(context, imageFile));
//        }));
//    }
//
//    public CameraPromise<CameraJpegFile> toGalleryFile() {
//        String label = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
//        return toGalleryFile(label);
//    }
//
//    public CameraPromise<CameraJpegFile> toGalleryFile(String folderName) {
//        return toGalleryFile(folderName, System.currentTimeMillis() + ".jpg");
//    }
//
//    public CameraPromise<CameraJpegFile> toGalleryFile(String folderName, String fileName) {
//        return new CameraPromise<>(((resolve, reject) -> {
//            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + folderName);
//
//            if (!directory.isDirectory()) {
//                directory.mkdirs();
//            }
//
//            File imageFile = new File(directory, fileName);
//            FileOutputStream out = new FileOutputStream(imageFile);
//            out.write(getBytes());
//            out.flush();
//            out.close();
//
//            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, (path, uri) -> {
//                resolve.call(new CameraJpegFile(context, imageFile));
//            });
//        }));
//    }
//
//
//    public BytesPromise toThumbnail() {
//        BytesPromise output = new BytesPromise();
//
//        output.run(pending -> {
//            ExifInterface exifInterface = new ExifInterface(mFile.getAbsolutePath());
//            if (exifInterface.hasThumbnail()) {
//                byte[] thumbnail = exifInterface.getThumbnail();
//                if (thumbnail != null) {
//                    pending.set(new CameraJpegBytes(context, thumbnail));
//                    return;
//                }
//            }
//
//            pending.set(new CameraException("No thumbnail available from " + mFile.getAbsolutePath()));
//        });
//
//        return output;
//    }

}
