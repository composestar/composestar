
class T15262r2 {
    public static void main(String args[]) {
        String s = "";
	char[] c = null;
        try {
            s += c;
            if (s.equals("null"))
    	        System.out.print("OK");
            else
	        System.out.print("WRONG string");
        } catch (NullPointerException e) {
            System.out.print("WRONG method");
        }
    }
}
    