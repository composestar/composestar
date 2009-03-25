
class T15262r1 {
    public static void main(String args[]) {
	String s = "";
	char[] c = {'w','r','o','n','g'};
	s += c;
	if (s.equals(c.toString()))
	    System.out.print("OK");
	else
	    System.out.print("WRONG");
    }
}
    