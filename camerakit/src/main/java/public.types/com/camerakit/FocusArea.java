package com.camerakit;

public class FocusArea {

    private float x;
    private float y;
    private Radius radius;
    private Priority priority;

    private FocusArea() {
        throw new RuntimeException("No empty constructor.");
    }

    public FocusArea(float x, float y, Radius radius, Priority priority) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.priority = priority;
    }

    public static class Radius {

        public static Radius LARGE = new Radius(50);
        public static Radius MEDIUM = new Radius(30);
        public static Radius SMALL = new Radius(10);

        private int radius;

        private Radius(int radius) {
            this.radius = radius;
        }

    }

    public static class Priority {

        public static Priority HIGH = new Priority(50);
        public static Priority NORMAL = new Priority(30);
        public static Priority LOW = new Priority(10);

        private int priority;

        private Priority(int priority) {
            this.priority = priority;
        }

    }


}
