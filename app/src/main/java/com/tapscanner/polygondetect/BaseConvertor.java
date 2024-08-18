package com.tapscanner.polygondetect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BaseConvertor {


    public static void Int2ByteArray(int value, byte[] data, int offset) {
        data[offset + 0] = (byte) (value & 255);
        data[offset + 1] = (byte) ((value >> 8) & 255);
        data[offset + 2] = (byte) ((value >> 16) & 255);
        data[offset + 3] = (byte) ((value >> 24) & 255);
    }

    public static double Byte2Double(byte[] pData, int offset) {
        byte[] pbyDouble = new byte[8];
        System.arraycopy(pData, offset, pbyDouble, 0, 8);
        return ByteBuffer.wrap(pbyDouble).getDouble();
    }

    public static void Double2ByteArray(double dbValue, byte[] pbyValue, int offset) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(dbValue);
        System.arraycopy(bytes, 0, pbyValue, offset, 8);
    }

    public static float byteArray2Float(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public static int byteArray2Int(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static byte[] float2ByteArray(float value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();
    }
}
