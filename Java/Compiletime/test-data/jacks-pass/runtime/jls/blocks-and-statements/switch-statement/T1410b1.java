
class T1410b1 {
    public static void main(String[] args) {
	label:
	switch (args.length) {
	    case 0:
	    break label;
	}
	System.out.print("OK");
    }
}
    