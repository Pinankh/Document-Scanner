package org.opencv.core;


public class Size {
    public double height;
    public double width;

    public Size(double width2, double height2) {
        this.width = width2;
        this.height = height2;
    }

    public Size() {
        this(0.0d, 0.0d);
    }

    public Size(Point p) {
        this.width = p.f14653x;
        this.height = p.f14654y;
    }

    public Size(double[] vals) {
        set(vals);
    }

    public void set(double[] vals) {
        double d = 0.0d;
        if (vals != null) {
            this.width = vals.length > 0 ? vals[0] : 0.0d;
            if (vals.length > 1) {
                d = vals[1];
            }
            this.height = d;
            return;
        }
        this.width = 0.0d;
        this.height = 0.0d;
    }

    public double area() {
        return this.width * this.height;
    }

    public boolean empty() {
        return this.width <= 0.0d || this.height <= 0.0d;
    }

    public Size clone() {
        return new Size(this.width, this.height);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.height);
        long temp2 = Double.doubleToLongBits(this.width);
        return (((1 * 31) + ((int) ((temp >>> 32) ^ temp))) * 31) + ((int) ((temp2 >>> 32) ^ temp2));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Size)) {
            return false;
        }
        Size it = (Size) obj;
        if (this.width == it.width && this.height == it.height) {
            return true;
        }
        return false;
    }

    public String toString() {
        return ((int) this.width) + "x" + ((int) this.height);
    }
}
