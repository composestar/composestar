
class T1410v2 {
    public static void main(String[] args) {
	switch (args.length) {
        case 1:
	    if (true)
                break;
	    int i = 1;
        case 0:
	    i = 2;
	    System.out.print(i + " ");
	}
	System.out.print(args.getClass().getName());
    }
}
    