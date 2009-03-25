
class T165r2 {
    public static void main(String[] args) {
        final int i;
	i = 1;
	class Local { int j = i; }
	new Local() {
	    int i = j + 1;
	    { System.out.print(i); }
	};
    }
}
    