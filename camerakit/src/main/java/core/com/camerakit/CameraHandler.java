package com.camerakit;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

class CameraHandler extends Handler {

    private HandlerThread thread;

    public CameraHandler() {
        this(new CameraHandlerThread());
    }

    public CameraHandler(HandlerThread handlerThread) {
        super(handlerThread.getLooper());
        this.thread = handlerThread;
    }

    private CameraHandler(Callback callback) {
        super(callback);
    }

    private CameraHandler(Looper looper) {
        super(looper);
    }

    private CameraHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    public void postSafely(SafeRunnable runnable) {
        post(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                Log.e("WonderKilna", e.toString());
            }
        });
    }

    public void kill() {
        killThread(this.thread);
        this.thread = null;
    }

    private static void killThread(HandlerThread thread) {
        if (thread == null) return;
        if (Build.VERSION.SDK_INT >= 18) {
            thread.quitSafely();
        } else {
            thread.quit();
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("CameraKit", e.toString());
        }
    }

    private static class CameraHandlerThread extends HandlerThread {

        public CameraHandlerThread() {
            this("CameraHandler" + System.currentTimeMillis());
        }

        public CameraHandlerThread(String name) {
            super(name);
            start();
        }

        public CameraHandlerThread(String name, int priority) {
            super(name, priority);
            start();
        }

    }

    interface SafeRunnable {
        void run() throws Exception;
    }

}
