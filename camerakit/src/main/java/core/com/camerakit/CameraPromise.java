package com.camerakit;

import android.os.Handler;
import android.os.Looper;

class CameraPromise<T> {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FULFILLED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_FINAL = 3;

    private final CameraHandler handler;

    private int status = STATUS_PENDING;

    private T result;
    private ResultCallback<T> onResult;

    private Throwable error;
    private ErrorCallback onError;

    private CameraPromise() {
        throw new RuntimeException("No empty constructor.");
    }

    private CameraPromise(CameraHandler cameraHandler) {
        this.handler = cameraHandler;
    }

    CameraPromise(Executor<T> executor) {
        this.handler = new CameraHandler();
        this.handler.post(() -> {
            try {
                executor.execute(this::resolve, this::reject);
            } catch (Throwable e) {
                reject(e);
            }
        });
    }

    private void resolve() {
        resolve(null);
    }

    private void resolve(T result) {
        synchronized (this.handler) {
            if (this.status != STATUS_PENDING) {
                return;
            }

            this.status = STATUS_FULFILLED;
            this.result = result;
            this.handler.notify();

            this.handler.post(this::handleResult);
        }
    }

    private void reject(Throwable error) {
        synchronized (this.handler) {
            if (this.status != STATUS_PENDING) {
                return;
            }

            this.status = STATUS_REJECTED;
            this.error = error;
            this.handler.notify();

            this.handler.post(this::handleResult);
        }
    }

    private synchronized void handleResult() {
        if (this.status == STATUS_FULFILLED && this.onResult != null) {
            this.status = STATUS_FINAL;
            this.onResult.onResult(this.result);
        } else if (this.status == STATUS_REJECTED && this.onError != null) {
            this.status = STATUS_FINAL;
            this.onError.onError(this.error);
        }
    }

    public CameraPromise<T> then(ResultCallback<T> callback) {
        CameraPromise<T> next = extendPromise(this);

        this.onError = next::reject;
        this.onResult = result -> this.handler.post(() -> {
            try {
                callback.onResult(result);
                next.resolve(result);
            } catch (Throwable e) {
                next.reject(e);
            }
        });

        this.handler.post(this::handleResult);
        return next;
    }

    public CameraPromise<T> thenUi(ResultCallback<T> callback) {
        CameraPromise<T> next = extendPromise(this);

        Handler uiHandler = new Handler(Looper.getMainLooper());

        this.onError = next::reject;
        this.onResult = result -> uiHandler.post(() -> {
            try {
                callback.onResult(result);
                next.resolve(result);
            } catch (Throwable e) {
                next.reject(e);
            }
        });

        this.handler.post(this::handleResult);
        return next;
    }

    public CameraPromise<T> thenAsync(ResultCallback<T> callback) {
        CameraPromise<T> next = extendPromise(this);

        CameraHandler asyncHandler = new CameraHandler();

        this.onError = next::reject;
        this.onResult = result -> {
            next.resolve(result);
            asyncHandler.postSafely(() -> callback.onResult(result));
        };

        this.handler.post(this::handleResult);
        return next;
    }

    public CameraPromise<T> error(ErrorCallback callback) {
        CameraPromise<T> next = extendPromise(this);

        this.onResult = next::resolve;
        this.onError = error -> this.handler.post(() -> {
            try {
                callback.onError(error);
                next.reject(error);
            } catch (Throwable e) {
                next.reject(e);
            }
        });

        this.handler.post(this::handleResult);
        return next;
    }

    public T await() {
        synchronized (handler) {
            try {
                handler.wait();
            } catch (Exception e) {
                throw new CameraException("Promise failure.", e);
            }

            if (result != null) {
                return result;
            } else if (error != null) {
                throw new CameraException("Promise failure.", error);
            } else {
                throw new CameraException("Promise failure.");
            }
        }
    }

    interface ResultCallback<T> {
        void onResult(T result);
    }

    interface ErrorCallback {
        void onError(Throwable error);
    }

    interface Executor<T> {
        void execute(Callback<T> result, ErrorCallback error) throws Throwable;

        interface Callback<T> {
            void resolve(T result);
        }

        interface ErrorCallback {
            void reject(Throwable error);
        }
    }

    private static <V> CameraPromise<V> extendPromise(CameraPromise<V> parent) {
        return new CameraPromise<>(parent.handler);
    }

}
