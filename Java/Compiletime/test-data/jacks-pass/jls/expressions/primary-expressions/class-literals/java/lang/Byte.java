
package java.lang;
public final class Byte extends Number implements Comparable
{
    public static final byte MIN_VALUE = -128;
    public static final byte MAX_VALUE = 127;
    // This may be recursive, but it works for the purpose of the test
    public static final Class TYPE = byte.class;
    public Byte(byte value) {}
    public Byte(String s) {}
    public static native String toString(byte b);
    public static native byte parseByte(String s);
    public static native byte parseByte(String s, int radix);
    public static native Byte valueOf(String s, int radix);
    public static native Byte valueOf(String s);
    public static native Byte decode(String s);
    public native byte byteValue();
    public native short shortValue();
    public native int intValue();
    public native long longValue();
    public native float floatValue();
    public native double doubleValue();
    public native String toString();
    public native int hashCode();
    public native boolean equals(Object obj);
    public native int compareTo(Byte b);
    public native int compareTo(Object o);
}
    