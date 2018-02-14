package com.camerakit;

public class ZoomRatio implements Comparable<ZoomRatio> {

    private int zoom;
    private int maxZoom;

    private ZoomRatio() {
        throw new RuntimeException("No empty constructor.");
    }

    public ZoomRatio(int zoom, int maxZoom) {
        this.zoom = zoom;
        this.maxZoom = maxZoom;
    }

    @Override
    public int compareTo(ZoomRatio o) {
        return 0;
    }

}
