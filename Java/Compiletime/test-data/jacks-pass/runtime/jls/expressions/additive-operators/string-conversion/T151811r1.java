
class T151811r1 {
    T151811r1() {}
    public static void main(String args[]) {
	char[] c = {'w','r','o','n','g'};
	String s = "" + c;
	if (s.equals(c.toString()))
    	    System.out.print("OK");
	else
	    System.out.print("WRONG");
    }
}
    