
class T1413r1 {
    public static void main(String[] args) {
	int i = 0;
	for ( ; i < 100000; i += 12345)
	    if (i < 100000)
	        continue;
	System.out.print(i);
    }
}
    