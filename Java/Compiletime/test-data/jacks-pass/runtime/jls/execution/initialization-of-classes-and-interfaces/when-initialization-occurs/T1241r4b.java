
interface T1241r4a {
    int i = new Object() {
	{ System.out.print(" oops "); }
	int i = 1;
    }.i;
    class Inner {
	static Class c = Inner.class;
    }
}
class T1241r4b {
    public static void main(String[] args) {
	System.out.print('O');
	T1241r4a.Inner.c = null;
	System.out.print('K');
	T1241r4a.Inner.c = T1241r4a.class;
    }
}
    