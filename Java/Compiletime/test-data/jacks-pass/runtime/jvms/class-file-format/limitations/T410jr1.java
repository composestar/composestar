
class T410jr1 {
    public static void main(String[] args) {
	try {
	    // Exploit fact that Class.forName causes class initialization,
	    // which runs the verifier to see if the class compiled successfully
	    Class.forName("T410jvms4");
	    Class.forName("T410jvms4_0");
	    Class.forName("T410jvms4_1");
	    Class.forName("T410jvms4_2");
	    Class.forName("T410jvms4_3");
	    Class.forName("T410jvms4_4");
	    Class.forName("T410jvms4_5");
	    Class.forName("T410jvms4_6");
	    System.out.print("4:OK ");
	} catch (ClassNotFoundException e) {
	    System.out.print("4:inconclusive ");
	} catch (Error e) {
	    System.out.print("4:failed(" + e + ") ");
	}
	try {
// Thanks to a nice infinite loop bug in java 1.4.1, do not test this class.
//	    Class.forName("T410jvms6");

	    Class.forName("T410jvms6_0");
	    Class.forName("T410jvms6_1");
	    Class.forName("T410jvms6_2");
	    Class.forName("T410jvms6_3");
	    Class.forName("T410jvms6_4");
	    Class.forName("T410jvms6_5");
	    Class.forName("T410jvms6_6");
	    System.out.print("6:OK ");
	} catch (ClassNotFoundException e) {
	    System.out.print("6:inconclusive ");
	} catch (Error e) {
	    System.out.print("6:failed(" + e + ") ");
	}
    }
}
    