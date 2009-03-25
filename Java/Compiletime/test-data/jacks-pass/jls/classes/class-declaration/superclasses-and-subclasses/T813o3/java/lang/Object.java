
package java.lang;
public class Object {
    public native boolean equals(Object o);
    public native int hashCode();
    public native String toString();
    protected native void finalize() throws Throwable;
    protected native Object clone() throws CloneNotSupportedException;
    public final native Class getClass();
    public final native void notify() throws IllegalMonitorStateException;
    public final native void notifyAll() throws IllegalMonitorStateException;
    public final native void wait()
        throws IllegalMonitorStateException, InterruptedException;
    public final native void wait(long ms)
        throws IllegalMonitorStateException, InterruptedException;
    public final native void wait(long ms, int ns)
        throws IllegalMonitorStateException, InterruptedException;
}
