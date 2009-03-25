
class T151811r4 {
    T151811r4() {}
    static int i = 0;
    static void check(String s, int value) {
	i++;
	if (s.length() != 1)
	    System.out.print(i++);
	else {
	    i++;
	    if (s.charAt(0) != value)
	        System.out.print(i);
	}
    }
    public static void main(String[] args) {
	check(""+'\0', 0);
	check('\0'+"", 0);
	check(""+'a', 0x61);
	check('a'+"", 0x61);
	check(""+'\200', 0x80);
	check('\200'+"", 0x80);
	check(""+'\uffff', 0xffff);
	check('\uffff'+"", 0xffff);
	check(""+0, 0x30);
	check(0+"", 0x30);
	check(""+1, 0x31);
	check(1+"", 0x31);
	check(""+0L, 0x30);
	check(0L+"", 0x30);
	check(""+1L, 0x31);
	check(1L+"", 0x31);
	System.out.print("OK");
    }
}
    