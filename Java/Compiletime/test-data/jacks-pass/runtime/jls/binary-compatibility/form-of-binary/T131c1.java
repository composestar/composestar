
class T131c1 {
    int m(T131c1 t) { return t.i; }
    int j = m(this);
    final int i = 1;
    public static void main(String[] args) {
	System.out.print(new T131c1().j);
    }
}
    