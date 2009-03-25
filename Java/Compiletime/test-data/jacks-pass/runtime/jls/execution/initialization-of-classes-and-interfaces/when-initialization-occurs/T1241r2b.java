
class T1241r2a {
    static { System.out.print(" oops "); }
}
class T1241r2b {
    public static void main(String[] args) {
	System.out.print('O');
	T1241r2a.class.toString();
	System.out.print('K');
    }
}
    