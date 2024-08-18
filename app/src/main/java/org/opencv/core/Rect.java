package org.opencv.core;

public class Rect {
    public int height;
    public int width;

    /* renamed from: x */
    public int f14658x;

    /* renamed from: y */
    public int f14659y;

    public Rect(int x, int y, int width2, int height2) {
        this.f14658x = x;
        this.f14659y = y;
        this.width = width2;
        this.height = height2;
    }

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(Point p1, Point p2) {
        this.f14658x = (int) (p1.f14653x < p2.f14653x ? p1.f14653x : p2.f14653x);
        this.f14659y = (int) (p1.f14654y < p2.f14654y ? p1.f14654y : p2.f14654y);
        this.width = ((int) (p1.f14653x > p2.f14653x ? p1.f14653x : p2.f14653x)) - this.f14658x;
        this.height = ((int) (p1.f14654y > p2.f14654y ? p1.f14654y : p2.f14654y)) - this.f14659y;
    }

    public Rect(Point p, Size s) {
        this((int) p.f14653x, (int) p.f14654y, (int) s.width, (int) s.height);
    }

    public Rect(double[] vals) {
        set(vals);
    }

    public void set(double[] vals) {
        int i = 0;
        if (vals != null) {
            this.f14658x = vals.length > 0 ? (int) vals[0] : 0;
            this.f14659y = vals.length > 1 ? (int) vals[1] : 0;
            this.width = vals.length > 2 ? (int) vals[2] : 0;
            if (vals.length > 3) {
                i = (int) vals[3];
            }
            this.height = i;
            return;
        }
        this.f14658x = 0;
        this.f14659y = 0;
        this.width = 0;
        this.height = 0;
    }

    public Rect clone() {
        return new Rect(this.f14658x, this.f14659y, this.width, this.height);
    }

    /* renamed from: tl */
    public Point mo64934tl() {
        return new Point((double) this.f14658x, (double) this.f14659y);
    }

    /* renamed from: br */
    public Point mo64926br() {
        return new Point((double) (this.f14658x + this.width), (double) (this.f14659y + this.height));
    }

    public Size size() {
        return new Size((double) this.width, (double) this.height);
    }

    public double area() {
        return (double) (this.width * this.height);
    }

    public boolean empty() {
        return this.width <= 0 || this.height <= 0;
    }

    public boolean contains(Point p) {
        return ((double) this.f14658x) <= p.f14653x && p.f14653x < ((double) (this.f14658x + this.width)) && ((double) this.f14659y) <= p.f14654y && p.f14654y < ((double) (this.f14659y + this.height));
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits((double) this.height);
        long temp2 = Double.doubleToLongBits((double) this.width);
        long temp3 = Double.doubleToLongBits((double) this.f14658x);
        long temp4 = Double.doubleToLongBits((double) this.f14659y);
        return (((((((1 * 31) + ((int) ((temp >>> 32) ^ temp))) * 31) + ((int) ((temp2 >>> 32) ^ temp2))) * 31) + ((int) ((temp3 >>> 32) ^ temp3))) * 31) + ((int) ((temp4 >>> 32) ^ temp4));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rect)) {
            return false;
        }
        Rect it = (Rect) obj;
        if (this.f14658x == it.f14658x && this.f14659y == it.f14659y && this.width == it.width && this.height == it.height) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "{" + this.f14658x + ", " + this.f14659y + ", " + this.width + "x" + this.height + "}";
    }
}
