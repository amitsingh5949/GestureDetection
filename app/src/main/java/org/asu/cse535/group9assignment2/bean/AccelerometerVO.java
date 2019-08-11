package org.asu.cse535.group9assignment2.bean;

import java.util.Date;
import java.sql.Timestamp;

public class AccelerometerVO {

    private float x;
    private float y;
    private float z;
    private Timestamp timestamp;

    public AccelerometerVO() { }

    public AccelerometerVO(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public  static AccelerometerVO getInstance(float x, float y, float z) {
        AccelerometerVO obj = new AccelerometerVO(x, y, z);
        obj.timestamp = new Timestamp(new Date().getTime());
        return obj;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AccelerometerVO{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", timestamp=" + timestamp +
                '}';
    }
}
