package org.opencv.core;


public class Point {

    /* renamed from: x */
    public double f14653x;

    /* renamed from: y */
    public double f14654y;

    public Point(double x, double y) {
        this.f14653x = x;
        this.f14654y = y;
    }

    public Point() {
        this(0.0d, 0.0d);
    }

    public Point(double[] vals) {
        this();
        set(vals);
    }

    public void set(double[] vals) {
        double d = 0.0d;
        if (vals != null) {
            this.f14653x = vals.length > 0 ? vals[0] : 0.0d;
            if (vals.length > 1) {
                d = vals[1];
            }
            this.f14654y = d;
            return;
        }
        this.f14653x = 0.0d;
        this.f14654y = 0.0d;
    }

    public Point clone() {
        return new Point(this.f14653x, this.f14654y);
    }

    public double dot(Point p) {
        return (this.f14653x * p.f14653x) + (this.f14654y * p.f14654y);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.f14653x);
        long temp2 = Double.doubleToLongBits(this.f14654y);
        return (((1 * 31) + ((int) ((temp >>> 32) ^ temp))) * 31) + ((int) ((temp2 >>> 32) ^ temp2));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point it = (Point) obj;
        if (this.f14653x == it.f14653x && this.f14654y == it.f14654y) {
            return true;
        }
        return false;
    }

    public boolean inside(Rect r) {
        return r.contains(this);
    }

    public String toString() {
        return "{" + this.f14653x + ", " + this.f14654y + "}";
    }
}
