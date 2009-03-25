
class T131c2 {
    static int m(T131c2 t) { return t.i; }
    static int j = m(new T131c2());
    static final int i = 1;
    public static void main(String[] args) {
	System.out.print(j);
    }
}
    