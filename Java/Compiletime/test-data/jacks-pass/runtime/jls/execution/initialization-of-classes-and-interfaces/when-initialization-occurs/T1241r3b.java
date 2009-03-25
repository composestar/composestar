
class T1241r3a {
    static { System.out.print(" oops "); }
    static class Inner {
	static Class c = Inner.class;
    }
}
class T1241r3b {
    public static void main(String[] args) {
	System.out.print('O');
	T1241r3a.Inner.c = null;
	System.out.print('K');
	T1241r3a.Inner.c = T1241r3a.class;
    }
}
    