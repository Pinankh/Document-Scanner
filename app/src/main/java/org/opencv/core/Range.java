package org.opencv.core;


public class Range {
    public int end;
    public int start;

    public Range(int s, int e) {
        this.start = s;
        this.end = e;
    }

    public Range() {
        this(0, 0);
    }

    public Range(double[] vals) {
        set(vals);
    }

    public void set(double[] vals) {
        int i = 0;
        if (vals != null) {
            this.start = vals.length > 0 ? (int) vals[0] : 0;
            if (vals.length > 1) {
                i = (int) vals[1];
            }
            this.end = i;
            return;
        }
        this.start = 0;
        this.end = 0;
    }

    public int size() {
        if (empty()) {
            return 0;
        }
        return this.end - this.start;
    }

    public boolean empty() {
        return this.end <= this.start;
    }

    public static Range all() {
        return new Range(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Range intersection(Range r1) {
        Range r = new Range(Math.max(r1.start, this.start), Math.min(r1.end, this.end));
        r.end = Math.max(r.end, r.start);
        return r;
    }

    public Range shift(int delta) {
        return new Range(this.start + delta, this.end + delta);
    }

    public Range clone() {
        return new Range(this.start, this.end);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits((double) this.start);
        long temp2 = Double.doubleToLongBits((double) this.end);
        return (((1 * 31) + ((int) ((temp >>> 32) ^ temp))) * 31) + ((int) ((temp2 >>> 32) ^ temp2));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        Range it = (Range) obj;
        if (this.start == it.start && this.end == it.end) {
            return true;
        }
        return false;
    }


}
