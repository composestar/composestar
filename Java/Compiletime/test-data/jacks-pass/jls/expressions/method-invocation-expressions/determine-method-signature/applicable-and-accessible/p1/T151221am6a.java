
package p1;
public class T151221am6a {
    public static class E1 extends Exception {}
    public static class E2 extends Exception {}
    public static abstract class A {
	protected abstract void m() throws E1; // note non-public accessibility
    }
}
    