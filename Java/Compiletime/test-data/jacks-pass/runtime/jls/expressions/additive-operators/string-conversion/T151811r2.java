
class T151811r2 {
    T151811r2() {}
    public static void main(String args[]) {
	char[] c = null;
        try {
            String s = "" + c;
            if (s.equals("null"))
    	        System.out.print("OK");
            else
	        System.out.print("WRONG string");
        } catch (NullPointerException e) {
            System.out.print("WRONG method");
        }
    }
}
    